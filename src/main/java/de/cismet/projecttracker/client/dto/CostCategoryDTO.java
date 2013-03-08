/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import java.util.ArrayList;

/**
 *
 * @author therter
 */
public class CostCategoryDTO extends BasicDTO<CostCategoryDTO> {
    private String name;
    private ProjectDTO project;
    private String description;
    private double fundingrate;
    private double vat;
    private ArrayList<ProjectCostsDTO> projectCosts = new ArrayList<ProjectCostsDTO>(0);
    private ArrayList<WorkPackageDTO> workPackages = new ArrayList<WorkPackageDTO>(0);

    public CostCategoryDTO() {
    }

    

    public CostCategoryDTO(long id, String name, ProjectDTO project, String description, double fundingrate, double vat, ArrayList<ProjectCostsDTO> projectCosts, ArrayList<WorkPackageDTO> workPackages) {
        this.id = id;
        this.name = name;
        this.project = project;
        this.description = description;
        this.fundingrate = fundingrate;
        this.vat = vat;
        this.projectCosts = projectCosts;
        this.workPackages = workPackages;
    }



    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the project
     */
    public ProjectDTO getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(ProjectDTO project) {
        this.project = project;
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
     * @return the fundingrate
     */
    public double getFundingrate() {
        return fundingrate;
    }

    /**
     * @param fundingrate the fundingrate to set
     */
    public void setFundingrate(double fundingrate) {
        this.fundingrate = fundingrate;
    }

    /**
     * @return the vat
     */
    public double getVat() {
        return vat;
    }

    /**
     * @param vat the vat to set
     */
    public void setVat(double vat) {
        this.vat = vat;
    }

    /**
     * @return the projectCosts
     */
    public ArrayList<ProjectCostsDTO> getProjectCosts() {
        return projectCosts;
    }

    /**
     * @param projectCosts the projectCosts to set
     */
    public void setProjectCosts(ArrayList<ProjectCostsDTO> projectCosts) {
        this.projectCosts = projectCosts;
    }

    /**
     * @return the workPackages
     */
    public ArrayList<WorkPackageDTO> getWorkPackages() {
        return workPackages;
    }

    /**
     * @param workPackages the workPackages to set
     */
    public void setWorkPackages(ArrayList<WorkPackageDTO> workPackages) {
        this.workPackages = workPackages;
    }

    @Override
    public CostCategoryDTO createCopy() {
        return new CostCategoryDTO(id, name, project, description, fundingrate, vat, projectCosts, workPackages);
    }

    @Override
    public void reset(CostCategoryDTO obj) {
        this.id = obj.getId();
        this.name = obj.getName();
        this.project = obj.getProject();
        this.description = obj.getDescription();
        this.fundingrate = obj.getFundingrate();
        this.vat = obj.getVat();
        this.projectCosts = obj.getProjectCosts();
        this.workPackages = obj.getWorkPackages();
    }
}
