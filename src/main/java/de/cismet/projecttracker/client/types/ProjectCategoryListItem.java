package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectCategoryDTO;


/**
 * A ListItem implementation that wraps ProjectCategoryDTO objects
 *
 * @author therter
 */
public class ProjectCategoryListItem extends ListItem {
    ProjectCategoryDTO projectCategory;

    public ProjectCategoryListItem(ProjectCategoryDTO projectCategory) {
        super("" + projectCategory.getId(), projectCategory.getName());
        this.projectCategory = projectCategory;
    }


    public ProjectCategoryListItem(String id, String name) {
        super(id, name);
    }

    public ProjectCategoryDTO getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(ProjectCategoryDTO projectCategory) {
        this.projectCategory = projectCategory;
        setId("" + projectCategory.getId());
        setName(projectCategory.getName());
    }
}
