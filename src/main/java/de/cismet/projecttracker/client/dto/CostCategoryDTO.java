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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
@JsonIdentityInfo(
    generator = ObjectIdGenerators.IntSequenceGenerator.class,
    property = "@JsonCostCategoryId"
)
public class CostCategoryDTO extends BasicDTO<CostCategoryDTO> {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private ProjectDTO project;
    private String description;
    private double fundingrate;
    private double vat;
    @JsonIgnore
    private ArrayList<ProjectCostsDTO> projectCosts = new ArrayList<ProjectCostsDTO>(0);
    @JsonIgnore
    private ArrayList<WorkPackageDTO> workPackages = new ArrayList<WorkPackageDTO>(0);

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CostCategoryDTO object.
     */
    public CostCategoryDTO() {
    }

    /**
     * Creates a new CostCategoryDTO object.
     *
     * @param  id            DOCUMENT ME!
     * @param  name          DOCUMENT ME!
     * @param  project       DOCUMENT ME!
     * @param  description   DOCUMENT ME!
     * @param  fundingrate   DOCUMENT ME!
     * @param  vat           DOCUMENT ME!
     * @param  projectCosts  DOCUMENT ME!
     * @param  workPackages  DOCUMENT ME!
     */
    public CostCategoryDTO(final long id,
            final String name,
            final ProjectDTO project,
            final String description,
            final double fundingrate,
            final double vat,
            final ArrayList<ProjectCostsDTO> projectCosts,
            final ArrayList<WorkPackageDTO> workPackages) {
        this.id = id;
        this.name = name;
        this.project = project;
        this.description = description;
        this.fundingrate = fundingrate;
        this.vat = vat;
        this.projectCosts = projectCosts;
        this.workPackages = workPackages;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the project
     */
    public ProjectDTO getProject() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  project  the project to set
     */
    public void setProject(final ProjectDTO project) {
        this.project = project;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  description  the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the fundingrate
     */
    public double getFundingrate() {
        return fundingrate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fundingrate  the fundingrate to set
     */
    public void setFundingrate(final double fundingrate) {
        this.fundingrate = fundingrate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the vat
     */
    public double getVat() {
        return vat;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  vat  the vat to set
     */
    public void setVat(final double vat) {
        this.vat = vat;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the projectCosts
     */
    public ArrayList<ProjectCostsDTO> getProjectCosts() {
        return projectCosts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectCosts  the projectCosts to set
     */
    public void setProjectCosts(final ArrayList<ProjectCostsDTO> projectCosts) {
        this.projectCosts = projectCosts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workPackages
     */
    public ArrayList<WorkPackageDTO> getWorkPackages() {
        return workPackages;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workPackages  the workPackages to set
     */
    public void setWorkPackages(final ArrayList<WorkPackageDTO> workPackages) {
        this.workPackages = workPackages;
    }

    @Override
    public CostCategoryDTO createCopy() {
        return new CostCategoryDTO(id, name, project, description, fundingrate, vat, projectCosts, workPackages);
    }

    @Override
    public void reset(final CostCategoryDTO obj) {
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
