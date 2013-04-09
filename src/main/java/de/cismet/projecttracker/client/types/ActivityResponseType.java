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

import java.io.Serializable;

import java.util.List;

import de.cismet.projecttracker.client.dto.ActivityDTO;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ActivityResponseType implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private List<ActivityDTO> activities;
    private List<HolidayType> holidays;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the activities
     */
    public List<ActivityDTO> getActivities() {
        return activities;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  activities  the activities to set
     */
    public void setActivities(final List<ActivityDTO> activities) {
        this.activities = activities;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the holidays
     */
    public List<HolidayType> getHolidays() {
        return holidays;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  holidays  the holidays to set
     */
    public void setHolidays(final List<HolidayType> holidays) {
        this.holidays = holidays;
    }
}
