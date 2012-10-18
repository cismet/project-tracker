/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.uicomps;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.profile.ProfileMenue;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.common.ui.profile.ChangePasswordForm;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.ActivityResponseType;
import de.cismet.projecttracker.client.types.HolidayType;
import de.cismet.projecttracker.client.utilities.TimeCalculator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmeiers
 */
public class ProfilePanel extends Composite implements MenuListener {

    private FlowPanel mainPanel = new FlowPanel();
    private FlowPanel pageHeaderPanel = new FlowPanel();
    private FlowPanel pageHeaderAccBalPanel = new FlowPanel();
    private FlowPanel pageHeaderHolidayPanel = new FlowPanel();
    private FlowPanel contentPanel = new FlowPanel();
    private FlowPanel masterView = new FlowPanel();
    private FlowPanel detailView = new FlowPanel();
    private Image gravatar = new Image();
    private static String GRAVATAR_URL_PREFIX = "http://www.gravatar.com/avatar/";
    double remainingVacation = 0;
    //HolidayLabels
    final Label vacationPlannedContent = new Label(" Days");
    FlowPanel carryOver = new FlowPanel();
    final Label carryOverKey = new Label("Carryover:");
    final Label carryOverContent = new Label(" Days");
    final Label totalVacationContent = new Label(" Days");
    final Label holidayBalContent = new Label(" Days");
    final Label vacationTakenContent = new Label(" Days");
    //AccountBalanceLabels
    final Label accBalContent = new Label("");
    final Label w1Content = new Label("");
    final Label w2Content = new Label("");
    final Label w3Content = new Label("");

    public ProfilePanel() {
        init();
        initWidget(mainPanel);
        setStyleName("content");
    }

    private void init() {
        initPageHeader();
        contentPanel.setStyleName("profile-content");
        masterView.setStyleName("profile-master-view");
        detailView.setStyleName("profile-detail-view");
        masterView.add(new ProfileMenue(detailView));
        contentPanel.add(masterView);
        contentPanel.add(detailView);
        mainPanel.add(pageHeaderPanel);
        mainPanel.add(contentPanel);

    }

    @Override
    public void menuChangeEvent(MenuEvent e) {
        if (e.getNumber() == TopPanel.PROFILE) {
            RootPanel.get("contentId").clear();
            RootPanel.get("contentId").add(this);
            refresh();
        }
    }

    private void initPageHeader() {
        pageHeaderPanel.setStyleName("profile-page-header");
        pageHeaderAccBalPanel.setStyleName("pull-right page-header-acc-bal");
        pageHeaderHolidayPanel.setStyleName("pull-right page-header-holidays");

        gravatar.setStyleName("pull-left profile-gravatar-image");
//        ProjectTrackerEntryPoint.getInstance().
        final String email = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff().getEmail();
        if (email != null) {
            gravatar.setUrl(GRAVATAR_URL_PREFIX
                    + ProjectTrackerEntryPoint.getInstance().md5(email)
                    + "?s=110");
        }

        pageHeaderPanel.add(gravatar);

        final String s = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff().getFirstname()
                + " " + ProjectTrackerEntryPoint.getInstance().getLoggedInStaff().getName();
        final Label nameLabel = new Label(s);

        nameLabel.setStyleName("profile-name-label");
        pageHeaderPanel.add(nameLabel);
        Label accountBalanceLabel = new Label("Account Balance");
        accountBalanceLabel.setStyleName("h3 page-header-title");
        pageHeaderAccBalPanel.add(accountBalanceLabel);
        pageHeaderPanel.add(pageHeaderAccBalPanel);
        Label holidayBalanceLabel = new Label("Holiday Balance");
        holidayBalanceLabel.setStyleName("h3 page-header-title");
        pageHeaderHolidayPanel.add(holidayBalanceLabel);
        pageHeaderPanel.add(pageHeaderHolidayPanel);
        initAccountBalance();
        initHolidayBalance();

    }

    private void initAccountBalance() {
        FlowPanel w3 = new FlowPanel();
        w3.setStyleName("clear");
        final Label w3Key = new Label("current week-2:");
        w3Key.setStyleName("profile-header-label");
        w3Content.setStyleName("pull-right");
        w3.add(w3Key);
        w3.add(w3Content);
        pageHeaderAccBalPanel.add(w3);

        FlowPanel w2 = new FlowPanel();
        w2.setStyleName("clear");
        final Label w2Key = new Label("current week-1:");
        w2Key.setStyleName("profile-header-label");
        w2Content.setStyleName("pull-right");
        w2.add(w2Key);
        w2.add(w2Content);
        pageHeaderAccBalPanel.add(w2);

        final FlowPanel w1 = new FlowPanel();
        w1.setStyleName("clear");
        final Label w1Key = new Label("current week:");
        w1Key.setStyleName("profile-header-label");
        w1Content.setStyleName("pull-right");
        w1.add(w1Key);
        w1.add(w1Content);
        pageHeaderAccBalPanel.add(w1);

        HTML spacerLabel = new HTML("<hr />");
        spacerLabel.setStyleName("page-header-spacer");
        pageHeaderAccBalPanel.add(spacerLabel);
        final FlowPanel accBal = new FlowPanel();
        final Label accBalKey = new Label("Account Balance:");
//        accBalKey.setStyleName("page-header-row label-info");
        accBalKey.setStyleName("label label-info");
        accBalContent.setStyleName("pull-right");
        accBal.add(accBalKey);
        accBal.add(accBalContent);
        pageHeaderAccBalPanel.add(accBal);
    }

