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
package de.cismet.projecttracker.client.common.ui.listener;

import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface TimeStoryListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    void timeNoticeCreated(TimeStoryEvent e);
    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    void timeNoticeChanged(TimeStoryEvent e);
    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    void timeNoticeDeleted(TimeStoryEvent e);
}
