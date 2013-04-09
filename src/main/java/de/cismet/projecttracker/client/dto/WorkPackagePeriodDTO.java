/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.dto;

import java.util.Date;

import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class WorkPackagePeriodDTO extends BasicDTO<WorkPackagePeriodDTO> implements Comparable<Object> {

    //~ Instance fields --------------------------------------------------------

    private WorkPackageDTO workPackage;
    private Date fromdate;
    private Date todate;
    private Date asof;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new WorkPackagePeriodDTO object.
     */
    public WorkPackagePeriodDTO() {
    }

    /**
     * Creates a new WorkPackagePeriodDTO object.
     *
     * @param  id           DOCUMENT ME!
     * @param  workPackage  DOCUMENT ME!
     * @param  fromdate     DOCUMENT ME!
     * @param  todate       DOCUMENT ME!
     * @param  asof         DOCUMENT ME!
     */
    public WorkPackagePeriodDTO(final long id,
            final WorkPackageDTO workPackage,
            final Date fromdate,
            final Date todate,
            final Date asof) {
        this.id = id;
        this.workPackage = workPackage;
        this.fromdate = fromdate;
        this.todate = todate;
        this.asof = asof;
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
     * @return  the fromdate
     */
    public Date getFromdate() {
        return fromdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fromdate  the fromdate to set
     */
    public void setFromdate(final Date fromdate) {
        this.fromdate = fromdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the todate
     */
    public Date getTodate() {
        return todate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  todate  the todate to set
     */
    public void setTodate(final Date todate) {
        this.todate = todate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the asof
     */
    public Date getAsof() {
        return asof;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  asof  the asof to set
     */
    public void setAsof(final Date asof) {
        this.asof = asof;
    }

    @Override
    public WorkPackagePeriodDTO createCopy() {
        return new WorkPackagePeriodDTO(id, workPackage, fromdate, todate, asof);
    }

    @Override
    public void reset(final WorkPackagePeriodDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.fromdate = obj.fromdate;
        this.todate = obj.todate;
        this.asof = obj.asof;
    }
    @Override
    public String toString() {
        final MessageConstants messages = getMessagesInstance();

        // the MessageConstants instance is only available in the client mode
        if (messages != null) {
            return messages.periodMessage(DateHelper.formatDateTime(asof),
                    DateHelper.formatDate(fromdate),
                    DateHelper.formatDate(todate));
        } else {
            return super.toString();
        }
    }

    @Override
    public int compareTo(final Object o) {
        if (o instanceof WorkPackagePeriodDTO) {
            final long result = asof.getTime() - ((WorkPackagePeriodDTO)o).asof.getTime();
            return (int)Math.signum(result);
        } else if (o instanceof Date) {
            final long result = asof.getTime() - ((Date)o).getTime();
            return (int)Math.signum(result);
        } else {
            return 0;
        }
    }
}
