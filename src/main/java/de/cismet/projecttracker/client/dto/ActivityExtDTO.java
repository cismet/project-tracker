/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This is a wrapper for ActivityDTO, that use the type String as return value for getDay()
 * 
 * @author therter
 */
public class ActivityExtDTO extends BasicDTO<ActivityExtDTO> {

    //~ Static fields/initializers ---------------------------------------------

    //~ Instance fields --------------------------------------------------------

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private ActivityDTO activity;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ActivityDTO object.
     */
    public ActivityExtDTO() {
    }

    /**
     * Creates a new ActivityDTO object.
     *
     * @param  id              DOCUMENT ME!
     * @param  staff           DOCUMENT ME!
     * @param  workPackage     DOCUMENT ME!
     * @param  workCategory    DOCUMENT ME!
     * @param  workinghours    DOCUMENT ME!
     * @param  description     DOCUMENT ME!
     * @param  day             DOCUMENT ME!
     * @param  committed       DOCUMENT ME!
     * @param  kindofactivity  DOCUMENT ME!
     */
    public ActivityExtDTO(ActivityDTO activity) {
        this.activity = activity;
        this.id = activity.getId();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the staff
     */
    public StaffDTO getStaff() {
        return activity.getStaff();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff  the staff to set
     */
    public void setStaff(final StaffDTO staff) {
        this.activity.setStaff(staff);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workPackage
     */
    public WorkPackageDTO getWorkPackage() {
        return activity.getWorkPackage();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workPackage  the workPackage to set
     */
    public void setWorkPackage(final WorkPackageDTO workPackage) {
        this.activity.setWorkPackage(workPackage);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workCategory
     */
    public WorkCategoryDTO getWorkCategory() {
        return activity.getWorkCategory();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workCategory  the workCategory to set
     */
    public void setWorkCategory(final WorkCategoryDTO workCategory) {
        this.activity.setWorkCategory(workCategory);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workinghours
     */
    public double getWorkinghours() {
        return activity.getWorkinghours();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workinghours  the workinghours to set
     */
    public void setWorkinghours(final double workinghours) {
        this.activity.setWorkinghours(workinghours);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the description
     */
    public String getDescription() {
        return activity.getDescription();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  the description to set
     */
    public void setDescription(final String description) {
        this.activity.setDescription(description);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the day
     */
    public String getDay() {
        return format.format(activity.getDay());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  the day to set
     */
    public void setDay(final String day) {
        try {
            this.activity.setDay(format.parse(day));
        } catch (Exception e)  {
            //nothing to do
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the forInternalUse
     */
    public int getKindofactivity() {
        return activity.getKindofactivity();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kindofactivity  forInternalUse the forInternalUse to set
     */
    public void setKindofactivity(final int kindofactivity) {
        this.activity.setKindofactivity(kindofactivity);
    }

    /**
     * DOCUMENT ME!
     *
     * @return      DOCUMENT ME!
     *
     * @deprecated  wird nicht mehr genutzt und wurde durch die Lock-Aktivität ersetzt
     */
    public boolean getCommitted() {
        return activity.getCommitted();
    }

    /**
     * DOCUMENT ME!
     *
     * @param       committed  DOCUMENT ME!
     *
     * @deprecated  wird nicht mehr genutzt und wurde durch die Lock-Aktivität ersetzt
     */
    public void setCommitted(final boolean committed) {
        this.activity.setCommitted(committed);
    }

    @Override
    public ActivityExtDTO createCopy() {
        return new ActivityExtDTO(activity);
    }

    @Override
    public void reset(final ActivityExtDTO obj) {
        activity = obj.activity;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass().getName().equals(this.getClass().getName())) {
            // sometimes, it happens, that objects will be added to a set, which has no id (id = 0).
            // In order to prevent that only the first object without id can stay within the set, the
            // equals method return false if both objects which should be compared has no id
            if ((this.id == 0) && (((BasicDTO)obj).getId() == 0)) {
                return this == obj;
            } else {
                return this.id == ((BasicDTO)obj).getId();
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = (53 * hash) + (int)(this.id ^ (this.id >>> 32));
        return hash;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean hasSameContent(final Object obj) {
        if (obj instanceof ActivityExtDTO) {
            final ActivityExtDTO other = (ActivityExtDTO)obj;
            return other.activity.hasSameContent(activity);
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   obj    DOCUMENT ME!
     * @param   other  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isSame(final Object obj, final Object other) {
        if ((obj == null) && (other == null)) {
            return true;
        }

        if ((obj == null) || (other == null)) {
            return false;
        }

        if ((obj instanceof Date) && (other instanceof Date)) {
            final long millis1 = ((Date)obj).getTime();
            final long millis2 = ((Date)other).getTime();

            return millis1 == millis2;
        }
        return obj.equals(other);
    }

    public int compareTo(final ActivityExtDTO o) {
        return o.activity.compareTo(activity);
    }
    
}
