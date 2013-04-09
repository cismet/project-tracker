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

import java.util.ArrayList;
import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class EstimatedComponentCostDTO extends BasicDTO<EstimatedComponentCostDTO> {

    //~ Instance fields --------------------------------------------------------

    private WorkPackageDTO workPackage;
    private Date creationtime;
    private ArrayList<EstimatedComponentCostMonthDTO> estimatedWorkPackageCostMonth =
        new ArrayList<EstimatedComponentCostMonthDTO>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EstimatedComponentCostDTO object.
     */
    public EstimatedComponentCostDTO() {
    }

    /**
     * Creates a new EstimatedComponentCostDTO object.
     *
     * @param  id                             DOCUMENT ME!
     * @param  workPackage                    DOCUMENT ME!
     * @param  creationtime                   DOCUMENT ME!
     * @param  estimatedWorkPackageCostMonth  DOCUMENT ME!
     */
    public EstimatedComponentCostDTO(final long id,
            final WorkPackageDTO workPackage,
            final Date creationtime,
            final ArrayList<EstimatedComponentCostMonthDTO> estimatedWorkPackageCostMonth) {
        this.id = id;
        this.workPackage = workPackage;
        this.creationtime = creationtime;
        this.estimatedWorkPackageCostMonth = estimatedWorkPackageCostMonth;
    }

    //~ Methods ----------------------------------------------------------------

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
     * @return  the creationtime
     */
    public Date getCreationtime() {
        return creationtime;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  creationtime  the creationtime to set
     */
    public void setCreationtime(final Date creationtime) {
        this.creationtime = creationtime;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the estimatedWorkPackageCostMonth
     */
    public ArrayList<EstimatedComponentCostMonthDTO> getEstimatedWorkPackageCostMonth() {
        return estimatedWorkPackageCostMonth;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  estimatedWorkPackageCostMonth  the estimatedWorkPackageCostMonth to set
     */
    public void setEstimatedWorkPackageCostMonth(
            final ArrayList<EstimatedComponentCostMonthDTO> estimatedWorkPackageCostMonth) {
        this.estimatedWorkPackageCostMonth = estimatedWorkPackageCostMonth;
    }

    @Override
    public EstimatedComponentCostDTO createCopy() {
        return new EstimatedComponentCostDTO(id, workPackage, creationtime, estimatedWorkPackageCostMonth);
    }

    @Override
    public void reset(final EstimatedComponentCostDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.creationtime = obj.creationtime;
        this.estimatedWorkPackageCostMonth = obj.estimatedWorkPackageCostMonth;
    }
}
