/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.CostCategoryDTO;

/**
 * A ListItem implementation that wraps CostCategoryDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CostCategoryListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private CostCategoryDTO category;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CostCategoryListItem object.
     *
     * @param  category  DOCUMENT ME!
     */
    public CostCategoryListItem(final CostCategoryDTO category) {
        super("" + category.getId(), category.getName());
        this.category = category;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the category
     */
    public CostCategoryDTO getCategory() {
        return category;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  category  the category to set
     */
    public void setCategory(final CostCategoryDTO category) {
        this.category = category;
        setId("" + category.getId());
        setName(category.getName());
    }
}
