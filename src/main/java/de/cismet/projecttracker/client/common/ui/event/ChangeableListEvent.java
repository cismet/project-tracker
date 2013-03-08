package de.cismet.projecttracker.client.common.ui.event;

import de.cismet.projecttracker.client.types.ListItem;

/**
 * ChangeableListEvent is used to notify interested parties that
 * the user want to performed an action in the event source.
 *
 * @author therter
 */
public class ChangeableListEvent {
    private ListItem item;
    private String itemName;

    /**
     * @return the item
     */
    public ListItem getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(ListItem item) {
        this.item = item;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
