/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui;

import com.allen_sauer.gwt.dnd.client.PickupDragController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class RecentStory extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static RecentStoryUiBinder uiBinder = GWT.create(RecentStoryUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    protected PickupDragController mondayDragController;
    protected TaskStory taskStory;
    protected boolean initialised = false;
    protected ArrayList<ActivityDTO> activites = new ArrayList<ActivityDTO>();
    @UiField
    FlowPanelWithSpacer recentTasks;
    @UiField
    AbsolutePanel boundaryPanel;
    @UiField
    Label recentLab;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new RecentStory object.
     */
    public RecentStory() {
        initWidget(uiBinder.createAndBindUi(this));
        setLabels();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    protected void setLabels() {
        recentLab.setStyleName("TimeHeader");
        recentLab.setText("My Recent Tasks");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  taskStory  DOCUMENT ME!
     */
    public void setTaskStory(final TaskStory taskStory) {
        if (!initialised) {
//            initialised = true;
            this.taskStory = taskStory;
            activites.clear();
            mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
            taskStory.initDragController(mondayDragController, null);

            final BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                    @Override
                    protected void afterExecution(final List<ActivityDTO> result, final boolean operationFailed) {
                        if (!operationFailed) {
                            for (final ActivityDTO activity : result) {
                                addTask(activity);
                            }
                            initialised = true;
                        }
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true)
                    .getLastActivitiesForUser(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tn  DOCUMENT ME!
     */
    public void addTask(final TaskNotice tn) {
        if (initialised) {
            if (contains(tn.getActivity())) {
                removeCorrespondingWidget(tn.getActivity());
            }
            activites.add(tn.getActivity());
            recentTasks.insert(tn, 0);
            mondayDragController.makeDraggable(tn, tn.getMouseHandledWidget());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  activity  DOCUMENT ME!
     */
    protected void removeCorrespondingWidget(final ActivityDTO activity) {
        for (int i = 0; i < recentTasks.getWidgetCount(); i++) {
            final ActivityDTO tmp = ((TaskNotice)recentTasks.getWidget(i)).getActivity();
            if (activity.getWorkPackage().equals(tmp.getWorkPackage())
                        && activity.getDescription().equals(tmp.getDescription())) {
                recentTasks.remove(recentTasks.getWidget(i));
                activites.remove(tmp);
                return;
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   act  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected boolean contains(final ActivityDTO act) {
        for (int i = 0; i < activites.size(); i++) {
            final ActivityDTO tmp = activites.get(i);
            if (act.getWorkPackage().equals(tmp.getWorkPackage())
                        && ((act.getDescription() == tmp.getDescription())
                            || ((act.getDescription() != null) && act.getDescription().equals(tmp.getDescription())))) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  activity  DOCUMENT ME!
     */
    protected void addTask(final ActivityDTO activity) {
        final TaskNotice widget = new TaskNotice(activity, true);
        recentTasks.add(widget);
        activites.add(activity);

        mondayDragController.makeDraggable(widget, widget.getMouseHandledWidget());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public PickupDragController getDragController() {
        return mondayDragController;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  initState  DOCUMENT ME!
     */
    public void setInitialised(final boolean initState) {
        initialised = initState;
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface RecentStoryUiBinder extends UiBinder<Widget, RecentStory> {
    }
}
