/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.helper;

//import de.cismet.projecttracker.client.common.ui.ChangeableTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;
import de.cismet.projecttracker.client.types.WorkpackageTreeItem;

/**
 * This class contains some static methods, which are used by the GUI classes.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class GUIHelper {

    //~ Methods ----------------------------------------------------------------

// /**
// * fills the given tree object with the workpackages of the given projects
// * @param project
// * @param workpackageTree
// */
// public static void fillWorkpackageTree(ProjectDTO project, ChangeableTree workpackageTree) {
// if (project != null) {
// workpackageTree.removeItems();
// ArrayList<WorkPackageDTO> workpackages = project.getWorkPackages();
// if (workpackages != null) {
// ArrayList<WorkpackageTreeItem> unsortedItems = new ArrayList<WorkpackageTreeItem>();
// ArrayList<WorkpackageTreeItem> sortedItems = new ArrayList<WorkpackageTreeItem>();
// for (WorkPackageDTO tmp : workpackages) {
// WorkpackageTreeItem item = new WorkpackageTreeItem(tmp);
// item.setHTML(tmp.getAbbreviation());
// unsortedItems.add(item);
// }
//
// Collections.sort( unsortedItems );
// while (unsortedItems.size() > 0) {
// for (int i = 0; i < unsortedItems.size(); ++i) {
// WorkpackageTreeItem tmp = unsortedItems.get(i);
// WorkPackageDTO work = tmp.getWorkpackage();
//
// if (work.getWorkPackage() != null && work.getWorkPackage().getId() > 0) {
// //this item has a parent, so search for the parent and append this item to the parent
// int parentIndex = indexOfWorkPackage(sortedItems, work.getWorkPackage());
//
// if (parentIndex != -1) {
// // add the item to its parent and to the bottom
// // of the list, so it can be found when its childs search for it
// sortedItems.get(parentIndex).addItem(tmp);
// sortedItems.add(tmp);
// unsortedItems.remove(tmp);
// --i;
// }
// } else {
// //this item has no parent
// sortedItems.add(tmp);
// unsortedItems.remove(tmp);
// --i;
// }
// }
// }
//
// Collections.sort( sortedItems );
// for (WorkpackageTreeItem tmp : sortedItems) {
// if (tmp.getWorkpackage().getWorkPackage() == null || tmp.getWorkpackage().getWorkPackage().getId() == 0) {
// // save the item only, if it has no parent. Otherwise this item will be added through his parent
// workpackageTree.addItem(tmp);
// }
// }
// }
// }
// }

    /**
     * DOCUMENT ME!
     *
     * @param   wk    DOCUMENT ME!
     * @param   time  DOCUMENT ME!
     *
     * @return  the period, which are used at the given time by the given work package
     */
    public static WorkPackagePeriodDTO getPeriodForTime(final WorkPackageDTO wk, final Date time) {
        WorkPackagePeriodDTO currentPeriod = null;

        if ((wk.getWorkPackagePeriods() != null) && (wk.getWorkPackagePeriods().size() > 0)) {
            final ArrayList<WorkPackagePeriodDTO> periods = new ArrayList<WorkPackagePeriodDTO>();
            for (final WorkPackagePeriodDTO tmp : wk.getWorkPackagePeriods()) {
                periods.add(tmp);
            }
            Collections.sort(periods);
            int searchRes = Collections.binarySearch(periods, time);

            if (searchRes > 0) {
                currentPeriod = periods.get(searchRes);
            } else {
                searchRes = (searchRes + 1) * (-1);
                if (searchRes != 0) {
                    // if searchRes == 0, the first period that was created after the given time will be returned.
                    // Otherwise, the period, that was created closest before the given time will be returned.
                    --searchRes;
                }
                currentPeriod = periods.get(searchRes);
            }
        }

        return currentPeriod;
    }

    /**
     * the index of the given work package within the given list.
     *
     * @param   itemList  DOCUMENT ME!
     * @param   work      DOCUMENT ME!
     *
     * @return  -1 if the work package is not contained in the list and the index of the work package otherwise
     */
    private static int indexOfWorkPackage(final ArrayList<WorkpackageTreeItem> itemList, final WorkPackageDTO work) {
        for (int i = 0; i < itemList.size(); ++i) {
            if (itemList.get(i).getWorkpackage().getId() == work.getId()) {
                return i;
            }
        }

        return -1;
    }
}
