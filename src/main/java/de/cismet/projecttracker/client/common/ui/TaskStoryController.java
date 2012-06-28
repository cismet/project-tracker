/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.utilities.ClientSidePauseChecker;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author therter
 */
public class TaskStoryController extends Composite implements ClickHandler, TimeStoryListener, DoubleClickHandler {

    private FlowPanel mainPanel = new FlowPanel();
    private Button add = new Button("<img src='" + ImageConstants.INSTANCE.plus().getURL() + "' />", this);
    private Button fill = new Button("<img src='" + ImageConstants.INSTANCE.magicFiller().getURL() + "' />", this);
    private Date day;
    private TaskStory taskStory;
    private Story story;
    private boolean isRegistered = false;

    public TaskStoryController() {
        add.setStyleName("btn inlineComponent");
        fill.setStyleName("btn inlineComponent");
        mainPanel.add(add);
        mainPanel.add(fill);
        initWidget(mainPanel);
        add.setTitle("add task");
        fill.setTitle("fill");
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public void setTaskStory(TaskStory taskStory) {
        this.taskStory = taskStory;

        if (!isRegistered) {
            taskStory.registerTaskStoryController(this, day);
            isRegistered = true;
        }
    }

    public void setStory(Story story) {
        if (this.story != null) {
            this.story.removeTimeStoryListener(this);
        }
        this.story = story;
        story.removeTimeStoryListener(this);
        story.addTimeStoryListener(this);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == add) {
            addTask();
        } else if (event.getSource() == fill) {
            fillTasks();
        } 
    }

    private void addTask() {
        StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) {
                if (!operationFailed) {
                    if (!result || ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                        List<TaskNotice> taskList = taskStory.getTasksForDay(day.getDay());
                        if (taskList.isEmpty()) {
                            taskStory.addPause(day);
                        }
                        DialogBox taskForm = new DialogBox();
                        taskForm.setWidget(new StoryForm(taskForm, taskStory, day));
                        taskForm.center();
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
    }

    private void fillTasks() {
        StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) {
                if (!operationFailed) {
                    if (!result || ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                        doFillTasks();
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
    }

    private void doFillTasks() {
        List<TaskNotice> list = taskStory.getTasksForDay(day.getDay());
        List<TaskNotice> zeroTasksToChange = new ArrayList<TaskNotice>();
        List<TaskNotice> procentualTasks = new ArrayList<TaskNotice>();
        double bookedHours = 0.0;
        double timeForDay = story.getTimeForDay(day.getDay());
        double fillableBookedHours = 0.0;

        if (list != null && list.size() > 0) {
            for (TaskNotice tmp : list) {
                if (tmp.getActivity().getKindofactivity() == ActivityDTO.ACTIVITY) {

                    if (tmp.getActivity().getWorkinghours() == 0.0) {
                        zeroTasksToChange.add(tmp);
                    } else if (!(tmp.getActivity().getWorkPackage().getId() == ActivityDTO.HOLIDAY_ID || tmp.getActivity().getWorkPackage().getId() == ActivityDTO.ILLNESS_ID || tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID)) {
                        fillableBookedHours += tmp.getActivity().getWorkinghours();
                        procentualTasks.add(tmp);
                    }
                    bookedHours += tmp.getActivity().getWorkinghours();
                }
            }
        }

        if ((timeForDay - bookedHours) <= 0.0) {
            ProjectTrackerEntryPoint.outputBox("There is no time left to fill the tasks");
        } else if (zeroTasksToChange.isEmpty()) {
            for (TaskNotice tmp : procentualTasks) {
                double fillFactor = tmp.getActivity().getWorkinghours() * 100 / fillableBookedHours;
                double newWorkingHours = tmp.getActivity().getWorkinghours() + ((fillFactor * (timeForDay - bookedHours)) / 100);
                tmp.getActivity().setWorkinghours(newWorkingHours);
                tmp.refresh();
                tmp.save();
                taskStory.taskChanged(tmp);
            }
        } else {
            if (zeroTasksToChange.size() > 0) {
                for (TaskNotice tmp : zeroTasksToChange) {
                    tmp.getActivity().setWorkinghours(tmp.getActivity().getWorkinghours() + (timeForDay - bookedHours) / zeroTasksToChange.size());
                    tmp.refresh();
                    tmp.save();
                    taskStory.taskChanged(tmp);
                }
            }
        }
    }

    @Override
    public void timeNoticeCreated(TimeStoryEvent e) {
        if (DateHelper.isSameDay(e.getDay(), day)) {
            List<TaskNotice> taskList = taskStory.getTasksForDay(e.getDay().getDay());

            if (taskList.isEmpty()) {
                taskStory.addPause(e.getDay());
            }
        }
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        addTask();
    }

    @Override
    public void timeNoticeChanged(TimeStoryEvent e) {
    }

    @Override
    public void timeNoticeDeleted(TimeStoryEvent e) {
    }
}