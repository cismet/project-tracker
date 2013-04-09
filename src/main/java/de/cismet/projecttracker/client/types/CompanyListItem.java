/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.CompanyDTO;

/**
 * A ListItem implementation that wraps CompanyDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class CompanyListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private CompanyDTO company;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CompanyListItem object.
     *
     * @param  company  DOCUMENT ME!
     */
    public CompanyListItem(final CompanyDTO company) {
        this.company = company;
        setName(company.getName());
        setId("" + company.getId());
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
        setName(company.getName());
        setId("" + company.getId());
    }
}
