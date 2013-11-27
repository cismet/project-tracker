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

import com.github.gwtbootstrap.client.ui.Label;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import java.util.Date;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.common.ui.profile.ProfileMenue;
import de.cismet.projecttracker.client.dto.ProfileDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.utilities.TimeCalculator;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class ProfilePanel extends Composite implements MenuListener, ChangeHandler, ResizeHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static String GRAVATAR_URL_PREFIX = "http://www.gravatar.com/avatar/";

    //~ Instance fields --------------------------------------------------------

    double remainingVacation = 0;
    // HolidayLabels
    final Label vacationPlannedContent = new Label(" Days");
    FlowPanel carryOver = new FlowPanel();
    Label carryOverKey;
    final Label carryOverContent = new Label(" Days");
    final Label totalVacationContent = new Label(" Days");
    final Label holidayBalContent = new Label(" Days");
    final Label vacationTakenContent = new Label(" Days");
    // AccountBalanceLabels
    final Label accBalContent = new Label("");
    final Label w1Content = new Label("");
    final Label w2Content = new Label("");
    final Label w3Content = new Label("");

    private FlowPanel mainPanel = new FlowPanel();
    private FlowPanel pageHeaderPanel = new FlowPanel();
    private FlowPanel pageHeaderAccBalPanel = new FlowPanel();
    private FlowPanel pageHeaderHolidayPanel = new FlowPanel();
    private FlowPanel contentPanel = new FlowPanel();
    private FlowPanel masterView = new FlowPanel();
    private FlowPanel detailView = new FlowPanel();
    private Image gravatar = new Image();
    private ProfileMenue menue;
    private Label nameLabel = new Label();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ProfilePanel object.
     */
    public ProfilePanel() {
        init();
        initWidget(mainPanel);
        setStyleName("content");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        initPageHeader();
        contentPanel.setStyleName("profile-content");
        masterView.setStyleName("profile-master-view");
        detailView.setStyleName("profile-detail-view");
        menue = new ProfileMenue(detailView);
        masterView.add(menue);
        contentPanel.add(masterView);
        contentPanel.add(detailView);
        mainPanel.add(pageHeaderPanel);
        mainPanel.add(contentPanel);
        Window.addResizeHandler(this);
        resize(Window.getClientHeight());
    }

    @Override
    public void menuChangeEvent(final MenuEvent e) {
        if (e.getNumber() == TopPanel.PROFILE) {
            RootPanel.get("contentId").clear();
            RootPanel.get("contentId").add(this);
            menue.refreshDetailContainer();
            refresh();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initPageHeader() {
        pageHeaderPanel.setStyleName("profile-page-header");
        pageHeaderAccBalPanel.setStyleName("pull-right page-header-acc-bal");
        pageHeaderHolidayPanel.setStyleName("pull-right page-header-holidays");

        gravatar.setStyleName("pull-left profile-gravatar-image");
        nameLabel.setStyleName("profile-name-label");

        refreshProfileImage();

        final Label accountBalanceLabel = new Label("Account Balance");
        accountBalanceLabel.setStyleName("h3 page-header-title");
        pageHeaderAccBalPanel.add(accountBalanceLabel);
        pageHeaderPanel.add(pageHeaderAccBalPanel);
        final Label holidayBalanceLabel = new Label("Holiday Balance");
        holidayBalanceLabel.setStyleName("h3 page-header-title");
        pageHeaderHolidayPanel.add(holidayBalanceLabel);
        pageHeaderPanel.add(pageHeaderHolidayPanel);
        initAccountBalance();
        initHolidayBalance();
    }

    /**
     * DOCUMENT ME!
     */
    private void initAccountBalance() {
        final FlowPanel w3 = new FlowPanel();
        w3.setStyleName("clear");
        final Label w3Key = new Label("current week-2:");
        w3Key.setStyleName("profile-header-label");
        w3Content.setStyleName("pull-right");
        w3.add(w3Key);
        w3.add(w3Content);
        pageHeaderAccBalPanel.add(w3);

        final FlowPanel w2 = new FlowPanel();
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

        final HTML spacerLabel = new HTML("<hr />");
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

    /**
     * DOCUMENT ME!
     */
    private void refreshAccountBalanceLables() {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
        final BasicAsyncCallback<Double> callback = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result, final boolean operationFailed) {
                    if (!operationFailed) {
                        accBalContent.setText(DateHelper.doubleToHours(result) + " h");
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).getAccountBalance(staff, callback);

        final Date now = new Date();
        final Date d = DateHelper.getBeginOfWeek(DateHelper.getYear(now), DateHelper.getWeekOfYear(now));

        TimeCalculator.getWorkingBalanceForWeek(DateHelper.getYear(d),
            DateHelper.getWeekOfYear(d),
            staff,
            w1Content,
            "");
        DateHelper.addDays(d, -7);
        TimeCalculator.getWorkingBalanceForWeek(DateHelper.getYear(d),
            DateHelper.getWeekOfYear(d),
            staff,
            w2Content,
            "");
        DateHelper.addDays(d, -7);
        TimeCalculator.getWorkingBalanceForWeek(DateHelper.getYear(d),
            DateHelper.getWeekOfYear(d),
            staff,
            w3Content,
            "");
    }

    /**
     * DOCUMENT ME!
     */
    private void initHolidayBalance() {
        final FlowPanel total = new FlowPanel();
        total.setStyleName("clear");
        final Label totalKey = new Label("Total:");
        totalKey.setStyleName("profile-header-label");
        totalVacationContent.setStyleName("pull-right");
        total.add(totalKey);
        total.add(totalVacationContent);
        pageHeaderHolidayPanel.add(total);

        final int lastYear = DateHelper.getYear(new Date()) - 1;
        carryOverKey = new Label("Remaining from " + lastYear);
        carryOver.setStyleName("clear");
        carryOverContent.setStyleName("pull-right profile-header-label");
        carryOver.add(carryOverKey);
        carryOver.add(carryOverContent);
        pageHeaderHolidayPanel.add(carryOver);

        final FlowPanel taken = new FlowPanel();
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

        final HTML spacerLabel = new HTML("<hr />");
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

    /**
     * DOCUMENT ME!
     */
    private void refreshHolidayBalanceLabels() {
        final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();

        final BasicAsyncCallback<Double> vacationDaysTakenCallback = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result, final boolean operationFailed) {
                    if (!operationFailed) {
                        remainingVacation -= result;
                        holidayBalContent.setText(formatDays(remainingVacation) + " Days");
                        vacationTakenContent.setText(formatDays(result) + " Days");
                    }
                }
            };

        final BasicAsyncCallback<Double> vacationDaysPlannedCallback = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result, final boolean operationFailed) {
                    if (!operationFailed) {
                        remainingVacation -= result;
                        holidayBalContent.setText(formatDays(remainingVacation) + " Days");
                        vacationPlannedContent.setText(formatDays(result) + " Days");
                    }
                }
            };

        final BasicAsyncCallback<Double> carryOverCallback = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (result != 0) {
                            remainingVacation += result;
                            carryOverContent.setText("+ " + formatDays(result) + " Days");
                            carryOver.removeStyleName("noDisplay");
                        } else {
                            carryOver.addStyleName("noDisplay");
                        }
                        ProjectTrackerEntryPoint.getProjectService(true)
                                .getVacationDaysTaken(new Date(), staff, vacationDaysTakenCallback);
                        final Date d = new Date();
                        DateHelper.addDays(d, 1);
                        ProjectTrackerEntryPoint.getProjectService(true)
                                .getVacationDaysPlanned(d, staff, vacationDaysPlannedCallback);
                    }
                }
            };

        final BasicAsyncCallback<Double> totalCB = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result, final boolean operationFailed) {
                    if (!operationFailed) {
                        // TODO if current year = 2012 take the carry over from the profile
                        remainingVacation = result;
                        totalVacationContent.setText(formatDays(result) + " Days");

                        if (DateHelper.getYear(new Date()) != 2012) {
                            // automatic calculation of remaining vacation
                            ProjectTrackerEntryPoint.getProjectService(
                                true).getVacationCarryOver(new Date(), staff, carryOverCallback);
                        } else {
                            final ProfileDTO profile = ProjectTrackerEntryPoint.getInstance().getStaff().getProfile();
                            final double residualVacLastYear;
                            if (profile != null) {
                                residualVacLastYear = profile.getResidualVacation();
                            } else {
                                residualVacLastYear = 0;
                            }

                            if (residualVacLastYear > 0) {
                                remainingVacation += residualVacLastYear;
                                carryOver.removeStyleName("noDisplay");
                                carryOverContent.setText(formatDays(residualVacLastYear) + " Days");
                            } else {
                                carryOver.addStyleName("noDisplay");
                            }
                            ProjectTrackerEntryPoint.getProjectService(true)
                                    .getVacationDaysTaken(new Date(), staff, vacationDaysTakenCallback);
                            ProjectTrackerEntryPoint.getProjectService(true)
                                    .getVacationDaysPlanned(new Date(), staff, vacationDaysPlannedCallback);
                        }
                    }
                }
            };

        ProjectTrackerEntryPoint.getProjectService(
            true).getTotalVacationForYear(staff, new Date(), totalCB);
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshProfileImage() {
        pageHeaderPanel.remove((gravatar));
        pageHeaderPanel.remove(nameLabel);
        final String email = ProjectTrackerEntryPoint.getInstance().getStaff().getEmail();
        if (email != null) {
            gravatar.setUrl(GRAVATAR_URL_PREFIX
                        + ProjectTrackerEntryPoint.getInstance().md5(email)
                        + "?s=110");
        }

        pageHeaderPanel.add(gravatar);

        final String s = ProjectTrackerEntryPoint.getInstance().getStaff().getFirstname()
                    + " " + ProjectTrackerEntryPoint.getInstance().getStaff().getName();
        nameLabel = new Label(s);
        nameLabel.setStyleName("profile-name-label");

        pageHeaderPanel.add(nameLabel);
    }

    /**
     * DOCUMENT ME!
     */
    private void refresh() {
        Scheduler.get().scheduleDeferred(new Command() {

                @Override
                public void execute() {
                    refreshAccountBalanceLables();
                    refreshHolidayBalanceLabels();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String formatDays(final double d) {
        return NumberFormat.getFormat("#.##").format(d);
    }

    /*
     * called when an administrator changes the user in the users box
     */
    @Override
    public void onChange(final ChangeEvent event) {
        refreshProfileImage();
        refresh();
        menue.refreshDetailContainer();
    }

    @Override
    public void onResize(final ResizeEvent event) {
        resize(event.getHeight());
    }

    /**
     * DOCUMENT ME!
     *
     * @param  heigth  DOCUMENT ME!
     */
    private void resize(final int heigth) {
        int newHeight = heigth - 300;
        if (newHeight < 150) {
            newHeight = 150;
        }

//        contentPanel.setHeight(newHeight+"px");
        contentPanel.getElement().getStyle().setProperty("maxHeight", newHeight + "px");
        final int nh = newHeight - 20;
//        detailView.setHeight(nh+"px");
        detailView.getElement().getStyle().setProperty("maxHeight", nh + "px");
    }
}
