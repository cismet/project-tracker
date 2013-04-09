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

import java.util.Date;

import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ProjectPeriodDTO extends BasicDTO<ProjectPeriodDTO> implements Comparable<ProjectPeriodDTO> {

    //~ Instance fields --------------------------------------------------------

    private ProjectDTO project;
    private Date fromdate;
    private Date todate;
    private Date asof;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProjectPeriodDTO object.
     */
    public ProjectPeriodDTO() {
    }

    /**
     * Creates a new ProjectPeriodDTO object.
     *
     * @param  id        DOCUMENT ME!
     * @param  project   DOCUMENT ME!
     * @param  fromdate  DOCUMENT ME!
     * @param  todate    DOCUMENT ME!
     * @param  asof      DOCUMENT ME!
     */
    public ProjectPeriodDTO(final long id,
            final ProjectDTO project,
            final Date fromdate,
            final Date todate,
            final Date asof) {
        this.id = id;
        this.project = project;
        this.fromdate = fromdate;
        this.todate = todate;
        this.asof = asof;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the project
     */
    public ProjectDTO getProject() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  project  the project to set
     */
    public void setProject(final ProjectDTO project) {
        this.project = project;
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
    public ProjectPeriodDTO createCopy() {
        return new ProjectPeriodDTO(id, project, fromdate, todate, asof);
    }

    @Override
    public void reset(final ProjectPeriodDTO obj) {
        this.id = obj.id;
        this.project = obj.project;
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
    public int compareTo(final ProjectPeriodDTO o) {
        if ((o == null) || (o.asof == null)) {
            return -1;
        }
        if (asof == null) {
            return 1;
        }
        final long diff = asof.getTime() - o.asof.getTime();

        return (int)Math.signum(diff);
    }
}
