/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.dto;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ContractDocumentDTO extends BasicDTO<ContractDocumentDTO> implements Comparable<ContractDocumentDTO> {

    //~ Instance fields --------------------------------------------------------

    private ContractDTO contract;
    private String documentname;
    private String mimetype;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ContractDocumentDTO object.
     */
    public ContractDocumentDTO() {
    }

    /**
     * Creates a new ContractDocumentDTO object.
     *
     * @param  id            DOCUMENT ME!
     * @param  contract      DOCUMENT ME!
     * @param  documentname  DOCUMENT ME!
     * @param  mimetype      DOCUMENT ME!
     */
    public ContractDocumentDTO(final long id,
            final ContractDTO contract,
            final String documentname,
            final String mimetype) {
        this.id = id;
        this.contract = contract;
        this.documentname = documentname;
        this.mimetype = mimetype;
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
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the documentname
     */
    public String getDocumentname() {
        return documentname;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  documentname  the documentname to set
     */
    public void setDocumentname(final String documentname) {
        this.documentname = documentname;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mimetype  the mimetype to set
     */
    public void setMimetype(final String mimetype) {
        this.mimetype = mimetype;
    }

    @Override
    public ContractDocumentDTO createCopy() {
        return new ContractDocumentDTO(id, contract, documentname, mimetype);
    }

    @Override
    public void reset(final ContractDocumentDTO obj) {
        this.id = obj.getId();
        this.contract = obj.getContract();
        this.documentname = obj.getDocumentname();
        this.mimetype = obj.getMimetype();
    }

    @Override
    public int compareTo(final ContractDocumentDTO o) {
        return documentname.compareTo(o.documentname);
    }
}
