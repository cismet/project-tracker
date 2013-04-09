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

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectCostsDTO extends BasicDTO<ProjectCostsDTO> {

    //~ Instance fields --------------------------------------------------------

    private CostCategoryDTO costCategory;
    private String comment;
    private double total;
    private Date time;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectCostsDTO object.
     */
    public ProjectCostsDTO() {
    }

    /**
     * Creates a new ProjectCostsDTO object.
     *
     * @param  id            DOCUMENT ME!
     * @param  costCategory  DOCUMENT ME!
     * @param  comment       DOCUMENT ME!
     * @param  total         DOCUMENT ME!
     * @param  time          DOCUMENT ME!
     */
    public ProjectCostsDTO(final long id,
            final CostCategoryDTO costCategory,
            final String comment,
            final double total,
            final Date time) {
        this.id = id;
        this.costCategory = costCategory;
        this.comment = comment;
        this.total = total;
        this.time = time;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the costCategory
     */
    public CostCategoryDTO getCostCategory() {
        return costCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  costCategory  the costCategory to set
     */
    public void setCostCategory(final CostCategoryDTO costCategory) {
        this.costCategory = costCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  comment  the comment to set
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  total  the total to set
     */
    public void setTotal(final double total) {
        this.total = total;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  time  the time to set
     */
    public void setTime(final Date time) {
        this.time = time;
    }

    @Override
    public ProjectCostsDTO createCopy() {
        return new ProjectCostsDTO(id, costCategory, comment, total, time);
    }

    @Override
    public void reset(final ProjectCostsDTO obj) {
        this.id = id;
        this.costCategory = costCategory;
        this.comment = comment;
        this.total = total;
        this.time = time;
    }
}
