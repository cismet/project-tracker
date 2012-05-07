package de.cismet.projecttracker.client.common.ui;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.List;

public class RecentStory extends Composite {

    private static RecentStoryUiBinder uiBinder = GWT.create(RecentStoryUiBinder.class);
    @UiField
    FlowPanelWithSpacer recentTasks;
    @UiField
    AbsolutePanel boundaryPanel;
    @UiField
    Label recentLab;
    protected PickupDragController mondayDragController;
    protected TaskStory taskStory;
    protected boolean initialised = false;

    interface RecentStoryUiBinder extends UiBinder<Widget, RecentStory> {
    }

    public RecentStory() {
        initWidget(uiBinder.createAndBindUi(this));
        setLabels();
    }

    protected void setLabels() {
        recentLab.setStyleName("TimeHeader");
        recentLab.setText("My Recent Tasks");
    }

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
                            addTask(activity);
                        }
                    }
                }
            };

            ProjectTrackerEntryPoint.getProjectService(true).getLastActivitiesForUser(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
        }
    }

    protected void addTask(ActivityDTO activity) {
        TaskNotice widget = new TaskNotice(activity, true);
        recentTasks.add(widget);

        mondayDragController.makeDraggable(widget, widget.getMouseHandledWidget());
    }

    public PickupDragController getDragController() {
        return mondayDragController;
    }
}
