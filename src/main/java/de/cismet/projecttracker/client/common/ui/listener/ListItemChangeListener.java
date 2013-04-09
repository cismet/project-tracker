/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.types.ListItem;

/**
 * This class can be used to register parties so, that they will be notified when the name of a list item was changed.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface ListItemChangeListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  newName  the new name of the item
     * @param  oldName  the old name of the item
     */
    void onChange(ListItem newName, String oldName);
}
