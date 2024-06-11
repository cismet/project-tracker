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

    private boolean weekLockModeEnabled;
    private boolean dayLockModeEnabled;
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
    public ProfileDTO(final boolean weekLockModeEnabled,
            final boolean dayLockModeEnabled,
            final double residualVacation) {
        this.weekLockModeEnabled = weekLockModeEnabled;
        this.dayLockModeEnabled = dayLockModeEnabled;
        this.residualVacation = residualVacation;
    }

    //~ Methods ----------------------------------------------------------------


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
                weekLockModeEnabled,
                dayLockModeEnabled,
                residualVacation);
    }

    @Override
    public void reset(final ProfileDTO obj) {
        this.weekLockModeEnabled = obj.weekLockModeEnabled;
        this.dayLockModeEnabled = obj.dayLockModeEnabled;
        this.residualVacation = obj.residualVacation;
    }
}
