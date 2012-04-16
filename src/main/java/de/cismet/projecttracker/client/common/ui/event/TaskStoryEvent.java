/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.common.ui.event;

import de.cismet.projecttracker.client.common.ui.TaskNotice;
import java.util.Date;

/**
 *
 * @author thorsten
 */
public class TaskStoryEvent {
    private Object source;
    private TaskNotice taskNotice;
    private Date day;

    public TaskStoryEvent() {
    }



    public TaskStoryEvent(Object source, TaskNotice taskNotice, Date day) {
        this.source = source;
        this.taskNotice = taskNotice;
        this.day = day;
    }



    /**
     * @return the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Object source) {
        this.source = source;
    }

    /**
     * @return the day
     */
    public Date getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(Date day) {
        this.day = day;
    }

    /**
     * @return the taskNotice
     */
    public TaskNotice getTaskNotice() {
        return taskNotice;
    }

    /**
     * @param taskNotice the taskNotice to set
     */
    public void setTaskNotice(TaskNotice taskNotice) {
        this.taskNotice = taskNotice;
    }
}
