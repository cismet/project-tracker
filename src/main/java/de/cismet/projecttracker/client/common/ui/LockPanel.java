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
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.utilities.ClientSidePauseChecker;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author dmeiers
 */
public class LockPanel extends Composite implements ClickHandler {

    private static LockPanel.LockPanelUiBinder uiBinder = GWT.create(LockPanel.LockPanelUiBinder.class);
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

    interface LockPanelUiBinder extends UiBinder<Widget, LockPanel> {
    }

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
    }

    @Override
    public void onClick(ClickEvent event) {
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
        handleClick(day, false);
    }

    private void unlockDay(final Date day) {

        final SimpleCheckBox lockCB = days[day.getDay()];

        lockCB.setEnabled(true);
        //logged in user has admin permission, lets unlock this day...
        StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();

        BasicAsyncCallback<ArrayList<ActivityDTO>> callback = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {

            @Override
            protected void afterExecution(ArrayList<ActivityDTO> result, boolean operationFailed) {
                if (!operationFailed) {
                    for (ActivityDTO activity : result) {
                        if (activity.getKindofactivity() == ActivityDTO.LOCKED_DAY) {
                            final ActivityDTO lockTask = activity;
                            BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                                @Override
                                protected void afterExecution(Void result, boolean operationFailed) {
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

    public void refuseLock(final Date day) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        lockCB.setValue(false);
        unlockDay(day);
        ProjectTrackerEntryPoint.outputBox("Pause Policy is not fulfilled! Can not lock this day.");
    }

    private void handleClick(final Date day, final boolean weekLockMode) {

        final SimpleCheckBox lockCB = days[day.getDay()];
        /*
         * if the lock status of the day is removed, check if the logged in user has admin permission and remove the
         * lock status if so this day shall be locked add a locked-day-activity
         */
        if (!lockCB.getValue()) {
            if (ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                unlockDay(day);
            }
        } else {
            // lets lock the day...
            //first check if that there are no times left
            double hours = times.getTimeForDay(day.getDay());
            double hoursWorked = 0.0;
            List<TaskNotice> tasks = taskStory.getTasksForDay(day.getDay());

            for (TaskNotice tmp : tasks) {
                if (tmp.getActivity() != null && tmp.getActivity().getWorkPackage() != null
                        && tmp.getActivity().getWorkPackage().getProject() != null) {
                    if (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID
                            || tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPARE_TIME_ID) {
                        hours -= tmp.getActivity().getWorkinghours();
                    } else {
                        hoursWorked += tmp.getActivity().getWorkinghours();
                    }
                }
            }
            //just in case the difference is greater than 1 minute raise the error
            if (Math.abs(
                    hours - hoursWorked) > 1d / 60d) {
                ProjectTrackerEntryPoint.outputBox("The working time of the activites does not match the time for this day. You have to correct in order to lock this day");
                lockCB.setValue(false);
                return;
            }
            
            performLock(day);
        }

    }

    private void performLock(final Date day) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        //create a new locked-day activtiy for that day and user and save it to db. 
        BasicAsyncCallback<Boolean> cb = new BasicAsyncCallback<Boolean>() {

            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) throws IllegalStateException {
                if (result) {
                    ActivityDTO lockedDayActivity = new ActivityDTO();
                    lockedDayActivity.setKindofactivity(3);
                    final Date d = new Date(day.getTime());
                    d.setHours(5);
                    lockedDayActivity.setDay(d);
                    lockedDayActivity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
                    lockedDayActivity.setCommitted(false);
                    BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>();
                    ProjectTrackerEntryPoint.getProjectService(true).createActivity(lockedDayActivity, callback);
                } else {
                    refuseLock(day);
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isPausePolicyFullfilled(ProjectTrackerEntryPoint.getInstance().getStaff(), day, cb);
        
        //disable the checkbox
        if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
            lockCB.setEnabled(false);
        }
    }

    public void setLocked(Date day, Boolean aFlag) {
        final SimpleCheckBox lockCB = days[day.getDay()];
        if (aFlag) {
            lockCB.setValue(aFlag);
            if (!ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                lockCB.setEnabled(false);
            }
        } else {
            lockCB.setValue(false);
            lockCB.setEnabled(true);
        }
    }

    public void initialise(final Date firstDayOfWeek, final Story times, final TaskStory story) {
        this.taskStory = story;
        this.times = times;
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public void lockAllDaysInWeek(boolean aflag) {
        //TODO check for all Days, that there are no times left...

        BasicAsyncCallback<ArrayList<Date>> callback = new BasicAsyncCallback<ArrayList<Date>>() {

            @Override
            protected void afterExecution(ArrayList<Date> result, boolean operationFailed) {
                if (!operationFailed && result.isEmpty()) {
//                    Pause policy is fullfilled lock each day...
                    final Date d = new Date(firstDayOfWeek.getTime());
                    final Date sunday = new Date(d.getTime());
                    DateHelper.addDays(sunday, 6);
                    for (int i = 0; i < days.length; i++) {
                        SimpleCheckBox cb = days[i];
                        cb.setValue(false);
                        if (i == 0) {
                            performLock(sunday);
                        } else {
                            performLock(d);
                            DateHelper.addDays(d, 1);
                        }
                    }
                }else{
                    //TODO generate a error message....
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isPausePolicyFullfilled(ProjectTrackerEntryPoint.getInstance().getStaff(), DateHelper.getWeekOfYear(firstDayOfWeek), DateHelper.getYear(firstDayOfWeek), callback);
    }
}
