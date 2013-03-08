package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.common.ui.event.ChangeableListEvent;


/**
 * This class can be used to register parties so, that they will be notified when
 * the user want to perform an action on a ChangeableListBox
 *
 * @author therter
 */
public interface ChangeableListBoxListener {
    /**
     * This event will be fired, if the user want to add a new item.
     * The item will not be created by the Changeable Tree, but it will only
     * be fired an addTreeItem event
     *
     * @param event contains information about the item, that should be added
     */
    public void addListItem(final ChangeableListEvent event);

    /**
     * This event will be fired, if the user want to delete a item.
     * The item will not be deleted by the Changeable list box, but it will only
     * be fired an deleteListItem event
     *
     * @param event contains information about the item, that should be deleted
     */
    public void deleteListItem(final ChangeableListEvent event);
}
