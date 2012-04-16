/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

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
            mondayDragController = new RestorePickupDragController(taskStory.getBoundaryPanel(), false);
            taskStory.initDragController(mondayDragController, null);

            BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                @Override
                protected void afterExecution(List<ActivityDTO> result, boolean operationFailed) {
                    if (!operationFailed) {
                        for (ActivityDTO activity : result) {
                            addTask(activity);
                        }
                    }
                }
            };

            ProjectTrackerEntryPoint.getProjectService(true).getLastActivitiesExceptForUser(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
        }
    }
    
    
    
}
