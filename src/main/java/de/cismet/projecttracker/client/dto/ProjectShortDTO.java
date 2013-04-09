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

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectShortDTO extends BasicDTO<BasicDTO> implements Comparable<ProjectShortDTO> {

    //~ Instance fields --------------------------------------------------------

    protected String name;
    protected String description;
    protected double overheadratio;
    protected ProjectCategoryDTO projectCategory;
    protected ArrayList<ProjectPeriodDTO> projectPeriods = new ArrayList<ProjectPeriodDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectShortDTO object.
     */
    public ProjectShortDTO() {
    }

    /**
     * Creates a new ProjectShortDTO object.
     *
     * @param  id               DOCUMENT ME!
     * @param  name             DOCUMENT ME!
     * @param  description      DOCUMENT ME!
     * @param  overheadratio    DOCUMENT ME!
     * @param  projectCategory  DOCUMENT ME!
     * @param  projectPeriods   DOCUMENT ME!
     */
    public ProjectShortDTO(final long id,
            final String name,
            final String description,
            final double overheadratio,
            final ProjectCategoryDTO projectCategory,
            final ArrayList<ProjectPeriodDTO> projectPeriods) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.overheadratio = overheadratio;
        this.projectCategory = projectCategory;
        this.projectPeriods = projectPeriods;
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

    /**
     * DOCUMENT ME!
     *
     * @return  the overheadratio
     */
    public double getOverheadratio() {
        return overheadratio;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  overheadratio  the overheadratio to set
     */
    public void setOverheadratio(final double overheadratio) {
        this.overheadratio = overheadratio;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the projectCategory
     */
    public ProjectCategoryDTO getProjectCategory() {
        return projectCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectCategory  the projectCategory to set
     */
    public void setProjectCategory(final ProjectCategoryDTO projectCategory) {
        this.projectCategory = projectCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the projectPeriods
     */
    public ArrayList<ProjectPeriodDTO> getProjectPeriods() {
        return projectPeriods;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectPeriods  the projectPeriods to set
     */
    public void setProjectPeriods(final ArrayList<ProjectPeriodDTO> projectPeriods) {
        this.projectPeriods = projectPeriods;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  an instance of the type ProjectShort
     */
    @Override
    public BasicDTO createCopy() {
        return new ProjectDTO(id, name, description, overheadratio, projectCategory, projectPeriods);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  obj  an instance of the type ProjectShort
     */
    @Override
    public void reset(final BasicDTO obj) {
        final ProjectShortDTO prj = (ProjectShortDTO)obj;
        this.id = prj.id;
        this.name = prj.name;
        this.description = prj.description;
        this.overheadratio = prj.overheadratio;
        this.projectCategory = prj.projectCategory;
        this.projectPeriods = prj.projectPeriods;
    }

    @Override
    public int compareTo(final ProjectShortDTO o) {
        return name.compareTo(o.name);
    }
}
