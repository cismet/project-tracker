package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.WorkCategoryDTO;



/**
 * A ListItem implementation that wraps WorkCategoryDTO objects
 *
 * @author therter
 */
public class WorkCategoryListItem extends ListItem {
    WorkCategoryDTO WorkCategory;

    public WorkCategoryListItem(WorkCategoryDTO WorkCategory) {
        super("" + WorkCategory.getId(), WorkCategory.getName());
        this.WorkCategory = WorkCategory;
    }

    public WorkCategoryDTO getWorkCategory() {
        return WorkCategory;
    }

    public void setWorkCategory(WorkCategoryDTO WorkCategory) {
        this.WorkCategory = WorkCategory;
        setId("" + WorkCategory.getId());
        setName(WorkCategory.getName());
    }
}
