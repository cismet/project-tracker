/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui.event;

import de.cismet.projecttracker.client.types.ListItem;

/**
 * ChangeableTreeEvent is used to notify interested parties that the selected item has changed in the event source.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ItemSelectEvent {

    //~ Instance fields --------------------------------------------------------

    private ListItem item = null;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the projectType
     */
    public ListItem getItem() {
        return item;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  item  projectType the projectType to set
     */
    public void setItem(final ListItem item) {
        this.item = item;
    }
}
