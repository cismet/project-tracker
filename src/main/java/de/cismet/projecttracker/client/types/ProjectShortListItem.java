package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectShortDTO;



/**
 * A ListItem implementation that wraps ProjectShortDTO objects
 *
 * @author therter
 */
public class ProjectShortListItem extends ListItem {
    private ProjectShortDTO project;

    public ProjectShortListItem(ProjectShortDTO project) {
        super("" + project.getId(), project.getName());
        this.project = project;
    }

    public ProjectShortListItem(String id, String name) {
        super(id, name);
    }

    /**
     * @return the project
     */
    public ProjectShortDTO getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(ProjectShortDTO project) {
        this.project = project;
        setId("" + project.getId());
        setName(project.getName());
    }
}
