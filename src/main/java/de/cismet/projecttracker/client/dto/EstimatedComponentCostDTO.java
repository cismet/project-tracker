/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import java.util.Date;
import java.util.ArrayList;

/**
 *
 * @author therter
 */
public class EstimatedComponentCostDTO extends BasicDTO<EstimatedComponentCostDTO> {
     private WorkPackageDTO workPackage;
     private Date creationtime;
     private ArrayList<EstimatedComponentCostMonthDTO> estimatedWorkPackageCostMonth = new ArrayList<EstimatedComponentCostMonthDTO>();

    public EstimatedComponentCostDTO() {
    }

     

    public EstimatedComponentCostDTO(long id, WorkPackageDTO workPackage, Date creationtime, ArrayList<EstimatedComponentCostMonthDTO> estimatedWorkPackageCostMonth) {
        this.id = id;
        this.workPackage = workPackage;
        this.creationtime = creationtime;
        this.estimatedWorkPackageCostMonth = estimatedWorkPackageCostMonth;
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
     * @return the creationtime
     */
    public Date getCreationtime() {
        return creationtime;
    }

    /**
     * @param creationtime the creationtime to set
     */
    public void setCreationtime(Date creationtime) {
        this.creationtime = creationtime;
    }

    /**
     * @return the estimatedWorkPackageCostMonth
     */
    public ArrayList<EstimatedComponentCostMonthDTO> getEstimatedWorkPackageCostMonth() {
        return estimatedWorkPackageCostMonth;
    }

    /**
     * @param estimatedWorkPackageCostMonth the estimatedWorkPackageCostMonth to set
     */
    public void setEstimatedWorkPackageCostMonth(ArrayList<EstimatedComponentCostMonthDTO> estimatedWorkPackageCostMonth) {
        this.estimatedWorkPackageCostMonth = estimatedWorkPackageCostMonth;
    }

    @Override
    public EstimatedComponentCostDTO createCopy() {
        return new EstimatedComponentCostDTO(id, workPackage, creationtime, estimatedWorkPackageCostMonth);
    }

    @Override
    public void reset(EstimatedComponentCostDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.creationtime = obj.creationtime;
        this.estimatedWorkPackageCostMonth = obj.estimatedWorkPackageCostMonth;
    }
}
