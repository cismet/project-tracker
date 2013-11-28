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
package de.cismet.projecttracker.client.uicomps;

import com.github.gwtbootstrap.client.ui.resources.ResourceInjector;
import com.github.gwtbootstrap.datepicker.client.ui.resources.DatepickerResourceInjector;

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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import de.cismet.projecttracker.client.dto.ProfileDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.ActivityResponseType;
import de.cismet.projecttracker.client.utilities.TimeCalculator;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class SheetsPanel extends Composite implements ResizeHandler,
    ClickHandler,
    ChangeHandler,
    TaskStoryListener,
    TimeStoryListener,
    MenuListener,
    ValueChangeHandler<Date> {

    //~ Static fields/initializers ---------------------------------------------

    private static final String WEEKLY_HOURS_OF_WORK = "Total: ";
    private static final String PREVIOUS_WEEK_BALANCE = "prev. Week Bal.: ";
    private static final String ACCOUNT_BALANCE = "Acc. Bal.: ";
    private static final String WEEK_BALANCE = "Week Bal.: ";
    private static final int TRAVEL_WORK_CATEGORY = 4;

    //~ Instance fields --------------------------------------------------------

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
    private TaskStory tasks;
    private RecentStory recent = new RecentStory();
    private ExtendedRecentTaskStory allRecent = new ExtendedRecentTaskStory();
    private DailyHoursOfWork dailyHours = new DailyHoursOfWork();
    private LockPanel lockPanel = new LockPanel();
    private Label weekHoursLab = new Label(WEEKLY_HOURS_OF_WORK);
    private Label accountBalanceLab = new Label(ACCOUNT_BALANCE);
    private Label previousWeekBalLab = new Label(PREVIOUS_WEEK_BALANCE);
    private Label weekBalanceLab = new Label(WEEK_BALANCE);
    private Label weekDates = new Label();
    private Label weekLockLab = new Label("Lock Week: ");
    private long lastAccountBalanceCalc = 0;
    private SimpleCheckBox weekLockCB = new SimpleCheckBox();
//    private DateBox datePicker;
    private WeekDatePicker datePicker;
    private Button datePickerButton = new Button("<i class='icon-calendar'></i>", this);
    private boolean isDatePickerVisible = false;
    private int requestNumber = 0;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new SheetsPanel object.
     */
    public SheetsPanel() {
        tasks = new TaskStory(times);
        init();
        initWidget(mainPanel);
        setStyleName("content");
        tasks.addTaskStoryListener(this);
        times.addTimeStoryListener(this);
        tasks.setLockPanel(lockPanel);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        final Label yearLab = new Label("Year:");
        final Label weekLab = new Label("Week:");
        pageHeaderPanel.setStyleName("page-header");
        contentNodeParentPanel.setStyleName("span12");
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
        weekLockLab.setStyleName("formlabel");
        prevWeek.addStyleName("btn btn-primary pull-left span3");
        nextWeek.addStyleName("btn btn-info pull-right span3");
        final FlowPanel upperCtrlPanel = new FlowPanel();
        // Inject the styles and js for datepicker component
        ResourceInjector.configure();
//        ResourceInjector.configureWithCssFile();
        DatepickerResourceInjector.configure();
        DatepickerResourceInjector.configureWithCssFile();
        datePicker = new WeekDatePicker();
        datePicker.setFormat("dd.mm.yyyy");
        datePicker.setAutoClose(true);
        datePicker.setStyleName("datepickerTextBox");
        datePicker.setWeekStart(1);
        datePicker.addValueChangeHandler(this);
        datePicker.setValue(DateHelper.getBeginOfWeek(
                DateHelper.getYear(new Date()),
                DateHelper.getWeekOfYear(new Date())));
        final Date lastDay = new Date(datePicker.getValue().getTime());
        DateHelper.addDays(lastDay, 6);
        weekDates.setText(" - Sun.: " + DateHelper.formatShortDate(lastDay) + "." + DateHelper.getYear(lastDay));
        final Label mondayLab = new Label("Mon.:");
        mondayLab.setStyleName("formLabel");
        upperCtrlPanel.add(mondayLab);
        upperCtrlPanel.add(datePicker);
        upperCtrlPanel.add(weekDates);
        datePickerButton.setWidth("10px;");
        datePickerButton.addStyleName("btn");
        datePickerButton.setTitle("Click to select a week");
        upperCtrlPanel.add(datePickerButton);
        final FlowPanel lowerCtrlPanel = new FlowPanel();
        lowerCtrlPanel.addStyleName("lowerCtrlPanel-margin");
        lowerCtrlPanel.add(weekHoursLab);
        lowerCtrlPanel.add(weekBalanceLab);
        lowerCtrlPanel.add(weekLockLab);
        weekLockCB.setTitle("Select to lock the entire week");
        lowerCtrlPanel.add(weekLockCB);
        weekLockCB.addClickHandler(this);
        controlPanel.add(upperCtrlPanel);
        controlPanel.add(lowerCtrlPanel);
        buttonPanel.add(prevWeek);
        buttonPanel.add(nextWeek);
        buttonPanel.add(controlPanel);
        pageHeaderPanel.add(buttonPanel);
        final FlowPanel whoPrevWeekLabel = new FlowPanel();
        whoPrevWeekLabel.addStyleName("howPrevWeek pull-left pre prettyprint noxoverflow");
        whoPrevWeekLabel.add(previousWeekBalLab);
        final FlowPanel accountBalancePanel = new FlowPanel();
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSelectedWeek() {
        return DateHelper.getWeekOfYear(datePicker.getValue());
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public int getSelectedYear() {
        return DateHelper.getYear(datePicker.getValue());
    }

    @Override
    public void onResize(final ResizeEvent event) {
        int newHeight = event.getHeight() - contentNodePanel.getAbsoluteTop() - 145 - contentNodePanel.getOffsetHeight()
                    - 50;
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
    public void onClick(final ClickEvent event) {
        if (event.getSource() == prevWeek) {
            final Date d = datePicker.getValue();
            DateHelper.addDays(d, -7);
            if (d.getDay() != 1) {
                // this should never happen
                datePicker.setValue(DateHelper.getBeginOfWeek(getSelectedYear(), getSelectedWeek()));
            }
            final Date lastDay = (Date)d.clone();
            DateHelper.addDays(lastDay, 6);
            weekDates.setText(" - Sun.: " + DateHelper.formatShortDate(lastDay) + "." + DateHelper.getYear(lastDay));
            datePicker.setValue(d);
        } else if (event.getSource() == nextWeek) {
            final Date d = datePicker.getValue();
            DateHelper.addDays(d, 7);
            if (d.getDay() != 1) {
                // this should never happen
                datePicker.setValue(DateHelper.getBeginOfWeek(getSelectedYear(), getSelectedWeek()));
            }
            final Date lastDay = (Date)d.clone();
            DateHelper.addDays(lastDay, 6);
            weekDates.setText(" - Sun.: " + DateHelper.formatShortDate(lastDay) + "." + DateHelper.getYear(lastDay));
            datePicker.setValue(d);
        } else if (event.getSource() == weekLockCB) {
            // set the enable status to false to prevent the user from a new click during calculation
            weekLockCB.setEnabled(false);
            if (!weekLockCB.getValue()) {
                if (ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                    lockPanel.unlockAllDaysInWeek();
                }
            } else {
                lockPanel.lockAllDaysInWeek();
            }
            // we dont need to refresh the whole SheetsPanel since the date has not changed..
            return;
        } else if (event.getSource() == datePickerButton) {
            if (isDatePickerVisible) {
                isDatePickerVisible = false;
                datePicker.hide();
            } else {
                isDatePickerVisible = true;
                datePicker.show();
            }
        }
        refresh();
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        final Date firstDay = datePicker.getValue();
        final Date lastDay = new Date(firstDay.getTime());
        DateHelper.addDays(lastDay, 6);
        final int request = ++requestNumber;

        final BasicAsyncCallback<ActivityResponseType> callback = new BasicAsyncCallback<ActivityResponseType>() {

                @Override
                protected void afterExecution(final ActivityResponseType result, final boolean operationFailed) {
                    if (request == requestNumber) {
                        if (!operationFailed) {
                            DateHelper.addDays(lastDay, 6);
                            lockPanel.initialise(firstDay, tasks, times, weekLockCB, prevWeek, nextWeek);
                            times.setTimes(firstDay, result.getActivities());
                            taskControlPanel.initialise(firstDay, tasks, times);
                            /*
                             * set the init status to false that event fired in fact of the creation of the new
                             * activities doesnt change the recent activities
                             */
                            recent.setInitialised(false);
                            tasks.setActivities(firstDay, result.getActivities(), result.getHolidays());
                            // initialise drag and drop for TaskStory
                            favs.setTaskStory(tasks);
                            /*
                             * this method sets the init status to true
                             */
                            recent.setTaskStory(tasks);
                            allRecent.setTaskStory(tasks);
                            // initialise drag and drop for favourite panel
                            favs.registerDropController(recent.getDragController());
                            favs.registerDropController(allRecent.getDragController());
                            favs.registerDropControllers(tasks.getDragControllers());
                            dailyHours.initialise(firstDay, times, tasks);
                            refreshWeeklyHoursOfWork();
                            refreshPrevWeekBalance();
                            refreshAccountBalance();
                            // setLockComponents();

                            ResizeEvent.fire(ProjectTrackerEntryPoint.getInstance(),
                                Window.getClientWidth(),
                                Window.getClientHeight());
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true)
                .getActivityDataByWeek(ProjectTrackerEntryPoint.getInstance().getStaff(), firstDay, lastDay, callback);
    }

    @Override
    public void onChange(final ChangeEvent event) {
        refresh();
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshAccountBalance() {
        final BasicAsyncCallback<Double> callback = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result, final boolean operationFailed) {
                    if (!operationFailed) {
                        accountBalanceLab.setText(ACCOUNT_BALANCE + " " + DateHelper.doubleToHours(result) + " h");
                    }
                }
            };

        if ((System.currentTimeMillis() - lastAccountBalanceCalc) > 100) {
            lastAccountBalanceCalc = System.currentTimeMillis();
            ProjectTrackerEntryPoint.getProjectService(true)
                    .getAccountBalance(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshWeeklyHoursOfWork() {
        double hours = 0.0;
        double weekDebit = 0.0;
        for (int i = 0; i < 7; ++i) {
//            hours += times.getTimeForDay(i);
            final List<TaskNotice> taskList = tasks.getTasksForDay(i);

            for (final TaskNotice tmp : taskList) {
                if ((tmp.getActivity().getKindofactivity() == ActivityDTO.ACTIVITY)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.SPARE_TIME_ID)) {
                    hours += TimeCalculator.getWorkingHoursForActivity(tmp.getActivity());
                } else if ((tmp.getActivity().getKindofactivity() == ActivityDTO.HOLIDAY)
                            || (tmp.getActivity().getKindofactivity() == ActivityDTO.HALF_HOLIDAY)) {
                    hours += TimeCalculator.getWorkingHoursForActivity(tmp.getActivity());
                }
            }
        }

        weekHoursLab.setText(WEEKLY_HOURS_OF_WORK + " " + DateHelper.doubleToHours(hours) + " h");
        try {
            final Date d = datePicker.getValue();
            double whow = 0.0d;
            for (int i = 0; i <= 4; i++) {
                final ContractDTO co = ProjectTrackerEntryPoint.getInstance().getContractForStaff(d);
                if (co != null) {
                    whow += co.getWhow() / 5;
                }
                DateHelper.addDays(d, 1);
            }
            weekDebit = hours - whow;
        } catch (InvalidInputValuesException ex) {
            Logger.getLogger(SheetsPanel.class.getName())
                    .log(
                        Level.SEVERE,
                        "Could not get valid "
                        + "Contract for Staff "
                        + ProjectTrackerEntryPoint.getInstance().getStaff()
                        + " and Week/year"
                        + getSelectedWeek()
                        + "/"
                        + getSelectedYear(),
                        ex);
        }

        weekBalanceLab.setText(WEEK_BALANCE + " " + DateHelper.doubleToHours(weekDebit) + " h");
    }

    /**
     * DOCUMENT ME!
     */
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

        TimeCalculator.getWorkingBalanceForWeek(
            pyear,
            pweek,
            ProjectTrackerEntryPoint.getInstance().getStaff(),
            previousWeekBalLab,
            PREVIOUS_WEEK_BALANCE);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  tn  DOCUMENT ME!
     */
    private void refreshMyRecentTasks(final TaskNotice tn) {
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

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Story getTimes() {
        return times;
    }

    @Override
    public void taskNoticeCreated(final TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
        refreshMyRecentTasks(e.getTaskNotice());
    }

    @Override
    public void taskNoticeChanged(final TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
        refreshMyRecentTasks(e.getTaskNotice());
    }

    @Override
    public void taskNoticeDeleted(final TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void timeNoticeCreated(final TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void timeNoticeChanged(final TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void timeNoticeDeleted(final TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
        refreshAccountBalance();
    }

    @Override
    public void menuChangeEvent(final MenuEvent e) {
        if (e.getNumber() == TopPanel.SHEETS) {
            final Timer t = new Timer() {

                    @Override
                    public void run() {
                        RootPanel.get("contentId").clear();
                        RootPanel.get("contentId").add(SheetsPanel.this);
                        setLockComponents();
                        refresh();
                    }
                };
            /*
             * workaround fixes the exception that is throwed if changes in the profile panel are made and then quickly
             * switched to sheets panel
             */
            t.schedule(500);
        }
    }

    @Override
    public void onValueChange(final ValueChangeEvent<Date> event) {
        isDatePickerVisible = false;
        final Date d = event.getValue();
        if (d.getDay() != 1) {
            datePicker.setValue(DateHelper.getBeginOfWeek(getSelectedYear(), getSelectedWeek()));
        }
        final Date lastDay = new Date(datePicker.getValue().getTime());
        DateHelper.addDays(lastDay, 6);
        weekDates.setText(" - Sun.: " + DateHelper.formatShortDate(lastDay) + "." + DateHelper.getYear(lastDay));
        refresh();
    }

    /**
     * DOCUMENT ME!
     */
    public void setLockComponents() {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff();
        if (staff != null) {
            final ProfileDTO profile = staff.getProfile();
            if (profile != null) {
                if (profile.getWeekLockModeEnabled()) {
                    weekLockCB.removeStyleName("noDisplay");
                    weekLockLab.removeStyleName("noDisplay");
                } else {
                    weekLockCB.addStyleName("noDisplay");
                    weekLockLab.addStyleName("noDisplay");
                }

                if (profile.getDayLockModeEnabled()) {
                    lockPanel.removeStyleName("noDisplay");
                } else {
                    lockPanel.addStyleName("noDisplay");
                }
            }
        }
    }
}
