/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.WorkPackageDTO;

/**
 * A ListItem implementation that wraps ProjectComponentDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WorkpackageListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private WorkPackageDTO workpackage;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkpackageListItem object.
     *
     * @param  workpackage  DOCUMENT ME!
     */
    public WorkpackageListItem(final WorkPackageDTO workpackage) {
        super("" + workpackage.getId(), getName(workpackage));
        this.workpackage = workpackage;
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
        setId("" + workpackage.getId());
        setName(getName(workpackage));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   workpackage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String getName(final WorkPackageDTO workpackage) {
        WorkPackageDTO tmp = workpackage.getWorkPackage();
        String prefix = "";

        while (tmp != null) {
            prefix += "&nbsp;&nbsp;";
            tmp = tmp.getWorkPackage();
        }

        return prefix + workpackage.getName();
    }
}
