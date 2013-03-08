package de.cismet.projecttracker.client.dto;

import java.util.Date;

/**
 *
 * @author therter
 */
public class RealOverheadDTO extends BasicDTO<RealOverheadDTO> {
     private CompanyDTO company;
     private double overheadratio;
     private Date validfrom;
     private Date validto;

    public RealOverheadDTO() {
    }


     

    public RealOverheadDTO(long id, CompanyDTO company, double overheadratio, Date validfrom, Date validto) {
        this.id = id;
        this.company = company;
        this.overheadratio = overheadratio;
        this.validfrom = validfrom;
        this.validto = validto;
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
     * @return the overheadratio
     */
    public double getOverheadratio() {
        return overheadratio;
    }

    /**
     * @param overheadratio the overheadratio to set
     */
    public void setOverheadratio(double overheadratio) {
        this.overheadratio = overheadratio;
    }

    /**
     * @return the validfrom
     */
    public Date getValidfrom() {
        return validfrom;
    }

    /**
     * @param validfrom the validfrom to set
     */
    public void setValidfrom(Date validfrom) {
        this.validfrom = validfrom;
    }

    /**
     * @return the validto
     */
    public Date getValidto() {
        return validto;
    }

    /**
     * @param validto the validto to set
     */
    public void setValidto(Date validto) {
        this.validto = validto;
    }

    @Override
    public RealOverheadDTO createCopy() {
        return new RealOverheadDTO(id, company, overheadratio, validfrom, validto);
    }

    @Override
    public void reset(RealOverheadDTO obj) {
        this.id = obj.getId();
        this.company = obj.getCompany();
        this.overheadratio = obj.getOverheadratio();
        this.validfrom = obj.getValidfrom();
        this.validto = obj.getValidto();
    }
}
