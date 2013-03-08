package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.types.ListItem;


/**
 * This class can be used to register parties so, that they will be notified when
 * the name of a list item was changed
 *
 * @author therter
 */
public interface ListItemChangeListener {
    /**
     *
     * @param newName the new name of the item
     * @param oldName the old name of the item
     */
    public void onChange(ListItem newName, String oldName);
}
