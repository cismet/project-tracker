/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui.event;

import com.google.gwt.user.client.ui.TreeItem;

/**
 * ChangeableTreeEvent is used to notify interested parties that the user want to performed an action in the event
 * source.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ChangeableTreeEvent {

    //~ Instance fields --------------------------------------------------------

    private TreeItem parentItem;
    private String itemName;
    private TreeItem item;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the parentItem
     */
    public TreeItem getParentItem() {
        return parentItem;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parentItem  the parentItem to set
     */
    public void setParentItem(final TreeItem parentItem) {
        this.parentItem = parentItem;
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

    /**
     * DOCUMENT ME!
     *
     * @return  the item
     */
    public TreeItem getItem() {
        return item;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  item  the item to set
     */
    public void setItem(final TreeItem item) {
        this.item = item;
    }
}
