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
 * ChangeableListEvent is used to notify interested parties that the user want to performed an action in the event
 * source.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ChangeableListEvent {

    //~ Instance fields --------------------------------------------------------

    private ListItem item;
    private String itemName;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the item
     */
    public ListItem getItem() {
        return item;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  item  the item to set
     */
    public void setItem(final ListItem item) {
        this.item = item;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  itemName  the itemName to set
     */
    public void setItemName(final String itemName) {
        this.itemName = itemName;
    }
}
