/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.event;

import java.util.Date;

/**
 *
 * @author therter
 */
public class TimeStoryEvent {
    private Object source;
    private boolean firstTimeNotice;
    private Date day;

    public TimeStoryEvent() {
    }

    
    
    public TimeStoryEvent(Object source, boolean firstTimeNotice, Date day) {
        this.source = source;
        this.firstTimeNotice = firstTimeNotice;
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
     * @return the firstTimeNotice
     */
    public boolean isFirstTimeNotice() {
        return firstTimeNotice;
    }

    /**
     * @param firstTimeNotice the firstTimeNotice to set
     */
    public void setFirstTimeNotice(boolean firstTimeNotice) {
        this.firstTimeNotice = firstTimeNotice;
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
}
