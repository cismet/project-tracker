/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.uicomps;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.github.gwtbootstrap.client.ui.resources.ResourceInjector;
import com.github.gwtbootstrap.datepicker.client.ui.resources.DatepickerResourceInjector;
import com.github.gwtbootstrap.datepicker.client.ui.DateBox;
import com.google.gwt.i18n.client.DateTimeFormat;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.*;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskStoryListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.ActivityResponseType;
import de.cismet.projecttracker.client.types.HolidayType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author therter
 */
public class SheetsPanel extends Composite implements ResizeHandler, ClickHandler, ChangeHandler, TaskStoryListener, TimeStoryListener, MenuListener, ValueChangeHandler<Date> {

    private static final String WEEKLY_HOURS_OF_WORK = "Total: ";
    private static final String PREVIOUS_WEEK_BALANCE = "prev. Week Bal.: ";
    private static final String ACCOUNT_BALANCE = "Acc. Bal.: ";
    private static final String WEEK_BALANCE = "Week Bal.: ";
    private FlowPanel mainPanel = new FlowPanel();
    private FlowPanel pageHeaderPanel = new FlowPanel();
    private FlowPanel contentNodeParentPanel = new FlowPanel();
    private FlowPanel contentNodePanel = new FlowPanel();
    private FlowPanel taskContentNodePanel = new FlowPanel();
    private FlowPanel controlPanel = new FlowPanel();
    private FlowPanel buttonPanel = new FlowPanel();
    private TaskStoryControllerPanel taskControlPanel = new TaskStoryControllerPanel();
    private Button prevWeek = new Button("previous week", this);
    private Button nextWeek = new Button("next week", this);
    private Story times = new Story();
    private FavouriteTaskStory favs = new FavouriteTaskStory();
    private TaskStory tasks = new TaskStory();
    private RecentStory recent = new RecentStory();
    private ExtendedRecentTaskStory allRecent = new ExtendedRecentTaskStory();
    private DailyHoursOfWork dailyHours = new DailyHoursOfWork();
    private LockPanel lockPanel = new LockPanel();
    private Label weekHoursLab = new Label(WEEKLY_HOURS_OF_WORK);
    private Label accountBalanceLab = new Label(ACCOUNT_BALANCE);
    private Label previousWeekBalLab = new Label(PREVIOUS_WEEK_BALANCE);
    private Label weekBalanceLab = new Label(WEEK_BALANCE);
    private Label weekDates = new Label();
    private long lastAccountBalanceCalc = 0;
    private static final int TRAVEL_WORK_CATEGORY = 4;
    private SimpleCheckBox weekLockCB = new SimpleCheckBox();
    private DateBox datePicker;

    public SheetsPanel() {
        init();
        initWidget(mainPanel);
        setStyleName("content");
        tasks.addTaskStoryListener(this);
        times.addTimeStoryListener(this);
        tasks.setLockPanel(lockPanel);

    }

