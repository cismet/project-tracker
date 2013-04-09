/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectBodyDTO extends BasicDTO<ProjectBodyDTO> implements Comparable<ProjectBodyDTO> {

    //~ Instance fields --------------------------------------------------------

    private String name;
    @JsonIgnore
    private ArrayList<ProjectShortDTO> projects = new ArrayList<ProjectShortDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectBodyDTO object.
     */
    public ProjectBodyDTO() {
    }

    /**
     * Creates a new ProjectBodyDTO object.
     *
     * @param  id        DOCUMENT ME!
     * @param  name      DOCUMENT ME!
     * @param  projects  DOCUMENT ME!
     */
    public ProjectBodyDTO(final long id, final String name, final ArrayList<ProjectShortDTO> projects) {
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
    public ArrayList<ProjectShortDTO> getProjects() {
        return projects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projects  the projects to set
     */
    public void setProjects(final ArrayList<ProjectShortDTO> projects) {
        this.projects = projects;
    }

    @Override
    public ProjectBodyDTO createCopy() {
        return new ProjectBodyDTO(id, name, projects);
    }

    @Override
    public void reset(final ProjectBodyDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.projects = obj.projects;
    }

    @Override
    public int compareTo(final ProjectBodyDTO o) {
        return name.compareTo(o.name);
    }
}
