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

import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;

/**
 * DOCUMENT ME!
 *
 * @author   thorsten
 * @version  $Revision$, $Date$
 */
public interface TaskStoryListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    void taskNoticeCreated(TaskStoryEvent e);
    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    void taskNoticeChanged(TaskStoryEvent e);
    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    void taskNoticeDeleted(TaskStoryEvent e);
}
