package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * A ListItem implementation that wraps ContractDTO objects
 *
 * @author therter
 */
public class ContractListItem extends ListItem {
    private ContractDTO contract;

    public ContractListItem(){
    }

    public ContractListItem(ContractDTO contract) {
        this.contract = contract;
        setId("" + contract.getId());
        setName(getContractName());
    }

    /**
     * @return the contract
     */
    public ContractDTO getContract() {
        return contract;
    }

    /**
     * @param contract the contract to set
     */
    public void setContract(ContractDTO contract) {
        this.contract = contract;
        setId("" + contract.getId());
        setName(getContractName());
    }


    private String getContractName() {
        String label = DateHelper.formatDate( contract.getFromdate() );
        if (contract.getTodate() != null) {
            label += " - " + DateHelper.formatDate(contract.getTodate());
        }

        return label;
    }
}
