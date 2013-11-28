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

import java.util.Date;
import java.util.List;

import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.utilities.TaskFiller;

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
                TaskFiller.fillTasksToTime(new Date(), day, taskStory, story);
            } else {
                TaskFiller.fillTasks(day, taskStory, story);
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
                            taskForm.setWidget(new StoryForm(taskForm, taskStory, story, day));
                            taskForm.center();
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(day, staff, callback);
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
}
