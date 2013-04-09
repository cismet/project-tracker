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
package de.cismet.projecttracker.server;

import java.text.SimpleDateFormat;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cismet.projecttracker.client.ProjectService;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.exceptions.*;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.types.ActivityResponseType;

import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.WorkCategory;
import de.cismet.projecttracker.report.db.entities.WorkPackage;

import de.cismet.projecttracker.utilities.DBManagerWrapper;
import de.cismet.projecttracker.utilities.DTOManager;
import de.cismet.projecttracker.utilities.LanguageBundle;
import de.cismet.projecttracker.utilities.Utilities;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class PauseChecker extends Timer {

    //~ Static fields/initializers ---------------------------------------------

    private static final int DAY = GregorianCalendar.DAY_OF_WEEK;
    private static final int YEAR = GregorianCalendar.YEAR;
    private static final int WEEK = GregorianCalendar.WEEK_OF_YEAR;
    private static final double PAUSE_TIME = 0.75d;
    private static final double WORK_TIME = 6.0d;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PauseChecker object.
     *
     * @param  service         DOCUMENT ME!
     * @param  intervalInDays  DOCUMENT ME!
     */
    public PauseChecker(final ProjectService service, final int intervalInDays) {
//        GregorianCalendar firstExec = new GregorianCalendar();
//        firstExec.set(firstExec.get(YEAR), firstExec.get(GregorianCalendar.MONTH), firstExec.get(GregorianCalendar.DAY_OF_MONTH), 4, 30);
//        this.schedule(new PauseCheckerTask(service, from), new Date(), 24 * 60 * 60 * 1000);
        this.schedule(new PauseCheckerTask(service, intervalInDays), 5000L);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class PauseCheckerTask extends TimerTask {

        //~ Static fields/initializers -----------------------------------------

        private static final String subject = "ProjectTracker added a Pause Task automatically";
        private static final String preMessage =
            "ProjectTracker has added a Pause Task for following days automatically: \n\n";
        private static final String postMessage = "\nJust to remeber: You need at least 45 min of Pause in case "
                    + " of 6 hours of workingtime!!!";

        //~ Instance fields ----------------------------------------------------

        private ProjectService service;
        private GregorianCalendar fromDay;
        private final ArrayList<Date> correctedDays = new ArrayList<Date>();
        private final DTOManager dtoManager = new DTOManager();

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new PauseCheckerTask object.
         *
         * @param  s               DOCUMENT ME!
         * @param  intervalInDays  DOCUMENT ME!
         */
        public PauseCheckerTask(final ProjectService s, final int intervalInDays) {
            this.service = s;
            this.fromDay = new GregorianCalendar();
            fromDay.set(GregorianCalendar.DAY_OF_YEAR, fromDay.get(GregorianCalendar.DAY_OF_YEAR) - intervalInDays);
        }

        //~ Methods ------------------------------------------------------------

        @Override
        public void run() {
            try {
                final ArrayList<StaffDTO> currentStaff = getCurrentEmployees();
                final GregorianCalendar now = new GregorianCalendar();
                if ((currentStaff == null) | currentStaff.isEmpty()) {
                    Logger.getLogger(PauseChecker.class.getName())
                            .log(Level.WARNING, "Employee List is null or empty", new IllegalStateException());
                    return;
                }

                // iterate for each staff through the weeks between 'from-due-date' and now. Check the pause for each
                // day
                for (final StaffDTO staff : currentStaff) {
                    final GregorianCalendar current = new GregorianCalendar();
                    current.setTime(fromDay.getTime());
                    correctedDays.clear();
                    do {
                        final ActivityResponseType activities = service.getActivityDataByWeek(
                                staff,
                                current.get(YEAR),
                                current.get(WEEK));
                        GregorianCalendar til;
                        GregorianCalendar from;
                        if ((current.get(YEAR) == fromDay.get(YEAR)) && (current.get(WEEK) == fromDay.get(WEEK))) {
                            // check from due date to last sunday of the currently examined week
                            from = fromDay;
                            til = new GregorianCalendar();
                            til.set(YEAR, current.get(YEAR));
                            til.set(WEEK, current.get(WEEK));
                            til.set(DAY, GregorianCalendar.SUNDAY);
                        } else if ((current.get(YEAR) == now.get(YEAR)) && (current.get(WEEK) == now.get(WEEK))) {
                            // check from first day of currently examined week to now
                            from = new GregorianCalendar();
                            from.set(YEAR, now.get(YEAR));
                            from.set(WEEK, now.get(WEEK));
                            from.set(DAY, GregorianCalendar.MONDAY);
                            til = now;
                        } else {
                            // check from first day to last day of the currently examined week
                            from = new GregorianCalendar();
                            from.set(YEAR, current.get(YEAR));
                            from.set(WEEK, current.get(WEEK));
                            from.set(DAY, GregorianCalendar.MONDAY);
                            til = new GregorianCalendar();
                            til.set(YEAR, current.get(YEAR));
                            til.set(WEEK, current.get(WEEK));
                            til.set(DAY, GregorianCalendar.SUNDAY);
                        }
                        checkPauseforWeek(staff, activities, from, til);
                        current.add(WEEK, 1);
                    } while ((current.get(YEAR) <= now.get(YEAR)) && (current.get(WEEK) <= now.get(WEEK)));

                    if (!correctedDays.isEmpty()) {
                        informUserPerMail(staff);
                    }
                }
            } catch (InvalidInputValuesException ex) {
                Logger.getLogger(PauseChecker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DataRetrievalException ex) {
                Logger.getLogger(PauseChecker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (PermissionDenyException ex) {
                Logger.getLogger(PauseChecker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSessionException ex) {
                Logger.getLogger(PauseChecker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   staff       DOCUMENT ME!
         * @param   activities  DOCUMENT ME!
         * @param   firstDoW    DOCUMENT ME!
         * @param   lastDoW     DOCUMENT ME!
         *
         * @throws  DataRetrievalException       DOCUMENT ME!
         * @throws  InvalidInputValuesException  DOCUMENT ME!
         * @throws  NoSessionException           DOCUMENT ME!
         * @throws  PermissionDenyException      DOCUMENT ME!
         */
        private void checkPauseforWeek(final StaffDTO staff,
                final ActivityResponseType activities,
                final GregorianCalendar firstDoW,
                final GregorianCalendar lastDoW) throws DataRetrievalException,
            InvalidInputValuesException,
            NoSessionException,
            PermissionDenyException {
            final GregorianCalendar currentDay = new GregorianCalendar();
            currentDay.setTime(firstDoW.getTime());

            for (int i = firstDoW.get(DAY); i <= lastDoW.get(DAY); i++) {
                currentDay.set(DAY, i);
                double pauseTime = 0;
                double workingHours = 0;
                ActivityDTO lastEndOfDayActivity = null;
                Collections.sort(activities.getActivities(), new ActivityDayComparator());
                for (final ActivityDTO activity : activities.getActivities()) {
                    final GregorianCalendar activityDay = new GregorianCalendar();
                    activityDay.setTime(activity.getDay());
                    if (activityDay.get(DAY) == currentDay.get(DAY)) {
                        if (activity.getKindofactivity() == ActivityDTO.END_OF_DAY) {
                            lastEndOfDayActivity = activity;
                        } else if (activity.getKindofactivity() == ActivityDTO.BEGIN_OF_DAY) {
                            if (lastEndOfDayActivity != null) {
                                pauseTime += (activity.getDay().getTime() - lastEndOfDayActivity.getDay().getTime())
                                            / 1000 / 60 / 60d;
                            }
                        } else {
                            final long wpId = activity.getWorkPackage().getId();
                            if (wpId == ActivityDTO.PAUSE_ID) {
                                pauseTime += activity.getWorkinghours();
                            } else if (!((wpId == ActivityDTO.ILLNESS_ID) || (wpId == ActivityDTO.SPARE_TIME_ID)
                                            || (wpId == ActivityDTO.HOLIDAY_ID)
                                            || (wpId == ActivityDTO.LECTURE_ID)
                                            || (wpId == ActivityDTO.SPECIAL_HOLIDAY_ID))) {
                                workingHours += activity.getWorkinghours();
                            }
                        }
                    }
                }

                // if the workingTime > than 6 hours and the pause time is < 45 min, add a pause task and save the
                // day...
                if ((workingHours > WORK_TIME) && (pauseTime < PAUSE_TIME)) {
                    correctedDays.add(currentDay.getTime());
                    final ActivityDTO pauseActivity = new ActivityDTO();
                    pauseActivity.setStaff(staff);
                    pauseActivity.setWorkinghours(PAUSE_TIME - pauseTime);
                    pauseActivity.setDay(currentDay.getTime());
                    final DBManagerWrapper dbManager = new DBManagerWrapper();

                    final WorkPackageDTO pauseWP = (WorkPackageDTO)dtoManager.clone((WorkPackage)
                            dbManager.getObjectByAttribute(WorkPackage.class, "id", 408L));
                    pauseActivity.setWorkPackage(pauseWP);
                    try {
                        saveActivity(pauseActivity);
                    } catch (FullStopException ex) {
                        Logger.getLogger(PauseChecker.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (PersistentLayerException ex) {
                        Logger.getLogger(PauseChecker.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  staff  DOCUMENT ME!
         */
        private void informUserPerMail(final StaffDTO staff) {
            String message = preMessage;
            for (final Date d : correctedDays) {
                message += d.toString() + "\n";
            }
            message += postMessage;
//            Utilities.sendCollectedEmail(staff.getEmail(), subject, message);
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  InvalidInputValuesException  DOCUMENT ME!
         * @throws  DataRetrievalException       DOCUMENT ME!
         * @throws  PermissionDenyException      DOCUMENT ME!
         * @throws  NoSessionException           DOCUMENT ME!
         */
        private ArrayList<StaffDTO> getCurrentEmployees() throws InvalidInputValuesException,
            DataRetrievalException,
            PermissionDenyException,
            NoSessionException {
            final DBManagerWrapper dbManager = new DBManagerWrapper();

            try {
                final List<Staff> result = dbManager.getObjectsByAttribute(
                        "select distinct staff from Staff as staff left join staff.contracts as contract where contract.todate=null or contract.todate>current_timestamp");

                // convert the server version of the Project type to the client version of the Project type
                return (ArrayList<StaffDTO>)dtoManager.clone(result);
            } finally {
                dbManager.closeSession();
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param   activity  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         *
         * @throws  FullStopException            DOCUMENT ME!
         * @throws  InvalidInputValuesException  DOCUMENT ME!
         * @throws  DataRetrievalException       DOCUMENT ME!
         * @throws  PersistentLayerException     DOCUMENT ME!
         * @throws  PermissionDenyException      DOCUMENT ME!
         * @throws  NoSessionException           DOCUMENT ME!
         */
        private ActivityDTO saveActivity(final ActivityDTO activity) throws FullStopException,
            InvalidInputValuesException,
            DataRetrievalException,
            PersistentLayerException,
            PermissionDenyException,
            NoSessionException {
            final Activity activityHib = (Activity)dtoManager.merge(activity);

            DBManagerWrapper dbManager = new DBManagerWrapper();

            try {
                final WarningSystem warningSystem = new WarningSystem();
                warningSystem.saveActivity(activityHib);
                final Activity act = (Activity)dbManager.getObject(Activity.class, new Long(activityHib.getId()));

                if (act != null) {
                    final String actText = act.toString();
                    activityHib.setReports(act.getReports());
                }
                if (activityHib.getWorkCategory() == null) {
                    activityHib.setWorkCategory((WorkCategory)dbManager.getObject(
                            WorkCategory.class,
                            WorkCategoryDTO.WORK));
                }

                dbManager.closeSession();
                dbManager = new DBManagerWrapper();
                dbManager.saveObject(activityHib);

                return activity;
            } finally {
                dbManager.closeSession();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    protected final class ActivityDayComparator implements Comparator<ActivityDTO> {

        //~ Methods ------------------------------------------------------------

        @Override
        public int compare(final ActivityDTO o1, final ActivityDTO o2) {
            final Date dayO1 = o1.getDay();
            final Date dayO2 = o2.getDay();

            if (dayO1.before(dayO2)) {
                return -1;
            } else if (dayO1.after(dayO2)) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
