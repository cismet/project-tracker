/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectDTO extends ProjectShortDTO {

    //~ Instance fields --------------------------------------------------------

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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectDTO object.
     */
    public ProjectDTO() {
    }

    /**
     * Creates a new ProjectDTO object.
     *
     * @param  id               DOCUMENT ME!
     * @param  name             DOCUMENT ME!
     * @param  description      DOCUMENT ME!
     * @param  overheadratio    DOCUMENT ME!
     * @param  projectCategory  DOCUMENT ME!
     * @param  projectPeriods   DOCUMENT ME!
     */
    public ProjectDTO(final long id,
            final String name,
            final String description,
            final double overheadratio,
            final ProjectCategoryDTO projectCategory,
            final ArrayList<ProjectPeriodDTO> projectPeriods) {
        super(id, name, description, overheadratio, projectCategory, projectPeriods);
    }

    /**
     * Creates a new ProjectDTO object.
     *
     * @param  id                    DOCUMENT ME!
     * @param  name                  DOCUMENT ME!
     * @param  description           DOCUMENT ME!
     * @param  overheadratio         DOCUMENT ME!
     * @param  projectCategory       DOCUMENT ME!
     * @param  projectPeriods        DOCUMENT ME!
     * @param  responsiblestaff      DOCUMENT ME!
     * @param  projectBody           DOCUMENT ME!
     * @param  project               DOCUMENT ME!
     * @param  hoursofamanday        DOCUMENT ME!
     * @param  daysofamanmonth       DOCUMENT ME!
     * @param  overtimehoursallowed  DOCUMENT ME!
     * @param  warnlevel             DOCUMENT ME!
     * @param  criticallevel         DOCUMENT ME!
     * @param  fullstoplevel         DOCUMENT ME!
     * @param  projects              DOCUMENT ME!
     * @param  costCategories        DOCUMENT ME!
     * @param  workPackages          DOCUMENT ME!
     * @param  projectComponentTags  DOCUMENT ME!
     */
    public ProjectDTO(final long id,
            final String name,
            final String description,
            final double overheadratio,
            final ProjectCategoryDTO projectCategory,
            final ArrayList<ProjectPeriodDTO> projectPeriods,
            final StaffDTO responsiblestaff,
            final ProjectBodyDTO projectBody,
            final ProjectDTO project,
            final double hoursofamanday,
            final double daysofamanmonth,
            final boolean overtimehoursallowed,
            final double warnlevel,
            final double criticallevel,
            final double fullstoplevel,
            final ArrayList<ProjectDTO> projects,
            final ArrayList<CostCategoryDTO> costCategories,
            final ArrayList<WorkPackageDTO> workPackages,
            final ArrayList<ProjectComponentTagDTO> projectComponentTags) {
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

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the projectBody
     */
    public ProjectBodyDTO getProjectBody() {
        return projectBody;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectBody  the projectBody to set
     */
    public void setProjectBody(final ProjectBodyDTO projectBody) {
        this.projectBody = projectBody;
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
     * @return  the hoursofamanday
     */
    public double getHoursofamanday() {
        return hoursofamanday;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  hoursofamanday  the hoursofamanday to set
     */
    public void setHoursofamanday(final double hoursofamanday) {
        this.hoursofamanday = hoursofamanday;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the daysofamanmonth
     */
    public double getDaysofamanmonth() {
        return daysofamanmonth;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  daysofamanmonth  the daysofamanmonth to set
     */
    public void setDaysofamanmonth(final double daysofamanmonth) {
        this.daysofamanmonth = daysofamanmonth;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the overtimehoursallowed
     */
    public boolean getOvertimehoursallowed() {
        return overtimehoursallowed;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  overtimehoursallowed  the overtimehoursallowed to set
     */
    public void setOvertimehoursallowed(final boolean overtimehoursallowed) {
        this.overtimehoursallowed = overtimehoursallowed;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the projects
     */
    public ArrayList<ProjectDTO> getProjects() {
        return projects;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projects  the projects to set
     */
    public void setProjects(final ArrayList<ProjectDTO> projects) {
        this.projects = projects;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the costCategories
     */
    public ArrayList<CostCategoryDTO> getCostCategories() {
        return costCategories;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  costCategories  the costCategories to set
     */
    public void setCostCategories(final ArrayList<CostCategoryDTO> costCategories) {
        this.costCategories = costCategories;
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

    /**
     * DOCUMENT ME!
     *
     * @return  the projectComponentTags
     */
    public ArrayList<ProjectComponentTagDTO> getProjectComponentTags() {
        return projectComponentTags;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  projectComponentTags  the projectComponentTags to set
     */
    public void setProjectComponentTags(final ArrayList<ProjectComponentTagDTO> projectComponentTags) {
        this.projectComponentTags = projectComponentTags;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  an isntance of the type Project, that contains the same data as this object
     */
    @Override
    public BasicDTO createCopy() {
        return new ProjectDTO(
                id,
                name,
                description,
                overheadratio,
                projectCategory,
                projectPeriods,
                getResponsiblestaff(),
                projectBody,
                project,
                hoursofamanday,
                daysofamanmonth,
                overtimehoursallowed,
                warnlevel,
                criticallevel,
                fullstoplevel,
                projects,
                costCategories,
                workPackages,
                projectComponentTags);
    }

    /**
     * copies the content of the given object in this object.
     *
     * @param  obj  an instance of the type Project
     */
    @Override
    public void reset(final BasicDTO obj) {
        final ProjectDTO prj = (ProjectDTO)obj;
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ProjectShortDTO toShortVersion() {
        return new ProjectShortDTO(id, name, description, overheadratio, projectCategory, projectPeriods);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the warnlevel
     */
    public double getWarnlevel() {
        return warnlevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  warnlevel  the warnlevel to set
     */
    public void setWarnlevel(final double warnlevel) {
        this.warnlevel = warnlevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the criticallevel
     */
    public double getCriticallevel() {
        return criticallevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  criticallevel  the criticallevel to set
     */
    public void setCriticallevel(final double criticallevel) {
        this.criticallevel = criticallevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the fullstoplevel
     */
    public double getFullstoplevel() {
        return fullstoplevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fullstoplevel  the fullstoplevel to set
     */
    public void setFullstoplevel(final double fullstoplevel) {
        this.fullstoplevel = fullstoplevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the responsiblestaff
     */
    public StaffDTO getResponsiblestaff() {
        return responsiblestaff;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  responsiblestaff  the responsiblestaff to set
     */
    public void setResponsiblestaff(final StaffDTO responsiblestaff) {
        this.responsiblestaff = responsiblestaff;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the most recent version of the project period or null, if no project period exists
     */
    public ProjectPeriodDTO determineMostRecentPeriod() {
        ProjectPeriodDTO currentPeriod = null;

        if ((getProjectPeriods() != null) && (getProjectPeriods().size() > 0)) {
            for (final ProjectPeriodDTO tmp : getProjectPeriods()) {
                if ((currentPeriod == null) || (tmp.getAsof() == null)
                            || ((currentPeriod.getAsof() != null) && tmp.getAsof().after(currentPeriod.getAsof()))) {
                    currentPeriod = tmp;
                }
            }
        }

        return currentPeriod;
    }
}
