/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui;

import com.allen_sauer.gwt.dnd.client.*;
import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskNoticeListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ProfileDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.HolidayType;
import de.cismet.projecttracker.client.utilities.IllnessChecker;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class TaskStory extends Composite implements TaskDeleteListener, DoubleClickHandler, TaskNoticeListener {

    //~ Static fields/initializers ---------------------------------------------

    private static final String PAUSE_PROJECT = "Abwesenheit";
    private static TaskStoryUiBinder uiBinder = GWT.create(TaskStoryUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    FlowPanelWithSpacer monday;
    @UiField
    FlowPanelWithSpacer tuesday;
    @UiField
    FlowPanelWithSpacer wednesday;
    @UiField
    FlowPanelWithSpacer thursday;
    @UiField
    FlowPanelWithSpacer friday;
    @UiField
    FlowPanelWithSpacer saturday;
    @UiField
    FlowPanelWithSpacer sunday;
    @UiField
    AbsolutePanel boundaryPanel;
    private FlowPanelWithSpacer[] daysOfWeek = new FlowPanelWithSpacer[7];
    private HashMap<FlowPanelWithSpacer, List<TaskNotice>> taskMap =
        new HashMap<FlowPanelWithSpacer, List<TaskNotice>>();
    private HashMap<FlowPanelWithSpacer, PickupDragController> dragMap =
        new HashMap<FlowPanelWithSpacer, PickupDragController>();
    private HashMap<FlowPanelWithSpacer, TaskStoryController> taskStoryControllerMap =
        new HashMap<FlowPanelWithSpacer, TaskStoryController>();
    private Date firstDayOfWeek = new Date();
    private PickupDragController mondayDragController;
    private PickupDragController tuesdayDragController;
    private PickupDragController wednesdayDragController;
    private PickupDragController thursdayDragController;
    private PickupDragController fridayDragController;
    private PickupDragController saturdayDragController;
    private PickupDragController sundayDragController;
    private LockPanel lockPanel;
    private List<TaskStoryListener> listener = new ArrayList<TaskStoryListener>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TaskStory object.
     */
    public TaskStory() {
        initWidget(uiBinder.createAndBindUi(this));

        boundaryPanel.addStyleName("verticalScroller");
        daysOfWeek[0] = sunday;
        daysOfWeek[1] = monday;
        daysOfWeek[2] = tuesday;
        daysOfWeek[3] = wednesday;
        daysOfWeek[4] = thursday;
        daysOfWeek[5] = friday;
        daysOfWeek[6] = saturday;

        for (final FlowPanelWithSpacer columnPanel : daysOfWeek) {
            taskMap.put(columnPanel, new ArrayList<TaskNotice>());
        }
        this.addTaskStoryListener(new IllnessChecker(this));

//        mondayDragController = new RestorePickupDragController(RootPanel.get("contentId"), false);
//        tuesdayDragController = new RestorePickupDragController(RootPanel.get("contentId"), false);
//        wednesdayDragController = new RestorePickupDragController(RootPanel.get("contentId"), false);
//        thursdayDragController = new RestorePickupDragController(RootPanel.get("contentId"), false);
//        fridayDragController = new RestorePickupDragController(RootPanel.get("contentId"), false);
//        saturdayDragController = new RestorePickupDragController(RootPanel.get("contentId"), false);
//        sundayDragController = new RestorePickupDragController(RootPanel.get("contentId"), false);
        mondayDragController = new RestorePickupDragController(RootPanel.get(), false);
        tuesdayDragController = new RestorePickupDragController(RootPanel.get(), false);
        wednesdayDragController = new RestorePickupDragController(RootPanel.get(), false);
        thursdayDragController = new RestorePickupDragController(RootPanel.get(), false);
        fridayDragController = new RestorePickupDragController(RootPanel.get(), false);
        saturdayDragController = new RestorePickupDragController(RootPanel.get(), false);
        sundayDragController = new RestorePickupDragController(RootPanel.get(), false);

        initDragController(mondayDragController, monday);
        initDragController(tuesdayDragController, tuesday);
        initDragController(wednesdayDragController, wednesday);
        initDragController(thursdayDragController, thursday);
        initDragController(fridayDragController, friday);
        initDragController(saturdayDragController, saturday);
        initDragController(sundayDragController, sunday);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void taskDelete(final Object source) {
        if (source instanceof TaskNotice) {
            final TaskNotice task = (TaskNotice)source;

            for (final FlowPanelWithSpacer tmpPanel : taskMap.keySet()) {
                final List<TaskNotice> tmpList = taskMap.get(tmpPanel);
                if (tmpList.contains(task)) {
                    tmpList.remove(task);
                    tmpPanel.remove(task);
                    break;
                }
            }

            final TaskStoryEvent e = new TaskStoryEvent(this, task, task.getActivity().getDay());
            fireTaskNoticeDeleted(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  controller  DOCUMENT ME!
     * @param  day         DOCUMENT ME!
     */
    public void registerTaskStoryController(final TaskStoryController controller, final Date day) {
        daysOfWeek[day.getDay()].addDoubleClickHandler(controller);
        taskStoryControllerMap.put(daysOfWeek[day.getDay()], controller);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  controller  DOCUMENT ME!
     * @param  except      DOCUMENT ME!
     */
    public void initDragController(final PickupDragController controller, final FlowPanelWithSpacer except) {
        controller.setBehaviorMultipleSelection(false);
        dragMap.put(except, controller);

        for (final FlowPanelWithSpacer columnPanel : daysOfWeek) {
            if (columnPanel != except) {
                final FlowPanelDropController widgetDropController = new FlowPanelDropController(columnPanel) {

                        @Override
                        public void onDrop(final DragContext context) {
                            if (context.finalDropController == null) {
                                return;
                            }
                            final Widget dropTarget = context.finalDropController.getDropTarget();
                            final List<Widget> selectedWidgets = context.selectedWidgets;

                            if (dropTarget instanceof FlowPanelWithSpacer) {
                                final List<TaskNotice> taskList = taskMap.get((FlowPanelWithSpacer)dropTarget);

                                if ((taskList != null) && (selectedWidgets != null)) {
                                    for (final Widget tmp : selectedWidgets) {
                                        if (tmp instanceof TaskNotice) {
                                            final TaskNotice origNotice = (TaskNotice)tmp;

                                            // save the changes on the server
                                            int days = 6;

                                            if (dropTarget == monday) {
                                                days = 0;
                                            } else if (dropTarget == tuesday) {
                                                days = 1;
                                            } else if (dropTarget == wednesday) {
                                                days = 2;
                                            } else if (dropTarget == thursday) {
                                                days = 3;
                                            } else if (dropTarget == friday) {
                                                days = 4;
                                            } else if (dropTarget == saturday) {
                                                days = 5;
                                            }
                                            final ActivityDTO activity = origNotice.getActivity().createCopy();
                                            final Date newDate = (Date)firstDayOfWeek.clone();
                                            DateHelper.addDays(newDate, days);
                                            if (activity.getWorkPackage() != null) {
                                                final ProjectDTO project = activity.getWorkPackage().getProject();
                                                if (project != null) {
                                                    final ProjectPeriodDTO period = project.determineMostRecentPeriod();

                                                    if (!((period == null)
                                                                    || DateHelper.isDayInProjectPeriod(
                                                                        newDate,
                                                                        period))) {
                                                        ProjectTrackerEntryPoint.outputBox(
                                                            "Can not drop this activity here since the workpackage is out of date ( "
                                                                    + DateHelper.formatDate(period.getFromdate())
                                                                    + " - "
                                                                    + DateHelper.formatDate(period.getTodate()));
                                                        return;
                                                    }
                                                }
                                            }
                                            activity.setId(0);
                                            activity.setWorkinghours(0.0);
                                            activity.setDay(newDate);
                                            activity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());

                                            final BasicAsyncCallback<Boolean> freezeDayCallback =
                                                new BasicAsyncCallback<Boolean>() {

                                                    @Override
                                                    protected void afterExecution(final Boolean result,
                                                            final boolean operationFailed) {
                                                        if (!operationFailed) {
                                                            if (!result) {
                                                                if (taskList.isEmpty()
                                                                            && (activity.getWorkPackage().getId()
                                                                                != ActivityDTO.PAUSE_ID)) {
                                                                    addPause(newDate);
                                                                }

                                                                final BasicAsyncCallback<Long> callback =
                                                                    new BasicAsyncCallback<Long>() {

                                                                        @Override
                                                                        protected void afterExecution(final Long result,
                                                                                final boolean operationFailed) {
                                                                            if (!operationFailed) {
                                                                                activity.setId(result);
                                                                                addTask(
                                                                                    activity,
                                                                                    (FlowPanelWithSpacer)dropTarget);
                                                                            }
                                                                        }
                                                                    };
                                                                activity.setDay(newDate);
                                                                ProjectTrackerEntryPoint.getProjectService(true)
                                                                        .createActivity(activity, callback);
                                                            }
                                                        }
                                                    }
                                                };

                                            ProjectTrackerEntryPoint.getProjectService(true)
                                                    .isDayLocked(newDate, activity.getStaff(), freezeDayCallback);
                                        }
                                    }
                                }
                            }
                        }
                    };
                controller.registerDropController(widgetDropController);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Collection<PickupDragController> getDragControllers() {
        return dragMap.values();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public AbsolutePanel getBoundaryPanel() {
        return boundaryPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  activity     DOCUMENT ME!
     * @param  columnPanel  DOCUMENT ME!
     */
    public void addTask(final ActivityDTO activity, final FlowPanelWithSpacer columnPanel) {
        TaskNotice widget = null;
        if (activity.getKindofactivity() == ActivityDTO.ACTIVITY) {
            widget = new TaskNotice(activity);
        } else {
            widget = new HolidayTaskNotice(activity);
        }
        widget.addListener(this);
        widget.addTaskNoticeListener(this);
        columnPanel.add(widget);

        ((HasDoubleClickHandlers)widget.getMouseHandledWidget()).addDoubleClickHandler(this);
        dragMap.get(columnPanel).makeDraggable(widget, widget.getMouseHandledWidget());

        taskMap.get(columnPanel).add(widget);
        final TaskStoryEvent e = new TaskStoryEvent(this, widget, widget.getActivity().getDay());
        fireTaskNoticeCreated(e);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fistDayOfWeek  DOCUMENT ME!
     * @param  activities     DOCUMENT ME!
     * @param  holidays       DOCUMENT ME!
     */
    public void setActivities(final Date fistDayOfWeek,
            final List<ActivityDTO> activities,
            final List<HolidayType> holidays) {
        this.firstDayOfWeek = fistDayOfWeek;

        removeAllTasks();
        Collections.sort(activities);

        for (final ActivityDTO tmp : activities) {
            if (tmp.getKindofactivity() == ActivityDTO.ACTIVITY) {
                addTask(tmp);
            } else if (tmp.getKindofactivity() == 3) {
                // we have found a freeze activitiy, so we have to update the checkbox of the taskStoryController
                lockPanel.setLocked(tmp.getDay(), true);
            }
        }

        for (final HolidayType tmp : holidays) {
            final int type = tmp.isHalfHoliday() ? ActivityDTO.HALF_HOLIDAY : ActivityDTO.HOLIDAY;
            addTask(new ActivityDTO(0, null, null, null, tmp.getHours(), tmp.getName(), tmp.getDate(), true, type));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  p  DOCUMENT ME!
     */
    public void setLockPanel(final LockPanel p) {
        this.lockPanel = p;
    }

    /**
     * DOCUMENT ME!
     */
    private void removeAllTasks() {
        final Date d = new Date(firstDayOfWeek.getTime());
        for (final FlowPanelWithSpacer tmp : daysOfWeek) {
            // update the freeze checkbox
            lockPanel.setLocked(d, false);
            DateHelper.addDays(d, 1);
            final List<TaskNotice> list = taskMap.get(tmp);
            for (final TaskNotice widget : list) {
                final PickupDragController c = dragMap.get(tmp);
                // warum kommt beim Aufruf der Methode makeNotDraggable immer eine Exception
// c.makeNotDraggable(widget);
                tmp.remove(widget);
            }
            list.clear();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  activity  DOCUMENT ME!
     */
    public void addTask(final ActivityDTO activity) {
        this.addTask(activity, daysOfWeek[activity.getDay().getDay()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   day  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<TaskNotice> getTasksForDay(final int day) {
        return taskMap.get(daysOfWeek[day]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notice  DOCUMENT ME!
     */
    public void modifyTask(final TaskNotice notice) {
        final DialogBox taskForm = new DialogBox();
        final StoryForm form = new StoryForm(taskForm, this, notice);
        taskForm.setWidget(form);
        taskForm.center();
    }

    @Override
    public void onDoubleClick(final DoubleClickEvent event) {
        final TaskNotice notice = (TaskNotice)((Widget)event.getSource()).getParent().getParent();
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            modifyTask(notice);
                        }
                    }
                }
            };

        ProjectTrackerEntryPoint.getProjectService(true)
                .isDayLocked(notice.getActivity().getDay(), notice.getActivity().getStaff(), callback);
    }

    @Override
    public void setHeight(final String height) {
        super.setHeight(height);

        boundaryPanel.setHeight(height);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  DOCUMENT ME!
     */
    public void addPause(final Date day) {
        final ProfileDTO profile = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff().getProfile();
        if ((profile == null) || profile.getAutoPauseEnabled()) {
            final int wd = day.getDay();

            if ((wd == 0) || (wd == 6)) {
                // no auto pause on saturdays and sundays
                return;
            }

            final WorkPackageDTO wp = getPauseWP();

            if (wp != null) {
                final ActivityDTO activity = new ActivityDTO();
                activity.setDay(day);
                activity.setKindofactivity(ActivityDTO.ACTIVITY);
                activity.setWorkPackage(wp);
                if (profile != null) {
                    activity.setWorkinghours(profile.getAutoPauseDuration());
                } else {
                    activity.setWorkinghours(1.5);
                }
                activity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());

                final BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {

                        @Override
                        protected void afterExecution(final Long result, final boolean operationFailed) {
                            if (!operationFailed) {
                                activity.setId(result);
                                addTask(activity);
                            }
                        }
                    };

                ProjectTrackerEntryPoint.getProjectService(true).createActivity(activity, callback);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private WorkPackageDTO getPauseWP() {
        final List<ProjectDTO> projects = ProjectTrackerEntryPoint.getInstance().getProjects();

        for (final ProjectDTO tmp : projects) {
            if (tmp.getName().equals(PAUSE_PROJECT)) {
                for (final WorkPackageDTO wp : tmp.getWorkPackages()) {
                    if (wp.getId() == ActivityDTO.PAUSE_ID) {
                        return wp;
                    }
                }
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addTaskStoryListener(final TaskStoryListener l) {
        listener.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeTaskStoryListener(final TaskStoryListener l) {
        listener.remove(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void fireTaskNoticeChange(final TaskStoryEvent e) {
        for (final TaskStoryListener l : listener) {
            l.taskNoticeChanged(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void fireTaskNoticeCreated(final TaskStoryEvent e) {
        for (final TaskStoryListener l : listener) {
            l.taskNoticeCreated(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  e  DOCUMENT ME!
     */
    private void fireTaskNoticeDeleted(final TaskStoryEvent e) {
        for (final TaskStoryListener l : listener) {
            l.taskNoticeDeleted(e);
        }
    }

    @Override
    public void taskChanged(final Object source) {
        final TaskStoryEvent e = new TaskStoryEvent(
                this,
                (TaskNotice)source,
                ((TaskNotice)source).getActivity().getDay());
        fireTaskNoticeChange(e);
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface TaskStoryUiBinder extends UiBinder<Widget, TaskStory> {
    }
}
