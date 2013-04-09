/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.TravelDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps TravelDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TravelListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private TravelDTO travel;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TravelListItem object.
     *
     * @param  travel  DOCUMENT ME!
     */
    public TravelListItem(final TravelDTO travel) {
        this.travel = travel;
        setName(DateHelper.formatDate(travel.getDate()));
        setTooltip(travel.getDestination());
        setId("" + travel.getId());
    }

    /**
     * Creates a new TravelListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public TravelListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the staff
     */
    public TravelDTO getTravel() {
        return travel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  travel  staff the staff to set
     */
    public void setTravel(final TravelDTO travel) {
        this.travel = travel;
        setName(DateHelper.formatDate(travel.getDate()));
        setTooltip(travel.getDestination());
        setId("" + travel.getId());
    }
}
