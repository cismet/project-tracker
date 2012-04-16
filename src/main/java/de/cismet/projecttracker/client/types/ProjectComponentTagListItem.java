package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ProjectComponentTagDTO;


/**
 * A ListItem implementation that wraps ProjectComponentTagDTO objects
 *
 * @author therter
 */
public class ProjectComponentTagListItem extends ListItem {
    private ProjectComponentTagDTO tag;

    public ProjectComponentTagListItem(ProjectComponentTagDTO tag) {
        super("" + tag.getId(), tag.getName());
        this.tag = tag;
    }

    /**
     * @return the project component tag
     */
    public ProjectComponentTagDTO getTag() {
        return tag;
    }

    /**
     * @param tag the project component tag that should be wrapped
     */
    public void setTag(ProjectComponentTagDTO tag) {
        this.tag = tag;
        setId("" + tag.getId());
        setName(tag.getName());
    }
}
