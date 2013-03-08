package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectCostsDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps ProjectCostsDTO objects
 *
 * @author therter
 */
public class ProjectCostsListItem extends ListItem {
    private ProjectCostsDTO projectCosts;


    public ProjectCostsListItem() {
    }


    public ProjectCostsListItem(ProjectCostsDTO projectCosts) {
        super( "" + projectCosts.getId(), getItemName(projectCosts) );
        this.projectCosts = projectCosts;
    }


    /**
     * @return the project
     */
    public ProjectCostsDTO getProjectCosts() {
        return projectCosts;
    }


    /**
     * @param project the project to set
     */
    public void setProjectCosts(ProjectCostsDTO projectCosts) {
        this.projectCosts = projectCosts;
        setId("" + projectCosts.getId());
        setName(  getItemName(projectCosts) );
    }


    private static String getItemName(ProjectCostsDTO projectCosts) {
        return projectCosts.getId() + "-" + DateHelper.formatDate( projectCosts.getTime() );
    }
}
