/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.FlowPanelWithSpacer;
import de.cismet.projecttracker.client.common.ui.TaskNotice;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author daniel
 */
public class ReportResultPanel extends Composite {

    private FlowPanel mainPanel = new FlowPanel();
    AccordionGroup activityAccordionPanel = new AccordionGroup();
    private FlowPanel activityList = new FlowPanel();
    private FlowPanel summaryPanel = new FlowPanel();
    private ReportFilterPanel filterPanel;
    private HashMap<StaffDTO, Set<ActivityDTO>> userMap = new HashMap<StaffDTO, Set<ActivityDTO>>();
    private HashMap<WorkPackageDTO, Set<ActivityDTO>> wpMap = new HashMap<WorkPackageDTO, Set<ActivityDTO>>();
    private double hoursInTotal = 0;
    private int activityCount = 0;
    private Date firstActivity = null;
    private Date lastActivity = null;

    public ReportResultPanel(ReportFilterPanel filterPanel) {
        initWidget(mainPanel);
        this.filterPanel = filterPanel;
        init();
    }

    private void init() {
        mainPanel.setStyleName("report-results-area verticalScroller");
        mainPanel.add(summaryPanel);
        activityAccordionPanel.setHeading("Show single activites");
        activityAccordionPanel.setDefaultOpen(false);
    }

    public void refresh() {
        hoursInTotal = 0;
        activityCount = 0;
        firstActivity = null;
        lastActivity = null;
        wpMap.clear();
        userMap.clear();
        mainPanel.clear();
        summaryPanel.clear();
        activityAccordionPanel.clear();
        activityList.clear();

        final HashMap<String, Object> params = filterPanel.getSearchParams();
        final List<WorkPackageDTO> workpackages = (List< WorkPackageDTO>) params.get(ReportFilterPanel.WORKPACKAGE_KEY);
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

        BasicAsyncCallback<ArrayList<ActivityDTO>> cb = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {
            @Override
            protected void afterExecution(ArrayList<ActivityDTO> result, boolean operationFailed) {
                Label l = new Label();
                l.setStyleName("report-result-lbl");
                if (operationFailed) {
                    l.setText("Error during acticity search");
                    l.addStyleName("label label-important report-result-error-lbl");
                    mainPanel.add(l);
                    return;
                }
                if (result == null || result.isEmpty()) {
                    l.setText("No corresponding activites can be found");
                    mainPanel.add(l);
                    return;
                }
                processActivites(result);
                fillSummaryPanel();
                generateAndPropagateStatisticsPanel();
                addActivitesToResultsPanel(result);
            }
        };
        //call the service..
        ProjectTrackerEntryPoint.getProjectService(true).getActivites(workpackages, staff, from, to, descr, cb);
    }

    private void generateAndPropagateStatisticsPanel() {
        StatisticsPanel statPan = new StatisticsPanel(hoursInTotal, userMap.keySet().size(), activityCount, firstActivity, lastActivity);
        filterPanel.setStatisticsPanel(statPan);
    }

    private void addActivitesToResultsPanel(ArrayList<ActivityDTO> result) {
        final FlowPanelWithSpacer activityPanel = new FlowPanelWithSpacer();
        for (ActivityDTO act : result) {
            activityPanel.add(new ReportTaskNotice(act));
        }
        activityAccordionPanel.add(activityPanel);
        mainPanel.add(activityAccordionPanel);
    }

    private void fillSummaryPanel() {
        final ReportResultsSummaryDataGrid table = new ReportResultsSummaryDataGrid(userMap, wpMap);
        summaryPanel.add(table);
        mainPanel.add(summaryPanel);
    }

    private void processActivites(ArrayList<ActivityDTO> result) {

        for (ActivityDTO tmp : result) {
            hoursInTotal += tmp.getWorkinghours();
            activityCount++;
            //find the earliest activity...
            if (firstActivity == null) {
                firstActivity = tmp.getDay();
            } else {
                if (tmp.getDay().before(firstActivity)) {
                    firstActivity = tmp.getDay();
                }
            }

            //fin the latest activity
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