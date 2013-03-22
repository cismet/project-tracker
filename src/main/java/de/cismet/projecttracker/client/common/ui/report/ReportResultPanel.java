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
        hoursInTotal=0;
        wpMap.clear();
        userMap.clear();
        mainPanel.clear();
        summaryPanel.clear();
        activityAccordionPanel.clear();
        activityList.clear();

        final HashMap<String, Object> params = filterPanel.getSearchParams();
        final List<WorkPackageDTO> workpackages = (List< WorkPackageDTO>) params.get(ReportFilterPanel.WORKPACKAGE_KEY);
        final ArrayList<StaffDTO> staff = (ArrayList<StaffDTO>) params.get(ReportFilterPanel.STAFF_KEY);
        final Date from = (Date) params.get(ReportFilterPanel.DATE_FROM_KEY);
        final Date to = (Date) params.get(ReportFilterPanel.DATE_TO_KEY);
        final String descr = (String) params.get(ReportFilterPanel.DESC_KEY);

        BasicAsyncCallback<ArrayList<ActivityDTO>> cb = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {
            @Override
            protected void afterExecution(ArrayList<ActivityDTO> result, boolean operationFailed) {
                Label l = new Label();
                l.setStyleName("report-result-lbl");
                if (operationFailed) {
                    l.setText("Error during acticity search");
                    l.addStyleName("label label-important report-result-error-lbl");
                    activityList.add(l);
                    return;
                }
                if (result == null || result.isEmpty()) {
                    l.setText("No corresponding activites can be found");
                    activityList.add(l);
                    return;
                }
                processActivites(result);
                fillSummaryPanel();
                addActivitesToResultsPanel(result);
                
            }
        };
        //call the service..
        ProjectTrackerEntryPoint.getProjectService(true).getActivites(workpackages, staff, from, to, descr, cb);
    }

    private void addActivitesToResultsPanel(ArrayList<ActivityDTO> result) {
        final FlowPanelWithSpacer activityPanel = new FlowPanelWithSpacer();
        for (ActivityDTO act : result) {
            //TODO create a special TaskNotice with custom look
//            final StaffDTO staff = act.getStaff();
//            Image gravatar = userIconMap.get(staff);
//            if(gravatar == null){
//                gravatar = new Image();
//                 final String email = ProjectTrackerEntryPoint.getInstance().getStaff().getEmail();
//                if (email != null) {
//                    gravatar.setUrl(GRAVATAR_URL_PREFIX
//                            + ProjectTrackerEntryPoint.getInstance().md5(email)
//                            + "?s=110");
//                }
//                userIconMap.put(staff, gravatar);
//            }
//            activityPanel.add(new ReportTaskNotice(act,gravatar));
            activityPanel.add(new TaskNotice(act, true));
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
