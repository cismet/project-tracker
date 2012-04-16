package de.cismet.projecttracker.client.dto;

/**
 *
 * @author therter
 */
public class FundingDTO extends BasicDTO<FundingDTO> {
    private ProjectShortDTO project;
    private CompanyDTO company;
    private double fundingratio;

    public FundingDTO() {
    }



    public FundingDTO(long id, ProjectShortDTO project, CompanyDTO company, double fundingratio) {
        this.id = id;
        this.project = project;
        this.company = company;
        this.fundingratio = fundingratio;
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
     * @return the fundingratio
     */
    public double getFundingratio() {
        return fundingratio;
    }

    /**
     * @param fundingratio the fundingratio to set
     */
    public void setFundingratio(double fundingratio) {
        this.fundingratio = fundingratio;
    }

    @Override
    public FundingDTO createCopy() {
        return new FundingDTO(id, project, company, fundingratio);
   }

    @Override
    public void reset(FundingDTO obj) {
        this.id = obj.id;
        this.project = obj.project;
        this.company = obj.company;
        this.fundingratio = obj.fundingratio;
    }
}
