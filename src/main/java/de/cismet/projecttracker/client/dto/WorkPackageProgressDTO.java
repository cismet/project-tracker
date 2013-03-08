package de.cismet.projecttracker.client.dto;

import java.util.Date;

/**
 *
 * @author therter
 */
public class WorkPackageProgressDTO extends BasicDTO<WorkPackageProgressDTO> {
     private WorkPackageDTO workPackage;
     private Date time;
     private int progress;

    public WorkPackageProgressDTO() {
    }

     

    public WorkPackageProgressDTO(long id, WorkPackageDTO workPackage, Date time, int progress) {
        this.id = id;
        this.workPackage = workPackage;
        this.time = time;
        this.progress = progress;
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

    /**
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * @param progress the progress to set
     */
    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public WorkPackageProgressDTO createCopy() {
        return new WorkPackageProgressDTO(id, workPackage, time, progress);
    }

    @Override
    public void reset(WorkPackageProgressDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.time = obj.time;
        this.progress = obj.progress;
    }

}
