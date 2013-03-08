/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

/**
 *
 * @author therter
 */
public class TravelDocumentDTO extends BasicDTO<TravelDocumentDTO> {
     private TravelDTO travel;
     private String documentname;
     private String mimetype;

    public TravelDocumentDTO() {
    }

     

    public TravelDocumentDTO(long id, TravelDTO travel, String documentname, String mimetype) {
        this.id = id;
        this.travel = travel;
        this.documentname = documentname;
        this.mimetype = mimetype;
    }

    /**
     * @return the travel
     */
    public TravelDTO getTravel() {
        return travel;
    }

    /**
     * @param travel the travel to set
     */
    public void setTravel(TravelDTO travel) {
        this.travel = travel;
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
    public TravelDocumentDTO createCopy() {
        return new TravelDocumentDTO(id, travel, documentname, mimetype);
    }

    @Override
    public void reset(TravelDocumentDTO obj) {
        this.id = obj.id;
        this.travel = obj.travel;
        this.documentname = obj.documentname;
        this.mimetype = obj.mimetype;
    }


}
