package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.CompanyDTO;


/**
 * A ListItem implementation that wraps CompanyDTO objects
 * @author therter
 */
public class CompanyListItem extends ListItem {
    private CompanyDTO company;

    public CompanyListItem(CompanyDTO company) {
        this.company = company;
        setName(company.getName());
        setId("" + company.getId());
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
        setName(company.getName());
        setId("" + company.getId());
    }

}