/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.utilities;

import com.google.gwt.user.client.ui.DialogBox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.FillButtonPreview;
import de.cismet.projecttracker.client.common.ui.OkCancelCallback;
import de.cismet.projecttracker.client.common.ui.OkCancelModal;
import de.cismet.projecttracker.client.common.ui.Story;
import de.cismet.projecttracker.client.common.ui.TaskNotice;
import de.cismet.projecttracker.client.common.ui.TaskStory;
import de.cismet.projecttracker.client.common.ui.TimeNotice;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class TaskFiller {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  timeToFillTo  DOCUMENT ME!
     * @param  day           DOCUMENT ME!
     * @param  taskStory     DOCUMENT ME!
     * @param  story         DOCUMENT ME!
     */
    private static void fillToTime(final Date timeToFillTo,
            final Date day,
            final TaskStory taskStory,
            final Story story) {
        final List<TaskNotice> list = taskStory.getTasksForDay(day.getDay());
        final List<TaskNotice> zeroTasksToChange = new ArrayList<TaskNotice>();
        final List<TaskNotice> procentualTasks = new ArrayList<TaskNotice>();
        double bookedHours = 0.0;
        double timeForDay = story.getTimeForDay(day.getDay());
        final List<TimeNotice> tn = story.getTimeNoticesForDay(day.getDay());
        // get the last timeNotice
        final TimeNotice tnn = tn.get(tn.size() - 1);

        if (tnn.getEnd() == null) {
            if (timeToFillTo == null) {
                final OkCancelCallback cb = new OkCancelCallback() {

                        @Override
                        public void onOkClicked() {
                            fillToTime(new Date(), day, taskStory, story);
                        }

                        @Override
                        public void onCancelClicked() {
                            // nope
                        }
                    };

                final DialogBox form = new DialogBox();
                final OkCancelModal modal = new OkCancelModal(
                        form,
                        cb,
                        "Warning!",
                        "No End time! Shall I assume the current time for the fill operation?");
                form.setWidget(modal);
                form.setModal(true);
                form.center();
                return;
            } else {
                final Date end = new Date(tnn.getStart().getTime());
                end.setHours(timeToFillTo.getHours());
                end.setMinutes(timeToFillTo.getMinutes());
                end.setSeconds(timeToFillTo.getSeconds());
                if (tnn.getStart().before(end)) {
                    timeForDay += DateHelper.substract(tnn.getStart(), end);
                }
            }
        }

        double fillableBookedHours = 0.0;

        if ((list != null) && (list.size() > 0)) {
            for (final TaskNotice tmp : list) {
                if (tmp.getActivity().getKindofactivity() == ActivityDTO.ACTIVITY) {
                    if ((tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID)
                                || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPARE_TIME_ID)) {
                        bookedHours += tmp.getActivity().getWorkinghours();
                    } else if (!((tmp.getActivity().getWorkPackage().getId() == ActivityDTO.HOLIDAY_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.LECTURE_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.ILLNESS_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPECIAL_HOLIDAY_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.Travel_ID))) {
                        if (tmp.getActivity().getWorkinghours() == 0.0) {
                            zeroTasksToChange.add(tmp);
                        }
                        fillableBookedHours += tmp.getActivity().getWorkinghours();
                        procentualTasks.add(tmp);
                        bookedHours += tmp.getActivity().getWorkinghours();
                    }
                }
            }
        }

        final double balance = (timeForDay - bookedHours);
        if (balance < 0.0) {
            doNegativeFill(new ArrayList<TaskNotice>(procentualTasks), balance, fillableBookedHours, taskStory);
        } else if (balance > 0) {
            if (zeroTasksToChange.isEmpty()) {
                for (final TaskNotice tmp : procentualTasks) {
                    final double fillFactor = tmp.getActivity().getWorkinghours() * 100 / fillableBookedHours;
                    final double newWorkingHours = tmp.getActivity().getWorkinghours()
                                + ((fillFactor * (timeForDay - bookedHours)) / 100);
                    if (newWorkingHours < 0) {
                        ProjectTrackerEntryPoint.outputBox("Can not set a negative working time for activtiy");
                        return;
                    }
                    tmp.getActivity().setWorkinghours(newWorkingHours);
                    tmp.refresh();
                    tmp.save();
                    taskStory.taskChanged(tmp);
                }
            } else {
                if (zeroTasksToChange.size() > 0) {
                    for (final TaskNotice tmp : zeroTasksToChange) {
                        final double newWorkingHours = tmp.getActivity().getWorkinghours()
                                    + ((timeForDay - bookedHours) / zeroTasksToChange.size());
                        if (newWorkingHours < 0) {
                            ProjectTrackerEntryPoint.outputBox("Can not set a negative working time for activtiy");
                            return;
                        }
                        tmp.getActivity().setWorkinghours(newWorkingHours);
                        tmp.refresh();
                        tmp.save();
                        taskStory.taskChanged(tmp);
                    }
                }
            }
        } else {
            ProjectTrackerEntryPoint.outputBox("There is no time left to fill the tasks");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d           DOCUMENT ME!
     * @param   fillToDate  DOCUMENT ME!
     * @param   taskStory   DOCUMENT ME!
     * @param   story       DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static double getTimeToFill(final Date d,
            final Date fillToDate,
            final TaskStory taskStory,
            final Story story) {
        final List<TaskNotice> list = taskStory.getTasksForDay(d.getDay());
        double bookedHours = 0.0;
        final double timeForDay;
        if (fillToDate == null) {
            timeForDay = story.getTimeForDay(d.getDay());
        } else {
            final double f = story.getTimeForDay(d.getDay());
            final List<TimeNotice> tn = story.getTimeNoticesForDay(d.getDay());
            double hours = story.getTimeForDay(d.getDay());
            // get the last timeNotice
            final TimeNotice tnn = tn.get(tn.size() - 1);

            if (tnn.getEnd() == null) {
                final Date end = new Date(tnn.getStart().getTime());
                end.setHours(d.getHours());
                end.setMinutes(d.getMinutes());
                end.setSeconds(d.getSeconds());
                if (tnn.getStart().before(end)) {
                    hours += DateHelper.substract(tnn.getStart(), end);
                }
            }
            timeForDay = hours;
        }

        if ((list != null) && (list.size() > 0)) {
            for (final TaskNotice tmp : list) {
                if (tmp.getActivity().getKindofactivity() == ActivityDTO.ACTIVITY) {
                    if ((tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID)
                                || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPARE_TIME_ID)) {
                        bookedHours += tmp.getActivity().getWorkinghours();
                    } else if (!((tmp.getActivity().getWorkPackage().getId() == ActivityDTO.HOLIDAY_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.LECTURE_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.ILLNESS_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPECIAL_HOLIDAY_ID)
                                    || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.Travel_ID))) {
                        bookedHours += tmp.getActivity().getWorkinghours();
                    }
                }
            }
        }

        final double balance = (timeForDay - bookedHours);
        return balance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  procentualTasks  DOCUMENT ME!
     * @param  balance          DOCUMENT ME!
     * @param  bookedHours      DOCUMENT ME!
     * @param  taskStory        DOCUMENT ME!
     */
    private static void doNegativeFill(final ArrayList<TaskNotice> procentualTasks,
            final double balance,
            final double bookedHours,
            final TaskStory taskStory) {
        if (procentualTasks.isEmpty()) {
            ProjectTrackerEntryPoint.outputBox("There are no tasks to fill");
        } else {
            final ArrayList<TaskNotice> negativeFillPreview = new ArrayList<TaskNotice>();
            final ArrayList<TaskNotice> real = new ArrayList<TaskNotice>();
            for (final TaskNotice tn : procentualTasks) {
                real.add(new TaskNotice(tn.getActivity().createCopy()));
                final ActivityDTO tmp = tn.getActivity().createCopy();
                final double whow = tmp.getWorkinghours();
                final double fillFactor = (whow * 100 / bookedHours);
                final double newWorkingHours = whow + (fillFactor * balance / 100);
                if (newWorkingHours < 0) {
                    ProjectTrackerEntryPoint.outputBox("Can not set a negative working time for activtiy");
                    return;
                }
                tmp.setWorkinghours(newWorkingHours);
                negativeFillPreview.add(new TaskNotice(tmp));
            }

            final DialogBox form = new DialogBox();
            form.setWidget(new FillButtonPreview(form, negativeFillPreview, procentualTasks, taskStory));
            form.setModal(false);
            form.center();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day        DOCUMENT ME!
     * @param  taskStory  DOCUMENT ME!
     * @param  story      DOCUMENT ME!
     */
    public static void fillTasks(final Date day, final TaskStory taskStory, final Story story) {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            fillToTime(null, day, taskStory, story);
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeToFillTo  DOCUMENT ME!
     * @param  day           DOCUMENT ME!
     * @param  taskStory     DOCUMENT ME!
     * @param  story         DOCUMENT ME!
     */
    public static void fillTasksToTime(final Date timeToFillTo,
            final Date day,
            final TaskStory taskStory,
            final Story story) {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            fillToTime(timeToFillTo, day, taskStory, story);
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
    }
}
