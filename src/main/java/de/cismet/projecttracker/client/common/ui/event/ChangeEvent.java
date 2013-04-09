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
 * ChangeEvent is used to notify interested parties that state has changed in the event source.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ChangeEvent {

    //~ Instance fields --------------------------------------------------------

    private Object Source;
    private ListItem selectedItem;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the Source
     */
    public Object getSource() {
        return Source;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  Source  the Source to set
     */
    public void setSource(final Object Source) {
        this.Source = Source;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the selectedElement
     */
    public ListItem getSelectedItem() {
        return selectedItem;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  selectedItem  the selectedElement to set
     */
    public void setSelectedItem(final ListItem selectedItem) {
        this.selectedItem = selectedItem;
    }
}