    private void refreshAccountBalanceLables() {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff();
        final BasicAsyncCallback<Double> callback = new BasicAsyncCallback<Double>() {

            @Override
            protected void afterExecution(Double result, boolean operationFailed) {
                if (!operationFailed) {
                    accBalContent.setText(DateHelper.doubleToHours(result) + " h");
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getAccountBalance(staff, callback);

        final Date now = new Date();
        final Date d = DateHelper.getBeginOfWeek(DateHelper.getYear(now), DateHelper.getWeekOfYear(now));

        TimeCalculator.getWorkingBalanceForWeek(DateHelper.getYear(d), DateHelper.getWeekOfYear(d), staff, w1Content, "");
        DateHelper.addDays(d, -7);
        TimeCalculator.getWorkingBalanceForWeek(DateHelper.getYear(d), DateHelper.getWeekOfYear(d), staff, w2Content, "");
        DateHelper.addDays(d, -7);
        TimeCalculator.getWorkingBalanceForWeek(DateHelper.getYear(d), DateHelper.getWeekOfYear(d), staff, w3Content, "");
    }

    private void initHolidayBalance() {
        FlowPanel total = new FlowPanel();
        total.setStyleName("clear");
        final Label totalKey = new Label("Total:");
        totalKey.setStyleName("profile-header-label");
        totalVacationContent.setStyleName("pull-right");
        total.add(totalKey);
        total.add(totalVacationContent);
        pageHeaderHolidayPanel.add(total);

        
        carryOver.setStyleName("clear");
        carryOverContent.setStyleName("pull-right profile-header-label");
        carryOver.add(carryOverKey);
        carryOver.add(carryOverContent);
        pageHeaderHolidayPanel.add(carryOver);

        FlowPanel taken = new FlowPanel();
        taken.setStyleName("clear");
        final Label takenKey = new Label("Taken:");
        takenKey.setStyleName("profile-header-labelw");

        vacationTakenContent.setStyleName("pull-right");
        taken.add(takenKey);
        taken.add(vacationTakenContent);
        pageHeaderHolidayPanel.add(taken);

        final FlowPanel planned = new FlowPanel();
        planned.setStyleName("clear");
        final Label plannedKey = new Label("Planned:");
        plannedKey.setStyleName("profile-header-label");
        vacationPlannedContent.setStyleName("pull-right");
        planned.add(plannedKey);
        planned.add(vacationPlannedContent);
        pageHeaderHolidayPanel.add(planned);


        HTML spacerLabel = new HTML("<hr />");
        spacerLabel.setStyleName("page-header-spacer");
        pageHeaderHolidayPanel.add(spacerLabel);
        final FlowPanel holidayBal = new FlowPanel();
        final Label holidayBalKey = new Label("Remaining:");
//        holidayBalKey.setStyleName("page-header-row label-info");
        holidayBalKey.setStyleName("label label-info");
        holidayBalContent.setStyleName("pull-right");
        holidayBal.add(holidayBalKey);
        holidayBal.add(holidayBalContent);
        pageHeaderHolidayPanel.add(holidayBal);
    }

    private void refreshHolidayBalanceLabels() {
        try {
            ContractDTO contract = ProjectTrackerEntryPoint.getInstance().getContractForStaff(new Date());
            remainingVacation = contract.getVacation();
            totalVacationContent.setText(contract.getVacation() + " Days");
        } catch (InvalidInputValuesException ex) {
            Logger.getLogger(ProfilePanel.class.getName()).log(Level.SEVERE, null, ex);
        }

        final BasicAsyncCallback<Double> callback = new BasicAsyncCallback<Double>() {

            @Override
            protected void afterExecution(Double result, boolean operationFailed) {
                if (!operationFailed) {
                    remainingVacation -= result;
                    holidayBalContent.setText(remainingVacation + " Days");
                    vacationTakenContent.setText("- "+result + " Days");
                }
            }
        };



        final BasicAsyncCallback<Double> cb = new BasicAsyncCallback<Double>() {

            @Override
            protected void afterExecution(Double result, boolean operationFailed) {
                if (!operationFailed) {
                    remainingVacation -= result;
                    holidayBalContent.setText(remainingVacation + " Days");
                    vacationPlannedContent.setText("- "+result + " Days");
                }
            }
        };

        final BasicAsyncCallback<Double> carryOverCallback = new BasicAsyncCallback<Double>() {

            @Override
            protected void afterExecution(Double result, boolean operationFailed) {
                if (!operationFailed) {
                    if (result > 0) {
                        remainingVacation += result;
                        carryOverContent.setText("+ "+result + " Days");
                    }else{
                        pageHeaderHolidayPanel.remove(carryOver);
                    }
                    ProjectTrackerEntryPoint.getProjectService(true).getVacationDaysTaken(new Date(), ProjectTrackerEntryPoint.getInstance().getLoggedInStaff(), callback);
                    ProjectTrackerEntryPoint.getProjectService(true).getVacationDaysPlanned(new Date(), ProjectTrackerEntryPoint.getInstance().getLoggedInStaff(), cb);
                }
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).getVacationCarryOver(new Date(), ProjectTrackerEntryPoint.getInstance().getLoggedInStaff(), carryOverCallback);

    }

    private void refresh() {
        Scheduler.get().scheduleDeferred(new Command() {

            @Override
            public void execute() {
                refreshAccountBalanceLables();
                refreshHolidayBalanceLabels();
            }
        });
    }
}