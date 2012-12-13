/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.utilities;

import com.google.gwt.user.client.ui.Label;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmeiers
 */
public class TimeCalculator {

    public static double getWorkingHoursForActivity(ActivityDTO act) {
        double hours = 0.0;
        double dhow = 0.0;

        ContractDTO contract = null;
        try {
            contract = ProjectTrackerEntryPoint.getInstance().getContractForStaff(act.getDay());
        } catch (InvalidInputValuesException ex) {
            Logger.getLogger(SheetsPanel.class.getName()).log(Level.SEVERE, "Could not get valid Contract for Staff "
                    + ProjectTrackerEntryPoint.getInstance().getStaff() + " and Date " + act.getDay(), ex);
        }
        if (contract != null) {
            dhow = contract.getWhow() / 5;
        }

        if (act.getKindofactivity() == ActivityDTO.HOLIDAY || act.getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
            hours += act.getWorkinghours();
        } else {
            if (act.getWorkPackage() != null && act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID) {
                if (act.getWorkPackage().getId() == ActivityDTO.HOLIDAY_ID || act.getWorkPackage().getId() == ActivityDTO.LECTURE_ID) {
                    if (act.getWorkinghours() == 0 && dhow > 0) {
                        hours += dhow;
                    } else {
                        hours += act.getWorkinghours();
                    }
                } else if (act.getWorkPackage().getId() == ActivityDTO.ILLNESS_ID) {
                    if (act.getWorkinghours() == 0 && dhow > 0) {
                        hours += dhow;
                    } else if(act.getWorkinghours()!=-1){
                        hours += act.getWorkinghours();
                    }
                } //                else if (act.getWorkCategory() != null && act.getWorkCategory().getId() == TRAVEL_WORK_CATEGORY) {
                //                    hours += act.getWorkinghours();
                //                } 
                else {
                    hours += act.getWorkinghours();
                }
            }
        }

        return hours;
    }

    /**
     * Calculates the wokring hours for the week of year for s and appends the tim in hours to the label l
     * @param year
     * @param week
     * @param s
     * @param l 
     */
    public static void getWorkingHoursForWeek(final int year, final int week, StaffDTO s, final Label l, final String prefix) {
        BasicAsyncCallback<ActivityResponseType> callback = new BasicAsyncCallback<ActivityResponseType>() {

            @Override
            protected void afterExecution(ActivityResponseType result, boolean operationFailed) {
                if (!operationFailed) {
                    double hours = 0;
                    for (ActivityDTO act : result.getActivities()) {
                        if (act.getKindofactivity() == ActivityDTO.ACTIVITY
                                && act.getWorkPackage() != null
                                && act.getWorkPackage().getId() != ActivityDTO.SPARE_TIME_ID
                                && act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID) {
                            hours += TimeCalculator.getWorkingHoursForActivity(act);
                        }
                    }
                    for (HolidayType holiday : result.getHolidays()) {
                        hours += holiday.getHours();
                    }

                    l.setText(prefix + DateHelper.doubleToHours(hours) + " h");
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getActivityDataByWeek(s, year, week, callback);
    }
    
     /**
     * Calculates the wokring balance for the week of year for s and sets the labels text 
     *  as the prefix appended the time in hours to the prefix. 
     * @param year
     * @param week
     * @param s
     * @param l 
     */
    public static void getWorkingBalanceForWeek(final int year, final int week, StaffDTO s, final Label l, final String prefix) {

        BasicAsyncCallback<ActivityResponseType> callback = new BasicAsyncCallback<ActivityResponseType>() {

            @Override
            protected void afterExecution(ActivityResponseType result, boolean operationFailed) {
                double hours = 0.0;
                if (!operationFailed) {
                    for (ActivityDTO act : result.getActivities()) {
                        if (act.getKindofactivity() == ActivityDTO.ACTIVITY
                                && act.getWorkPackage() != null
                                && act.getWorkPackage().getId() != ActivityDTO.SPARE_TIME_ID
                                && act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID) {
                            hours += getWorkingHoursForActivity(act);
                        }
                    }
                    for (HolidayType holiday : result.getHolidays()) {
                        hours += holiday.getHours();
                    }

                    double weekDebit = 0.0;
                    try {
                        weekDebit = hours - ProjectTrackerEntryPoint.getInstance().getContractForStaff(week, year).getWhow();
                    } catch (InvalidInputValuesException ex) {
                        Logger.getLogger(SheetsPanel.class.getName()).log(Level.SEVERE, "Could not get valid "
                                + "Contract for Staff " + ProjectTrackerEntryPoint.getInstance().getStaff()
                                + " and Week/year" + week + "/" + year, ex);
                    }
                    l.setText(prefix + DateHelper.doubleToHours(weekDebit) + " h");
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getActivityDataByWeek(ProjectTrackerEntryPoint.getInstance().getStaff(), year, week, callback);
    }
}
