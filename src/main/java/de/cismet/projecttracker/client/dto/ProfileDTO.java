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
    private boolean weekLockModeEnabled;
    private boolean dayLockModeEnabled;
    private double autoPauseDuration;
    private double residualVacation;
    
    private long id;
    
    public ProfileDTO(){
    }

    public ProfileDTO( boolean autoPauseEnabled, boolean weekLockModeEnabled, boolean dayLockModeEnabled, double pauseDuration, double residualVacation) {
        this.autoPauseEnabled = autoPauseEnabled;
        this.weekLockModeEnabled = weekLockModeEnabled;
        this.dayLockModeEnabled = dayLockModeEnabled;
        this.autoPauseDuration = pauseDuration;
        this.residualVacation = residualVacation;
    }

    public boolean getAutoPauseEnabled() {
        return autoPauseEnabled;
    }

    public void setAutoPauseEnabled(boolean autoPauseEnabled) {
        this.autoPauseEnabled = autoPauseEnabled;
    }
    
     public boolean getWeekLockModeEnabled() {
        return weekLockModeEnabled;
    }

    public void setWeekLockModeEnabled(boolean weekLockModeEnabled) {
        this.weekLockModeEnabled = weekLockModeEnabled;
    }

    public boolean getDayLockModeEnabled() {
        return dayLockModeEnabled;
    }

    public void setDayLockModeEnabled(boolean dayLockModeEnabled) {
        this.dayLockModeEnabled = dayLockModeEnabled;
    }

    public double getAutoPauseDuration() {
        return autoPauseDuration;
    }

    public void setAutoPauseDuration(double autoPauseDuration) {
        this.autoPauseDuration = autoPauseDuration;
    }

    public double getResidualVacation() {
        return residualVacation;
    }

    public void setResidualVacation(double residualVacation) {
        this.residualVacation = residualVacation;
    }

    @Override
    public ProfileDTO createCopy() {
        return new ProfileDTO(autoPauseEnabled, weekLockModeEnabled, dayLockModeEnabled, autoPauseDuration, residualVacation);
    }

    @Override
    public void reset(ProfileDTO obj) {
        this.autoPauseEnabled = obj.autoPauseEnabled;
        this.weekLockModeEnabled = obj.autoPauseEnabled;
        this.dayLockModeEnabled = obj.dayLockModeEnabled;
        this.autoPauseDuration  = obj.autoPauseDuration;
        this.residualVacation = obj.residualVacation;
        
    }

}
