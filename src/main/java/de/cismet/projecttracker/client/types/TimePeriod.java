/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.types;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author therter
 */
public class TimePeriod implements Serializable {
    private Date start;
    private Date end;


    public TimePeriod(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    
    public TimePeriod() {
    }
    

    /**
     * @return the start
     */
    public Date getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Date start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Date end) {
        this.end = end;
    }
}
