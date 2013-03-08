package de.cismet.projecttracker.client.types;

import com.google.gwt.core.client.GWT;
import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.dto.EstimatedComponentCostMonthDTO;


/**
 * A ListItem implementation that wraps EstimationComponentCostMonthDTO objects
 *
 * @author therter
 */
public class EstimationMonthListItem extends ListItem {
    private final static MessageConstants MESSAGES = (MessageConstants)GWT.create(MessageConstants.class);
    private EstimatedComponentCostMonthDTO estimation;

    public EstimationMonthListItem() {}

    public EstimationMonthListItem(String id, String name) {
        super(id, name);
    }


    public EstimationMonthListItem(EstimatedComponentCostMonthDTO estimation) {
        super( "" + estimation.getId(), getEstimationName(estimation) );
        this.estimation = estimation;
    }


    /**
     * @return the etimation
     */
    public EstimatedComponentCostMonthDTO getEstimation() {
        return estimation;
    }


    /**
     * @param estimation the estimation to set
     */
    public void setEstimation(EstimatedComponentCostMonthDTO estimation) {
        this.estimation = estimation;
        super.setId("" + estimation.getId());
        super.setName( getEstimationName(estimation) );
    }


    /**
     * this method must be static to allow a call of this method before the invocation of the super constructor
     * @param estimation
     * @return the name of the given estimation.
     */
    private static String getEstimationName(EstimatedComponentCostMonthDTO estimation) {
        return estimation.getMonth() + ". " + MESSAGES.monthString();
    }


    @Override
    public int compareTo(ListItem o) {
        if (o instanceof EstimationMonthListItem) {
            return estimation.getMonth() - ((EstimationMonthListItem)o).getEstimation().getMonth();
        }

        return super.compareTo(o);
    }
}
