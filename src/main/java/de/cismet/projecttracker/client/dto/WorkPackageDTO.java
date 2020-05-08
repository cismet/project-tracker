/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
@JsonIdentityInfo(
    generator = ObjectIdGenerators.IntSequenceGenerator.class,
    property = "@jsonWorkPackageId"
)
public class WorkPackageDTO extends BasicDTO<WorkPackageDTO> implements Comparable<WorkPackageDTO> {

    //~ Instance fields --------------------------------------------------------

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
    private ArrayList<EstimatedComponentCostDTO> estimatedWorkPackageCosts = new ArrayList<EstimatedComponentCostDTO>(
            0);
    @JsonIgnore
    private ArrayList<WorkPackageProgressDTO> workPackageProgresses = new ArrayList<WorkPackageProgressDTO>(0);
    private String abbreviation;
    private boolean issubversion = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkPackageDTO object.
     */
    public WorkPackageDTO() {
    }

    /**
     * Creates a new WorkPackageDTO object.
     *
     * @param  id                         DOCUMENT ME!
     * @param  responsiblestaff           DOCUMENT ME!
     * @param  workPackage                DOCUMENT ME!
     * @param  project                    DOCUMENT ME!
     * @param  costCategory               DOCUMENT ME!
     * @param  name                       DOCUMENT ME!
     * @param  description                DOCUMENT ME!
     * @param  warnlevel                  DOCUMENT ME!
     * @param  criticallevel              DOCUMENT ME!
     * @param  fullstoplevel              DOCUMENT ME!
     * @param  workPackages               DOCUMENT ME!
     * @param  workPackagePeriods         DOCUMENT ME!
     * @param  projectComponentTags       DOCUMENT ME!
     * @param  estimatedWorkPackageCosts  DOCUMENT ME!
     * @param  workPackageProgresses      DOCUMENT ME!
     * @param  abbreviation               DOCUMENT ME!
     * @param  issubversion               DOCUMENT ME!
     * @param  expirationDescription      DOCUMENT ME!
     */
    public WorkPackageDTO(final long id,
            final StaffDTO responsiblestaff,
            final WorkPackageDTO workPackage,
            final ProjectDTO project,
            final CostCategoryDTO costCategory,
            final String name,
            final String description,
            final double warnlevel,
            final double criticallevel,
            final double fullstoplevel,
            final ArrayList<WorkPackageDTO> workPackages,
            final ArrayList<WorkPackagePeriodDTO> workPackagePeriods,
            final ArrayList<ProjectComponentTagDTO> projectComponentTags,
            final ArrayList<EstimatedComponentCostDTO> estimatedWorkPackageCosts,
            final ArrayList<WorkPackageProgressDTO> workPackageProgresses,
            final String abbreviation,
            final boolean issubversion,
            final String expirationDescription) {
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

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the workPackage
     */
    public WorkPackageDTO getWorkPackage() {
        return workPackage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workPackage  the workPackage to set
     */
    public void setWorkPackage(final WorkPackageDTO workPackage) {
        this.workPackage = workPackage;
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
     * @return  the costCategory
     */
    public CostCategoryDTO getCostCategory() {
        return costCategory;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  costCategory  the costCategory to set
     */
    public void setCostCategory(final CostCategoryDTO costCategory) {
        this.costCategory = costCategory;
    }

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
     * @return  DOCUMENT ME!
     */
    public String getExpirationDescription() {
        return expirationDescription;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  expirationDescription  DOCUMENT ME!
     */
    public void setExpirationDescription(final String expirationDescription) {
        this.expirationDescription = expirationDescription;
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
     * @return  the workPackagePeriods
     */
    public ArrayList<WorkPackagePeriodDTO> getWorkPackagePeriods() {
        return workPackagePeriods;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workPackagePeriods  the workPackagePeriods to set
     */
    public void setWorkPackagePeriods(final ArrayList<WorkPackagePeriodDTO> workPackagePeriods) {
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
     * @return  the estimatedWorkPackageCosts
     */
    public ArrayList<EstimatedComponentCostDTO> getEstimatedWorkPackageCosts() {
        return estimatedWorkPackageCosts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  estimatedWorkPackageCosts  the estimatedWorkPackageCosts to set
     */
    public void setEstimatedWorkPackageCosts(final ArrayList<EstimatedComponentCostDTO> estimatedWorkPackageCosts) {
        this.estimatedWorkPackageCosts = estimatedWorkPackageCosts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the workPackageProgresses
     */
    public ArrayList<WorkPackageProgressDTO> getWorkPackageProgresses() {
        return workPackageProgresses;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workPackageProgresses  the workPackageProgresses to set
     */
    public void setWorkPackageProgresses(final ArrayList<WorkPackageProgressDTO> workPackageProgresses) {
        this.workPackageProgresses = workPackageProgresses;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  abbreviation  the abbreviation to set
     */
    public void setAbbreviation(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the issubversion
     */
    public boolean getIssubversion() {
        return issubversion;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  issubversion  the issubversion to set
     */
    public void setIssubversion(final boolean issubversion) {
        this.issubversion = issubversion;
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

    @Override
    public WorkPackageDTO createCopy() {
        return new WorkPackageDTO(
                id,
                getResponsiblestaff(),
                workPackage,
                project,
                costCategory,
                name,
                description,
                warnlevel,
                criticallevel,
                fullstoplevel,
                workPackages,
                workPackagePeriods,
                projectComponentTags,
                estimatedWorkPackageCosts,
                workPackageProgresses,
                abbreviation,
                issubversion,
                expirationDescription);
    }

    @Override
    public void reset(final WorkPackageDTO obj) {
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
    public int compareTo(final WorkPackageDTO o) {
        if (isSameLevel(this, o)) {
            return compareByName(this, o);
        } else if (isSubworkPackage(this, o)) {
            return -1;
        } else if (isSubworkPackage(o, this)) {
            return 1;
        } else {
            final List<WorkPackageDTO> parents1 = getParents(this);
            final List<WorkPackageDTO> parents2 = getParents(o);
            int i;

            for (i = 0; i < parents1.size(); ++i) {
                if (parents2.contains(parents1.get(i))) {
                    break;
                }
            }

            if (i >= parents1.size()) {
                final WorkPackageDTO ob1 = ((parents1.size() > 0) ? parents1.get(parents1.size() - 1) : this);
                final WorkPackageDTO ob2 = ((parents2.size() > 0) ? parents2.get(parents2.size() - 1) : o);

                return compareByName(ob1, ob2);
            } else {
                final int i2 = parents2.indexOf(parents1.get(i));
                final WorkPackageDTO ob1 = getElementAfterIndex(parents1, i, this);
                final WorkPackageDTO ob2 = getElementAfterIndex(parents2, i2, o);

                return compareByName(ob1, ob2);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   list    DOCUMENT ME!
     * @param   index   DOCUMENT ME!
     * @param   object  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static WorkPackageDTO getElementAfterIndex(final List<WorkPackageDTO> list,
            final int index,
            final WorkPackageDTO object) {
        if ((index - 1) >= 0) {
            return list.get(index - 1);
        } else {
            return object;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o1  DOCUMENT ME!
     * @param   o2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static int compareById(final WorkPackageDTO o1, final WorkPackageDTO o2) {
        return (int)Math.signum(o1.id - o2.id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o1  DOCUMENT ME!
     * @param   o2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static int compareByName(final WorkPackageDTO o1, final WorkPackageDTO o2) {
        if (o1.getName() == null && o2.getName() != null) {
            return 1;
        } else if (o1.getName() != null && o2.getName() == null) {
            return -1;
        } else if (o1.getName() == null && o2.getName() == null) {
            return 0;
        } else {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   wp  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static List<WorkPackageDTO> getParents(final WorkPackageDTO wp) {
        final List<WorkPackageDTO> parents = new ArrayList<WorkPackageDTO>();
        WorkPackageDTO tmp = wp;
        while (tmp.getWorkPackage() != null) {
            parents.add(tmp.getWorkPackage());
            tmp = tmp.getWorkPackage();
        }

        return parents;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o1  DOCUMENT ME!
     * @param   o2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isSameLevel(final WorkPackageDTO o1, final WorkPackageDTO o2) {
        if (((o1.getWorkPackage() == null) && (o2.getWorkPackage() == null))
                    || ((o1.getWorkPackage() != null) && (o2.getWorkPackage() != null)
                        && o1.getWorkPackage().equals(o2.getWorkPackage()))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   possibleParent  DOCUMENT ME!
     * @param   possibleChild   DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static boolean isSubworkPackage(final WorkPackageDTO possibleParent, final WorkPackageDTO possibleChild) {
        boolean subWorkPackage = false;

        if (possibleChild.getWorkPackage() == null) {
            return false;
        } else if (possibleParent.equals(possibleChild.getWorkPackage())) {
            return true;
        } else if ((possibleParent.getWorkPackage() != null) && possibleChild.equals(possibleParent.getWorkPackage())) {
            // this if block is not needed, but it improves the performance
            return false;
        } else {
            for (final WorkPackageDTO tmp : possibleParent.getWorkPackages()) {
                subWorkPackage |= isSubworkPackage(tmp, possibleChild);
            }
        }

        return subWorkPackage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the most recent version of the project period or null, if no project period exists
     */
    public WorkPackagePeriodDTO determineMostRecentPeriod() {
        WorkPackagePeriodDTO currentPeriod = null;

        if ((workPackagePeriods != null) && (workPackagePeriods.size() > 0)) {
            for (final WorkPackagePeriodDTO tmp : workPackagePeriods) {
                if ((currentPeriod == null) || tmp.getAsof().after(currentPeriod.getAsof())) {
                    currentPeriod = tmp;
                }
            }
        }

        return currentPeriod;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the most recent version of the project progress or null, if no project progress exists
     */
    public WorkPackageProgressDTO determineMostRecentProgress() {
        WorkPackageProgressDTO progress = null;

        if ((workPackageProgresses != null) && (workPackageProgresses.size() > 0)) {
            for (final WorkPackageProgressDTO tmp : workPackageProgresses) {
                if ((progress == null) || tmp.getTime().after(progress.getTime())) {
                    progress = tmp;
                }
            }
        }

        return progress;
    }
}
