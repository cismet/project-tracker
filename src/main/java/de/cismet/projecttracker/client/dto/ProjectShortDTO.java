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
 *
 * @author therter
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ProjectShortDTO extends BasicDTO<BasicDTO> implements Comparable<ProjectShortDTO> {

    protected String name;
    protected String description;
    protected double overheadratio;
    protected ProjectCategoryDTO projectCategory;
    @JsonIgnore
    protected ArrayList<ProjectPeriodDTO> projectPeriods = new ArrayList<ProjectPeriodDTO>(0);

    public ProjectShortDTO() {
    }

    public ProjectShortDTO(long id, String name, String description, double overheadratio, ProjectCategoryDTO projectCategory, ArrayList<ProjectPeriodDTO> projectPeriods) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.overheadratio = overheadratio;
        this.projectCategory = projectCategory;
        this.projectPeriods = projectPeriods;
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
     * @return the projectCategory
     */
    public ProjectCategoryDTO getProjectCategory() {
        return projectCategory;
    }

    /**
     * @param projectCategory the projectCategory to set
     */
    public void setProjectCategory(ProjectCategoryDTO projectCategory) {
        this.projectCategory = projectCategory;
    }

    /**
     * @return the projectPeriods
     */
    public ArrayList<ProjectPeriodDTO> getProjectPeriods() {
        return projectPeriods;
    }

    /**
     * @param projectPeriods the projectPeriods to set
     */
    public void setProjectPeriods(ArrayList<ProjectPeriodDTO> projectPeriods) {
        this.projectPeriods = projectPeriods;
    }

    /**
     * @return an instance of the type ProjectShort
     */
    @Override
    public BasicDTO createCopy() {
        return new ProjectDTO(id, name, description, overheadratio, projectCategory, projectPeriods);
    }

    /**
     *
     * @param obj an instance of the type ProjectShort
     */
    @Override
    public void reset(BasicDTO obj) {
        ProjectShortDTO prj = (ProjectShortDTO) obj;
        this.id = prj.id;
        this.name = prj.name;
        this.description = prj.description;
        this.overheadratio = prj.overheadratio;
        this.projectCategory = prj.projectCategory;
        this.projectPeriods = prj.projectPeriods;
    }

    @Override
    public int compareTo(ProjectShortDTO o) {
        return name.compareTo(o.name);
    }
}
