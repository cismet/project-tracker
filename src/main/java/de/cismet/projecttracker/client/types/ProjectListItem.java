/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectDTO;

/**
 * A ListItem implementation that wraps ProjectListItem objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private ProjectDTO project;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectListItem object.
     */
    public ProjectListItem() {
    }

    /**
     * Creates a new ProjectListItem object.
     *
     * @param  project  DOCUMENT ME!
     */
    public ProjectListItem(final ProjectDTO project) {
        super("" + project.getId(), project.getName());
        this.project = project;
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
        setId("" + project.getId());
        setName(project.getName());
    }
}
