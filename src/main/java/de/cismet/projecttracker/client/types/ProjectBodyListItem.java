/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectBodyDTO;

/**
 * A ListItem implementation that wraps ProjectBodyDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectBodyListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    ProjectBodyDTO projectBody;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectBodyListItem object.
     *
     * @param  projectBody  DOCUMENT ME!
     */
    public ProjectBodyListItem(final ProjectBodyDTO projectBody) {
        super("" + projectBody.getId(), projectBody.getName());
        this.projectBody = projectBody;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ProjectBodyDTO getProjectBody() {
        return projectBody;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectBody  DOCUMENT ME!
     */
    public void setProjectBody(final ProjectBodyDTO projectBody) {
        this.projectBody = projectBody;
        setId("" + projectBody.getId());
        setName(projectBody.getName());
    }
}
