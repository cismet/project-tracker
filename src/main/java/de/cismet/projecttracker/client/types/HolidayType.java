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
package de.cismet.projecttracker.client.types;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class HolidayType implements IsSerializable {

    //~ Instance fields --------------------------------------------------------

    private Date date;
    private boolean halfHoliday;
    private String name;
    private double hours;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  date  the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the isHalfHoliday
     */
    public boolean isHalfHoliday() {
        return halfHoliday;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  halfHoliday  the isHalfHoliday to set
     */
    public void setHalfHoliday(final boolean halfHoliday) {
        this.halfHoliday = halfHoliday;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the hours
     */
    public double getHours() {
        return hours;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hours  the hours to set
     */
    public void setHours(final double hours) {
        this.hours = hours;
    }
}
