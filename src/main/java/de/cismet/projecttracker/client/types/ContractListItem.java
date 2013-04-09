/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps ContractDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ContractListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private ContractDTO contract;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ContractListItem object.
     */
    public ContractListItem() {
    }

    /**
     * Creates a new ContractListItem object.
     *
     * @param  contract  DOCUMENT ME!
     */
    public ContractListItem(final ContractDTO contract) {
        this.contract = contract;
        setId("" + contract.getId());
        setName(getContractName());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the contract
     */
    public ContractDTO getContract() {
        return contract;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  contract  the contract to set
     */
    public void setContract(final ContractDTO contract) {
        this.contract = contract;
        setId("" + contract.getId());
        setName(getContractName());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getContractName() {
        String label = DateHelper.formatDate(contract.getFromdate());
        if (contract.getTodate() != null) {
            label += " - " + DateHelper.formatDate(contract.getTodate());
        }

        return label;
    }
}
