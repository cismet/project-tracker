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

import java.util.ArrayList;
import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TravelDTO extends BasicDTO<TravelDTO> {

    //~ Instance fields --------------------------------------------------------

    private StaffDTO staff;
    private ProjectShortDTO project;
    private double grossprice;
    private double netprice;
    private double allowablevat;
    private Date date;
    private String destination;
    private String description;
    private String storagelocation;
    private Date paymentdate;
    private ArrayList<TravelDocumentDTO> traveldocuments = new ArrayList<TravelDocumentDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TravelDTO object.
     */
    public TravelDTO() {
    }

    /**
     * Creates a new TravelDTO object.
     *
     * @param  id               DOCUMENT ME!
     * @param  staff            DOCUMENT ME!
     * @param  project          DOCUMENT ME!
     * @param  grossprice       DOCUMENT ME!
     * @param  netprice         DOCUMENT ME!
     * @param  allowablevat     DOCUMENT ME!
     * @param  date             DOCUMENT ME!
     * @param  destination      DOCUMENT ME!
     * @param  description      DOCUMENT ME!
     * @param  storagelocation  DOCUMENT ME!
     * @param  paymentdate      DOCUMENT ME!
     * @param  traveldocuments  DOCUMENT ME!
     */
    public TravelDTO(final long id,
            final StaffDTO staff,
            final ProjectShortDTO project,
            final double grossprice,
            final double netprice,
            final double allowablevat,
            final Date date,
            final String destination,
            final String description,
            final String storagelocation,
            final Date paymentdate,
            final ArrayList<TravelDocumentDTO> traveldocuments) {
        this.id = id;
        this.staff = staff;
        this.project = project;
        this.grossprice = grossprice;
        this.netprice = netprice;
        this.allowablevat = allowablevat;
        this.date = date;
        this.destination = destination;
        this.description = description;
        this.storagelocation = storagelocation;
        this.paymentdate = paymentdate;
        this.traveldocuments = traveldocuments;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff  the staff to set
     */
    public void setStaff(final StaffDTO staff) {
        this.staff = staff;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the project
     */
    public ProjectShortDTO getProject() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  project  the project to set
     */
    public void setProject(final ProjectShortDTO project) {
        this.project = project;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the grossprice
     */
    public double getGrossprice() {
        return grossprice;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  grossprice  the grossprice to set
     */
    public void setGrossprice(final double grossprice) {
        this.grossprice = grossprice;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the netprice
     */
    public double getNetprice() {
        return netprice;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  netprice  the netprice to set
     */
    public void setNetprice(final double netprice) {
        this.netprice = netprice;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the allowablevat
     */
    public double getAllowablevat() {
        return allowablevat;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  allowablevat  the allowablevat to set
     */
    public void setAllowablevat(final double allowablevat) {
        this.allowablevat = allowablevat;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  date  the date to set
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  destination  the destination to set
     */
    public void setDestination(final String destination) {
        this.destination = destination;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the storagelocation
     */
    public String getStoragelocation() {
        return storagelocation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  storagelocation  the storagelocation to set
     */
    public void setStoragelocation(final String storagelocation) {
        this.storagelocation = storagelocation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the paymentdate
     */
    public Date getPaymentdate() {
        return paymentdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  paymentdate  the paymentdate to set
     */
    public void setPaymentdate(final Date paymentdate) {
        this.paymentdate = paymentdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the traveldocuments
     */
    public ArrayList<TravelDocumentDTO> getTraveldocuments() {
        return traveldocuments;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  traveldocuments  the traveldocuments to set
     */
    public void setTraveldocuments(final ArrayList<TravelDocumentDTO> traveldocuments) {
        this.traveldocuments = traveldocuments;
    }

    @Override
    public TravelDTO createCopy() {
        return new TravelDTO(
                id,
                staff,
                project,
                grossprice,
                netprice,
                allowablevat,
                date,
                destination,
                description,
                storagelocation,
                paymentdate,
                traveldocuments);
    }

    @Override
    public void reset(final TravelDTO obj) {
        this.id = obj.id;
        this.staff = obj.staff;
        this.project = obj.project;
        this.grossprice = obj.grossprice;
        this.netprice = obj.netprice;
        this.allowablevat = obj.allowablevat;
        this.date = obj.date;
        this.destination = obj.destination;
        this.description = obj.description;
        this.storagelocation = obj.storagelocation;
        this.paymentdate = obj.paymentdate;
        this.traveldocuments = obj.traveldocuments;
    }
}
