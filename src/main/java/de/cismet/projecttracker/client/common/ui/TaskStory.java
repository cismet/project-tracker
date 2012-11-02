package de.cismet.projecttracker.client.common.ui;

import com.allen_sauer.gwt.dnd.client.*;
import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.drop.FlowPanelDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskNoticeListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ProfileDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.HolidayType;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class TaskStory extends Composite implements TaskDeleteListener, DoubleClickHandler, TaskNoticeListener {

    private static final String PAUSE_PROJECT = "Abwesenheit";
    private static TaskStoryUiBinder uiBinder = GWT.create(TaskStoryUiBinder.class);
    private FlowPanelWithSpacer[] daysOfWeek = new FlowPanelWithSpacer[7];
    private HashMap<FlowPanelWithSpacer, List<TaskNotice>> taskMap = new HashMap<FlowPanelWithSpacer, List<TaskNotice>>();
    private HashMap<FlowPanelWithSpacer, PickupDragController> dragMap = new HashMap<FlowPanelWithSpacer, PickupDragController>();
    private HashMap<FlowPanelWithSpacer, TaskStoryController> taskStoryControllerMap = new HashMap<FlowPanelWithSpacer, TaskStoryController>();
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

    @Override
    public void taskDelete(Object source) {
        if (source instanceof TaskNotice) {
            TaskNotice task = (TaskNotice) source;

            for (FlowPanelWithSpacer tmpPanel : taskMap.keySet()) {
                List<TaskNotice> tmpList = taskMap.get(tmpPanel);
                if (tmpList.contains(task)) {
                    tmpList.remove(task);
                    tmpPanel.remove(task);
                    break;
                }
            }

            TaskStoryEvent e = new TaskStoryEvent(this, task, task.getActivity().getDay());
            fireTaskNoticeDeleted(e);
        }
    }

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

        for (FlowPanelWithSpacer columnPanel : daysOfWeek) {
            taskMap.put(columnPanel, new ArrayList<TaskNotice>());
        }


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

    public void registerTaskStoryController(TaskStoryController controller, Date day) {
        daysOfWeek[day.getDay()].addDoubleClickHandler(controller);
        taskStoryControllerMap.put(daysOfWeek[day.getDay()], controller);
    }

    public void initDragController(PickupDragController controller, FlowPanelWithSpacer except) {
        controller.setBehaviorMultipleSelection(false);
        dragMap.put(except, controller);

        for (FlowPanelWithSpacer columnPanel : daysOfWeek) {
            if (columnPanel != except) {
                FlowPanelDropController widgetDropController = new FlowPanelDropController(columnPanel) {
                    @Override
                    public void onDrop(DragContext context) {
                        if (context.finalDropController == null) {
                            return;
                        }
                        final Widget dropTarget = context.finalDropController.getDropTarget();
                        List<Widget> selectedWidgets = context.selectedWidgets;

                        if (dropTarget instanceof FlowPanelWithSpacer) {
                            final List<TaskNotice> taskList = taskMap.get((FlowPanelWithSpacer) dropTarget);

                            if (taskList != null && selectedWidgets != null) {
                                for (Widget tmp : selectedWidgets) {
                                    if (tmp instanceof TaskNotice) {
                                        TaskNotice origNotice = (TaskNotice) tmp;

                                        //save the changes on the server
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
                                        final Date newDate = (Date) firstDayOfWeek.clone();
                                        DateHelper.addDays(newDate, days);

                                        activity.setId(0);
                                        activity.setWorkinghours(0.0);
                                        activity.setDay(newDate);
                                        activity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());

                                        BasicAsyncCallback<Boolean> freezeDayCallback = new BasicAsyncCallback<Boolean>() {
                                            @Override
                                            protected void afterExecution(Boolean result, boolean operationFailed) {
                                                if (!operationFailed) {
                                                    if (!result || ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                                                        if (taskList.isEmpty() && activity.getWorkPackage().getId() != ActivityDTO.PAUSE_ID) {
                                                            addPause(newDate);
                                                        }

                                                        BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {
                                                            @Override
                                                            protected void afterExecution(Long result, boolean operationFailed) {
                                                                if (!operationFailed) {
                                                                    activity.setId(result);
                                                                    addTask(activity, (FlowPanelWithSpacer) dropTarget);
                                                                }
                                                            }
                                                        };
                                                        activity.setDay(newDate);
                                                        ProjectTrackerEntryPoint.getProjectService(true).createActivity(activity, callback);
                                                    }
                                                }
                                            }
                                        };

                                        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(newDate, activity.getStaff(), freezeDayCallback);

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

    public Collection<PickupDragController> getDragControllers() {
        return dragMap.values();
    }

    public AbsolutePanel getBoundaryPanel() {
        return boundaryPanel;
    }

    public void addTask(ActivityDTO activity, FlowPanelWithSpacer columnPanel) {
        TaskNotice widget = null;
        if (activity.getKindofactivity() == ActivityDTO.ACTIVITY) {
            widget = new TaskNotice(activity);
        } else {
            widget = new HolidayTaskNotice(activity);
        }
        widget.addListener(this);
        widget.addTaskNoticeListener(this);
        columnPanel.add(widget);

        ((HasDoubleClickHandlers) widget.getMouseHandledWidget()).addDoubleClickHandler(this);
        dragMap.get(columnPanel).makeDraggable(widget, widget.getMouseHandledWidget());

        taskMap.get(columnPanel).add(widget);
        TaskStoryEvent e = new TaskStoryEvent(this, widget, widget.getActivity().getDay());
        fireTaskNoticeCreated(e);
    }

    public void setActivities(Date fistDayOfWeek, List<ActivityDTO> activities, List<HolidayType> holidays) {
        this.firstDayOfWeek = fistDayOfWeek;

        removeAllTasks();
        Collections.sort(activities);

        for (ActivityDTO tmp : activities) {
            if (tmp.getKindofactivity() == ActivityDTO.ACTIVITY) {
                addTask(tmp);
            } else if (tmp.getKindofactivity() == 3) {
                // we have found a freeze activitiy, so we have to update the checkbox of the taskStoryController
                lockPanel.setLocked(tmp.getDay(), true);
            }

        }

        for (HolidayType tmp : holidays) {
            int type = tmp.isHalfHoliday() ? ActivityDTO.HALF_HOLIDAY : ActivityDTO.HOLIDAY;
            addTask(new ActivityDTO(0, null, null, null, tmp.getHours(), tmp.getName(), tmp.getDate(), true, type));
        }
    }

    public void setLockPanel(LockPanel p) {
        this.lockPanel = p;
    }

    private void removeAllTasks() {
        final Date d = new Date(firstDayOfWeek.getTime());
        for (FlowPanelWithSpacer tmp : daysOfWeek) {
            //update the freeze checkbox
            lockPanel.setLocked(d, false);
            DateHelper.addDays(d, 1);
            List<TaskNotice> list = taskMap.get(tmp);
            for (TaskNotice widget : list) {
                PickupDragController c = dragMap.get(tmp);
                // warum kommt beim Aufruf der Methode makeNotDraggable immer eine Exception 
//                c.makeNotDraggable(widget);
                tmp.remove(widget);
            }
            list.clear();
        }
    }

    public void addTask(ActivityDTO activity) {
        this.addTask(activity, daysOfWeek[activity.getDay().getDay()]);
    }

    public List<TaskNotice> getTasksForDay(int day) {
        return taskMap.get(daysOfWeek[day]);
    }

    public void modifyTask(TaskNotice notice) {
        DialogBox taskForm = new DialogBox();
        StoryForm form = new StoryForm(taskForm, this, notice);
        taskForm.setWidget(form);
        taskForm.center();
    }

    @Override
    public void onDoubleClick(DoubleClickEvent event) {
        final TaskNotice notice = (TaskNotice) ((Widget) event.getSource()).getParent().getParent();
        BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {
            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) {
                if (!operationFailed) {
                    if (!result || ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                        modifyTask(notice);
                    }
                }
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(notice.getActivity().getDay(), notice.getActivity().getStaff(), callback);
    }

    @Override
    public void setHeight(String height) {
        super.setHeight(height);

        boundaryPanel.setHeight(height);
    }

    public void addPause(Date day) {
        ProfileDTO profile = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff().getProfile();
        if (profile == null || profile.getAutoPauseEnabled()) {

            int wd = day.getDay();

            if (wd == 0 || wd == 6) {
                //no auto pause on saturdays and sundays
                return;
            }

            WorkPackageDTO wp = getPauseWP();

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
    }

    private WorkPackageDTO getPauseWP() {
        List<ProjectDTO> projects = ProjectTrackerEntryPoint.getInstance().getProjects();

        for (ProjectDTO tmp : projects) {
            if (tmp.getName().equals(PAUSE_PROJECT)) {
                for (WorkPackageDTO wp : tmp.getWorkPackages()) {
                    if (wp.getId() == ActivityDTO.PAUSE_ID) {
                        return wp;
                    }
                }
            }
        }

        return null;
    }

    public void addTaskStoryListener(TaskStoryListener l) {
        listener.add(l);
    }

    public void removeTaskStoryListener(TaskStoryListener l) {
        listener.remove(l);
    }

    private void fireTaskNoticeChange(TaskStoryEvent e) {
        for (TaskStoryListener l : listener) {
            l.taskNoticeChanged(e);
        }
    }

    private void fireTaskNoticeCreated(TaskStoryEvent e) {
        for (TaskStoryListener l : listener) {
            l.taskNoticeCreated(e);
        }
    }

    private void fireTaskNoticeDeleted(TaskStoryEvent e) {
        for (TaskStoryListener l : listener) {
            l.taskNoticeDeleted(e);
        }
    }

    @Override
    public void taskChanged(Object source) {
        TaskStoryEvent e = new TaskStoryEvent(this, (TaskNotice) source, ((TaskNotice) source).getActivity().getDay());
        fireTaskNoticeChange(e);
    }

    interface TaskStoryUiBinder extends UiBinder<Widget, TaskStory> {
    }
}
