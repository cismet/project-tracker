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
public class ProjectComponentTagDTO extends BasicDTO<ProjectComponentTagDTO> {

    //~ Instance fields --------------------------------------------------------

    private ProjectDTO project;
    private String name;
    private ArrayList<ProjectComponentTagDTO> projectComponentTagsForProjectcomponenttagdest =
        new ArrayList<ProjectComponentTagDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectComponentTagDTO object.
     */
    public ProjectComponentTagDTO() {
    }

    /**
     * Creates a new ProjectComponentTagDTO object.
     *
     * @param  id                                              DOCUMENT ME!
     * @param  project                                         DOCUMENT ME!
     * @param  name                                            DOCUMENT ME!
     * @param  projectComponentTagsForProjectcomponenttagdest  DOCUMENT ME!
     */
    public ProjectComponentTagDTO(final long id,
            final ProjectDTO project,
            final String name,
            final ArrayList<ProjectComponentTagDTO> projectComponentTagsForProjectcomponenttagdest) {
        this.id = id;
        this.project = project;
        this.name = name;
        this.projectComponentTagsForProjectcomponenttagdest = projectComponentTagsForProjectcomponenttagdest;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the project
     */
    public ProjectDTO getProject() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  project  the project to set
     */
    public void setProject(final ProjectDTO project) {
        this.project = project;
    }

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
     * @return  the projectComponentTagsForProjectcomponenttagdest
     */
    public ArrayList<ProjectComponentTagDTO> getProjectComponentTagsForProjectcomponenttagdest() {
        return projectComponentTagsForProjectcomponenttagdest;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectComponentTagsForProjectcomponenttagdest  the projectComponentTagsForProjectcomponenttagdest to set
     */
    public void setProjectComponentTagsForProjectcomponenttagdest(
            final ArrayList<ProjectComponentTagDTO> projectComponentTagsForProjectcomponenttagdest) {
        this.projectComponentTagsForProjectcomponenttagdest = projectComponentTagsForProjectcomponenttagdest;
    }

    @Override
    public ProjectComponentTagDTO createCopy() {
        return new ProjectComponentTagDTO(id, project, name, projectComponentTagsForProjectcomponenttagdest);
    }

    @Override
    public void reset(final ProjectComponentTagDTO obj) {
        this.id = obj.getId();
        this.project = obj.getProject();
        this.name = obj.getName();
        this.projectComponentTagsForProjectcomponenttagdest = obj.getProjectComponentTagsForProjectcomponenttagdest();
    }
}
