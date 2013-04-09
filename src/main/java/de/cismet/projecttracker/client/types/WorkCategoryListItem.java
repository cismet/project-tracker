/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.WorkCategoryDTO;

/**
 * A ListItem implementation that wraps WorkCategoryDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WorkCategoryListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    WorkCategoryDTO WorkCategory;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkCategoryListItem object.
     *
     * @param  WorkCategory  DOCUMENT ME!
     */
    public WorkCategoryListItem(final WorkCategoryDTO WorkCategory) {
        super("" + WorkCategory.getId(), WorkCategory.getName());
        this.WorkCategory = WorkCategory;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public WorkCategoryDTO getWorkCategory() {
        return WorkCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  WorkCategory  DOCUMENT ME!
     */
    public void setWorkCategory(final WorkCategoryDTO WorkCategory) {
        this.WorkCategory = WorkCategory;
        setId("" + WorkCategory.getId());
        setName(WorkCategory.getName());
    }
}
