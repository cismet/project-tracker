/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TaskStoryListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import java.util.Date;
import java.util.List;

/**
 *
 * @author therter
 */
public class DailyHoursOfWork extends Composite implements TimeStoryListener, TaskStoryListener {

    private static DailyHoursOfWorkUiBinder uiBinder = GWT.create(DailyHoursOfWorkUiBinder.class);
    private Date firstDayOfWeek = new Date();
    private Story story;
    private TaskStory taskStory;
    private HTML[] days;
    private boolean initialised = false;
    @UiField
    HTML monday;
    @UiField
    HTML tuesday;
    @UiField
    HTML wednesday;
    @UiField
    HTML thursday;
    @UiField
    HTML friday;
    @UiField
    HTML saturday;
    @UiField
    HTML sunday;
    @UiField
    AbsolutePanel boundaryPanel;

    @Override
    public void timeNoticeCreated(TimeStoryEvent e) {
        fillLabel(e.getDay().getDay());;
    }


    @Override
    public void timeNoticeChanged(TimeStoryEvent e) {
        fillLabel(e.getDay().getDay());;
    }

    @Override
    public void timeNoticeDeleted(TimeStoryEvent e) {
        fillLabel(e.getDay().getDay());;
    }

    @Override
    public void taskNoticeCreated(TaskStoryEvent e) {
        fillLabel(e.getDay().getDay());;
    }

    @Override
    public void taskNoticeChanged(TaskStoryEvent e) {
        fillLabel(e.getDay().getDay());;
    }

    @Override
    public void taskNoticeDeleted(TaskStoryEvent e) {
        fillLabel(e.getDay().getDay());;
    }

    interface DailyHoursOfWorkUiBinder extends UiBinder<Widget, DailyHoursOfWork> {
    }

    public DailyHoursOfWork() {
        initWidget(uiBinder.createAndBindUi(this));
        days = new HTML [7];
        
        days[0] = sunday;
        days[1] = monday;
        days[2] = tuesday;
        days[3] = wednesday;
        days[4] = thursday;
        days[5] = friday;
        days[6] = saturday;
    }

    public void initialise(Date firstDayOfWeek, Story time, TaskStory taskStory) {
        this.firstDayOfWeek = firstDayOfWeek;
        this.story = time;
        this.taskStory = taskStory;
        Date day = (Date) firstDayOfWeek.clone();
        monday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        tuesday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        wednesday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        thursday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        friday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        saturday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        sunday.setHTML(getText(day.getDay()));
        
        if (!initialised) {
            story.addTimeStoryListener(this);
            taskStory.addTaskStoryListener(this);
            initialised = true;
        }
    }

    private void fillLabel(int day) {
        days[day].setHTML(getText(day));
    }
    
    private String getText(int day) {
        double hours = story.getTimeForDay(day);
        double hoursWorked = 0.0;
        List<TaskNotice> tasks = taskStory.getTasksForDay(day);

        for (TaskNotice tmp : tasks) {
            if (tmp.getActivity() != null && tmp.getActivity().getWorkPackage() != null && 
                    tmp.getActivity().getWorkPackage().getProject() != null) {
                if (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID || 
                        tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPARE_TIME_ID) {
                    hours -= tmp.getActivity().getWorkinghours();
                } else {
                    hoursWorked += tmp.getActivity().getWorkinghours();
                }
            }
        }

        return DateHelper.doubleToHours(hours) + " (" + DateHelper.doubleToHours( hours - hoursWorked ) + ")";
    }
}
