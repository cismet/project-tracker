package de.cismet.projecttracker.server;

import de.cismet.projecttracker.report.db.entities.Staff;


/**
 * Instances of this class contains information about the users, which are currently
 * logged in.
 * @author therter
 */
public class SessionInformation {
    private Staff currentUser;
    private boolean admin;

    /**
     * @return the currentUser
     */
    public Staff getCurrentUser() {
        return currentUser;
    }

    /**
     * @param currentUser the currentUser to set
     */
    public void setCurrentUser(Staff currentUser) {
        this.currentUser = currentUser;
        admin = ( (currentUser.getPermissions() & 0x1) == 0x1 );
    }

    /**
     * @return the admin
     */
    public boolean isAdmin() {
        return admin;
    }
}
