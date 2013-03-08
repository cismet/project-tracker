/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import java.util.Date;

/**
 *
 * @author therter
 */
public class ProjectCostsDTO extends BasicDTO<ProjectCostsDTO> {
    private CostCategoryDTO costCategory;
    private String comment;
    private double total;
    private Date time;

    public ProjectCostsDTO() {
    }

    

    public ProjectCostsDTO(long id, CostCategoryDTO costCategory, String comment, double total, Date time) {
        this.id = id;
        this.costCategory = costCategory;
        this.comment = comment;
        this.total = total;
        this.time = time;
    }



    /**
     * @return the costCategory
     */
    public CostCategoryDTO getCostCategory() {
        return costCategory;
    }

    /**
     * @param costCategory the costCategory to set
     */
    public void setCostCategory(CostCategoryDTO costCategory) {
        this.costCategory = costCategory;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
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
    public ProjectCostsDTO createCopy() {
        return new ProjectCostsDTO(id, costCategory, comment, total, time);
    }

    @Override
    public void reset(ProjectCostsDTO obj) {
        this.id = id;
        this.costCategory = costCategory;
        this.comment = comment;
        this.total = total;
        this.time = time;
    }
}
