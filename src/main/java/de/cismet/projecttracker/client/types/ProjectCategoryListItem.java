/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectCategoryDTO;

/**
 * A ListItem implementation that wraps ProjectCategoryDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectCategoryListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    ProjectCategoryDTO projectCategory;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectCategoryListItem object.
     *
     * @param  projectCategory  DOCUMENT ME!
     */
    public ProjectCategoryListItem(final ProjectCategoryDTO projectCategory) {
        super("" + projectCategory.getId(), projectCategory.getName());
        this.projectCategory = projectCategory;
    }

    /**
     * Creates a new ProjectCategoryListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public ProjectCategoryListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ProjectCategoryDTO getProjectCategory() {
        return projectCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectCategory  DOCUMENT ME!
     */
    public void setProjectCategory(final ProjectCategoryDTO projectCategory) {
        this.projectCategory = projectCategory;
        setId("" + projectCategory.getId());
        setName(projectCategory.getName());
    }
}
