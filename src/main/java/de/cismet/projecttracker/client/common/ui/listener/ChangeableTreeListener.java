package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.common.ui.event.ChangeableTreeEvent;


/**
 * This class can be used to register parties so, that they will be notified when
 * the user want to perform an action on a ChangeableTree
 *
 * @author therter
 */
public interface ChangeableTreeListener {
    /**
     * This event will be fired, if the user want to add a new item.
     * The item will not be created by the Changeable Tree, but it will only
     * be fired an addTreeItem event
     *
     * @param event contains information about the item, that should be added
     */
    public void addTreeItem(final ChangeableTreeEvent event);

    /**
     * This event will be fired, if the user want to delete a item.
     * The item will not be deleted by the Changeable Tree, but it will only
     * be fired an deleteTreeItem event
     *
     * @param event contains information about the item, that should be deleted
     */
    public void deleteTreeItem(final ChangeableTreeEvent event);

}
