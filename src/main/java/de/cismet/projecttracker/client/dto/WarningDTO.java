/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WarningDTO extends BasicDTO<WarningDTO> {

    //~ Instance fields --------------------------------------------------------

    private WorkPackageDTO workPackage;
    private int level;
    private Date time;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WarningDTO object.
     */
    public WarningDTO() {
    }

    /**
     * Creates a new WarningDTO object.
     *
     * @param  id           DOCUMENT ME!
     * @param  workPackage  DOCUMENT ME!
     * @param  level        DOCUMENT ME!
     * @param  time         DOCUMENT ME!
     */
    public WarningDTO(final long id, final WorkPackageDTO workPackage, final int level, final Date time) {
        this.workPackage = workPackage;
        this.level = level;
        this.time = time;
        this.id = id;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the workPackage
     */
    public WorkPackageDTO getWorkPackage() {
        return workPackage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  workPackage  the workPackage to set
     */
    public void setWorkPackage(final WorkPackageDTO workPackage) {
        this.workPackage = workPackage;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  level  the level to set
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the time
     */
    public Date getTime() {
        return time;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  time  the time to set
     */
    public void setTime(final Date time) {
        this.time = time;
    }

    @Override
    public WarningDTO createCopy() {
        return new WarningDTO(id, workPackage, level, time);
    }

    @Override
    public void reset(final WarningDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.level = obj.level;
        this.time = obj.time;
    }
}
