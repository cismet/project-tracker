/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class EstimatedComponentCostMonthDTO extends BasicDTO<EstimatedComponentCostMonthDTO> {

    //~ Instance fields --------------------------------------------------------

    private EstimatedComponentCostDTO estimatedWorkPackageCost;
    private int month;
    private double workinghours;
    private String description;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EstimatedComponentCostMonthDTO object.
     */
    public EstimatedComponentCostMonthDTO() {
    }

    /**
     * Creates a new EstimatedComponentCostMonthDTO object.
     *
     * @param  id                        DOCUMENT ME!
     * @param  estimatedWorkPackageCost  DOCUMENT ME!
     * @param  month                     DOCUMENT ME!
     * @param  workinghours              DOCUMENT ME!
     * @param  description               DOCUMENT ME!
     */
    public EstimatedComponentCostMonthDTO(final long id,
            final EstimatedComponentCostDTO estimatedWorkPackageCost,
            final int month,
            final double workinghours,
            final String description) {
        this.id = id;
        this.estimatedWorkPackageCost = estimatedWorkPackageCost;
        this.month = month;
        this.workinghours = workinghours;
        this.description = description;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the estimatedWorkPackageCost
     */
    public EstimatedComponentCostDTO getEstimatedWorkPackageCost() {
        return estimatedWorkPackageCost;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  estimatedWorkPackageCost  the estimatedWorkPackageCost to set
     */
    public void setEstimatedWorkPackageCost(final EstimatedComponentCostDTO estimatedWorkPackageCost) {
        this.estimatedWorkPackageCost = estimatedWorkPackageCost;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  month  the month to set
     */
    public void setMonth(final int month) {
        this.month = month;
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

    @Override
    public EstimatedComponentCostMonthDTO createCopy() {
        return new EstimatedComponentCostMonthDTO(id, estimatedWorkPackageCost, month, workinghours, description);
    }

    @Override
    public void reset(final EstimatedComponentCostMonthDTO obj) {
        this.id = obj.id;
        this.estimatedWorkPackageCost = obj.estimatedWorkPackageCost;
        this.month = obj.month;
        this.workinghours = obj.workinghours;
        this.description = obj.description;
    }
}
