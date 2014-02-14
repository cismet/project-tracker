/**
 * *************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 * 
* ... and it just works.
 * 
***************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.base.AlertBase;
import com.github.gwtbootstrap.client.ui.constants.AlertType;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.FlowPanelWithSpacer;
import de.cismet.projecttracker.client.common.ui.LoadingSpinner;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.utilities.TimeCalculator;

/**
 * DOCUMENT ME!
 *
 * @author daniel
 * @version $Revision$, $Date$
 */
public class ReportResultPanel extends Composite {

    //~ Instance fields --------------------------------------------------------
    AccordionGroup activityAccordionPanel = new AccordionGroup();

    private LargeReportResultAlert sizeAlert = new LargeReportResultAlert(this);
    private FlowPanel mainPanel = new FlowPanel();
    private FlowPanel activityList = new FlowPanel();
    private FlowPanel summaryPanel = new FlowPanel();
    private ReportFilterPanel filterPanel;
//    private Label l = new Label("Jo, is jo gudd, ich sin noch am laden");
//    private Image l = new Image(ImageConstants.INSTANCE.loadImage());
    private LoadingSpinner l = new LoadingSpinner();
    private HashMap<StaffDTO, Set<ActivityDTO>> userMap = new HashMap<StaffDTO, Set<ActivityDTO>>();
    private HashMap<WorkPackageDTO, Set<ActivityDTO>> wpMap = new HashMap<WorkPackageDTO, Set<ActivityDTO>>();
    private double hoursInTotal = 0;
    private int activityCount = 0;
    private Date firstActivity = null;
    private Date lastActivity = null;
    private ArrayList<ActivityDTO> searchResultCache;

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new ReportResultPanel object.
     *
     * @param filterPanel DOCUMENT ME!
     */
    public ReportResultPanel(final ReportFilterPanel filterPanel) {
        initWidget(mainPanel);
        this.filterPanel = filterPanel;
        init();
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * DOCUMENT ME!
     */
    private void init() {
        l.setStyleName("report-result-lbl");
        mainPanel.setStyleName("report-results-area verticalScroller");
        mainPanel.add(summaryPanel);
        activityAccordionPanel.setHeading("Show single activites");
        activityAccordionPanel.setDefaultOpen(false);
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        hoursInTotal = 0;
        activityCount = 0;
        firstActivity = null;
        lastActivity = null;
        wpMap.clear();
        userMap.clear();
        mainPanel.clear();
        mainPanel.add(l);
        summaryPanel.clear();
        activityAccordionPanel.clear();
        activityList.clear();

        final HashMap<String, Object> params = filterPanel.getSearchParams();
        final List<WorkPackageDTO> workpackages = (List<WorkPackageDTO>) params.get(ReportFilterPanel.WORKPACKAGE_KEY);
        final ArrayList<StaffDTO> staff = (ArrayList<StaffDTO>) params.get(ReportFilterPanel.STAFF_KEY);
        Date from = null;
        Date to = null;
        if (params.containsKey(ReportFilterPanel.DATE_FROM_KEY)) {
            from = (Date) params.get(ReportFilterPanel.DATE_FROM_KEY);
        }
        if (params.containsKey(ReportFilterPanel.DATE_TO_KEY)) {
            to = (Date) params.get(ReportFilterPanel.DATE_TO_KEY);
        }
        final String descr = (String) params.get(ReportFilterPanel.DESC_KEY);

        final BasicAsyncCallback<ArrayList<ActivityDTO>> cb = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {

            @Override
            protected void afterExecution(final ArrayList<ActivityDTO> result, final boolean operationFailed) {
                mainPanel.remove(ReportResultPanel.this.l);
                final AlertBlock message = new AlertBlock(true);
                final AlertBase b = (AlertBase) message;
                b.setAnimation(true);
                final Timer closeAlertTimer = new Timer() {

                    @Override
                    public void run() {
                        b.close();
                    }
                };

                if (operationFailed) {
                    b.setClose(false);
                    b.setType(AlertType.ERROR);
                    b.setText("Error during acticity search");
                    mainPanel.add(message);
//                        closeAlertTimer.schedule(10 * 1000);
                    return;
                }
                if ((result == null) || result.isEmpty()) {
                    b.setClose(false);
                    b.setType(AlertType.INFO);
                    b.setText("No corresponding activites can be found");
                    mainPanel.add(message);
//                        closeAlertTimer.schedule(3 * 1000);
                    return;
                }
                searchResultCache = result;
                if (result.size() > 1000) {
                    showSizeAlert();
                }
                processSearchResultIncremental(result);
//                proceedWithLastSearchResult();
            }

        };
        // call the service..
        ProjectTrackerEntryPoint.getProjectService(true).getActivites(workpackages, staff, from, to, descr, cb);
    }

    private void processSearchResultIncremental(ArrayList<ActivityDTO> result) {
        processActivites(result);
        
    }

    /**
     * DOCUMENT ME!
     */
    public void proceedWithLastSearchResult() {
        mainPanel.remove(sizeAlert);

        Scheduler.get().scheduleIncremental(new IncrementalCommand() {

            @Override
            public boolean execute() {
                if (!searchResultCache.isEmpty()) {
                    searchResultCache.remove(searchResultCache.size() - 1);
                    return true;
                }
                processActivites(searchResultCache);
                fillSummaryPanel();
                mainPanel.remove(ReportResultPanel.this.l);
                generateAndPropagateStatisticsPanel();
                addActivitesToResultsPanel(searchResultCache);
                return false;
            }
        });
    }

    /**
     * DOCUMENT ME!
     */
    private void showSizeAlert() {
        sizeAlert = new LargeReportResultAlert(this);
        mainPanel.add(sizeAlert);
        mainPanel.add(l);
    }

    /**
     * DOCUMENT ME!
     */
    private void abort() {
        mainPanel.clear();
        final Label l = new Label();
        l.setStyleName("report-result-lbl");
        l.setText("Long running process detected. Search aborted");
        l.addStyleName("label label-important report-result-error-lbl");
        mainPanel.add(l);
    }

    /**
     * DOCUMENT ME!
     */
    private void generateAndPropagateStatisticsPanel() {
        final StatisticsPanel statPan = new StatisticsPanel(
                hoursInTotal,
                userMap.keySet().size(),
                activityCount,
                firstActivity,
                lastActivity);
        filterPanel.setStatisticsPanel(statPan);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     */
    private void addActivitesToResultsPanel(final ArrayList<ActivityDTO> result) {
        final FlowPanelWithSpacer activityPanel = new FlowPanelWithSpacer();
        for (final ActivityDTO act : result) {
            activityPanel.add(new ReportTaskNotice(act));
        }
        activityAccordionPanel.add(activityPanel);
//        mainPanel.add(activityAccordionPanel);
    }

    /**
     * DOCUMENT ME!
     */
    private void fillSummaryPanel() {
        final ReportResultsSummaryDataGrid table = new ReportResultsSummaryDataGrid(userMap, wpMap);
        summaryPanel.add(table);
        mainPanel.add(summaryPanel);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     */
    private void processActivites(final ArrayList<ActivityDTO> result) {
        for (final ActivityDTO tmp : result) {
            hoursInTotal += TimeCalculator.getWorkingHoursForActivity(tmp);
            activityCount++;
            // find the earliest activity...
            if (firstActivity == null) {
                firstActivity = tmp.getDay();
            } else {
                if (tmp.getDay().before(firstActivity)) {
                    firstActivity = tmp.getDay();
                }
            }

            // fin the latest activity
            if (lastActivity == null) {
                lastActivity = tmp.getDay();
            } else {
                if (tmp.getDay().after(lastActivity)) {
                    lastActivity = tmp.getDay();
                }
            }
            final StaffDTO staff = tmp.getStaff();
            Set<ActivityDTO> userActivitySet = userMap.get(staff);
            if (userActivitySet == null) {
                userActivitySet = new HashSet<ActivityDTO>();
            }
            userActivitySet.add(tmp);
            userMap.put(staff, userActivitySet);
            final WorkPackageDTO wp = tmp.getWorkPackage();
            Set<ActivityDTO> wpActivitySet = wpMap.get(wp);
            if (wpActivitySet == null) {
                wpActivitySet = new HashSet<ActivityDTO>();
            }
            wpActivitySet.add(tmp);
            wpMap.put(wp, wpActivitySet);
        }
    }
}
