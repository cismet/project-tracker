/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.utilities;

import com.google.gwt.user.client.Timer;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.ServerDataChangeListener;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.utilities.DBManagerWrapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author therter
 */
public class ChangeChecker extends Timer {

    private static final int TIME_INTERVAL = 10;
    private static final ChangeChecker INSTANCE = new ChangeChecker();
    private List<ServerDataChangeListener> listenerList = new ArrayList<ServerDataChangeListener>();
    private static final String LAST_MODIFICATION_QUERY = "select lastmodification from staff where staffid = %1$s";

    private ChangeChecker() {
        startChecks();
    }

    public static ChangeChecker getInstance() {
        return INSTANCE;
    }

    public void addListener(ServerDataChangeListener listener) {
        listenerList.add(listener);
    }

    public void removeListener(ServerDataChangeListener listener) {
        listenerList.remove(listener);
    }

    private void startChecks() {
        scheduleRepeating(TIME_INTERVAL * 1000);
    }

    @Override
    public void run() {
        BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) {
                if (result) {
                    for (ServerDataChangeListener l : listenerList) {
                        l.dataChanged();
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isDataChanged(callback);

    }
}
