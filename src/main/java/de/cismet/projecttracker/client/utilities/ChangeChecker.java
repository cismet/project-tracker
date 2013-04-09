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
package de.cismet.projecttracker.client.utilities;

import com.google.gwt.user.client.Timer;

import java.util.ArrayList;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.ServerDataChangeListener;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ChangeChecker extends Timer {

    //~ Static fields/initializers ---------------------------------------------

    private static final int TIME_INTERVAL = 10;
    private static final ChangeChecker INSTANCE = new ChangeChecker();
    private static final String LAST_MODIFICATION_QUERY = "select lastmodification from staff where staffid = %1$s";

    //~ Instance fields --------------------------------------------------------

    private List<ServerDataChangeListener> listenerList = new ArrayList<ServerDataChangeListener>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ChangeChecker object.
     */
    private ChangeChecker() {
        startChecks();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ChangeChecker getInstance() {
        return INSTANCE;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void addListener(final ServerDataChangeListener listener) {
        listenerList.add(listener);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  listener  DOCUMENT ME!
     */
    public void removeListener(final ServerDataChangeListener listener) {
        listenerList.remove(listener);
    }

    /**
     * DOCUMENT ME!
     */
    private void startChecks() {
        scheduleRepeating(TIME_INTERVAL * 1000);
    }

    @Override
    public void run() {
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                public void onFailure(final Throwable caught) {
                    // do nothing
                }

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (result) {
                            for (final ServerDataChangeListener l : listenerList) {
                                l.dataChanged();
                            }
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDataChanged(callback);
    }
}
