package de.cismet.projecttracker.client.common.ui.event;

import de.cismet.projecttracker.client.types.ListItem;

/**
 * ChangeEvent is used to notify interested parties that
 * state has changed in the event source.
 *
 * @author therter
 */
public class ChangeEvent {
    private Object Source;
    private ListItem selectedItem;

    
    /**
     * @return the Source
     */
    public Object getSource() {
        return Source;
    }

    /**
     * @param Source the Source to set
     */
    public void setSource(Object Source) {
        this.Source = Source;
    }

    /**
     * @return the selectedElement
     */
    public ListItem getSelectedItem() {
        return selectedItem;
    }

    /**
     * @param selectedElement the selectedElement to set
     */
    public void setSelectedItem(ListItem selectedItem) {
        this.selectedItem = selectedItem;
    }
}
