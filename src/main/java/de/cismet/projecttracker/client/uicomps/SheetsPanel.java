/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.uicomps;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.*;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskStoryListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.ActivityResponseType;
import java.util.Date;
import java.util.List;

/**
 *
 * @author therter
 */
public class SheetsPanel extends Composite implements ResizeHandler, ClickHandler, ChangeHandler, TaskStoryListener, TimeStoryListener, MenuListener {

    private static final String WEEKLY_HOURS_OF_WORK = "Total: ";
    private static final String ACCOUNT_BALANCE = "Account Balance: ";
    private FlowPanel mainPanel = new FlowPanel();
    private FlowPanel pageHeaderPanel = new FlowPanel();
    private FlowPanel contentNodeParentPanel = new FlowPanel();
    private FlowPanel contentNodePanel = new FlowPanel();
    private FlowPanel taskContentNodePanel = new FlowPanel();
    private FlowPanel controlPanel = new FlowPanel();
    private FlowPanel buttonPanel = new FlowPanel();
    private TaskStoryControllerPanel taskControlPanel = new TaskStoryControllerPanel();
    private ListBox year = new ListBox();
    private ListBox week = new ListBox();
    private Button prevWeek = new Button("previous week", this);
    private Button nextWeek = new Button("next week", this);
    private Story times = new Story();
    private FavouriteTaskStory favs = new FavouriteTaskStory();
    private TaskStory tasks = new TaskStory(favs);
    private RecentStory recent = new RecentStory();
    private ExtendedRecentTaskStory allRecent = new ExtendedRecentTaskStory();
    private DailyHoursOfWork dailyHours = new DailyHoursOfWork();
    private Label weekHoursLab = new Label(WEEKLY_HOURS_OF_WORK);
    private Label accountBalanceLab = new Label(ACCOUNT_BALANCE);

    public SheetsPanel() {
        init();
        initWidget(mainPanel);
        setStyleName("content");
        tasks.addTaskStoryListener(this);
        times.addTimeStoryListener(this);
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
        contentNodeParentPanel.add(taskContentNodePanel);
        contentNodeParentPanel.add(taskControlPanel);
        controlPanel.setStyleName("pull-center");
        year.setStyleName("span2 inlineComponent");
        week.setStyleName("mini inlineComponent");
        yearLab.setStyleName("formLabel");
        weekLab.setStyleName("formLabel");
        weekHoursLab.setStyleName("formLabel totalLab");
        accountBalanceLab.setStyleName("formLabel accountBalanceLab");
        prevWeek.addStyleName("btn primary pull-left span3");
        nextWeek.addStyleName("btn info pull-right span3");
        controlPanel.add(yearLab);
        controlPanel.add(year);
        controlPanel.add(weekLab);
        controlPanel.add(week);
        controlPanel.add(weekHoursLab);
        controlPanel.add(accountBalanceLab);
        buttonPanel.add(prevWeek);
        buttonPanel.add(nextWeek);
        buttonPanel.add(controlPanel);
        pageHeaderPanel.add(buttonPanel);
        mainPanel.add(allRecent);
        mainPanel.add(recent);
        mainPanel.add(favs);
        mainPanel.add(pageHeaderPanel);
        mainPanel.add(contentNodeParentPanel);

        year.addChangeHandler(this);
        week.addChangeHandler(this);

        fillYear();
        fillWeek();
    }

    private void fillYear() {
        int currentYear = (new Date()).getYear() + 1900;

        year.clear();
        for (int i = (currentYear + 1); i >= 2009; --i) {
            year.addItem("" + i);
        }

        year.setSelectedIndex(year.getSelectedIndex() + 1);
    }

    private void fillWeek() {
        int selectedWeek = getSelectedWeek();
        Date date = new Date(getSelectedYear() - 1900, 0, 1);
        int weekCount = DateHelper.getWeekCountForYear(date.getYear() + 1900);
        week.clear();

        if (selectedWeek == -1) {
            selectedWeek = DateHelper.getCurrentWeek();
        }

        for (int i = 1; i <= weekCount; ++i) {
            week.addItem("" + i);
        }

        week.setSelectedIndex(selectedWeek - 1);
    }

