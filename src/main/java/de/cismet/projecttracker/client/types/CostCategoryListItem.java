package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.CostCategoryDTO;


/**
 * A ListItem implementation that wraps CostCategoryDTO objects
 *
 * @author therter
 */
public class CostCategoryListItem extends ListItem {
    private CostCategoryDTO category;

    public CostCategoryListItem(CostCategoryDTO category) {
        super("" + category.getId(), category.getName());
        this.category = category;
    }

    /**
     * @return the category
     */
    public CostCategoryDTO getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(CostCategoryDTO category) {
        this.category = category;
        setId("" + category.getId());
        setName(category.getName());
    }
}
