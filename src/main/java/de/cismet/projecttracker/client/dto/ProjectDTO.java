package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;

/**
 *
 * @author therter
 */
public class ProjectDTO extends ProjectShortDTO {
    private ProjectBodyDTO projectBody;
    private ProjectDTO project;
    private double hoursofamanday = 8;
    private double daysofamanmonth = 19;
    private boolean overtimehoursallowed = true;
    private double warnlevel;
    private double criticallevel;
    private double fullstoplevel;
    private StaffDTO responsiblestaff;
    @JsonIgnore
    private ArrayList<ProjectDTO> projects = new ArrayList<ProjectDTO>(0);
    @JsonIgnore
    private ArrayList<CostCategoryDTO> costCategories = new ArrayList<CostCategoryDTO>(0);
    @JsonIgnore
    private ArrayList<WorkPackageDTO> workPackages = new ArrayList<WorkPackageDTO>(0);
    @JsonIgnore
    private ArrayList<ProjectComponentTagDTO> projectComponentTags = new ArrayList<ProjectComponentTagDTO>(0);

    public ProjectDTO() {
    }



    public ProjectDTO(long id, String name, String description, double overheadratio, ProjectCategoryDTO projectCategory, ArrayList<ProjectPeriodDTO> projectPeriods) {
        super(id, name, description, overheadratio, projectCategory, projectPeriods);
    }


    public ProjectDTO(long id, String name, String description, double overheadratio, ProjectCategoryDTO projectCategory, ArrayList<ProjectPeriodDTO> projectPeriods, StaffDTO responsiblestaff, ProjectBodyDTO projectBody, ProjectDTO project, double hoursofamanday, double daysofamanmonth, boolean overtimehoursallowed, double warnlevel, double criticallevel, double fullstoplevel, ArrayList<ProjectDTO> projects, ArrayList<CostCategoryDTO> costCategories, ArrayList<WorkPackageDTO> workPackages, ArrayList<ProjectComponentTagDTO> projectComponentTags) {
        super(id, name, description, overheadratio, projectCategory, projectPeriods);
        this.responsiblestaff = responsiblestaff;
        this.projectBody = projectBody;
        this.project = project;
        this.projectBody = projectBody;
        this.project = project;
        this.hoursofamanday = hoursofamanday;
        this.daysofamanmonth = daysofamanmonth;
        this.overtimehoursallowed = overtimehoursallowed;
        this.projects = projects;
        this.costCategories = costCategories;
        this.workPackages = workPackages;
        this.projectComponentTags = projectComponentTags;
        this.warnlevel = warnlevel;
        this.criticallevel = criticallevel;
        this.fullstoplevel = fullstoplevel;
    }


    
    /**
     * @return the projectBody
     */
    public ProjectBodyDTO getProjectBody() {
        return projectBody;
    }

