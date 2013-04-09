/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import java.util.ArrayList;

import de.cismet.projecttracker.client.common.ui.listener.ListItemChangeListener;

/**
 * This is the basic class of all ListItem classes, which are used by CustomListBox. This class implements the
 * Comparable interface to allow the sorting of list items. By default, the list items will be ordered lexicographically
 * by the name. To change this behavior, the method {@link #compareTo(de.cismet.projecttracker.client.types.ListItem)}
 * can be overridden by the sub class.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ListItem implements Comparable<ListItem> {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private String id;
    private String tooltip;
    private ArrayList<ListItemChangeListener> listener = new ArrayList<ListItemChangeListener>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ListItem object.
     */
    public ListItem() {
    }

    /**
     * Creates a new ListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public ListItem(final String id, final String name) {
        this.id = id;
        this.name = name;
        this.tooltip = name;
    }

    /**
     * Creates a new ListItem object.
     *
     * @param  id       DOCUMENT ME!
     * @param  name     DOCUMENT ME!
     * @param  tooltip  DOCUMENT ME!
     */
    public ListItem(final String id, final String name, final String tooltip) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  the name to set
     */
    public void setName(final String name) {
        final String oldName = this.name;
        this.name = name;
        changeEvent(oldName);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the value
     */
    public String getId() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  value  the value to set. This must be an unique identifier
     */
    public void setId(final String value) {
        this.id = value;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lis  DOCUMENT ME!
     */
    public void addChangeListener(final ListItemChangeListener lis) {
        listener.add(lis);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   lis  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean removeChangeListener(final ListItemChangeListener lis) {
        return listener.remove(lis);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  oldName  DOCUMENT ME!
     */
    private void changeEvent(final String oldName) {
        for (final ListItemChangeListener tmp : listener) {
            tmp.onChange(this, oldName);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    @Override
    public int compareTo(final ListItem o) {
        return name.compareTo(o.name);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the tooltip
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tooltip  the tooltip to set
     */
    public void setTooltip(final String tooltip) {
        this.tooltip = tooltip;
    }
}
