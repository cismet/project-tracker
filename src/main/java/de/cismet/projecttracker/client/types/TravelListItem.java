package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.TravelDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps TravelDTO objects
 *
 * @author therter
 */
public class TravelListItem extends ListItem {
    private TravelDTO travel;

    public TravelListItem(TravelDTO travel) {
        this.travel = travel;
        setName( DateHelper.formatDate( travel.getDate() ) );
        setTooltip( travel.getDestination() );
        setId("" + travel.getId());
    }

    public TravelListItem(String id, String name) {
        super(id, name);
    }

    /**
     * @return the staff
     */
    public TravelDTO getTravel() {
        return travel;
    }

    /**
     * @param staff the staff to set
     */
    public void setTravel(TravelDTO travel) {
        this.travel = travel;
        setName( DateHelper.formatDate( travel.getDate() ) );
        setTooltip( travel.getDestination() );
        setId("" + travel.getId());
    }
}
