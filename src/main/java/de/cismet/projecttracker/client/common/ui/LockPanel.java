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
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.*;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.*;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class LockPanel extends Composite implements ClickHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static LockPanel.LockPanelUiBinder uiBinder = GWT.create(LockPanel.LockPanelUiBinder.class);
    private static final String CHECKBOX_TOOLTIP =
        "Select this box to lock the corresponding day. If you lock a day, you cannot change the activities of this day until an administrator unlocks the day.";

    //~ Instance fields --------------------------------------------------------

    @UiField
    SimpleCheckBox monday;
    @UiField
    SimpleCheckBox tuesday;
    @UiField
    SimpleCheckBox wednesday;
    @UiField
    SimpleCheckBox thursday;
    @UiField
    SimpleCheckBox friday;
    @UiField
    SimpleCheckBox saturday;
    @UiField
    SimpleCheckBox sunday;
    @UiField
    AbsolutePanel boundaryPanel;
    private SimpleCheckBox[] days = new SimpleCheckBox[7];
    private TaskStory taskStory;
    private Date firstDayOfWeek;
    private Story times;
    private SimpleCheckBox weekLockCB;
    private ArrayList<SimpleCheckBox> lockedDays = new ArrayList<SimpleCheckBox>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LockPanel object.
     */
    public LockPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        days[0] = sunday;
        days[1] = monday;
        days[2] = tuesday;
        days[3] = wednesday;
        days[4] = thursday;
        days[5] = friday;
        days[6] = saturday;
        monday.addClickHandler(this);
        tuesday.addClickHandler(this);
        wednesday.addClickHandler(this);
        thursday.addClickHandler(this);
        friday.addClickHandler(this);
        saturday.addClickHandler(this);
        sunday.addClickHandler(this);

        monday.setTitle(CHECKBOX_TOOLTIP);
        tuesday.setTitle(CHECKBOX_TOOLTIP);
        wednesday.setTitle(CHECKBOX_TOOLTIP);
        thursday.setTitle(CHECKBOX_TOOLTIP);
        friday.setTitle(CHECKBOX_TOOLTIP);
        saturday.setTitle(CHECKBOX_TOOLTIP);
        sunday.setTitle(CHECKBOX_TOOLTIP);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void onClick(final ClickEvent event) {
        int offset = 0;
        final Date day = new Date(firstDayOfWeek.getTime());
        if (event.getSource() == monday) {
            offset = 0;
        } else if (event.getSource() == tuesday) {
            offset = 1;
        } else if (event.getSource() == wednesday) {
            offset = 2;
        } else if (event.getSource() == thursday) {
            offset = 3;
        } else if (event.getSource() == friday) {
            offset = 4;
        } else if (event.getSource() == saturday) {
            offset = 5;
        } else if (event.getSource() == sunday) {
            offset = 6;
        }
        DateHelper.addDays(day, offset);
        handleClick(day);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  DOCUMENT ME!
     */
    private void unlockDay(final Date day) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        setDisabledStatusVisible(day, false);

        lockCB.setEnabled(true);
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();

        final BasicAsyncCallback<ArrayList<ActivityDTO>> callback = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {

                @Override
                protected void afterExecution(final ArrayList<ActivityDTO> result, final boolean operationFailed) {
                    if (!operationFailed) {
                        for (final ActivityDTO activity : result) {
                            if (activity.getKindofactivity() == ActivityDTO.LOCKED_DAY) {
                                final ActivityDTO lockTask = activity;
                                final BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                                        @Override
                                        protected void afterExecution(final Void result,
                                                final boolean operationFailed) {
                                        }
                                    };
                                if (lockTask != null) {
                                    ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(lockTask, callback);
                                }
                            }
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).getActivityByDay(staff, day, callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  DOCUMENT ME!
     */
    public void refuseLock(final Date day) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        lockCB.setValue(false);
        lockedDays.remove(lockCB);
        weekLockCB.setValue(false);
        weekLockCB.setEnabled(true);
        unlockDay(day);
        ProjectTrackerEntryPoint.outputBox("Pause Policy is not fulfilled! Can not lock this day.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  DOCUMENT ME!
     */
    private void handleClick(final Date day) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        // set the enable status to false to prevent the user from a new click during calculation
        lockCB.setEnabled(false);
        /*
         * if the lock status of the day is removed, check if the logged in user has admin permission and remove the
         * lock status if so this day shall be locked add a locked-day-activity
         */
        if (!lockCB.getValue()) {
            if (ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                // logged in user has admin permission, lets unlock this day...
                lockedDays.remove(lockCB);
                weekLockCB.setValue(false);
                weekLockCB.setEnabled(true);
                unlockDay(day);
            }
        } else {
            // lets lock the day...
            // check if there are tasks with an exired wp
            if (!checkWorkpackagePeriods(day)) {
                lockCB.setEnabled(true);
                lockCB.setValue(false);
                return;
            }

            // first check if that there are no times left
            if (!checkTimeSlots(day)) {
                ProjectTrackerEntryPoint.outputBox(
                    "The working time of the activites does not match the time for this day. You have to correct in order to lock this day");
                lockCB.setEnabled(true);
                lockCB.setValue(false);
            } else {
                final BasicAsyncCallback<Boolean> cb = new BasicAsyncCallback<Boolean>() {

                        @Override
                        protected void afterExecution(final Boolean result, final boolean operationFailed)
                                throws IllegalStateException {
                            if (result) {
                                performLock(day);
                            } else {
                                refuseLock(day);
                            }
                        }
                    };
                ProjectTrackerEntryPoint.getProjectService(true)
                        .isPausePolicyFullfilled(ProjectTrackerEntryPoint.getInstance().getStaff(), day, cb);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   day  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkTimeSlots(final Date day) {
        double hours = times.getTimeForDay(day.getDay());
        double hoursWorked = 0.0;
        final List<TaskNotice> tasks = taskStory.getTasksForDay(day.getDay());
        boolean justAbsenceTasks = true;

        for (final TaskNotice tmp : tasks) {
            if ((tmp.getActivity() != null) && (tmp.getActivity().getWorkPackage() != null)
                        && (tmp.getActivity().getWorkPackage().getProject() != null)) {
                if ((tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID)
                            || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPARE_TIME_ID)) {
                    hours -= tmp.getActivity().getWorkinghours();
                    justAbsenceTasks = false;
                } else if ((tmp.getActivity().getWorkPackage().getId() != ActivityDTO.HOLIDAY_ID)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.ILLNESS_ID)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.LECTURE_ID)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.SPECIAL_HOLIDAY_ID)) {
                    hoursWorked += tmp.getActivity().getWorkinghours();
                    justAbsenceTasks = false;
                }
            }
        }
        // just in case the difference is greater than 1 minute raise the error
        if (!justAbsenceTasks && (Math.abs(
                            hours
                            - hoursWorked)
                        > (1d / 60d))) {
            return false;
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  DOCUMENT ME!
     */
    private void performLock(final Date day) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        // create a new locked-day activtiy for that day and user and save it to db.
        final ActivityDTO lockedDayActivity = new ActivityDTO();
        lockedDayActivity.setKindofactivity(3);
        final Date d = new Date(day.getTime());
        d.setHours(5);
        lockedDayActivity.setDay(d);
        lockedDayActivity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
        lockedDayActivity.setCommitted(false);
        final BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>();
        ProjectTrackerEntryPoint.getProjectService(true).createActivity(lockedDayActivity, callback);
        lockedDays.add(lockCB);
        if (lockedDays.size() == days.length) {
            weekLockCB.setValue(true);
            if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                weekLockCB.setEnabled(false);
            }
        }
        // disable the checkbox
        lockCB.setValue(Boolean.TRUE);
        if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
            lockCB.setEnabled(false);
        } else {
            lockCB.setEnabled(true);
        }
        // set the disable status for tasks and time slots
        setDisabledStatusVisible(day, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day    DOCUMENT ME!
     * @param  aFlag  DOCUMENT ME!
     */
    public void setLocked(final Date day, final Boolean aFlag) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        if (aFlag) {
            lockedDays.add(lockCB);
            if (lockedDays.size() == days.length) {
                weekLockCB.setValue(true);
                if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                    weekLockCB.setEnabled(false);
                }
            }
            lockCB.setValue(aFlag);
            if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                lockCB.setEnabled(false);
            }
            setDisabledStatusVisible(day, true);
        } else {
            lockedDays.remove(lockCB);
            weekLockCB.setValue(false);
            weekLockCB.setEnabled(true);
            lockCB.setValue(false);
            lockCB.setEnabled(true);
            setDisabledStatusVisible(day, false);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  firstDayOfWeek  DOCUMENT ME!
     * @param  story           DOCUMENT ME!
     * @param  times           DOCUMENT ME!
     * @param  weekLockCB      DOCUMENT ME!
     */
    public void initialise(final Date firstDayOfWeek,
            final TaskStory story,
            final Story times,
            final SimpleCheckBox weekLockCB) {
        this.taskStory = story;
        this.times = times;
        this.firstDayOfWeek = firstDayOfWeek;
        this.weekLockCB = weekLockCB;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean checkWorkpackagePeriods(final Date d) {
        // check if there are tasks with an exired wp
        final List<TaskNotice> tasks = taskStory.getTasksForDay(d.getDay());

        for (final TaskNotice tn : tasks) {
            final WorkPackageDTO tmp = tn.getActivity().getWorkPackage();
            if (tmp != null) {
                final WorkPackagePeriodDTO period = tmp.determineMostRecentPeriod();
                if ((period != null) && !DateHelper.isDayInWorkPackagePeriod(d, period)) {
                    if ((tmp.getExpirationDescription() != null) && !tmp.getExpirationDescription().equals("")) {
                        ProjectTrackerEntryPoint.outputBox("The workpackage \"" + tmp.getName() + "\" for the task: \""
                                    + tn.getActivity().getDescription() + "\" is expired. The administrators adice is: "
                                    + tmp.getExpirationDescription());
                    } else {
                        ProjectTrackerEntryPoint.outputBox("The workpackage \"" + tmp.getName() + "\" for the task: \""
                                    + tn.getActivity().getDescription()
                                    + "\" is expired. Please contact the administrator for further instructions.");
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     */
    public void lockAllDaysInWeek() {
        // check for all Days, that there are no times left...
        final Date d = new Date(firstDayOfWeek.getTime());
        final Date sunday = new Date(d.getTime());
        final ArrayList<Date> faultyTimeSlotsDays = new ArrayList<Date>();
        DateHelper.addDays(sunday, 6);
        for (int i = 0; i < days.length; i++) {
            // check if there tasks with an expired workpackage
            if (!checkWorkpackagePeriods((i == 0) ? sunday : d)) {
                weekLockCB.setValue(false);
                weekLockCB.setEnabled(true);
                return;
            }

            if (i == 0) {
                if (!checkTimeSlots(sunday)) {
                    faultyTimeSlotsDays.add(sunday);
                }
            } else {
                if (!checkTimeSlots(d)) {
                    faultyTimeSlotsDays.add(new Date(d.getTime()));
                }
                DateHelper.addDays(d, 1);
            }
        }

        if (!faultyTimeSlotsDays.isEmpty()) {
            String timeSlotNotCorrectDays = "";
            for (final Date tmp : faultyTimeSlotsDays) {
                if (faultyTimeSlotsDays.indexOf(tmp) == (faultyTimeSlotsDays.size() - 1)) {
                    timeSlotNotCorrectDays += DateHelper.getDayAbbreviation(tmp) + ".";
                } else {
                    timeSlotNotCorrectDays += DateHelper.getDayAbbreviation(tmp) + ", ";
                }
            }
            ProjectTrackerEntryPoint.outputBox(
                ("Can not lock the week. For the following days the times have to be corrected: "
                            + timeSlotNotCorrectDays));
            weekLockCB.setValue(false);
            weekLockCB.setEnabled(true);
            return;
        }

        final BasicAsyncCallback<ArrayList<Date>> callback = new BasicAsyncCallback<ArrayList<Date>>() {

                @Override
                protected void afterExecution(final ArrayList<Date> result, final boolean operationFailed) {
                    if (!operationFailed && result.isEmpty()) {
//                    Pause policy is fullfilled lock each day...
                        final Date d = new Date(firstDayOfWeek.getTime());
                        final Date sunday = new Date(d.getTime());
                        DateHelper.addDays(sunday, 6);
                        for (int i = 0; i < days.length; i++) {
                            final SimpleCheckBox cb = days[i];
                            if (i == 0) {
                                if (!cb.getValue()) {
                                    performLock(sunday);
                                }
                            } else {
                                if (!cb.getValue()) {
                                    performLock(d);
                                }
                                DateHelper.addDays(d, 1);
                            }
                        }
                        if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                            weekLockCB.setEnabled(false);
                        } else {
                            weekLockCB.setEnabled(true);
                        }
                    } else {
                        String pauseNotFulfulfilledDays = "";
                        for (final Date tmp : result) {
                            if (result.indexOf(tmp) == (result.size() - 1)) {
                                pauseNotFulfulfilledDays += DateHelper.getDayAbbreviation(tmp) + ".";
                            } else {
                                pauseNotFulfulfilledDays += DateHelper.getDayAbbreviation(tmp) + ", ";
                            }
                        }
                        ProjectTrackerEntryPoint.outputBox(
                            "Can not lock the week. Pause Policy is not fulfilled for the following days: "
                                    + pauseNotFulfulfilledDays);
                        weekLockCB.setValue(false);
                        weekLockCB.setEnabled(true);
                    }
                }
            };

        final Date lastDayOfWeek = new Date(firstDayOfWeek.getTime());
        DateHelper.addDays(lastDayOfWeek, 7);
//        ProjectTrackerEntryPoint.getProjectService(
//                true).isPausePolicyFullfilled(ProjectTrackerEntryPoint.getInstance().getStaff(), DateHelper.getYear(firstDayOfWeek), DateHelper.getWeekOfYear(firstDayOfWeek), callback);
        ProjectTrackerEntryPoint.getProjectService(
                true)
                .isPausePolicyFullfilled(ProjectTrackerEntryPoint.getInstance().getStaff(),
                    firstDayOfWeek,
                    lastDayOfWeek,
                    callback);
    }

    /**
     * DOCUMENT ME!
     */
    public void unlockAllDaysInWeek() {
        final Date d = new Date(firstDayOfWeek.getTime());
        final Date sunday = new Date(d.getTime());
        DateHelper.addDays(sunday, 6);
        lockedDays.removeAll(lockedDays);
        weekLockCB.setValue(false);
        for (int i = 0; i < days.length; i++) {
            final SimpleCheckBox cb = days[i];
            cb.setValue(false);
            if (i == 0) {
                unlockDay(sunday);
            } else {
                unlockDay(d);
                DateHelper.addDays(d, 1);
            }
        }
        weekLockCB.setEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day             DOCUMENT ME!
     * @param  disabledStatus  DOCUMENT ME!
     */
    private void setDisabledStatusVisible(final Date day, final boolean disabledStatus) {
        final ArrayList<TaskNotice> tasks = (ArrayList<TaskNotice>)taskStory.getTasksForDay(day.getDay());
        for (final TaskNotice tn : tasks) {
            tn.setCloseButtonVisible(!disabledStatus);
            if (disabledStatus) {
                tn.addStyleName("lockedDay");
            } else {
                tn.removeStyleName("lockedDay");
            }
        }

        // disable the delete buttons of all time slots...
        final List<TimeNotice> timeSlots = times.getTimeNoticesForDay(day.getDay());
        final Iterator<TimeNotice> it = timeSlots.iterator();
        while (it.hasNext()) {
            final TimeNotice slot = it.next();
            slot.setEnabled(!disabledStatus);
            if (disabledStatus) {
                slot.addStyleName("lockedDay");
            } else {
                slot.removeStyleName("lockedDay");
            }
        }
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface LockPanelUiBinder extends UiBinder<Widget, LockPanel> {
    }
}
