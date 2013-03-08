package de.cismet.projecttracker.client.dto;

import java.util.Date;

/**
 *
 * @author therter
 */
public class WarningDTO extends BasicDTO<WarningDTO> {
    private WorkPackageDTO workPackage;
    private int level;
    private Date time;


    public WarningDTO() {
    }

    
    public WarningDTO(long id, WorkPackageDTO workPackage, int level, Date time) {
        this.workPackage = workPackage;
        this.level = level;
        this.time = time;
        this.id = id;
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
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public WarningDTO createCopy() {
        return new WarningDTO(id, workPackage, level, time);
    }

    @Override
    public void reset(WarningDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.level = obj.level;
        this.time = obj.time;
    }
}
