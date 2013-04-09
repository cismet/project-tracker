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
public class ContractDTO extends BasicDTO<ContractDTO> {

    //~ Instance fields --------------------------------------------------------

    private StaffDTO staff;
    private CompanyDTO company;
    private Date fromdate;
    private Date todate;
    private double whow;
    private int vacation;
    private ArrayList<ContractDocumentDTO> contractDocuments = new ArrayList<ContractDocumentDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ContractDTO object.
     */
    public ContractDTO() {
    }

    /**
     * Creates a new ContractDTO object.
     *
     * @param  id                 DOCUMENT ME!
     * @param  staff              DOCUMENT ME!
     * @param  company            DOCUMENT ME!
     * @param  fromdate           DOCUMENT ME!
     * @param  todate             DOCUMENT ME!
     * @param  whow               DOCUMENT ME!
     * @param  contractDocuments  DOCUMENT ME!
     * @param  vacation           DOCUMENT ME!
     */
    public ContractDTO(final long id,
            final StaffDTO staff,
            final CompanyDTO company,
            final Date fromdate,
            final Date todate,
            final double whow,
            final ArrayList<ContractDocumentDTO> contractDocuments,
            final int vacation) {
        this.id = id;
        this.staff = staff;
        this.company = company;
        this.fromdate = fromdate;
        this.todate = todate;
        this.whow = whow;
        this.vacation = vacation;
        this.contractDocuments = contractDocuments;
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
     * @return  the company
     */
    public CompanyDTO getCompany() {
        return company;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  company  the company to set
     */
    public void setCompany(final CompanyDTO company) {
        this.company = company;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the fromdate
     */
    public Date getFromdate() {
        return fromdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fromdate  the fromdate to set
     */
    public void setFromdate(final Date fromdate) {
        this.fromdate = fromdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the todate
     */
    public Date getTodate() {
        return todate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  todate  the todate to set
     */
    public void setTodate(final Date todate) {
        this.todate = todate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the whow
     */
    public double getWhow() {
        return whow;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  whow  the whow to set
     */
    public void setWhow(final double whow) {
        this.whow = whow;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getVacation() {
        return vacation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vacation  DOCUMENT ME!
     */
    public void setVacation(final int vacation) {
        this.vacation = vacation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the contractDocuments
     */
    public ArrayList<ContractDocumentDTO> getContractDocuments() {
        return contractDocuments;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  contractDocuments  the contractDocuments to set
     */
    public void setContractDocuments(final ArrayList<ContractDocumentDTO> contractDocuments) {
        this.contractDocuments = contractDocuments;
    }

    @Override
    public ContractDTO createCopy() {
        return new ContractDTO(id, staff, company, fromdate, todate, whow, contractDocuments, vacation);
    }

    @Override
    public void reset(final ContractDTO obj) {
        this.id = obj.id;
        this.staff = obj.staff;
        this.company = obj.company;
        this.fromdate = obj.fromdate;
        this.todate = obj.todate;
        this.whow = obj.whow;
        this.vacation = obj.vacation;
        this.contractDocuments = obj.contractDocuments;
    }
}
