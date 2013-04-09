/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectShortDTO;

/**
 * A ListItem implementation that wraps ProjectShortDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectShortListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private ProjectShortDTO project;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectShortListItem object.
     *
     * @param  project  DOCUMENT ME!
     */
    public ProjectShortListItem(final ProjectShortDTO project) {
        super("" + project.getId(), project.getName());
        this.project = project;
    }

    /**
     * Creates a new ProjectShortListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public ProjectShortListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the project
     */
    public ProjectShortDTO getProject() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  project  the project to set
     */
    public void setProject(final ProjectShortDTO project) {
        this.project = project;
        setId("" + project.getId());
        setName(project.getName());
    }
}
