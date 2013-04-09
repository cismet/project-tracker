/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import com.google.gwt.user.client.ui.TreeItem;

import de.cismet.projecttracker.client.dto.WorkPackageDTO;

/**
 * A TressItem implementation that wraps projectComponentDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WorkpackageTreeItem extends TreeItem implements Comparable<WorkpackageTreeItem> {

    //~ Instance fields --------------------------------------------------------

    private WorkPackageDTO workpackage;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkpackageTreeItem object.
     *
     * @param  workpackage  DOCUMENT ME!
     */
    public WorkpackageTreeItem(final WorkPackageDTO workpackage) {
        this.workpackage = workpackage;
        setTitle(workpackage.getName());
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the workpackage
     */
    public WorkPackageDTO getWorkpackage() {
        return workpackage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workpackage  the workpackage to set
     */
    public void setWorkpackage(final WorkPackageDTO workpackage) {
        this.workpackage = workpackage;
        setTitle(workpackage.getName());
    }

    @Override
    public int compareTo(final WorkpackageTreeItem o) {
        return getHTML().compareTo(o.getHTML());
    }
}
