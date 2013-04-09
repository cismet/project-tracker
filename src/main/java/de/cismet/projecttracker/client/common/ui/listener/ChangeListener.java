/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.common.ui.event.ChangeEvent;

/**
 * This class can be used to register parties so, that they will be notified when a change has occured.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface ChangeListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  event  the field selectedItem is null, if and only if no item is selected
     */
    void onChange(ChangeEvent event);
}
