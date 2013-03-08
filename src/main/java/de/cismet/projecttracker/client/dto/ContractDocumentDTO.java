/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

/**
 *
 * @author therter
 */
public class ContractDocumentDTO extends BasicDTO<ContractDocumentDTO> implements Comparable<ContractDocumentDTO>{
     private ContractDTO contract;
     private String documentname;
     private String mimetype;

    public ContractDocumentDTO() {
    }

     

    public ContractDocumentDTO(long id, ContractDTO contract, String documentname, String mimetype) {
        this.id = id;
        this.contract = contract;
        this.documentname = documentname;
        this.mimetype = mimetype;
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
    }

    /**
     * @return the documentname
     */
    public String getDocumentname() {
        return documentname;
    }

    /**
     * @param documentname the documentname to set
     */
    public void setDocumentname(String documentname) {
        this.documentname = documentname;
    }

    /**
     * @return the mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * @param mimetype the mimetype to set
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    @Override
    public ContractDocumentDTO createCopy() {
        return new ContractDocumentDTO(id, contract, documentname, mimetype);
    }

    @Override
    public void reset(ContractDocumentDTO obj) {
        this.id = obj.getId();
        this.contract = obj.getContract();
        this.documentname = obj.getDocumentname();
        this.mimetype = obj.getMimetype();
    }

    @Override
    public int compareTo(ContractDocumentDTO o) {
        return documentname.compareTo(o.documentname);
    }
}
