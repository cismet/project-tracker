/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import de.cismet.projecttracker.client.helper.DateHelper;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author therter
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.IntSequenceGenerator.class, property="id")
public class StaffDTO extends BasicDTO<StaffDTO> implements Comparable<StaffDTO> {
    private String firstname;
    private String name;
    private int permissions;
    private String username;
    private String email;
    @JsonIgnore
    private ArrayList<ContractDTO> contracts = new ArrayList<ContractDTO>(0);
    @JsonIgnore
    private ProfileDTO profile;


     public StaffDTO() {
    }



    public StaffDTO(long id, String firstname, String name, int permissions, String username, String email, ArrayList<ContractDTO> contracts, ProfileDTO profile) {
        this.id = id;
        this.firstname = firstname;
        this.name = name;
        this.permissions = permissions;
        this.username = username;
        this.email = email;
        this.contracts = contracts;
        this.profile = profile;
    }


    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the permissions
     */
    public int getPermissions() {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(int permissions) {
        this.permissions = permissions;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the username
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param username the username to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the contracts
     */
    public ArrayList<ContractDTO> getContracts() {
        return contracts;
    }

    /**
     * @param contracts the contracts to set
     */
    public void setContracts(ArrayList<ContractDTO> contracts) {
        this.contracts = contracts;
    }

    public ProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(ProfileDTO profile) {
        this.profile = profile;
    }
    

    @Override
    public StaffDTO createCopy() {
        return new StaffDTO(id, firstname, name, permissions, username, email, contracts,profile);
    }

    @Override
    public void reset(StaffDTO obj) {
        this.id = obj.id;
        this.firstname = obj.firstname;
        this.name = obj.name;
        this.permissions = obj.permissions;
        this.username = obj.username;
        this.email = obj.email;
        this.contracts = obj.contracts;
        this.profile = obj.profile;
    }


    @JsonIgnore
    public boolean isActiveStaff() {
        Date now = new Date();
        ArrayList<ContractDTO> cont = getContracts();
        
        if (cont == null || cont.isEmpty()) {
            return true;
        }
        
        for (ContractDTO tmp : cont) {
            if (tmp.getFromdate() != null) {
                if (DateHelper.isDateGreaterOrEqual(now, tmp.getFromdate()) && 
                        (tmp.getTodate() == null || DateHelper.isDateLessOrEqual(now, tmp.getTodate())) ) {
                    return true;
                }
            }
        }
        
        return false;
    }

    
    @Override
    public int compareTo(StaffDTO o) {
        int result = firstname.compareTo(o.firstname);

        if (result == 0) {
            return name.compareTo(o.name);
        } else {
            return result;
        }
    }
}
