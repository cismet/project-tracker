package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectDTO;


/**
 * A ListItem implementation that wraps ProjectListItem objects
 *
 * @author therter
 */
public class ProjectListItem extends ListItem {
    private ProjectDTO project;

    public ProjectListItem() {
    }

    public ProjectListItem(ProjectDTO project) {
        super("" + project.getId(), project.getName());
        this.project = project;
    }

    /**
     * @return the project
     */
    public ProjectDTO getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(ProjectDTO project) {
        this.project = project;
        setId("" + project.getId());
        setName(project.getName());
    }
}
