/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.RealOverheadDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps RealOverheadDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class OverheadListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private RealOverheadDTO overhead;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new OverheadListItem object.
     */
    public OverheadListItem() {
    }

    /**
     * Creates a new OverheadListItem object.
     *
     * @param  overhead  DOCUMENT ME!
     */
    public OverheadListItem(final RealOverheadDTO overhead) {
        this.overhead = overhead;
        setName(getOverheadName());
        setId("" + overhead.getId());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the real overhead
     */
    public RealOverheadDTO getOverhead() {
        return overhead;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  overhead  the overhead to set
     */
    public void setOverhead(final RealOverheadDTO overhead) {
        this.overhead = overhead;
        setId("" + overhead.getId());
        setName(getOverheadName());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getOverheadName() {
        String label = DateHelper.formatDate(overhead.getValidfrom());
        if (overhead.getValidto() != null) {
            label += " - " + DateHelper.formatDate(overhead.getValidto());
        }

        return label;
    }
}
