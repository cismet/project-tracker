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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TaskStoryController extends Composite implements ClickHandler, TimeStoryListener, DoubleClickHandler {

    //~ Instance fields --------------------------------------------------------

    private FlowPanel mainPanel = new FlowPanel();
    private Button add = new Button("<img src='" + ImageConstants.INSTANCE.plus().getURL() + "' />", this);
    private Button fill = new Button("<img src='" + ImageConstants.INSTANCE.magicFiller().getURL() + "' />", this);
    private Date day;
    private TaskStory taskStory;
    private Story story;
    private boolean isRegistered = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TaskStoryController object.
     */
    public TaskStoryController() {
        add.setStyleName("btn inlineComponent");
        fill.setStyleName("btn inlineComponent");
        mainPanel.add(add);
        mainPanel.add(fill);
        initWidget(mainPanel);
        add.setTitle("add task");
        fill.setTitle("fill");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  day  DOCUMENT ME!
     */
    public void setDay(final Date day) {
        this.day = day;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskStory  DOCUMENT ME!
     */
    public void setTaskStory(final TaskStory taskStory) {
        this.taskStory = taskStory;

        if (!isRegistered) {
            taskStory.registerTaskStoryController(this, day);
            isRegistered = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  story  DOCUMENT ME!
     */
    public void setStory(final Story story) {
        if (this.story != null) {
            this.story.removeTimeStoryListener(this);
        }
        this.story = story;
        story.removeTimeStoryListener(this);
        story.addTimeStoryListener(this);
    }

    @Override
    public void onClick(final ClickEvent event) {
        if (event.getSource() == add) {
            addTask();
        } else if (event.getSource() == fill) {
            if (event.isControlKeyDown()) {
                fillTaskToCurrentTime();
            } else {
                fillTasks();
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void addTask() {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            final List<TaskNotice> taskList = taskStory.getTasksForDay(day.getDay());
                            if (taskList.isEmpty()) {
                                taskStory.addPause(day);
                            }
                            final DialogBox taskForm = new DialogBox();
                            taskForm.setWidget(new StoryForm(taskForm, taskStory, day));
                            taskForm.center();
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
    }

    /**
     * DOCUMENT ME!
     */
    private void fillTasks() {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            doFillTasks();
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
    }

    /**
     * DOCUMENT ME!
     */
    private void fillTaskToCurrentTime() {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        final Date d = new Date();
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            doFillTasks(d);
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
    }

    /**
     * DOCUMENT ME!
     */
    private void doFillTasks() {
        doFillTasks(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  d  DOCUMENT ME!
     */
    private void doFillTasks(final Date d) {
        final List<TaskNotice> list = taskStory.getTasksForDay(day.getDay());
        final List<TaskNotice> zeroTasksToChange = new ArrayList<TaskNotice>();
        final List<TaskNotice> procentualTasks = new ArrayList<TaskNotice>();
        double bookedHours = 0.0;
        final double timeForDay;
        if (d == null) {
            timeForDay = story.getTimeForDay(day.getDay());
        } else {
            final double f = story.getTimeForDay(day.getDay());
            final List<TimeNotice> tn = story.getTimeNoticesForDay(day.getDay());
            double hours = story.getTimeForDay(day.getDay());
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
            doNegativeFill(new ArrayList<TaskNotice>(procentualTasks), balance, fillableBookedHours);
        } else if (balance > 0) {
            if (zeroTasksToChange.isEmpty()) {
                for (final TaskNotice tmp : procentualTasks) {
                    final double fillFactor = tmp.getActivity().getWorkinghours() * 100 / fillableBookedHours;
                    final double newWorkingHours = tmp.getActivity().getWorkinghours()
                                + ((fillFactor * (timeForDay - bookedHours)) / 100);
                    tmp.getActivity().setWorkinghours(newWorkingHours);
                    tmp.refresh();
                    tmp.save();
                    taskStory.taskChanged(tmp);
                }
            } else {
                if (zeroTasksToChange.size() > 0) {
                    for (final TaskNotice tmp : zeroTasksToChange) {
                        tmp.getActivity()
                                .setWorkinghours(tmp.getActivity().getWorkinghours()
                                    + ((timeForDay - bookedHours) / zeroTasksToChange.size()));
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

    @Override
    public void timeNoticeCreated(final TimeStoryEvent e) {
        if (DateHelper.isSameDay(e.getDay(), day)) {
            final List<TaskNotice> taskList = taskStory.getTasksForDay(e.getDay().getDay());

            if (taskList.isEmpty()) {
                taskStory.addPause(e.getDay());
            }
        }
    }

    @Override
    public void onDoubleClick(final DoubleClickEvent event) {
        addTask();
    }

    @Override
    public void timeNoticeChanged(final TimeStoryEvent e) {
    }

    @Override
    public void timeNoticeDeleted(final TimeStoryEvent e) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param  procentualTasks  DOCUMENT ME!
     * @param  balance          DOCUMENT ME!
     * @param  bookedHours      DOCUMENT ME!
     */
    private void doNegativeFill(final ArrayList<TaskNotice> procentualTasks,
            final double balance,
            final double bookedHours) {
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
                tmp.setWorkinghours(newWorkingHours);
                negativeFillPreview.add(new TaskNotice(tmp));
            }

            final DialogBox form = new DialogBox();
            form.setWidget(new FillButtonPreview(form, negativeFillPreview, procentualTasks, taskStory));
            form.setModal(false);
            form.center();
        }
    }
}
