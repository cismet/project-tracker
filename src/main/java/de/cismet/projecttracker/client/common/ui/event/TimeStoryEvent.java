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
package de.cismet.projecttracker.client.common.ui.event;

import java.util.Date;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TimeStoryEvent {

    //~ Instance fields --------------------------------------------------------

    private Object source;
    private boolean firstTimeNotice;
    private Date day;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeStoryEvent object.
     */
    public TimeStoryEvent() {
    }

    /**
     * Creates a new TimeStoryEvent object.
     *
     * @param  source           DOCUMENT ME!
     * @param  firstTimeNotice  DOCUMENT ME!
     * @param  day              DOCUMENT ME!
     */
    public TimeStoryEvent(final Object source, final boolean firstTimeNotice, final Date day) {
        this.source = source;
        this.firstTimeNotice = firstTimeNotice;
        this.day = day;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  source  the source to set
     */
    public void setSource(final Object source) {
        this.source = source;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the firstTimeNotice
     */
    public boolean isFirstTimeNotice() {
        return firstTimeNotice;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  firstTimeNotice  the firstTimeNotice to set
     */
    public void setFirstTimeNotice(final boolean firstTimeNotice) {
        this.firstTimeNotice = firstTimeNotice;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the day
     */
    public Date getDay() {
        return day;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  the day to set
     */
    public void setDay(final Date day) {
        this.day = day;
    }
}
