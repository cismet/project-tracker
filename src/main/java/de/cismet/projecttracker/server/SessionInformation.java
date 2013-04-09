/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.server;

import de.cismet.projecttracker.report.db.entities.Staff;

/**
 * Instances of this class contains information about the users, which are currently logged in.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SessionInformation {

    //~ Instance fields --------------------------------------------------------

    private Staff currentUser;
    private boolean admin;
    private boolean dataChanged = false;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the currentUser
     */
    public Staff getCurrentUser() {
        return currentUser;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  currentUser  the currentUser to set
     */
    public void setCurrentUser(final Staff currentUser) {
        this.currentUser = currentUser;
        admin = ((currentUser.getPermissions() & 0x1) == 0x1);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the dataChanged
     */
    public boolean isDataChanged() {
        return dataChanged;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dataChanged  the dataChanged to set
     */
    public void setDataChanged(final boolean dataChanged) {
        this.dataChanged = dataChanged;
    }
}
