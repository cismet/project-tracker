package de.cismet.projecttracker.client.common.ui.listener;

import com.google.gwt.user.client.ui.ClickListener;

/**
 * This class can be used to register parties so, that they will be notified when
 * the user want to perform an action on a StackPanel
 *
 * @author therter
 */
public interface CustomStackPanelClickListener extends ClickListener {
    /**
     *
     * @param desc contains the description of a button with the following structure:<br />
     *          operation;type<br />
     *          example:<br />
     *          add;Profile
     */
    public void onClick(String desc);
}
