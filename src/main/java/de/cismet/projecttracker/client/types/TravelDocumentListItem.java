package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.TravelDocumentDTO;


/**
 * A ListItem implementation that wraps TravelDocumentDTO objects
 *
 * @author therter
 */
public class TravelDocumentListItem extends ListItem {
    private TravelDocumentDTO travelDocument;

    public TravelDocumentListItem(TravelDocumentDTO travelDocument) {
        this.travelDocument = travelDocument;
        setName( travelDocument.getDocumentname() );
        setId("" + travelDocument.getId());
    }

    public TravelDocumentListItem(String id, String name) {
        super(id, name);
    }

    /**
     * @return the staff
     */
    public TravelDocumentDTO getTravelDocument() {
        return travelDocument;
    }

    /**
     * @param staff the staff to set
     */
    public void setTravelDocument(TravelDocumentDTO travelDocument) {
        this.travelDocument = travelDocument;
        setName( travelDocument.getDocumentname() );
        setId("" + travelDocument.getId());
    }
}
