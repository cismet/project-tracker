/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class RealOverheadDTO extends BasicDTO<RealOverheadDTO> {

    //~ Instance fields --------------------------------------------------------

    private CompanyDTO company;
    private double overheadratio;
    private Date validfrom;
    private Date validto;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RealOverheadDTO object.
     */
    public RealOverheadDTO() {
    }

    /**
     * Creates a new RealOverheadDTO object.
     *
     * @param  id             DOCUMENT ME!
     * @param  company        DOCUMENT ME!
     * @param  overheadratio  DOCUMENT ME!
     * @param  validfrom      DOCUMENT ME!
     * @param  validto        DOCUMENT ME!
     */
    public RealOverheadDTO(final long id,
            final CompanyDTO company,
            final double overheadratio,
            final Date validfrom,
            final Date validto) {
        this.id = id;
        this.company = company;
        this.overheadratio = overheadratio;
        this.validfrom = validfrom;
        this.validto = validto;
    }

    //~ Methods ----------------------------------------------------------------

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
     * @return  the overheadratio
     */
    public double getOverheadratio() {
        return overheadratio;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  overheadratio  the overheadratio to set
     */
    public void setOverheadratio(final double overheadratio) {
        this.overheadratio = overheadratio;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the validfrom
     */
    public Date getValidfrom() {
        return validfrom;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  validfrom  the validfrom to set
     */
    public void setValidfrom(final Date validfrom) {
        this.validfrom = validfrom;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the validto
     */
    public Date getValidto() {
        return validto;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  validto  the validto to set
     */
    public void setValidto(final Date validto) {
        this.validto = validto;
    }

    @Override
    public RealOverheadDTO createCopy() {
        return new RealOverheadDTO(id, company, overheadratio, validfrom, validto);
    }

    @Override
    public void reset(final RealOverheadDTO obj) {
        this.id = obj.getId();
        this.company = obj.getCompany();
        this.overheadratio = obj.getOverheadratio();
        this.validfrom = obj.getValidfrom();
        this.validto = obj.getValidto();
    }
}
