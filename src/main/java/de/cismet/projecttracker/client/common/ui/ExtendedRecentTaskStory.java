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
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ExtendedRecentTaskStory extends RecentStory {

    //~ Instance fields --------------------------------------------------------

    private Timer refreshTimer;

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void setLabels() {
        recentLab.setStyleName("TimeHeader");
        recentLab.setText("Recent Tasks");
    }

    @Override
    public void setTaskStory(final TaskStory taskStory) {
        if (!initialised) {
            initialised = true;
            this.taskStory = taskStory;
            mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
            taskStory.initDragController(mondayDragController, null);
            final BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                    @Override
                    protected void afterExecution(final List<ActivityDTO> result, final boolean operationFailed) {
                        if (!operationFailed) {
                            for (final ActivityDTO activity : result) {
                                if (contains(activity)) {
                                    removeCorrespondingWidget(activity);
                                }
                                addTask(activity);
                            }
                        }
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true)
                    .getLastActivitiesExceptForUser(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);

            refreshTimer = new Timer() {

                    @Override
                    public void run() {
                        loadRecentActivites();
                    }
                };

            // Schedule the timer to run all 30 seconds.
            refreshTimer.scheduleRepeating(30000);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void loadRecentActivites() {
        mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
        taskStory.initDragController(mondayDragController, null);

        final BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                @Override
                public void onFailure(final Throwable caught) {
                    // do nothing TODO logging?
                }

                @Override
                protected void afterExecution(final List<ActivityDTO> result, final boolean operationFailed) {
                    if (!operationFailed) {
                        for (int i = 0; i < result.size(); i++) {
                            final ActivityDTO activity = result.get(i);
                            if (!contains(activity)) {
//                            removeCorrespondingWidget(activity);
                                final TaskNotice widget = new TaskNotice(activity, true);
                                recentTasks.insert(widget, 0);
                                activites.add(activity);

                                mondayDragController.makeDraggable(widget, widget.getMouseHandledWidget());
                                ;
                            }
                        }
                    } else {
                        refreshTimer.cancel();
                    }
                }
            };

        ProjectTrackerEntryPoint.getProjectService(true)
                .getLastActivitiesExceptForUser(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }
}
