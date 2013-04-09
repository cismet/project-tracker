/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.TravelDocumentDTO;

/**
 * A ListItem implementation that wraps TravelDocumentDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TravelDocumentListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private TravelDocumentDTO travelDocument;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TravelDocumentListItem object.
     *
     * @param  travelDocument  DOCUMENT ME!
     */
    public TravelDocumentListItem(final TravelDocumentDTO travelDocument) {
        this.travelDocument = travelDocument;
        setName(travelDocument.getDocumentname());
        setId("" + travelDocument.getId());
    }

    /**
     * Creates a new TravelDocumentListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public TravelDocumentListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the staff
     */
    public TravelDocumentDTO getTravelDocument() {
        return travelDocument;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  travelDocument  staff the staff to set
     */
    public void setTravelDocument(final TravelDocumentDTO travelDocument) {
        this.travelDocument = travelDocument;
        setName(travelDocument.getDocumentname());
        setId("" + travelDocument.getId());
    }
}
