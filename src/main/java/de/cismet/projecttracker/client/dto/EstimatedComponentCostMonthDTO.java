package de.cismet.projecttracker.client.dto;

/**
 *
 * @author therter
 */
public class EstimatedComponentCostMonthDTO extends BasicDTO<EstimatedComponentCostMonthDTO> {
    private EstimatedComponentCostDTO estimatedWorkPackageCost;
    private int month;
    private double workinghours;
    private String description;

    public EstimatedComponentCostMonthDTO() {
    }

    

    public EstimatedComponentCostMonthDTO(long id, EstimatedComponentCostDTO estimatedWorkPackageCost, int month, double workinghours, String description) {
        this.id = id;
        this.estimatedWorkPackageCost = estimatedWorkPackageCost;
        this.month = month;
        this.workinghours = workinghours;
        this.description = description;
    }


    /**
     * @return the estimatedWorkPackageCost
     */
    public EstimatedComponentCostDTO getEstimatedWorkPackageCost() {
        return estimatedWorkPackageCost;
    }

    /**
     * @param estimatedWorkPackageCost the estimatedWorkPackageCost to set
     */
    public void setEstimatedWorkPackageCost(EstimatedComponentCostDTO estimatedWorkPackageCost) {
        this.estimatedWorkPackageCost = estimatedWorkPackageCost;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
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

    @Override
    public EstimatedComponentCostMonthDTO createCopy() {
        return new EstimatedComponentCostMonthDTO(id, estimatedWorkPackageCost, month, workinghours, description);
    }

    @Override
    public void reset(EstimatedComponentCostMonthDTO obj) {
        this.id = obj.id;
        this.estimatedWorkPackageCost = obj.estimatedWorkPackageCost;
        this.month = obj.month;
        this.workinghours = obj.workinghours;
        this.description = obj.description;
    }
}
