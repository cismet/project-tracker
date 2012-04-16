package de.cismet.projecttracker.client.dto;

import java.util.ArrayList;

/**
 *
 * @author therter
 */
public class ProjectCategoryDTO extends BasicDTO<ProjectCategoryDTO> implements Comparable<ProjectCategoryDTO>{
    private String name;
    private ArrayList<ProjectDTO> projects = new ArrayList<ProjectDTO>(0);

    public ProjectCategoryDTO() {
    }



    public ProjectCategoryDTO(long id, String name, ArrayList<ProjectDTO> projects) {
        this.id = id;
        this.name = name;
        this.projects = projects;
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
     * @return the projects
     */
    public ArrayList<ProjectDTO> getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(ArrayList<ProjectDTO> projects) {
        this.projects = projects;
    }

    @Override
    public ProjectCategoryDTO createCopy() {
        return new ProjectCategoryDTO(id, name, projects);
    }

    @Override
    public void reset(ProjectCategoryDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.projects = obj.projects;
    }

    @Override
    public int compareTo(ProjectCategoryDTO o) {
        return name.compareTo(o.name);
    }
}