    private void init() {
        Label yearLab = new Label("Year:");
        Label weekLab = new Label("Week:");
        pageHeaderPanel.setStyleName("page-header");
        contentNodeParentPanel.setStyleName("span16");
        recent.setStyleName("my-recent-tasks pull-left pre prettyprint noxoverflow");
        allRecent.setStyleName("recent-tasks pull-left pre prettyprint noxoverflow");
        favs.setStyleName("fav-tasks pull-right pre prettyprint noxoverflow");
        contentNodePanel.addStyleName("times-panel pre prettyprint noxoverflow");
        contentNodePanel.add(times);
        taskContentNodePanel.addStyleName("contentNode pre prettyprint verticalScroller");
        taskContentNodePanel.setWidth("802px");
        taskContentNodePanel.add(tasks);
//        contentNodeParentPanel.add(allRecent);
//        contentNodeParentPanel.add(recent);
//        contentNodeParentPanel.add(favs);
        contentNodeParentPanel.add(contentNodePanel);
        contentNodeParentPanel.add(dailyHours);
        contentNodeParentPanel.add(lockPanel);
        contentNodeParentPanel.add(taskContentNodePanel);
        contentNodeParentPanel.add(taskControlPanel);
        controlPanel.setStyleName("pull-center");
        yearLab.setStyleName("formLabel");
        weekLab.setStyleName("formLabel");
        weekDates.setStyleName("formLabel weekDates");
        weekHoursLab.setStyleName("formLabel totalLab");
        weekBalanceLab.setStyleName("formLabel accountBalanceLab");
        prevWeek.addStyleName("btn primary pull-left span3");
        nextWeek.addStyleName("btn info pull-right span3");
        FlowPanel upperCtrlPanel = new FlowPanel();
        //Inject the styles and js for datepicker component
        ResourceInjector.configure();
        DatepickerResourceInjector.configure();
        DatepickerResourceInjector.configureWithCssFile();
        datePicker = new DateBox();
        datePicker.setFormat("dd-mm-yyyy");
        datePicker.setAutoClose(true);
        datePicker.setStyleName("datepickerTextBox");
        datePicker.setWeekStart(1);
        datePicker.setTitle("Click to select a week");
        datePicker.addValueChangeHandler(this);
        Label mondayLab = new Label("Mon.:");
        mondayLab.setStyleName("formLabel");
        upperCtrlPanel.add(mondayLab);
        upperCtrlPanel.add(datePicker);
        upperCtrlPanel.add(weekDates);
        FlowPanel lowerCtrlPanel = new FlowPanel();
        lowerCtrlPanel.addStyleName("lowerCtrlPanel-margin");
        lowerCtrlPanel.add(weekHoursLab);
        lowerCtrlPanel.add(weekBalanceLab);
        lowerCtrlPanel.add(weekLockCB);
        weekLockCB.addClickHandler(this);
//        lowerCtrlPanel.add(previousWeekLab);
        controlPanel.add(upperCtrlPanel);
        controlPanel.add(lowerCtrlPanel);
        buttonPanel.add(prevWeek);
        buttonPanel.add(nextWeek);
        buttonPanel.add(controlPanel);
        pageHeaderPanel.add(buttonPanel);
        FlowPanel whoPrevWeekLabel = new FlowPanel();
        whoPrevWeekLabel.addStyleName("howPrevWeek pull-left pre prettyprint noxoverflow");
        whoPrevWeekLabel.add(previousWeekBalLab);
        FlowPanel accountBalancePanel = new FlowPanel();
        accountBalancePanel.addStyleName("accountBalance pull-right pre prettyprint noxoverflow");
        accountBalancePanel.add(accountBalanceLab);
        mainPanel.add(whoPrevWeekLabel);
        mainPanel.add(allRecent);
        mainPanel.add(recent);
        mainPanel.add(accountBalancePanel);
        mainPanel.add(favs);
        mainPanel.add(pageHeaderPanel);
        mainPanel.add(contentNodeParentPanel);

    }

    public int getSelectedWeek() {
        return DateHelper.getWeekOfYear(datePicker.getValue());
    }

    public int getSelectedYear() {
        return DateHelper.getYear(datePicker.getValue());
    }

