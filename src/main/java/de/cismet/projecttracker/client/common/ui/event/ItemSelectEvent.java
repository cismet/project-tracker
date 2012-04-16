package de.cismet.projecttracker.client.common.ui.event;

import de.cismet.projecttracker.client.types.ListItem;


/**
 * ChangeableTreeEvent is used to notify interested parties that
 * the selected item has changed in the event source.
 *
 * @author therter
 */
public class ItemSelectEvent {
    private ListItem item = null;

    /**
     * @return the projectType
     */
    public ListItem getItem() {
        return item;
    }

    /**
     * @param projectType the projectType to set
     */
    public void setItem(ListItem item) {
        this.item = item;
    }
}
