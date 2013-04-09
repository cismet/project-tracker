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
package de.cismet.projecttracker.client.dto;

import java.util.ArrayList;
import java.util.Date;

import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class StaffDTO extends BasicDTO<StaffDTO> implements Comparable<StaffDTO> {

    //~ Instance fields --------------------------------------------------------

    private String firstname;
    private String name;
    private int permissions;
    private String username;
    private String email;
    private ArrayList<ContractDTO> contracts = new ArrayList<ContractDTO>(0);
    private ProfileDTO profile;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StaffDTO object.
     */
    public StaffDTO() {
    }

    /**
     * Creates a new StaffDTO object.
     *
     * @param  id           DOCUMENT ME!
     * @param  firstname    DOCUMENT ME!
     * @param  name         DOCUMENT ME!
     * @param  permissions  DOCUMENT ME!
     * @param  username     DOCUMENT ME!
     * @param  email        DOCUMENT ME!
     * @param  contracts    DOCUMENT ME!
     * @param  profile      DOCUMENT ME!
     */
    public StaffDTO(final long id,
            final String firstname,
            final String name,
            final int permissions,
            final String username,
            final String email,
            final ArrayList<ContractDTO> contracts,
            final ProfileDTO profile) {
        this.id = id;
        this.firstname = firstname;
        this.name = name;
        this.permissions = permissions;
        this.username = username;
        this.email = email;
        this.contracts = contracts;
        this.profile = profile;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  firstname  the firstname to set
     */
    public void setFirstname(final String firstname) {
        this.firstname = firstname;
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
     * @return  the permissions
     */
    public int getPermissions() {
        return permissions;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  permissions  the permissions to set
     */
    public void setPermissions(final int permissions) {
        this.permissions = permissions;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  username  the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the username
     */
    public String getEmail() {
        return email;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  email  username the username to set
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the contracts
     */
    public ArrayList<ContractDTO> getContracts() {
        return contracts;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  contracts  the contracts to set
     */
    public void setContracts(final ArrayList<ContractDTO> contracts) {
        this.contracts = contracts;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ProfileDTO getProfile() {
        return profile;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  profile  DOCUMENT ME!
     */
    public void setProfile(final ProfileDTO profile) {
        this.profile = profile;
    }

    @Override
    public StaffDTO createCopy() {
        return new StaffDTO(id, firstname, name, permissions, username, email, contracts, profile);
    }

    @Override
    public void reset(final StaffDTO obj) {
        this.id = obj.id;
        this.firstname = obj.firstname;
        this.name = obj.name;
        this.permissions = obj.permissions;
        this.username = obj.username;
        this.email = obj.email;
        this.contracts = obj.contracts;
        this.profile = obj.profile;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isActiveStaff() {
        final Date now = new Date();
        final ArrayList<ContractDTO> cont = getContracts();

        if ((cont == null) || cont.isEmpty()) {
            return true;
        }

        for (final ContractDTO tmp : cont) {
            if (tmp.getFromdate() != null) {
                if (DateHelper.isDateGreaterOrEqual(now, tmp.getFromdate())
                            && ((tmp.getTodate() == null) || DateHelper.isDateLessOrEqual(now, tmp.getTodate()))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int compareTo(final StaffDTO o) {
        final int result = firstname.compareTo(o.firstname);

        if (result == 0) {
            return name.compareTo(o.name);
        } else {
            return result;
        }
    }
}