    @Override
    public void onResize(ResizeEvent event) {
        int newHeight = event.getHeight() - contentNodePanel.getAbsoluteTop() - 145 - contentNodePanel.getOffsetHeight() - 50;
        if (newHeight < 150) {
            newHeight = 150;
        }
        taskContentNodePanel.setHeight((newHeight) + "px");
        tasks.setHeight((newHeight) + "px");
        newHeight += contentNodePanel.getOffsetHeight() + 110;
        recent.setHeight(newHeight + "px");
        allRecent.setHeight(newHeight + "px");
        favs.setHeight(newHeight + "px");
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == prevWeek) {
            final Date d = datePicker.getValue();
            DateHelper.addDays(d, -7);
            datePicker.setValue(d);
        } else if (event.getSource() == nextWeek) {
            final Date d = datePicker.getValue();
            DateHelper.addDays(d, 7);
            datePicker.setValue(d);
        } else if (event.getSource() == weekLockCB) {
            //set the enable status to false to prevent the user from a new click during calculation
            weekLockCB.setEnabled(false);
            if (!weekLockCB.getValue()) {
                if (ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                    lockPanel.unlockAllDaysInWeek();
                }
            } else {
                lockPanel.lockAllDaysInWeek();
            }
            //we dont need to refresh the whole SheetsPanel since the date has not changed..
            return;
        }
        refresh();
    }

    public void refresh() {
        final int sweek = getSelectedWeek();
        final int syear = getSelectedYear();

        BasicAsyncCallback<ActivityResponseType> callback = new BasicAsyncCallback<ActivityResponseType>() {

            @Override
            protected void afterExecution(ActivityResponseType result, boolean operationFailed) {
                if (!operationFailed) {
                    Date firstDay = DateHelper.getBeginOfWeek(syear, sweek);
                    Date lastDay = new Date(firstDay.getTime());
                    DateHelper.addDays(lastDay, 6);
                    weekDates.setText(" - Sun.:" + DateHelper.formatShortDate(lastDay) + "." + DateHelper.getYear(lastDay));
                    lockPanel.initialise(firstDay, tasks, times, weekLockCB);
                    times.setTimes(firstDay, result.getActivities());
                    taskControlPanel.initialise(firstDay, tasks, times);
                    /*
                     * set the init status to false that event fired in fact of the creation of the new activities
                     * doesnt change the recent activities
                     */
                    recent.setInitialised(false);
                    tasks.setActivities(firstDay, result.getActivities(), result.getHolidays());
                    //initialise drag and drop for TaskStory
                    favs.setTaskStory(tasks);
                    /*
                     * this method sets the init status to true
                     */
                    recent.setTaskStory(tasks);
                    allRecent.setTaskStory(tasks);
                    //initialise drag and drop for favourite panel
                    favs.registerDropController(recent.getDragController());
                    favs.registerDropController(allRecent.getDragController());
                    favs.registerDropControllers(tasks.getDragControllers());
                    dailyHours.initialise(firstDay, times, tasks);
                    refreshWeeklyHoursOfWork();
                    refreshPrevWeekBalance();
                    refreshAccountBalance();
                    ResizeEvent.fire(ProjectTrackerEntryPoint.getInstance(), Window.getClientWidth(), Window.getClientHeight());
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getActivityDataByWeek(ProjectTrackerEntryPoint.getInstance().getStaff(), syear, sweek, callback);
    }

    @Override
    public void onChange(ChangeEvent event) {
        refresh();
    }

    private void refreshAccountBalance() {
        final BasicAsyncCallback<Double> callback = new BasicAsyncCallback<Double>() {

            @Override
            protected void afterExecution(Double result, boolean operationFailed) {
                if (!operationFailed) {
                    accountBalanceLab.setText(ACCOUNT_BALANCE + " " + DateHelper.doubleToHours(result) + " h");
                }
            }
        };

        if (System.currentTimeMillis() - lastAccountBalanceCalc > 100) {
            lastAccountBalanceCalc = System.currentTimeMillis();
            ProjectTrackerEntryPoint.getProjectService(true).getAccountBalance(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
        }
    }

    private double getWorkingHoursForActivity(ActivityDTO act) {
        double hours = 0.0;
        double dhow = 0.0;

        ContractDTO contract = null;
        try {
            contract = ProjectTrackerEntryPoint.getInstance().getContractForStaff(act.getDay());
        } catch (InvalidInputValuesException ex) {
            Logger.getLogger(SheetsPanel.class.getName()).log(Level.SEVERE, "Could not get valid Contract for Staff "
                    + ProjectTrackerEntryPoint.getInstance().getStaff() + " and Date " + act.getDay(), ex);
        }
        if (contract != null) {
            dhow = contract.getWhow() / 5;
        }

        if (act.getKindofactivity() == ActivityDTO.HOLIDAY || act.getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
            hours += act.getWorkinghours();
        } else {
            if (act.getWorkPackage() != null && act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID) {
                if (act.getWorkPackage().getId() == ActivityDTO.HOLIDAY_ID || act.getWorkPackage().getId() == ActivityDTO.LECTURE_ID) {
                    if (act.getWorkinghours() == 0 && dhow > 0) {
                        hours += dhow;
                    } else {
                        hours += act.getWorkinghours();
                    }
                } else if (act.getWorkPackage().getId() == ActivityDTO.ILLNESS_ID) {
                    if (act.getWorkinghours() == 0 && dhow > 0) {
                        hours += dhow;
                    } else {
                        hours += act.getWorkinghours();
                    }
                } else if (act.getWorkCategory() != null && act.getWorkCategory().getId() == TRAVEL_WORK_CATEGORY) {
                    hours += act.getWorkinghours() / 2;
                } else {
                    hours += act.getWorkinghours();
                }
            }
        }

        return hours;
    }

    private void refreshWeeklyHoursOfWork() {
        double hours = 0.0;
        double weekDebit = 0.0;
        for (int i = 0; i < 7; ++i) {
//            hours += times.getTimeForDay(i);
            List<TaskNotice> taskList = tasks.getTasksForDay(i);

            for (TaskNotice tmp : taskList) {
                if (tmp.getActivity().getKindofactivity() == ActivityDTO.ACTIVITY && tmp.getActivity().getWorkPackage().getId() != ActivityDTO.SPARE_TIME_ID) {
                    hours += getWorkingHoursForActivity(tmp.getActivity());
                } else if (tmp.getActivity().getKindofactivity() == ActivityDTO.HOLIDAY || tmp.getActivity().getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
                    hours += getWorkingHoursForActivity(tmp.getActivity());
                }
            }
        }

        weekHoursLab.setText(WEEKLY_HOURS_OF_WORK + " " + DateHelper.doubleToHours(hours) + " h");
        try {
            ContractDTO co = ProjectTrackerEntryPoint.getInstance().getContractForStaff(getSelectedWeek(), getSelectedYear());
            if (co != null) {
                weekDebit = hours - co.getWhow();
            }
        } catch (InvalidInputValuesException ex) {
            Logger.getLogger(SheetsPanel.class.getName()).log(Level.SEVERE, "Could not get valid "
                    + "Contract for Staff " + ProjectTrackerEntryPoint.getInstance().getStaff()
                    + " and Week/year" + getSelectedWeek() + "/" + getSelectedYear(), ex);
        }

        weekBalanceLab.setText(WEEK_BALANCE + " " + DateHelper.doubleToHours(weekDebit) + " h");
    }

    private void refreshPrevWeekBalance() {
        int pweek;
        int pyear;
        if (getSelectedWeek() > 0) {
            pweek = getSelectedWeek() - 1;
            pyear = getSelectedYear();
        } else {
            pyear = getSelectedYear() + 1;
            pweek = getSelectedWeek() - 1;
        }

        BasicAsyncCallback<ActivityResponseType> callback = new BasicAsyncCallback<ActivityResponseType>() {

            @Override
            protected void afterExecution(ActivityResponseType result, boolean operationFailed) {
                double hours = 0.0;
                if (!operationFailed) {
                    for (ActivityDTO act : result.getActivities()) {
                        if (act.getKindofactivity() == ActivityDTO.ACTIVITY
                                && act.getWorkPackage() != null
                                && act.getWorkPackage().getId() != ActivityDTO.SPARE_TIME_ID
                                && act.getWorkPackage().getId() != ActivityDTO.PAUSE_ID) {
                            hours += SheetsPanel.this.getWorkingHoursForActivity(act);
                        }
                    }
                    for (HolidayType holiday : result.getHolidays()) {
                        hours += holiday.getHours();
                    }

                    double weekDebit = 0.0;
                    try {
                        weekDebit = hours - ProjectTrackerEntryPoint.getInstance().getContractForStaff(getSelectedWeek(), getSelectedYear()).getWhow();
                    } catch (InvalidInputValuesException ex) {
                        Logger.getLogger(SheetsPanel.class.getName()).log(Level.SEVERE, "Could not get valid "
                                + "Contract for Staff " + ProjectTrackerEntryPoint.getInstance().getStaff()
                                + " and Week/year" + getSelectedWeek() + "/" + getSelectedYear(), ex);
                    }
                    previousWeekBalLab.setText(PREVIOUS_WEEK_BALANCE + DateHelper.doubleToHours(weekDebit) + " h");
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getActivityDataByWeek(ProjectTrackerEntryPoint.getInstance().getStaff(), pyear, pweek, callback);
    }

    private void refreshMyRecentTasks(TaskNotice tn) {
        if (tn.getActivity().getKindofactivity() == ActivityDTO.ACTIVITY) {
            final long wpId = tn.getActivity().getWorkPackage().getId();
            if (wpId != ActivityDTO.PAUSE_ID) {
                final TaskNotice newTaskNotice = new TaskNotice(tn.getActivity(), true);

                Scheduler.get().scheduleDeferred(new Command() {

                    @Override
                    public void execute() {
                        recent.addTask(newTaskNotice);
                    }
                });
            }
        }
    }

    public Story getTimes() {
        return times;
    }

    @Override
    public void taskNoticeCreated(TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
        refreshMyRecentTasks(e.getTaskNotice());
    }

    @Override
    public void taskNoticeChanged(TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
        refreshMyRecentTasks(e.getTaskNotice());
    }

    @Override
    public void taskNoticeDeleted(TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void timeNoticeCreated(TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void timeNoticeChanged(TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void timeNoticeDeleted(TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void menuChangeEvent(MenuEvent e) {
        refresh();
    }

    @Override
    public void onValueChange(ValueChangeEvent<Date> event) {
        final Date d = event.getValue();
        if (d.getDay() != 1) {
            datePicker.setValue(DateHelper.getBeginOfWeek(getSelectedYear(), getSelectedWeek()));
        }
        refresh();
    }
}
