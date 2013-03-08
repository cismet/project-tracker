package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.common.ui.event.ChangeEvent;


/**
 * This class can be used to register parties so, that they will be notified when
 * a change has occured.
 *
 * @author therter
 */
public interface ChangeListener {
    /**
     *
     * @param event the field selectedItem is null, if and only if no item is selected
     */
    public void onChange(ChangeEvent event);
}
