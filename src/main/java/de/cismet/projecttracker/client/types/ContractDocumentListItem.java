package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ContractDocumentDTO;

/**
 * A ListItem implementation that wraps ContractDocumentDTO objects
 *
 * @author therter
 */
public class ContractDocumentListItem extends ListItem {
    private ContractDocumentDTO document;

    public ContractDocumentListItem(ContractDocumentDTO document) {
        this.document = document;
        setName("" + document.getDocumentname());
        setId("" + document.getId());
    }

    /**
     * @return the contract document
     */
    public ContractDocumentDTO getDocument() {
        return document;
    }

    /**
     * @param document the contract document to set
     */
    public void setDocument(ContractDocumentDTO document) {
        this.document = document;
        setName("" + document.getDocumentname());
        setId("" + document.getId());
    }

}