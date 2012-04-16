/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import java.util.Date;
import java.util.ArrayList;

/**
 *
 * @author therter
 */
public class TravelDTO extends BasicDTO<TravelDTO> {
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


    public TravelDTO() {
    }



    public TravelDTO(long id, StaffDTO staff, ProjectShortDTO project, double grossprice, double netprice, double allowablevat, Date date, String destination, String description, String storagelocation, Date paymentdate, ArrayList<TravelDocumentDTO> traveldocuments) {
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

    /**
     * @return the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    /**
     * @return the project
     */
    public ProjectShortDTO getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(ProjectShortDTO project) {
        this.project = project;
    }

    /**
     * @return the grossprice
     */
    public double getGrossprice() {
        return grossprice;
    }

    /**
     * @param grossprice the grossprice to set
     */
    public void setGrossprice(double grossprice) {
        this.grossprice = grossprice;
    }

    /**
     * @return the netprice
     */
    public double getNetprice() {
        return netprice;
    }

    /**
     * @param netprice the netprice to set
     */
    public void setNetprice(double netprice) {
        this.netprice = netprice;
    }

    /**
     * @return the allowablevat
     */
    public double getAllowablevat() {
        return allowablevat;
    }

    /**
     * @param allowablevat the allowablevat to set
     */
    public void setAllowablevat(double allowablevat) {
        this.allowablevat = allowablevat;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * @param destination the destination to set
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the storagelocation
     */
    public String getStoragelocation() {
        return storagelocation;
    }

    /**
     * @param storagelocation the storagelocation to set
     */
    public void setStoragelocation(String storagelocation) {
        this.storagelocation = storagelocation;
    }

    /**
     * @return the paymentdate
     */
    public Date getPaymentdate() {
        return paymentdate;
    }

    /**
     * @param paymentdate the paymentdate to set
     */
    public void setPaymentdate(Date paymentdate) {
        this.paymentdate = paymentdate;
    }

    /**
     * @return the traveldocuments
     */
    public ArrayList<TravelDocumentDTO> getTraveldocuments() {
        return traveldocuments;
    }

    /**
     * @param traveldocuments the traveldocuments to set
     */
    public void setTraveldocuments(ArrayList<TravelDocumentDTO> traveldocuments) {
        this.traveldocuments = traveldocuments;
    }

    @Override
    public TravelDTO createCopy() {
        return new TravelDTO(id, staff, project, grossprice, netprice, allowablevat, date, destination, description, storagelocation, paymentdate, traveldocuments);
    }

    @Override
    public void reset(TravelDTO obj) {
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
