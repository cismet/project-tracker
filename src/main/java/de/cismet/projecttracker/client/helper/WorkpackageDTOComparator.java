/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.helper;

import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import java.util.Comparator;

/**
 *
 * @author therter
 */
public class WorkpackageDTOComparator implements Comparator<WorkPackageDTO> {

    @Override
    public int compare(WorkPackageDTO o1, WorkPackageDTO o2) {
        if ( isSameLevel(o1, o2) ) {
            return o1.compareTo(o2);
        }
        
        return 0;
    }
    
    private boolean isSameLevel(WorkPackageDTO o1, WorkPackageDTO o2) {
        if ( (o1.getWorkPackage() == null && o2.getWorkPackage() == null) || 
             (o1.getWorkPackage().equals(o2.getWorkPackage())) ) {
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isSubworkPackageOf(WorkPackageDTO possibleParent, WorkPackageDTO possibleChild) {
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
                subWorkPackage |= isSubworkPackageOf(tmp, possibleChild);
            }
        }
        
        return subWorkPackage;
    }
}
