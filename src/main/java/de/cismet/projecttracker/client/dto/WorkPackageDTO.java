package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author therter
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="@jsonWorkPackageId")
public class WorkPackageDTO extends BasicDTO<WorkPackageDTO> implements Comparable<WorkPackageDTO> {
    private WorkPackageDTO workPackage;
    private ProjectDTO project;
    private CostCategoryDTO costCategory;
    private StaffDTO responsiblestaff;
    private String name;
    private String description;
    private String expirationDescription;
    private double warnlevel;
    private double criticallevel;
    private double fullstoplevel;
    @JsonIgnore
    private ArrayList<WorkPackageDTO> workPackages = new ArrayList<WorkPackageDTO>(0);
    @JsonIgnore
    private ArrayList<WorkPackagePeriodDTO> workPackagePeriods = new ArrayList<WorkPackagePeriodDTO>(0);
    @JsonIgnore
    private ArrayList<ProjectComponentTagDTO> projectComponentTags = new ArrayList<ProjectComponentTagDTO>(0);
    @JsonIgnore
    private ArrayList<EstimatedComponentCostDTO> estimatedWorkPackageCosts = new ArrayList<EstimatedComponentCostDTO>(0);
    @JsonIgnore
    private ArrayList<WorkPackageProgressDTO> workPackageProgresses = new ArrayList<WorkPackageProgressDTO>(0);
    private String abbreviation;
    private boolean issubversion = false;


    public WorkPackageDTO() {
    }
     

    public WorkPackageDTO(long id, StaffDTO responsiblestaff, WorkPackageDTO workPackage, ProjectDTO project, CostCategoryDTO costCategory, String name, String description, double warnlevel, double criticallevel, double fullstoplevel, ArrayList<WorkPackageDTO> workPackages, ArrayList<WorkPackagePeriodDTO> workPackagePeriods, ArrayList<ProjectComponentTagDTO> projectComponentTags, ArrayList<EstimatedComponentCostDTO> estimatedWorkPackageCosts, ArrayList<WorkPackageProgressDTO> workPackageProgresses, String abbreviation, boolean issubversion, String expirationDescription) {
        this.id = id;
        this.responsiblestaff = responsiblestaff;
        this.workPackage = workPackage;
        this.project = project;
        this.costCategory = costCategory;
        this.name = name;
        this.description = description;
        this.expirationDescription = expirationDescription;
        this.workPackages = workPackages;
        this.workPackagePeriods = workPackagePeriods;
//        this.activityWorkPackages = activityWorkPackages;
        this.projectComponentTags = projectComponentTags;
        this.estimatedWorkPackageCosts = estimatedWorkPackageCosts;
        this.workPackageProgresses = workPackageProgresses;
        this.abbreviation = abbreviation;
        this.issubversion = issubversion;
        this.warnlevel = warnlevel;
        this.criticallevel = criticallevel;
        this.fullstoplevel = fullstoplevel;
    }


    /**
     * @return the workPackage
     */
    public WorkPackageDTO getWorkPackage() {
        return workPackage;
    }

