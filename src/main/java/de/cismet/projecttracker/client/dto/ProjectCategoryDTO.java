/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectCategoryDTO extends BasicDTO<ProjectCategoryDTO> implements Comparable<ProjectCategoryDTO> {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private ArrayList<ProjectDTO> projects = new ArrayList<ProjectDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectCategoryDTO object.
     */
    public ProjectCategoryDTO() {
    }

    /**
     * Creates a new ProjectCategoryDTO object.
     *
     * @param  id        DOCUMENT ME!
     * @param  name      DOCUMENT ME!
     * @param  projects  DOCUMENT ME!
     */
    public ProjectCategoryDTO(final long id, final String name, final ArrayList<ProjectDTO> projects) {
        this.id = id;
        this.name = name;
        this.projects = projects;
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
     * @return  the projects
     */
    public ArrayList<ProjectDTO> getProjects() {
        return projects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projects  the projects to set
     */
    public void setProjects(final ArrayList<ProjectDTO> projects) {
        this.projects = projects;
    }

    @Override
    public ProjectCategoryDTO createCopy() {
        return new ProjectCategoryDTO(id, name, projects);
    }

    @Override
    public void reset(final ProjectCategoryDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.projects = obj.projects;
    }

    @Override
    public int compareTo(final ProjectCategoryDTO o) {
        return name.compareTo(o.name);
    }
}
