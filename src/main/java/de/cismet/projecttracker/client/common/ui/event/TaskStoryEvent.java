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
package de.cismet.projecttracker.client.common.ui.event;

import java.util.Date;

import de.cismet.projecttracker.client.common.ui.TaskNotice;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public class TaskStoryEvent {

    //~ Instance fields --------------------------------------------------------

    private Object source;
    private TaskNotice taskNotice;
    private Date day;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TaskStoryEvent object.
     */
    public TaskStoryEvent() {
    }

    /**
     * Creates a new TaskStoryEvent object.
     *
     * @param  source      DOCUMENT ME!
     * @param  taskNotice  DOCUMENT ME!
     * @param  day         DOCUMENT ME!
     */
    public TaskStoryEvent(final Object source, final TaskNotice taskNotice, final Date day) {
        this.source = source;
        this.taskNotice = taskNotice;
        this.day = day;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  source  the source to set
     */
    public void setSource(final Object source) {
        this.source = source;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the day
     */
    public Date getDay() {
        return day;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  the day to set
     */
    public void setDay(final Date day) {
        this.day = day;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the taskNotice
     */
    public TaskNotice getTaskNotice() {
        return taskNotice;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskNotice  the taskNotice to set
     */
    public void setTaskNotice(final TaskNotice taskNotice) {
        this.taskNotice = taskNotice;
    }
}
