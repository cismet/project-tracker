package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectBodyDTO;


/**
 * A ListItem implementation that wraps ProjectBodyDTO objects
 *
 * @author therter
 */
public class ProjectBodyListItem extends ListItem {
    ProjectBodyDTO projectBody;

    public ProjectBodyListItem(ProjectBodyDTO projectBody) {
        super("" + projectBody.getId(), projectBody.getName());
        this.projectBody = projectBody;
    }

    public ProjectBodyDTO getProjectBody() {
        return projectBody;
    }

    public void setProjectBody(ProjectBodyDTO projectBody) {
        this.projectBody = projectBody;
        setId("" + projectBody.getId());
        setName(projectBody.getName());
    }
}
