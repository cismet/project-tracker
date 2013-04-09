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

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WorkCategoryDTO extends BasicDTO<WorkCategoryDTO> {

    //~ Static fields/initializers ---------------------------------------------

    public static final long WORK = 3;
    public static final long TRAVEL = 4;

    //~ Instance fields --------------------------------------------------------

    private String name;
    private boolean workpackagerelated = true;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkCategoryDTO object.
     */
    public WorkCategoryDTO() {
    }

    /**
     * Creates a new WorkCategoryDTO object.
     *
     * @param  id                  DOCUMENT ME!
     * @param  name                DOCUMENT ME!
     * @param  workpackagerelated  DOCUMENT ME!
     */
    public WorkCategoryDTO(final long id, final String name, final boolean workpackagerelated) {
        this.id = id;
        this.name = name;
        this.workpackagerelated = workpackagerelated;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workpackageonly
     */
    public boolean getWorkpackagerelated() {
        return workpackagerelated;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workpackagerelated  the workpackageonly to set
     */
    public void setWorkpackagerelated(final boolean workpackagerelated) {
        this.workpackagerelated = workpackagerelated;
    }

    @Override
    public WorkCategoryDTO createCopy() {
        return new WorkCategoryDTO(id, name, workpackagerelated);
    }

    @Override
    public void reset(final WorkCategoryDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.workpackagerelated = obj.workpackagerelated;
    }
}
