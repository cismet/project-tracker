package de.cismet.projecttracker.client.common.ui.event;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * ChangeableTreeEvent is used to notify interested parties that
 * the user want to performed an action in the event source.
 *
 * @author therter
 */
public class ChangeableTreeEvent {
    private TreeItem parentItem;
    private String itemName;
    private TreeItem item;

    /**
     * @return the parentItem
     */
    public TreeItem getParentItem() {
        return parentItem;
    }

    /**
     * @param parentItem the parentItem to set
     */
    public void setParentItem(TreeItem parentItem) {
        this.parentItem = parentItem;
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

    /**
     * @return the item
     */
    public TreeItem getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(TreeItem item) {
        this.item = item;
    }
}
