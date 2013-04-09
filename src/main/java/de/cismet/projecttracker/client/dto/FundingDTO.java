/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FundingDTO extends BasicDTO<FundingDTO> {

    //~ Instance fields --------------------------------------------------------

    private ProjectShortDTO project;
    private CompanyDTO company;
    private double fundingratio;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FundingDTO object.
     */
    public FundingDTO() {
    }

    /**
     * Creates a new FundingDTO object.
     *
     * @param  id            DOCUMENT ME!
     * @param  project       DOCUMENT ME!
     * @param  company       DOCUMENT ME!
     * @param  fundingratio  DOCUMENT ME!
     */
    public FundingDTO(final long id,
            final ProjectShortDTO project,
            final CompanyDTO company,
            final double fundingratio) {
        this.id = id;
        this.project = project;
        this.company = company;
        this.fundingratio = fundingratio;
    }

    //~ Methods ----------------------------------------------------------------

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
     * @return  the fundingratio
     */
    public double getFundingratio() {
        return fundingratio;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fundingratio  the fundingratio to set
     */
    public void setFundingratio(final double fundingratio) {
        this.fundingratio = fundingratio;
    }

    @Override
    public FundingDTO createCopy() {
        return new FundingDTO(id, project, company, fundingratio);
    }

    @Override
    public void reset(final FundingDTO obj) {
        this.id = obj.id;
        this.project = obj.project;
        this.company = obj.company;
        this.fundingratio = obj.fundingratio;
    }
}
