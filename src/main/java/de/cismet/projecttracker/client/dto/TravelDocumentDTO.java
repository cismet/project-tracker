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
public class TravelDocumentDTO extends BasicDTO<TravelDocumentDTO> {

    //~ Instance fields --------------------------------------------------------

    private TravelDTO travel;
    private String documentname;
    private String mimetype;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TravelDocumentDTO object.
     */
    public TravelDocumentDTO() {
    }

    /**
     * Creates a new TravelDocumentDTO object.
     *
     * @param  id            DOCUMENT ME!
     * @param  travel        DOCUMENT ME!
     * @param  documentname  DOCUMENT ME!
     * @param  mimetype      DOCUMENT ME!
     */
    public TravelDocumentDTO(final long id, final TravelDTO travel, final String documentname, final String mimetype) {
        this.id = id;
        this.travel = travel;
        this.documentname = documentname;
        this.mimetype = mimetype;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the travel
     */
    public TravelDTO getTravel() {
        return travel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  travel  the travel to set
     */
    public void setTravel(final TravelDTO travel) {
        this.travel = travel;
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
    public TravelDocumentDTO createCopy() {
        return new TravelDocumentDTO(id, travel, documentname, mimetype);
    }

    @Override
    public void reset(final TravelDocumentDTO obj) {
        this.id = obj.id;
        this.travel = obj.travel;
        this.documentname = obj.documentname;
        this.mimetype = obj.mimetype;
    }
}
