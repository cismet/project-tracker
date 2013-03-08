/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.types;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.Date;

/**
 *
 * @author therter
 */
public class HolidayType implements IsSerializable {
    private Date date;
    private boolean halfHoliday;
    private String name;
    private double hours;

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the isHalfHoliday
     */
    public boolean isHalfHoliday() {
        return halfHoliday;
    }

    /**
     * @param isHalfHoliday the isHalfHoliday to set
     */
    public void setHalfHoliday(boolean halfHoliday) {
        this.halfHoliday = halfHoliday;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the hours
     */
    public double getHours() {
        return hours;
    }

    /**
     * @param hours the hours to set
     */
    public void setHours(double hours) {
        this.hours = hours;
    }
}
