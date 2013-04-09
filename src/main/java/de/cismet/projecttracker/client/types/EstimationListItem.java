/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.EstimatedComponentCostDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps EstimationComponentCostDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class EstimationListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private EstimatedComponentCostDTO estimation;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EstimationListItem object.
     */
    public EstimationListItem() {
    }

    /**
     * Creates a new EstimationListItem object.
     *
     * @param  estimation  DOCUMENT ME!
     */
    public EstimationListItem(final EstimatedComponentCostDTO estimation) {
        super("" + estimation.getId(), DateHelper.formatDateTime(estimation.getCreationtime()));
        this.estimation = estimation;
    }

    /**
     * Creates a new EstimationListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public EstimationListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the estimation
     */
    public EstimatedComponentCostDTO getEstimation() {
        return estimation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  estimation  the estimation to set
     */
    public void setEstimation(final EstimatedComponentCostDTO estimation) {
        this.estimation = estimation;
        super.setId("" + estimation.getId());
        super.setName(DateHelper.formatDateTime(estimation.getCreationtime()));
    }

    @Override
    public int compareTo(final ListItem o) {
        if (o instanceof EstimationListItem) {
            final long result = estimation.getCreationtime().getTime()
                        - ((EstimationListItem)o).estimation.getCreationtime().getTime();
            return (int)Math.signum(result);
        }

        return super.compareTo(o);
    }
}
