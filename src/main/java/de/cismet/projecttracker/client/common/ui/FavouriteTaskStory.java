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

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FavouriteTaskStory extends RecentStory implements TaskDeleteListener {

    //~ Instance fields --------------------------------------------------------

    FlowPanelDropController widgetDropController;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FavouriteTaskStory object.
     */
    public FavouriteTaskStory() {
        widgetDropController = new FlowPanelDropController(this.recentTasks) {

                @Override
                public void onEnter(final DragContext context) {
                    super.onEnter(context);
                }

                @Override
                public void onDrop(final DragContext context) {
//                super.onDrop(context);
                    final List<Widget> selectedWidgets = context.selectedWidgets;
                    for (final Widget w : selectedWidgets) {
                        if (w instanceof TaskNotice) {
                            final TaskNotice origNotice = (TaskNotice)w;

                            final ActivityDTO activity = origNotice.getActivity().createCopy();

                            activity.setId(0);
                            activity.setWorkinghours(0.0);
                            activity.setDay(null);
                            activity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());

                            final BasicAsyncCallback favCallback = new BasicAsyncCallback<Boolean>() {

                                    @Override
                                    protected void afterExecution(final Boolean result, final boolean operationFailed) {
                                        if (!operationFailed && !result) {
                                            final BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {

                                                    @Override
                                                    protected void afterExecution(final Long result,
                                                            final boolean operationFailed) {
                                                        if (!operationFailed) {
                                                            activity.setId(result);
                                                            addTask(activity);
                                                        }
                                                    }
                                                };
                                            ProjectTrackerEntryPoint.getProjectService(true)
                                                    .createActivity(activity, callback);
                                        }
                                    }
                                };
                            ProjectTrackerEntryPoint.getProjectService(true)
                                    .isExisitingFavouriteTask(activity, favCallback);
                        }
                    }
                }
            };
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    protected void setLabels() {
        recentLab.setStyleName("TimeHeader");
        recentLab.setText("Favourite Tasks");
    }

    @Override
    public void setTaskStory(final TaskStory taskStory) {
        // todo an Favs anpassen
        if (!initialised) {
            initialised = true;
            this.taskStory = taskStory;
            mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
            taskStory.initDragController(mondayDragController, null);
            // allow reordering of favourite tasks... FlowPanelDropController dropController = new
            // FlowPanelDropController(this.recentTasks); mondayDragController.registerDropController(dropController);

            final BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                    @Override
                    protected void afterExecution(final List<ActivityDTO> result, final boolean operationFailed) {
                        if (!operationFailed) {
                            for (final ActivityDTO activity : result) {
                                addTask(activity);
                            }
                        }
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true)
                    .getFavouriteActivities(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
        }
    }

    @Override
    protected void addTask(final ActivityDTO activity) {
        TaskNotice widget = null;

        if (activity.getKindofactivity() == ActivityDTO.ACTIVITY) {
            widget = new TaskNotice(activity, false, true);
        } else {
            widget = new HolidayTaskNotice(activity, true);
        }
        widget.setRedBorderEnabled(false);
        widget.addListener(this);
//        widget.addTaskNoticeListener(this);
        recentTasks.add(widget);

        mondayDragController.makeDraggable(widget, widget.getMouseHandledWidget());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  controller  DOCUMENT ME!
     */
    public void registerDropController(final PickupDragController controller) {
        controller.registerDropController(widgetDropController);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  controllers  DOCUMENT ME!
     */
    public void registerDropControllers(final Collection<PickupDragController> controllers) {
        for (final PickupDragController ctrl : controllers) {
            ctrl.registerDropController(widgetDropController);
        }
    }

    @Override
    public void taskDelete(final Object source) {
        if (source instanceof TaskNotice) {
            final TaskNotice task = (TaskNotice)source;

            this.recentTasks.remove(task);
        }
    }
}
