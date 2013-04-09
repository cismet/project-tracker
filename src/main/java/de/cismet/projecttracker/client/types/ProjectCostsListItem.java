/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectCostsDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps ProjectCostsDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectCostsListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private ProjectCostsDTO projectCosts;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectCostsListItem object.
     */
    public ProjectCostsListItem() {
    }

    /**
     * Creates a new ProjectCostsListItem object.
     *
     * @param  projectCosts  DOCUMENT ME!
     */
    public ProjectCostsListItem(final ProjectCostsDTO projectCosts) {
        super("" + projectCosts.getId(), getItemName(projectCosts));
        this.projectCosts = projectCosts;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the project
     */
    public ProjectCostsDTO getProjectCosts() {
        return projectCosts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectCosts  the project to set
     */
    public void setProjectCosts(final ProjectCostsDTO projectCosts) {
        this.projectCosts = projectCosts;
        setId("" + projectCosts.getId());
        setName(getItemName(projectCosts));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   projectCosts  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String getItemName(final ProjectCostsDTO projectCosts) {
        return projectCosts.getId() + "-" + DateHelper.formatDate(projectCosts.getTime());
    }
}
