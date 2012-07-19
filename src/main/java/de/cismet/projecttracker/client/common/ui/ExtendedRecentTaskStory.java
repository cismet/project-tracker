/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.List;

/**
 *
 * @author therter
 */
public class ExtendedRecentTaskStory extends RecentStory {

    @Override
    protected void setLabels() {
        recentLab.setStyleName("TimeHeader");
        recentLab.setText("Recent Tasks");
    }

    @Override
    public void setTaskStory(TaskStory taskStory) {
        if (!initialised) {
            initialised = true;
            this.taskStory = taskStory;
            mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
            taskStory.initDragController(mondayDragController, null);
            BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                @Override
                protected void afterExecution(List<ActivityDTO> result, boolean operationFailed) {
                    if (!operationFailed) {
                        for (ActivityDTO activity : result) {
                            if (contains(activity)) {
                                removeCorrespondingWidget(activity);
                            }
                            addTask(activity);
                        }
                    }
                }
            };

            ProjectTrackerEntryPoint.getProjectService(true).getLastActivitiesExceptForUser(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);

            Timer t = new Timer() {

                public void run() {
                    loadRecentActivites();
                }
            };

            // Schedule the timer to run all 30 seconds.
            t.scheduleRepeating(30000);

        }
    }

    private void loadRecentActivites() {
        mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
        taskStory.initDragController(mondayDragController, null);

        BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

            @Override
            protected void afterExecution(List<ActivityDTO> result, boolean operationFailed) {
                if (!operationFailed) {
                    for (int i = 0; i < result.size(); i++) {
                        final ActivityDTO activity = result.get(i);
                        if (!contains(activity)) {
//                            removeCorrespondingWidget(activity);
                            TaskNotice widget = new TaskNotice(activity, true);
                            recentTasks.insert(widget,0);
                            activites.add(activity);

                            mondayDragController.makeDraggable(widget, widget.getMouseHandledWidget());;
                        }
                    }
                }
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).getLastActivitiesExceptForUser(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }
}
