/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.utilities;

import com.google.gwt.user.client.Timer;
import de.cismet.projecttracker.client.common.ui.listener.ServerDataChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author therter
 */
public class ChangeChecker extends Timer {
    private static final int TIME_INTERVAL = 20;
    private static final ChangeChecker INSTANCE = new ChangeChecker();
    private List<ServerDataChangeListener> listenerList = new ArrayList<ServerDataChangeListener>();

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
        schedule(TIME_INTERVAL * 1000);
    }

    @Override
    public void run() {
        
    }
}