    public int getSelectedWeek() {
        try {
            if (week.getSelectedIndex() > -1) {
                return Integer.parseInt(week.getItemText(week.getSelectedIndex()));
            }
        } catch (NumberFormatException e) {
            //no week selected. return -1
        }
        return -1;
    }

    public int getSelectedYear() {
        try {
            if (year.getSelectedIndex() > -1) {
                return Integer.parseInt(year.getItemText(year.getSelectedIndex()));
            }
        } catch (NumberFormatException e) {
            ProjectTrackerEntryPoint.outputBox("NumberFormatException for number: " + year.getItemText(year.getSelectedIndex()));
        }
        return (new Date()).getYear() + 1900;
    }

    @Override
    public void onResize(ResizeEvent event) {
        int newHeight = event.getHeight() - contentNodePanel.getAbsoluteTop() - 145 - contentNodePanel.getOffsetHeight() - 50;
        if (newHeight < 150) {
            newHeight = 150;
        }
        taskContentNodePanel.setHeight((newHeight) + "px");
        tasks.setHeight((newHeight) + "px");
        newHeight += contentNodePanel.getOffsetHeight() + 105;
        recent.setHeight(newHeight + "px");
        allRecent.setHeight(newHeight + "px");
        favs.setHeight(newHeight + "px");
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == prevWeek) {
            if (week.getSelectedIndex() > 0) {
                week.setSelectedIndex(week.getSelectedIndex() - 1);
            } else {
                if ((year.getSelectedIndex() + 1) < year.getItemCount()) {
                    year.setSelectedIndex(year.getSelectedIndex() + 1);
                    fillWeek();
                    week.setSelectedIndex(week.getItemCount() - 1);
                }
            }
        } else if (event.getSource() == nextWeek) {
            if ((week.getSelectedIndex() + 1) < week.getItemCount()) {
                week.setSelectedIndex(week.getSelectedIndex() + 1);
            } else {
                if (year.getSelectedIndex() > 0) {
                    year.setSelectedIndex(year.getSelectedIndex() - 1);
                    fillWeek();
                    week.setSelectedIndex(0);
                }
            }
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
                    times.setTimes(firstDay, result.getActivities());
                    taskControlPanel.initialise(firstDay, tasks, times);
                    tasks.setActivities(firstDay, result.getActivities(), result.getHolidays());
                    recent.setTaskStory(tasks);
                    allRecent.setTaskStory(tasks);
                    dailyHours.initialise(firstDay, times, tasks);
                    refreshWeeklyHoursOfWork();
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
        ProjectTrackerEntryPoint.getProjectService(true).getAccountBalance(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }

    private void refreshWeeklyHoursOfWork() {
        double hours = 0.0;

        for (int i = 0; i < 7; ++i) {
            hours += times.getTimeForDay(i);
            List<TaskNotice> taskList = tasks.getTasksForDay(i);

            for (TaskNotice tmp : taskList) {
                if (tmp.getActivity().getKindofactivity() == ActivityDTO.HOLIDAY) {
                    hours += tmp.getActivity().getWorkinghours();
                } else if (tmp.getActivity().getKindofactivity() == ActivityDTO.HALF_HOLIDAY) {
                    hours += tmp.getActivity().getWorkinghours();
                } else {
                    if (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID) {
                        hours -= tmp.getActivity().getWorkinghours();
                    } else if (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.HOLIDAY_ID
                            || tmp.getActivity().getWorkPackage().getId() == ActivityDTO.ILLNESS_ID
                            || tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPECIAL_HOLIDAY_ID) {
                        hours += tmp.getActivity().getWorkinghours();
                    }
                }
            }
        }
        weekHoursLab.setText(WEEKLY_HOURS_OF_WORK + " " + DateHelper.doubleToHours(hours) + " h");
    }

    @Override
    public void taskNoticeCreated(TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
    }

    @Override
    public void taskNoticeChanged(TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
    }

    @Override
    public void taskNoticeDeleted(TaskStoryEvent e) {
        refreshWeeklyHoursOfWork();
    }

    @Override
    public void timeNoticeCreated(TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
    }

    @Override
    public void timeNoticeChanged(TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
    }

    @Override
    public void timeNoticeDeleted(TimeStoryEvent e) {
        refreshWeeklyHoursOfWork();
    }

    @Override
    public void menuChangeEvent(MenuEvent e) {
        refresh();
    }
}
