/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.helper;

import java.util.Comparator;

import de.cismet.projecttracker.client.dto.WorkPackageDTO;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WorkpackageDTOComparator implements Comparator<WorkPackageDTO> {

    //~ Methods ----------------------------------------------------------------

    @Override
    public int compare(final WorkPackageDTO o1, final WorkPackageDTO o2) {
        if (isSameLevel(o1, o2)) {
            return o1.compareTo(o2);
        }

        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   o1  DOCUMENT ME!
     * @param   o2  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isSameLevel(final WorkPackageDTO o1, final WorkPackageDTO o2) {
        if (((o1.getWorkPackage() == null) && (o2.getWorkPackage() == null))
                    || (o1.getWorkPackage().equals(o2.getWorkPackage()))) {
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
    private boolean isSubworkPackageOf(final WorkPackageDTO possibleParent, final WorkPackageDTO possibleChild) {
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
                subWorkPackage |= isSubworkPackageOf(tmp, possibleChild);
            }
        }

        return subWorkPackage;
    }
}
