/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

/**
 *
 * @author therter
 */
public class ActivityDTO extends BasicDTO<ActivityDTO> implements Comparable<ActivityDTO> {
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
    private StaffDTO staff;
    private WorkPackageDTO workPackage;
    private WorkCategoryDTO workCategory;
    private double workinghours;
    private String description;
    private Date day;
    private int kindofactivity;
    private boolean committed;

    public ActivityDTO() {
    }


    

    public ActivityDTO(long id, StaffDTO staff, WorkPackageDTO workPackage, WorkCategoryDTO workCategory, double workinghours, String description, Date day, boolean committed, int kindofactivity) {
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


    /**
     * @return the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    /**
     * @return the workPackage
     */
    public WorkPackageDTO getWorkPackage() {
        return workPackage;
    }

    /**
     * @param workPackage the workPackage to set
     */
    public void setWorkPackage(WorkPackageDTO workPackage) {
        this.workPackage = workPackage;
    }

    /**
     * @return the workCategory
     */
    public WorkCategoryDTO getWorkCategory() {
        return workCategory;
    }

    /**
     * @param workCategory the workCategory to set
     */
    public void setWorkCategory(WorkCategoryDTO workCategory) {
        this.workCategory = workCategory;
    }

    /**
     * @return the workinghours
     */
    public double getWorkinghours() {
        return workinghours;
    }

    /**
     * @param workinghours the workinghours to set
     */
    public void setWorkinghours(double workinghours) {
        this.workinghours = workinghours;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
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
     * @return the forInternalUse
     */
    public int getKindofactivity() {
        return kindofactivity;
    }

    /**
     * @param forInternalUse the forInternalUse to set
     */
    public void setKindofactivity(int kindofactivity) {
        this.kindofactivity = kindofactivity;
    }

    /**
     * @deprecated wird nicht mehr genutzt und wurde durch die Lock-Aktivität ersetzt
     */
    public boolean getCommitted() {
        return committed;
    }

    /**
     * @deprecated wird nicht mehr genutzt und wurde durch die Lock-Aktivität ersetzt
     */
    public void setCommitted(boolean committed) {
        this.committed = committed;
    }

    @Override
    public ActivityDTO createCopy() {
        return new ActivityDTO(id, staff, workPackage, workCategory, workinghours, description, day, committed, kindofactivity);
    }

    @Override
    public void reset(ActivityDTO obj) {
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
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if ( obj.getClass().getName().equals(this.getClass().getName()) ) {
            // sometimes, it happens, that objects will be added to a set, which has no id (id = 0).
            // In order to prevent that only the first object without id can stay within the set, the
            // equals method return false if both objects which should be compared has no id
            if (this.id == 0 && ((BasicDTO)obj).getId() == 0) {
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
        hash = 53 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    public boolean hasSameContent(Object obj) {
        if (obj instanceof ActivityDTO) {
            ActivityDTO other = (ActivityDTO)obj;
            if (this.id == other.id &&
                   isSameDTOEntity(this.workPackage , other.workPackage) && isSameDTOEntity(this.staff, other.staff) &&
                   isSameDTOEntity(this.workCategory, other.workCategory) && this.workinghours == other.workinghours && this.kindofactivity == other.kindofactivity &&
                   isSame(this.description, other.description) && isSame(this.day, other.day) && this.committed == other.committed) {
                return true;
            }
        }

        return false;
    }


    private boolean isSame(Object obj, Object other) {
        if (obj == null && other == null) {
            return true;
        }

        if (obj == null || other == null) {
            return false;
        }

        if (obj instanceof Date && other instanceof Date) {
            long millis1 = ((Date)obj).getTime();
            long millis2 = ((Date)other).getTime();
            
            return millis1 == millis2;
        }
        return obj.equals(other);
    }

    @Override
    public int compareTo(ActivityDTO o) {
        int result;
        long time1 = this.day.getTime();
        long time2 = o.day.getTime();
        

        if ( time1 == -1 && time2 == -1 ) {
            result = 0;
        } else if (time1 == -1) {
            result = -1;
        } else if (time2 == -1) {
            result = 1;
        } else {
            long diff = time1 - time2;
            result = (int)Math.signum(diff);
            
            if (result == 0) {
                if (this.workPackage != null && 
                        this.workPackage.getId() == PAUSE_ID) {
                    result = -1;
                } else if (o.workPackage != null &&
                        o.workPackage.getId() == PAUSE_ID) {
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
