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
public class ContractDTO extends BasicDTO<ContractDTO> {
     private StaffDTO staff;
     private CompanyDTO company;
     private Date fromdate;
     private Date todate;
     private double whow;
     private ArrayList<ContractDocumentDTO> contractDocuments = new ArrayList<ContractDocumentDTO>(0);

    public ContractDTO() {
    }

     
    public ContractDTO(long id, StaffDTO staff, CompanyDTO company, Date fromdate, Date todate, double whow, ArrayList<ContractDocumentDTO> contractDocuments) {
        this.id = id;
        this.staff = staff;
        this.company = company;
        this.fromdate = fromdate;
        this.todate = todate;
        this.whow = whow;
        this.contractDocuments = contractDocuments;
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
     * @return the company
     */
    public CompanyDTO getCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    /**
     * @return the fromdate
     */
    public Date getFromdate() {
        return fromdate;
    }

    /**
     * @param fromdate the fromdate to set
     */
    public void setFromdate(Date fromdate) {
        this.fromdate = fromdate;
    }

    /**
     * @return the todate
     */
    public Date getTodate() {
        return todate;
    }

    /**
     * @param todate the todate to set
     */
    public void setTodate(Date todate) {
        this.todate = todate;
    }

    /**
     * @return the whow
     */
    public double getWhow() {
        return whow;
    }

    /**
     * @param whow the whow to set
     */
    public void setWhow(double whow) {
        this.whow = whow;
    }

    /**
     * @return the contractDocuments
     */
    public ArrayList<ContractDocumentDTO> getContractDocuments() {
        return contractDocuments;
    }

    /**
     * @param contractDocuments the contractDocuments to set
     */
    public void setContractDocuments(ArrayList<ContractDocumentDTO> contractDocuments) {
        this.contractDocuments = contractDocuments;
    }

    
    @Override
    public ContractDTO createCopy() {
        return new ContractDTO(id, staff, company, fromdate, todate, whow, contractDocuments);
    }



    @Override
    public void reset(ContractDTO obj) {
        this.id = obj.id;
        this.staff = obj.staff;
        this.company = obj.company;
        this.fromdate = obj.fromdate;
        this.todate = obj.todate;
        this.whow = obj.whow;
        this.contractDocuments = obj.contractDocuments;
    }


}
