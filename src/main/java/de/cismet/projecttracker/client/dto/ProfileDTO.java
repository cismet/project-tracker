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

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class ProfileDTO extends BasicDTO<ProfileDTO> {

    //~ Instance fields --------------------------------------------------------

    private boolean autoPauseEnabled;
    private boolean weekLockModeEnabled;
    private boolean dayLockModeEnabled;
    private double autoPauseDuration;
    private double residualVacation;

    private long id;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProfileDTO object.
     */
    public ProfileDTO() {
    }

    /**
     * Creates a new ProfileDTO object.
     *
     * @param  autoPauseEnabled     DOCUMENT ME!
     * @param  weekLockModeEnabled  DOCUMENT ME!
     * @param  dayLockModeEnabled   DOCUMENT ME!
     * @param  pauseDuration        DOCUMENT ME!
     * @param  residualVacation     DOCUMENT ME!
     */
    public ProfileDTO(final boolean autoPauseEnabled,
            final boolean weekLockModeEnabled,
            final boolean dayLockModeEnabled,
            final double pauseDuration,
            final double residualVacation) {
        this.autoPauseEnabled = autoPauseEnabled;
        this.weekLockModeEnabled = weekLockModeEnabled;
        this.dayLockModeEnabled = dayLockModeEnabled;
        this.autoPauseDuration = pauseDuration;
        this.residualVacation = residualVacation;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean getAutoPauseEnabled() {
        return autoPauseEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  autoPauseEnabled  DOCUMENT ME!
     */
    public void setAutoPauseEnabled(final boolean autoPauseEnabled) {
        this.autoPauseEnabled = autoPauseEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean getWeekLockModeEnabled() {
        return weekLockModeEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  weekLockModeEnabled  DOCUMENT ME!
     */
    public void setWeekLockModeEnabled(final boolean weekLockModeEnabled) {
        this.weekLockModeEnabled = weekLockModeEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean getDayLockModeEnabled() {
        return dayLockModeEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dayLockModeEnabled  DOCUMENT ME!
     */
    public void setDayLockModeEnabled(final boolean dayLockModeEnabled) {
        this.dayLockModeEnabled = dayLockModeEnabled;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getAutoPauseDuration() {
        return autoPauseDuration;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  autoPauseDuration  DOCUMENT ME!
     */
    public void setAutoPauseDuration(final double autoPauseDuration) {
        this.autoPauseDuration = autoPauseDuration;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getResidualVacation() {
        return residualVacation;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  residualVacation  DOCUMENT ME!
     */
    public void setResidualVacation(final double residualVacation) {
        this.residualVacation = residualVacation;
    }

    @Override
    public ProfileDTO createCopy() {
        return new ProfileDTO(
                autoPauseEnabled,
                weekLockModeEnabled,
                dayLockModeEnabled,
                autoPauseDuration,
                residualVacation);
    }

    @Override
    public void reset(final ProfileDTO obj) {
        this.autoPauseEnabled = obj.autoPauseEnabled;
        this.weekLockModeEnabled = obj.autoPauseEnabled;
        this.dayLockModeEnabled = obj.dayLockModeEnabled;
        this.autoPauseDuration = obj.autoPauseDuration;
        this.residualVacation = obj.residualVacation;
    }
}
