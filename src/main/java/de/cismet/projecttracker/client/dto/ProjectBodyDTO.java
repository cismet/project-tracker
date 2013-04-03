package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;

/**
 *
 * @author therter
 */
public class ProjectBodyDTO extends BasicDTO<ProjectBodyDTO> implements Comparable<ProjectBodyDTO> {
    private String name;
    @JsonIgnore
    private ArrayList<ProjectShortDTO> projects = new ArrayList<ProjectShortDTO>(0);

    public ProjectBodyDTO() {
    }


    

    public ProjectBodyDTO(long id, String name, ArrayList<ProjectShortDTO> projects) {
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
    public ArrayList<ProjectShortDTO> getProjects() {
        return projects;
    }

    /**
     * @param projects the projects to set
     */
    public void setProjects(ArrayList<ProjectShortDTO> projects) {
        this.projects = projects;
    }

    @Override
    public ProjectBodyDTO createCopy() {
        return new ProjectBodyDTO(id, name, projects);
    }

    @Override
    public void reset(ProjectBodyDTO obj) {
        this.id = obj.id;
        this.name = obj.name;
        this.projects = obj.projects;
    }

    @Override
    public int compareTo(ProjectBodyDTO o) {
        return name.compareTo(o.name);
    }
}
