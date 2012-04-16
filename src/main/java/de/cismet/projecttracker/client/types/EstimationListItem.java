package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.EstimatedComponentCostDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps EstimationComponentCostDTO objects
 *
 * @author therter
 */
public class EstimationListItem extends ListItem {
    private EstimatedComponentCostDTO estimation;

    public EstimationListItem() {}

    public EstimationListItem(String id, String name) {
        super(id, name);
    }


    public EstimationListItem(EstimatedComponentCostDTO estimation) {
        super("" + estimation.getId(), DateHelper.formatDateTime( estimation.getCreationtime()) );
        this.estimation = estimation;
    }

    /**
     * @return the estimation
     */
    public EstimatedComponentCostDTO getEstimation() {
        return estimation;
    }

    /**
     * @param estimation the estimation to set
     */
    public void setEstimation(EstimatedComponentCostDTO estimation) {
        this.estimation = estimation;
        super.setId("" + estimation.getId());
        super.setName( DateHelper.formatDateTime( estimation.getCreationtime()) );
    }

    @Override
    public int compareTo(ListItem o) {
        if (o instanceof EstimationListItem) {
            long result = estimation.getCreationtime().getTime() - ((EstimationListItem)o).estimation.getCreationtime().getTime();
            return  (int)Math.signum(result);
        }

        return super.compareTo(o);
    }
}
