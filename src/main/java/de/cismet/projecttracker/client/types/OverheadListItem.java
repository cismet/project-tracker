package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.RealOverheadDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps RealOverheadDTO objects
 *
 * @author therter
 */
public class OverheadListItem extends ListItem {
    private RealOverheadDTO overhead;

    public OverheadListItem(){
    }

    public OverheadListItem(RealOverheadDTO overhead) {
        this.overhead = overhead;
        setName(getOverheadName());
        setId("" + overhead.getId());
    }

    /**
     * @return the real overhead
     */
    public RealOverheadDTO getOverhead() {
        return overhead;
    }

    /**
     * @param overhead the overhead to set
     */
    public void setOverhead(RealOverheadDTO overhead) {
        this.overhead = overhead;
        setId( "" + overhead.getId() );
        setName( getOverheadName() );
    }


    private String getOverheadName() {
        String label = DateHelper.formatDate( overhead.getValidfrom() );
        if (overhead.getValidto() != null) {
            label += " - " + DateHelper.formatDate(overhead.getValidto());
        }

        return label;
    }
}
