/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.utilities.DBManagerWrapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author therter
 */
public class FavouriteTaskStory extends RecentStory implements TaskDeleteListener {

    FlowPanelDropController widgetDropController;

    public FavouriteTaskStory() {
        widgetDropController = new FlowPanelDropController(this.recentTasks) {

            @Override
            public void onEnter(DragContext context) {
                super.onEnter(context);
            }

            @Override
            public void onDrop(DragContext context) {
//                super.onDrop(context);
                List<Widget> selectedWidgets = context.selectedWidgets;
                for (Widget w : selectedWidgets) {
                    if (w instanceof TaskNotice) {
                        final TaskNotice origNotice = (TaskNotice) w;

                        final ActivityDTO activity = origNotice.getActivity().createCopy();

                        activity.setId(0);
                        activity.setWorkinghours(0.0);
                        activity.setDay(null);
                        activity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());

                        BasicAsyncCallback favCallback = new BasicAsyncCallback<Boolean>(){

                            @Override
                            protected void afterExecution(Boolean result, boolean operationFailed) {
                                if (!operationFailed && !result) {

                                    BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {

                                        @Override
                                        protected void afterExecution(Long result, boolean operationFailed) {
                                            if (!operationFailed) {
                                                activity.setId(result);
                                                addTask(activity);
                                            }
                                        }
                                    };
                                    ProjectTrackerEntryPoint.getProjectService(true).createActivity(activity, callback);
                                }
                            }
                            
                        };
                        ProjectTrackerEntryPoint.getProjectService(true).isExisitingFavouriteTask(activity, favCallback);

                    }
                }
            }
        };
    }

    @Override
    protected void setLabels() {
        recentLab.setStyleName("TimeHeader");
        recentLab.setText("Favourite Tasks");
    }

    @Override
    public void setTaskStory(TaskStory taskStory) {
        //todo an Favs anpassen
        if (!initialised) {
            initialised = true;
            this.taskStory = taskStory;
            mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
            taskStory.initDragController(mondayDragController, null);
            //allow reordering of favourite tasks... 
//            FlowPanelDropController dropController = new FlowPanelDropController(this.recentTasks);
//            mondayDragController.registerDropController(dropController);

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

            ProjectTrackerEntryPoint.getProjectService(true).getFavouriteActivities(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
        }
    }

    @Override
    protected void addTask(ActivityDTO activity) {
        TaskNotice widget = null;

        if (activity.getKindofactivity() == ActivityDTO.ACTIVITY) {
            widget = new TaskNotice(activity);
        } else {
            widget = new HolidayTaskNotice(activity);
        }
        widget.setRedBorderEnabled(false);
        widget.addListener(this);
//        widget.addTaskNoticeListener(this);
        recentTasks.add(widget);

        mondayDragController.makeDraggable(widget, widget.getMouseHandledWidget());

    }

    public void registerDropController(PickupDragController controller) {
        controller.registerDropController(widgetDropController);
    }

    public void registerDropControllers(Collection<PickupDragController> controllers) {
        for (PickupDragController ctrl : controllers) {
            ctrl.registerDropController(widgetDropController);
        }
    }

    @Override
    public void taskDelete(Object source) {
        if (source instanceof TaskNotice) {
            TaskNotice task = (TaskNotice) source;

            this.recentTasks.remove(task);
        }
    }
}
