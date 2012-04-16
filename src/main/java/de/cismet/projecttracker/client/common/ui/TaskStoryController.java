/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
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
        add.setTitle("add");
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
        List<TaskNotice> taskList = taskStory.getTasksForDay(day.getDay());
        if (taskList.isEmpty()) {
            taskStory.addPause(day);
        }        
        DialogBox taskForm = new DialogBox();
        taskForm.setWidget(new StoryForm(taskForm, taskStory, day));
        taskForm.center();
    }
    
    private void fillTasks() {
        List<TaskNotice> list = taskStory.getTasksForDay(day.getDay());
        List<TaskNotice> tasksToChange = new ArrayList<TaskNotice>();
        double bookedHours = 0.0;
        double timeForDay = story.getTimeForDay(day.getDay());


        if (list != null && list.size() > 0) {
            for (TaskNotice tmp : list) {
                if (tmp.getActivity().getKindofactivity() == ActivityDTO.ACTIVITY) {
                    if (tmp.getActivity().getWorkinghours() == 0.0) {
                        tasksToChange.add(tmp);
                    } else {
                        bookedHours += tmp.getActivity().getWorkinghours();
                    }
                }
            }

            if (tasksToChange.isEmpty()) {
                ProjectTrackerEntryPoint.outputBox("There are no tasks without duration, which can be filled");
            } else if ( (timeForDay - bookedHours) == 0.0) {
                ProjectTrackerEntryPoint.outputBox("There is no time left to fill the tasks");
            }
            
            if (tasksToChange.size() > 0) {
                for (TaskNotice tmp : tasksToChange) {
                    tmp.getActivity().setWorkinghours((timeForDay - bookedHours) / tasksToChange.size());
                    tmp.refresh();
                    tmp.save();
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