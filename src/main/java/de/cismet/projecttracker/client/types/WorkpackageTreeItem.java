package de.cismet.projecttracker.client.types;

import com.google.gwt.user.client.ui.TreeItem;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;

/**
 * A TressItem implementation that wraps projectComponentDTO objects
 *
 * @author therter
 */
public class WorkpackageTreeItem extends TreeItem implements Comparable<WorkpackageTreeItem> {
    private WorkPackageDTO workpackage;

    public WorkpackageTreeItem(WorkPackageDTO workpackage) {
        this.workpackage = workpackage;
        setTitle(workpackage.getName());
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
        setTitle(workpackage.getName());
    }


    @Override
    public int compareTo(WorkpackageTreeItem o) {
        return getHTML().compareTo(o.getHTML());
    }
}
