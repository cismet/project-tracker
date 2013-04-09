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
public class WorkPackageProgressDTO extends BasicDTO<WorkPackageProgressDTO> {

    //~ Instance fields --------------------------------------------------------

    private WorkPackageDTO workPackage;
    private Date time;
    private int progress;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkPackageProgressDTO object.
     */
    public WorkPackageProgressDTO() {
    }

    /**
     * Creates a new WorkPackageProgressDTO object.
     *
     * @param  id           DOCUMENT ME!
     * @param  workPackage  DOCUMENT ME!
     * @param  time         DOCUMENT ME!
     * @param  progress     DOCUMENT ME!
     */
    public WorkPackageProgressDTO(final long id,
            final WorkPackageDTO workPackage,
            final Date time,
            final int progress) {
        this.id = id;
        this.workPackage = workPackage;
        this.time = time;
        this.progress = progress;
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

    /**
     * DOCUMENT ME!
     *
     * @return  the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  progress  the progress to set
     */
    public void setProgress(final int progress) {
        this.progress = progress;
    }

    @Override
    public WorkPackageProgressDTO createCopy() {
        return new WorkPackageProgressDTO(id, workPackage, time, progress);
    }

    @Override
    public void reset(final WorkPackageProgressDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.time = obj.time;
        this.progress = obj.progress;
    }
}