    /**
     * @param workPackage the workPackage to set
     */
    public void setWorkPackage(WorkPackageDTO workPackage) {
        this.workPackage = workPackage;
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
     * @return the costCategory
     */
    public CostCategoryDTO getCostCategory() {
        return costCategory;
    }

    /**
     * @param costCategory the costCategory to set
     */
    public void setCostCategory(CostCategoryDTO costCategory) {
        this.costCategory = costCategory;
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

    public String getExpirationDescription() {
        return expirationDescription;
    }

    public void setExpirationDescription(String expirationDescription) {
        this.expirationDescription = expirationDescription;
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
     * @return the workPackagePeriods
     */
    public ArrayList<WorkPackagePeriodDTO> getWorkPackagePeriods() {
        return workPackagePeriods;
    }

    /**
     * @param workPackagePeriods the workPackagePeriods to set
     */
    public void setWorkPackagePeriods(ArrayList<WorkPackagePeriodDTO> workPackagePeriods) {
        this.workPackagePeriods = workPackagePeriods;
    }

//    /**
//     * @return the activityWorkPackages
//     */
//    public ArrayList<ActivityDTO> getActivityWorkPackages() {
//        return activityWorkPackages;
//    }
//
//    /**
//     * @param activityWorkPackages the activityWorkPackages to set
//     */
//    public void setActivityWorkPackages(ArrayList<ActivityDTO> activityWorkPackages) {
//        this.activityWorkPackages = activityWorkPackages;
//    }

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
     * @return the estimatedWorkPackageCosts
     */
    public ArrayList<EstimatedComponentCostDTO> getEstimatedWorkPackageCosts() {
        return estimatedWorkPackageCosts;
    }

    /**
     * @param estimatedWorkPackageCosts the estimatedWorkPackageCosts to set
     */
    public void setEstimatedWorkPackageCosts(ArrayList<EstimatedComponentCostDTO> estimatedWorkPackageCosts) {
        this.estimatedWorkPackageCosts = estimatedWorkPackageCosts;
    }

    /**
     * @return the workPackageProgresses
     */
    public ArrayList<WorkPackageProgressDTO> getWorkPackageProgresses() {
        return workPackageProgresses;
    }

    /**
     * @param workPackageProgresses the workPackageProgresses to set
     */
    public void setWorkPackageProgresses(ArrayList<WorkPackageProgressDTO> workPackageProgresses) {
        this.workPackageProgresses = workPackageProgresses;
    }

    /**
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * @param abbreviation the abbreviation to set
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * @return the issubversion
     */
    public boolean getIssubversion() {
        return issubversion;
    }

    /**
     * @param issubversion the issubversion to set
     */
    public void setIssubversion(boolean issubversion) {
        this.issubversion = issubversion;
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

    
    @Override
    public WorkPackageDTO createCopy() {
        return new WorkPackageDTO(id, getResponsiblestaff(), workPackage, project, costCategory, name, description, warnlevel, criticallevel, fullstoplevel, workPackages, workPackagePeriods, projectComponentTags, estimatedWorkPackageCosts, workPackageProgresses, abbreviation, issubversion, expirationDescription);
    }

    
    @Override
    public void reset(WorkPackageDTO obj) {
        this.id = obj.id;
        this.setResponsiblestaff(obj.getResponsiblestaff());
        this.workPackage = obj.workPackage;
        this.project = obj.project;
        this.costCategory = obj.costCategory;
        this.name = obj.name;
        this.description = obj.description;
        this.description = obj.description;
        this.warnlevel = obj.warnlevel;
        this.criticallevel = obj.criticallevel;
        this.fullstoplevel = obj.fullstoplevel;
        this.workPackages = obj.workPackages;
        this.workPackagePeriods = obj.workPackagePeriods;
        this.projectComponentTags = obj.projectComponentTags;
        this.estimatedWorkPackageCosts = obj.estimatedWorkPackageCosts;
        this.workPackageProgresses = obj.workPackageProgresses;
        this.abbreviation = obj.abbreviation;
        this.issubversion = obj.issubversion;
    }

    @Override
    public int compareTo(WorkPackageDTO o) {
        if ( isSameLevel(this, o) ) {
            return (int)Math.signum(id - o.id);
        } else if (isSubworkPackage(this, o)) {
            return -1;
        } else if (isSubworkPackage(o, this)) {
            return 1;
        } else {
            List<WorkPackageDTO> parents1 = getParents(this);
            List<WorkPackageDTO> parents2 = getParents(o);
            int i;

            for ( i = 0; i < parents1.size(); ++i ) {
                if ( parents2.contains( parents1.get(i) ) ) {
                    break;
                }
            }
            
            if (i >= parents1.size()) {
                WorkPackageDTO ob1 = ( parents1.size() > 0 ? parents1.get(parents1.size() - 1) : this);
                WorkPackageDTO ob2 = ( parents2.size() > 0 ? parents2.get(parents2.size() - 1) : o);
                
                return compareById(ob1, ob2);
            } else {
                int i2 = parents2.indexOf(parents1.get(i));
                WorkPackageDTO ob1 = getElementAfterIndex(parents1, i, this);
                WorkPackageDTO ob2 = getElementAfterIndex(parents2, i2, o);
                
                return compareById(ob1, ob2);
            }
        }
    }
    
    private static WorkPackageDTO getElementAfterIndex(List<WorkPackageDTO> list, int index, WorkPackageDTO object) {
        if ((index - 1) >= 0) {
            return list.get(index - 1);
        } else {
            return object;
        }
    }

    private static int compareById(WorkPackageDTO o1, WorkPackageDTO o2) {
        return (int)Math.signum(o1.id - o2.id);
    }
    
    private static List<WorkPackageDTO> getParents(WorkPackageDTO wp) {
        List<WorkPackageDTO> parents = new ArrayList<WorkPackageDTO>();
        WorkPackageDTO tmp = wp;
        while (tmp.getWorkPackage() != null) {
            parents.add(tmp.getWorkPackage());
            tmp = tmp.getWorkPackage();
        }
        
        return parents;
    }
    
    
    public static boolean isSameLevel(WorkPackageDTO o1, WorkPackageDTO o2) {
        if ( (o1.getWorkPackage() == null && o2.getWorkPackage() == null) || 
             (o1.getWorkPackage() != null && o2.getWorkPackage() != null && o1.getWorkPackage().equals(o2.getWorkPackage())) ) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isSubworkPackage(WorkPackageDTO possibleParent, WorkPackageDTO possibleChild) {
        boolean subWorkPackage = false;
        
        if (possibleChild.getWorkPackage() == null) {
            return false;
        } else if (possibleParent.equals(possibleChild.getWorkPackage())) {
            return true;
        } else if (possibleParent.getWorkPackage() != null && possibleChild.equals(possibleParent.getWorkPackage())) {
            // this if block is not needed, but it improves the performance
            return false;
        } else {
            for (WorkPackageDTO tmp : possibleParent.getWorkPackages()) {
                subWorkPackage |= isSubworkPackage(tmp, possibleChild);
            }
        }
        
        return subWorkPackage;
    }

    /**
     * @return the most recent version of the project period or null, if no project period exists
     */
    public WorkPackagePeriodDTO determineMostRecentPeriod() {
        WorkPackagePeriodDTO currentPeriod = null;

        if (workPackagePeriods != null && workPackagePeriods.size() > 0) {
            for (WorkPackagePeriodDTO tmp : workPackagePeriods) {
                if ( currentPeriod == null || tmp.getAsof().after( currentPeriod.getAsof() ) ) {
                    currentPeriod = tmp;
                }
            }
        }

        return currentPeriod;
    }

    /**
     * @return the most recent version of the project progress or null, if no project progress exists
     */
    public WorkPackageProgressDTO determineMostRecentProgress() {
        WorkPackageProgressDTO progress = null;

        if (workPackageProgresses != null && workPackageProgresses.size() > 0) {
            for (WorkPackageProgressDTO tmp : workPackageProgresses) {
                if ( progress == null || tmp.getTime().after( progress.getTime() ) ) {
                    progress = tmp;
                }
            }
        }

        return progress;
    }
}
