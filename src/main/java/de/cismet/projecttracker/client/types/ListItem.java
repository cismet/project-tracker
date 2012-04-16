package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.common.ui.listener.ListItemChangeListener;
import java.util.ArrayList;

/**
 * This is the basic class of all ListItem classes, which are used by CustomListBox.
 * This class implements the Comparable interface to allow the sorting of list items. By default,
 * the list items will be ordered lexicographically by the name. To change this behavior, the method
 * {@link #compareTo(de.cismet.projecttracker.client.types.ListItem)} can be overridden by the sub class.
 *
 * @author therter
 */
public class ListItem implements Comparable<ListItem> {
    private String name;
    private String id;
    private String tooltip;
    private ArrayList<ListItemChangeListener> listener = new ArrayList<ListItemChangeListener>();

    public ListItem() {
    }

    public ListItem(String id, String name) {
        this.id = id;
        this.name = name;
        this.tooltip = name;
    }

    public ListItem(String id, String name, String tooltip) {
        this.id = id;
        this.name = name;
        this.tooltip = tooltip;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        changeEvent(oldName);
    }

    /**
     * @return the value
     */
    public String getId() {
        return id;
    }

    /**
     * @param value the value to set. This must be an unique identifier
     */
    public void setId(String value) {
        this.id = value;
    }
    
    public void addChangeListener(ListItemChangeListener lis) {
        listener.add(lis);
    }

    public boolean removeChangeListener(ListItemChangeListener lis) {
        return listener.remove(lis);
    }

    private void changeEvent(String oldName) {
        for (ListItemChangeListener tmp : listener) {
            tmp.onChange(this, oldName);
        }
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(ListItem o) {
        return name.compareTo(o.name);
    }

    /**
     * @return the tooltip
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * @param tooltip the tooltip to set
     */
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }
}