    /**
     * @param projectBody the projectBody to set
     */
    public void setProjectBody(ProjectBodyDTO projectBody) {
        this.projectBody = projectBody;
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
     * @return the hoursofamanday
     */
    public double getHoursofamanday() {
        return hoursofamanday;
    }

    /**
     * @param hoursofamanday the hoursofamanday to set
     */
    public void setHoursofamanday(double hoursofamanday) {
        this.hoursofamanday = hoursofamanday;
    }

    /**
     * @return the daysofamanmonth
     */
    public double getDaysofamanmonth() {
        return daysofamanmonth;
    }

    /**
     * @param daysofamanmonth the daysofamanmonth to set
     */
    public void setDaysofamanmonth(double daysofamanmonth) {
        this.daysofamanmonth = daysofamanmonth;
    }

    /**
     * @return the overtimehoursallowed
     */
    public boolean getOvertimehoursallowed() {
        return overtimehoursallowed;
    }

    /**
     * @param overtimehoursallowed the overtimehoursallowed to set
     */
    public void setOvertimehoursallowed(boolean overtimehoursallowed) {
        this.overtimehoursallowed = overtimehoursallowed;
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

    /**
     * @return the costCategories
     */
    public ArrayList<CostCategoryDTO> getCostCategories() {
        return costCategories;
    }

    /**
     * @param costCategories the costCategories to set
     */
    public void setCostCategories(ArrayList<CostCategoryDTO> costCategories) {
        this.costCategories = costCategories;
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

    /**
     * @return the projectComponentTags
     */
    public ArrayList<ProjectComponentTagDTO> getProjectComponentTags() {
        return projectComponentTags;
    }

    /**
     * @param projectComponentTags the projectComponentTags to set
     */
    public void setProjectComponentTags(ArrayList<ProjectComponentTagDTO> projectComponentTags) {
        this.projectComponentTags = projectComponentTags;
    }

    /**
     *
     * @return an isntance of the type Project, that contains the same data as this object
     */
    @Override
    public BasicDTO createCopy() {
        return new ProjectDTO(id, name, description, overheadratio, projectCategory, projectPeriods, getResponsiblestaff(), projectBody, project, hoursofamanday, daysofamanmonth, overtimehoursallowed, warnlevel, criticallevel, fullstoplevel, projects, costCategories, workPackages, projectComponentTags);
    }

    /**
     * copies the content of the given object in this object
     * @param obj an instance of the type Project
     */
    @Override
    public void reset(BasicDTO obj) {
        ProjectDTO prj = (ProjectDTO)obj;
        this.id = prj.id;
        this.name = prj.name;
        this.description = prj.description;
        this.overheadratio = prj.overheadratio;
        this.projectCategory = prj.projectCategory;
        this.projectPeriods = prj.projectPeriods;
        this.setResponsiblestaff(prj.getResponsiblestaff());
        this.projectBody = prj.projectBody;
        this.project = prj.project;
        this.hoursofamanday = prj.hoursofamanday;
        this.daysofamanmonth = prj.daysofamanmonth;
        this.overtimehoursallowed = prj.overtimehoursallowed;
        this.warnlevel = prj.warnlevel;
        this.criticallevel = prj.criticallevel;
        this.fullstoplevel = prj.fullstoplevel;
        this.projects = prj.projects;
        this.costCategories = prj.costCategories;
        this.workPackages = prj.workPackages;
        this.projectComponentTags = prj.projectComponentTags;
    }

    public ProjectShortDTO toShortVersion() {
        return new ProjectShortDTO(id, name, description, overheadratio, projectCategory, projectPeriods);
    }

    /**
     * @return the warnlevel
     */
    public double getWarnlevel() {
        return warnlevel;
    }

    /**
     * @param warnlevel the warnlevel to set
     */
    public void setWarnlevel(double warnlevel) {
        this.warnlevel = warnlevel;
    }

    /**
     * @return the criticallevel
     */
    public double getCriticallevel() {
        return criticallevel;
    }

    /**
     * @param criticallevel the criticallevel to set
     */
    public void setCriticallevel(double criticallevel) {
        this.criticallevel = criticallevel;
    }

    /**
     * @return the fullstoplevel
     */
    public double getFullstoplevel() {
        return fullstoplevel;
    }

    /**
     * @param fullstoplevel the fullstoplevel to set
     */
    public void setFullstoplevel(double fullstoplevel) {
        this.fullstoplevel = fullstoplevel;
    }

    /**
     * @return the responsiblestaff
     */
    public StaffDTO getResponsiblestaff() {
        return responsiblestaff;
    }

    /**
     * @param responsiblestaff the responsiblestaff to set
     */
    public void setResponsiblestaff(StaffDTO responsiblestaff) {
        this.responsiblestaff = responsiblestaff;
    }


    /**
     * @return the most recent version of the project period or null, if no project period exists
     */
    public ProjectPeriodDTO determineMostRecentPeriod() {
        ProjectPeriodDTO currentPeriod = null;

        if (getProjectPeriods() != null && getProjectPeriods().size() > 0) {
            for (ProjectPeriodDTO tmp : getProjectPeriods()) {
                if ( currentPeriod == null || tmp.getAsof() == null || 
                        ( currentPeriod.getAsof() != null && tmp.getAsof().after( currentPeriod.getAsof() ) ) ) {
                    currentPeriod = tmp;
                }
            }
        }

        return currentPeriod;
    }
}
