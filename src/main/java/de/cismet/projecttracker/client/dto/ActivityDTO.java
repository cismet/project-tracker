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
package de.cismet.projecttracker.client.dto;

import java.sql.Timestamp;

import java.util.Date;

import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ActivityDTO extends BasicDTO<ActivityDTO> implements Comparable<ActivityDTO> {

    //~ Static fields/initializers ---------------------------------------------

    public static final int ACTIVITY = 0;
    public static final int BEGIN_OF_DAY = 1;
    public static final int END_OF_DAY = 2;
    public static final int LOCKED_DAY = 3;
    public static final int HOLIDAY = -1;
    public static final int HALF_HOLIDAY = -2;
    public static final int SPARE_TIME_ID = 407;
    public static final int PAUSE_ID = 408;
    public static final int HOLIDAY_ID = 409;
    public static final int LECTURE_ID = 419;
    public static final int ILLNESS_ID = 410;
    public static final int Travel_ID = 414;
    public static final int SPECIAL_HOLIDAY_ID = 411;

    //~ Instance fields --------------------------------------------------------

    private StaffDTO staff;
    private WorkPackageDTO workPackage;
    private WorkCategoryDTO workCategory;
    private double workinghours;
    private String description;
    private Date day;
    private int kindofactivity;
    private boolean committed;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ActivityDTO object.
     */
    public ActivityDTO() {
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
    public ActivityDTO(final long id,
            final StaffDTO staff,
            final WorkPackageDTO workPackage,
            final WorkCategoryDTO workCategory,
            final double workinghours,
            final String description,
            final Date day,
            final boolean committed,
            final int kindofactivity) {
        this.id = id;
        this.staff = staff;
        this.workPackage = workPackage;
        this.workCategory = workCategory;
        this.workinghours = workinghours;
        this.description = description;
        this.day = day;
        this.committed = committed;
        this.kindofactivity = kindofactivity;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff  the staff to set
     */
    public void setStaff(final StaffDTO staff) {
        this.staff = staff;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workPackage
     */
    public WorkPackageDTO getWorkPackage() {
        return workPackage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workPackage  the workPackage to set
     */
    public void setWorkPackage(final WorkPackageDTO workPackage) {
        this.workPackage = workPackage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workCategory
     */
    public WorkCategoryDTO getWorkCategory() {
        return workCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workCategory  the workCategory to set
     */
    public void setWorkCategory(final WorkCategoryDTO workCategory) {
        this.workCategory = workCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workinghours
     */
    public double getWorkinghours() {
        return workinghours;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workinghours  the workinghours to set
     */
    public void setWorkinghours(final double workinghours) {
        this.workinghours = workinghours;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
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
     * @return  the forInternalUse
     */
    public int getKindofactivity() {
        return kindofactivity;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  kindofactivity  forInternalUse the forInternalUse to set
     */
    public void setKindofactivity(final int kindofactivity) {
        this.kindofactivity = kindofactivity;
    }

    /**
     * DOCUMENT ME!
     *
     * @return      DOCUMENT ME!
     *
     * @deprecated  wird nicht mehr genutzt und wurde durch die Lock-Aktivität ersetzt
     */
    public boolean getCommitted() {
        return committed;
    }

    /**
     * DOCUMENT ME!
     *
     * @param       committed  DOCUMENT ME!
     *
     * @deprecated  wird nicht mehr genutzt und wurde durch die Lock-Aktivität ersetzt
     */
    public void setCommitted(final boolean committed) {
        this.committed = committed;
    }

    @Override
    public ActivityDTO createCopy() {
        return new ActivityDTO(
                id,
                staff,
                workPackage,
                workCategory,
                workinghours,
                description,
                day,
                committed,
                kindofactivity);
    }

    @Override
    public void reset(final ActivityDTO obj) {
        this.id = obj.id;
        this.staff = obj.staff;
        this.workPackage = obj.workPackage;
        this.workCategory = obj.workCategory;
        this.workinghours = obj.workinghours;
        this.description = obj.description;
        this.day = obj.day;
        this.committed = obj.committed;
        this.kindofactivity = obj.kindofactivity;
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
        if (obj instanceof ActivityDTO) {
            final ActivityDTO other = (ActivityDTO)obj;
            if ((this.id == other.id)
                        && isSameDTOEntity(this.workPackage, other.workPackage)
                        && isSameDTOEntity(this.staff, other.staff)
                        && isSameDTOEntity(this.workCategory, other.workCategory)
                        && (this.workinghours == other.workinghours)
                        && (this.kindofactivity == other.kindofactivity)
                        && isSame(this.description, other.description) && isSame(this.day, other.day)
                        && (this.committed == other.committed)) {
                return true;
            }
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

    @Override
    public int compareTo(final ActivityDTO o) {
        int result;
        final long time1 = this.day.getTime();
        final long time2 = o.day.getTime();

        if ((time1 == -1) && (time2 == -1)) {
            result = 0;
        } else if (time1 == -1) {
            result = -1;
        } else if (time2 == -1) {
            result = 1;
        } else {
            final long diff = time1 - time2;
            result = (int)Math.signum(diff);

            if (result == 0) {
                if ((this.workPackage != null)
                            && (this.workPackage.getId() == PAUSE_ID)) {
                    result = -1;
                } else if ((o.workPackage != null)
                            && (o.workPackage.getId() == PAUSE_ID)) {
                    result = 1;
                }
            }
        }

        return result;
    }

//    private long getTime(ActivityDTO o) {
//        long time = -1;
//        Date date = o.getDay();
//
//        if (date != null) {
//            time = date.getTime() - (date.getTimezoneOffset() * DateHelper.MINUTE_IN_MILLIS);
//            time = time - (time % DateHelper.DAY_IN_MILLIS);
//        }
//
//        if (o.getKindofactivity() == BEGIN_OF_DAY || o.getKindofactivity() == END_OF_DAY) {
//            if (o.getKindofactivity() == ActivityDTO.BEGIN_OF_DAY) {
//                ++time;
//            } else {
//                --time;
//            }
//        }
//        return time;
//    }

}
