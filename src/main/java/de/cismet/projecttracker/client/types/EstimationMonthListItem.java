/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import com.google.gwt.core.client.GWT;

import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.dto.EstimatedComponentCostMonthDTO;

/**
 * A ListItem implementation that wraps EstimationComponentCostMonthDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class EstimationMonthListItem extends ListItem {

    //~ Static fields/initializers ---------------------------------------------

    private static final MessageConstants MESSAGES = (MessageConstants)GWT.create(MessageConstants.class);

    //~ Instance fields --------------------------------------------------------

    private EstimatedComponentCostMonthDTO estimation;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EstimationMonthListItem object.
     */
    public EstimationMonthListItem() {
    }

    /**
     * Creates a new EstimationMonthListItem object.
     *
     * @param  estimation  DOCUMENT ME!
     */
    public EstimationMonthListItem(final EstimatedComponentCostMonthDTO estimation) {
        super("" + estimation.getId(), getEstimationName(estimation));
        this.estimation = estimation;
    }

    /**
     * Creates a new EstimationMonthListItem object.
     *
     * @param  id    DOCUMENT ME!
     * @param  name  DOCUMENT ME!
     */
    public EstimationMonthListItem(final String id, final String name) {
        super(id, name);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the etimation
     */
    public EstimatedComponentCostMonthDTO getEstimation() {
        return estimation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  estimation  the estimation to set
     */
    public void setEstimation(final EstimatedComponentCostMonthDTO estimation) {
        this.estimation = estimation;
        super.setId("" + estimation.getId());
        super.setName(getEstimationName(estimation));
    }

    /**
     * this method must be static to allow a call of this method before the invocation of the super constructor.
     *
     * @param   estimation  DOCUMENT ME!
     *
     * @return  the name of the given estimation.
     */
    private static String getEstimationName(final EstimatedComponentCostMonthDTO estimation) {
        return estimation.getMonth() + ". " + MESSAGES.monthString();
    }

    @Override
    public int compareTo(final ListItem o) {
        if (o instanceof EstimationMonthListItem) {
            return estimation.getMonth() - ((EstimationMonthListItem)o).getEstimation().getMonth();
        }

        return super.compareTo(o);
    }
}
