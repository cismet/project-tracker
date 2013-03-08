package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.WorkPackageDTO;



/**
 * A ListItem implementation that wraps ProjectComponentDTO objects
 *
 * @author therter
 */
public class WorkpackageListItem extends ListItem {
    private WorkPackageDTO workpackage;

    public WorkpackageListItem(WorkPackageDTO workpackage) {
        super("" + workpackage.getId(), getName(workpackage));
        this.workpackage = workpackage;
    }

    /**
     * @return the workpackage
     */
    public WorkPackageDTO getWorkpackage() {
        return workpackage;
    }

    /**
     * @param workpackage the workpackage to set
     */
    public void setWorkpackage(WorkPackageDTO workpackage) {
        this.workpackage = workpackage;
        setId("" + workpackage.getId());
        setName( getName(workpackage) );
    }


    private static String getName(WorkPackageDTO workpackage) {
        WorkPackageDTO tmp = workpackage.getWorkPackage();
        String prefix = "";

        while (tmp != null) {
            prefix += "&nbsp;&nbsp;";
            tmp = tmp.getWorkPackage();
        }

        return prefix + workpackage.getName();
    }
}
