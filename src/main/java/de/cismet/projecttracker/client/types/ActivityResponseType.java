/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ActivityDTO;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author therter
 */
public class ActivityResponseType implements Serializable {
    private List<ActivityDTO> activities;
    private List<HolidayType> holidays;

    /**
     * @return the activities
     */
    public List<ActivityDTO> getActivities() {
        return activities;
    }

    /**
     * @param activities the activities to set
     */
    public void setActivities(List<ActivityDTO> activities) {
        this.activities = activities;
    }

    /**
     * @return the holidays
     */
    public List<HolidayType> getHolidays() {
        return holidays;
    }

    /**
     * @param holidays the holidays to set
     */
    public void setHolidays(List<HolidayType> holidays) {
        this.holidays = holidays;
    }
}
