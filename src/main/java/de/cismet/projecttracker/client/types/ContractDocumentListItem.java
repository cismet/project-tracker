/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ContractDocumentDTO;

/**
 * A ListItem implementation that wraps ContractDocumentDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ContractDocumentListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private ContractDocumentDTO document;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ContractDocumentListItem object.
     *
     * @param  document  DOCUMENT ME!
     */
    public ContractDocumentListItem(final ContractDocumentDTO document) {
        this.document = document;
        setName("" + document.getDocumentname());
        setId("" + document.getId());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the contract document
     */
    public ContractDocumentDTO getDocument() {
        return document;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  document  the contract document to set
     */
    public void setDocument(final ContractDocumentDTO document) {
        this.document = document;
        setName("" + document.getDocumentname());
        setId("" + document.getId());
    }
}
