/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui.listener;

import java.util.Date;

/**
 * This class can be used to register parties so, that they will be notified when the user has a date selected.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public interface DateSelectionListener {

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  date  the selected date
     */
    void setDate(Date date);
}
