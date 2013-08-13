/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.utilities;

import com.google.gwt.user.client.ui.Label;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.ActivityResponseType;
import de.cismet.projecttracker.client.types.HolidayType;
import de.cismet.projecttracker.client.uicomps.SheetsPanel;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class TimeCalculator {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param   act             DOCUMENT ME!
     * @param   calculatePause  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getWorkingHoursForActivity(final ActivityDTO act, final boolean calculatePause) {
        double hours = 0.0;
        double dhow = 0.0;

        /*
         * it is possible that the there is no staff for the activity ( e.g Holiday activites), in this case calculate
         * the dhow with the currently logged in user
         */
        ContractDTO contract = null;
        if (act.getStaff() == null) {
            try {
                contract = ProjectTrackerEntryPoint.getInstance().getContractForStaff(act.getDay());
            } catch (InvalidInputValuesException ex) {
                Logger.getLogger(TimeCalculator.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            contract = getContractForDay(act.getStaff(), act.getDay());
        }

        if (contract != null) {
            dhow = contract.getWhow() / 5;
        }

        // if the pause should be taken into account return the pause time
        if (calculatePause && (act.getWorkCategory() != null)
                    && (act.getWorkPackage().getId() == ActivityDTO.PAUSE_ID)) {
            return act.getWorkinghours();
        }

        if (act.getKindofactivity() == ActivityDTO.HOLIDAY) {
            hours += dhow;
        } else if (act.getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
            hours += dhow / 2;
        } else {
            if ((act.getWorkPackage() != null) && (act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID)) {
                if ((act.getWorkPackage().getId() == ActivityDTO.HOLIDAY_ID)
                            || (act.getWorkPackage().getId() == ActivityDTO.LECTURE_ID)) {
                    if ((act.getWorkinghours() == 0) && (dhow > 0)) {
                        hours += dhow;
                    } else {
                        hours += act.getWorkinghours();
                    }
                } else if (act.getWorkPackage().getId() == ActivityDTO.ILLNESS_ID) {
                    if ((act.getWorkinghours() == 0) && (dhow > 0)) {
                        hours += dhow;
                    } else if (act.getWorkinghours() != -1) {
                        hours += act.getWorkinghours();
                    }
                } // else if (act.getWorkCategory() != null && act.getWorkCategory().getId() == TRAVEL_WORK_CATEGORY) {
                // hours += act.getWorkinghours();
                // }
                else {
                    hours += act.getWorkinghours();
                }
            }
        }

        return hours;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   act  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getWorkingHoursForActivity(final ActivityDTO act) {
        return getWorkingHoursForActivity(act, false);
    }

    /**
     * Calculates the wokring hours for the week of year for s and appends the tim in hours to the label l.
     *
     * @param  year    DOCUMENT ME!
     * @param  week    DOCUMENT ME!
     * @param  s       DOCUMENT ME!
     * @param  l       DOCUMENT ME!
     * @param  prefix  DOCUMENT ME!
     */
    public static void getWorkingHoursForWeek(final int year,
            final int week,
            final StaffDTO s,
            final Label l,
            final String prefix) {
        final BasicAsyncCallback<ActivityResponseType> callback = new BasicAsyncCallback<ActivityResponseType>() {

                @Override
                protected void afterExecution(final ActivityResponseType result, final boolean operationFailed) {
                    if (!operationFailed) {
                        double hours = 0;
                        for (final ActivityDTO act : result.getActivities()) {
                            if ((act.getKindofactivity() == ActivityDTO.ACTIVITY)
                                        && (act.getWorkPackage() != null)
                                        && (act.getWorkPackage().getId() != ActivityDTO.SPARE_TIME_ID)
                                        && (act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID)) {
                                hours += TimeCalculator.getWorkingHoursForActivity(act);
                            }
                        }
                        for (final HolidayType holiday : result.getHolidays()) {
                            hours += holiday.getHours();
                        }

                        l.setText(prefix + DateHelper.doubleToHours(hours) + " h");
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).getActivityDataByWeek(s, year, week, callback);
    }

    /**
     * Calculates the wokring balance for the week of year for s and sets the labels text as the prefix appended the
     * time in hours to the prefix.
     *
     * @param  year    DOCUMENT ME!
     * @param  week    DOCUMENT ME!
     * @param  s       DOCUMENT ME!
     * @param  l       DOCUMENT ME!
     * @param  prefix  DOCUMENT ME!
     */
    public static void getWorkingBalanceForWeek(final int year,
            final int week,
            final StaffDTO s,
            final Label l,
            final String prefix) {
        final BasicAsyncCallback<ActivityResponseType> callback = new BasicAsyncCallback<ActivityResponseType>() {

                @Override
                protected void afterExecution(final ActivityResponseType result, final boolean operationFailed) {
                    double hours = 0.0;
                    if (!operationFailed) {
                        for (final ActivityDTO act : result.getActivities()) {
                            if ((act.getKindofactivity() == ActivityDTO.ACTIVITY)
                                        && (act.getWorkPackage() != null)
                                        && (act.getWorkPackage().getId() != ActivityDTO.SPARE_TIME_ID)
                                        && (act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID)) {
                                hours += getWorkingHoursForActivity(act);
                            }
                        }
                        for (final HolidayType holiday : result.getHolidays()) {
                            hours += holiday.getHours();
                        }

                        double weekDebit = 0.0;
                        try {
                            weekDebit = hours
                                        - ProjectTrackerEntryPoint.getInstance().getContractForStaff(week, year)
                                        .getWhow();
                        } catch (InvalidInputValuesException ex) {
                            Logger.getLogger(SheetsPanel.class.getName())
                                    .log(
                                        Level.SEVERE,
                                        "Could not get valid "
                                        + "Contract for Staff "
                                        + ProjectTrackerEntryPoint.getInstance().getStaff()
                                        + " and Week/year"
                                        + week
                                        + "/"
                                        + year,
                                        ex);
                        }
                        l.setText(prefix + DateHelper.doubleToHours(weekDebit) + " h");
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true)
                .getActivityDataByWeek(ProjectTrackerEntryPoint.getInstance().getStaff(), year, week, callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ContractDTO getContractForDay(final StaffDTO s, final Date d) {
        if ((s != null) && (s.getContracts() != null) && !s.getContracts().isEmpty()) {
            for (final ContractDTO contract : s.getContracts()) {
                if (contract.getFromdate().before(d)
                            && ((contract.getTodate() == null) || contract.getTodate().after(d))) {
                    return contract;
                }
            }
        }
        return null;
    }
}
