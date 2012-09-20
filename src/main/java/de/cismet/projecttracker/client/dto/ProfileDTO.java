/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.dto;

/**
 *
 * @author dmeiers
 */
public class ProfileDTO extends BasicDTO<ProfileDTO> {

    private boolean autoPauseEnabled;
    private boolean weekLockMode;
    private long id;
    
    public ProfileDTO(){
    }

    public ProfileDTO( boolean autoPauseEnabled, boolean weekLockMode) {
        this.autoPauseEnabled = autoPauseEnabled;
        this.weekLockMode = weekLockMode;
    }

    public boolean getAutoPauseEnabled() {
        return autoPauseEnabled;
    }

    public void setAutoPauseEnabled(boolean autoPauseEnabled) {
        this.autoPauseEnabled = autoPauseEnabled;
    }

    public boolean getWeekLockMode() {
        return weekLockMode;
    }

    public void setWeekLockMode(boolean weekLockMode) {
        this.weekLockMode = weekLockMode;
    }

    @Override
    public ProfileDTO createCopy() {
        return new ProfileDTO(autoPauseEnabled, weekLockMode);
    }

    @Override
    public void reset(ProfileDTO obj) {
        this.autoPauseEnabled = obj.autoPauseEnabled;
        this.weekLockMode = obj.autoPauseEnabled;
    }
}
