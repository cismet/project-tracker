/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;


/**
 *
 * @author therter
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@id")
public class ProjectComponentTagDTO extends BasicDTO<ProjectComponentTagDTO> {
     private ProjectDTO project;
     private String name;
     private ArrayList<ProjectComponentTagDTO> projectComponentTagsForProjectcomponenttagdest = new ArrayList<ProjectComponentTagDTO>(0);


    public ProjectComponentTagDTO() {
    }
     

     public ProjectComponentTagDTO(long id, ProjectDTO project, String name, ArrayList<ProjectComponentTagDTO> projectComponentTagsForProjectcomponenttagdest) {
        this.id = id;
        this.project = project;
        this.name = name;
        this.projectComponentTagsForProjectcomponenttagdest = projectComponentTagsForProjectcomponenttagdest;
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
     * @return the projectComponentTagsForProjectcomponenttagdest
     */
    public ArrayList<ProjectComponentTagDTO> getProjectComponentTagsForProjectcomponenttagdest() {
        return projectComponentTagsForProjectcomponenttagdest;
    }

    /**
     * @param projectComponentTagsForProjectcomponenttagdest the projectComponentTagsForProjectcomponenttagdest to set
     */
    public void setProjectComponentTagsForProjectcomponenttagdest(ArrayList<ProjectComponentTagDTO> projectComponentTagsForProjectcomponenttagdest) {
        this.projectComponentTagsForProjectcomponenttagdest = projectComponentTagsForProjectcomponenttagdest;
    }

    @Override
    public ProjectComponentTagDTO createCopy() {
        return new ProjectComponentTagDTO(id, project, name, projectComponentTagsForProjectcomponenttagdest);
    }

    @Override
    public void reset(ProjectComponentTagDTO obj) {
        this.id = obj.getId();
        this.project = obj.getProject();
        this.name = obj.getName();
        this.projectComponentTagsForProjectcomponenttagdest = obj.getProjectComponentTagsForProjectcomponenttagdest();
    }
}
