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
package de.cismet.projecttracker.client.types;

import java.io.Serializable;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TimePeriod implements Serializable {

    //~ Instance fields --------------------------------------------------------

    private Date start;
    private Date end;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimePeriod object.
     */
    public TimePeriod() {
    }

    /**
     * Creates a new TimePeriod object.
     *
     * @param  start  DOCUMENT ME!
     * @param  end    DOCUMENT ME!
     */
    public TimePeriod(final Date start, final Date end) {
        this.start = start;
        this.end = end;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the start
     */
    public Date getStart() {
        return start;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  start  the start to set
     */
    public void setStart(final Date start) {
        this.start = start;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the end
     */
    public Date getEnd() {
        return end;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  end  the end to set
     */
    public void setEnd(final Date end) {
        this.end = end;
    }
}
