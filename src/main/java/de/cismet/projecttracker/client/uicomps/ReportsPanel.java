/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.uicomps;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.FlowPanelWithSpacer;
import de.cismet.projecttracker.client.common.ui.TaskNotice;
import de.cismet.projecttracker.client.common.ui.event.MenuEvent;
import de.cismet.projecttracker.client.common.ui.listener.MenuListener;
import de.cismet.projecttracker.client.common.ui.report.ReportFilterPanel;
import de.cismet.projecttracker.client.common.ui.report.ReportSearchParamListener;
import de.cismet.projecttracker.client.common.ui.report.ReportTaskNotice;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author daniel
 */
public class ReportsPanel extends Composite implements MenuListener, ChangeHandler, ReportSearchParamListener, ResizeHandler {

    private FlowPanel mainPanel = new FlowPanel();
    private FlowPanel contentPanel = new FlowPanel();
    private FlowPanel filterContainerPanel = new FlowPanel();
    private FlowPanel summaryPanel = new FlowPanel();
    private FlowPanel resultsPanel = new FlowPanel();
    private ReportFilterPanel filterPanel;
    private HashMap<StaffDTO, ActivityDTO> userMap = new HashMap<StaffDTO, ActivityDTO>();
    private HashMap<StaffDTO, Image> userIconMap = new HashMap<StaffDTO, Image>();
    private HashMap<WorkPackageDTO, ActivityDTO> wpMap = new HashMap<WorkPackageDTO, ActivityDTO>();
    private double hoursInTotal = 0;
    private static String GRAVATAR_URL_PREFIX = "http://www.gravatar.com/avatar/";

    public ReportsPanel() {
        init();
        initWidget(mainPanel);
        setStyleName("content");
    }

    @Override
    public void menuChangeEvent(MenuEvent e) {
        if (e.getNumber() == TopPanel.REPORTS) {
            if (ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                RootPanel.get("contentId").clear();
                RootPanel.get("contentId").add(this);
            } else {
                ProjectTrackerEntryPoint.outputBox("You have no admin permission");
            }

        }
    }

    @Override
    public void onChange(ChangeEvent event) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void init() {

        contentPanel.setStyleName("report-content");
        initFilterArea();
        initResultsArea();
        contentPanel.add(filterContainerPanel);
        contentPanel.add(summaryPanel);
        contentPanel.add(resultsPanel);

        mainPanel.add(contentPanel);
        Window.addResizeHandler(this);
        resize(Window.getClientHeight());
    }

    private void initFilterArea() {
        filterContainerPanel.setStyleName("report-filter-area");
        final Label lblHeader = new Label("Activity Quick-Search");
        lblHeader.setStyleName("profile-name-label");
        filterContainerPanel.add(lblHeader);
        filterPanel = new ReportFilterPanel();
        filterPanel.addSearchParamListener(this);
        filterContainerPanel.add(filterPanel);
    }

    private void initResultsArea() {
        resultsPanel.setStyleName("report-results-area verticalScroller");
        final Label l = new Label("Results");
        l.setStyleName("profile-name-label");
        resultsPanel.add(l);
    }

    private void addActivitesToResultsPanel(ArrayList<ActivityDTO> result) {
        final FlowPanelWithSpacer activityPanel = new FlowPanelWithSpacer();
        for (ActivityDTO act : result) {
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
        resultsPanel.add(activityPanel);
    }

    private void generateSummaryPanel() {
//        summaryPanel.add;
    }

    @Override
    public void searchParamsChanged() {
        resultsPanel.clear();

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
                    resultsPanel.add(l);
                    return;
                }
                if (result == null || result.isEmpty()) {
                    l.setText("No corresponding activites can be found");
                    resultsPanel.add(l);
                    return;
                }
                processActivites(result);
                generateSummaryPanel();
                addActivitesToResultsPanel(result);
            }
        };
        //call the service..
        ProjectTrackerEntryPoint.getProjectService(true).getActivites(workpackages, staff, from, to, descr, cb);
    }

    private void processActivites(ArrayList<ActivityDTO> result) {
        for (ActivityDTO tmp : result) {
            final StaffDTO staff = tmp.getStaff();
            userMap.put(staff, tmp);
            final WorkPackageDTO wp = tmp.getWorkPackage();
            wpMap.put(wp, tmp);
            hoursInTotal += tmp.getWorkinghours();
        }
    }

    private void resize(int heigth) {

        int newHeight = heigth - 300;
        if (newHeight < 150) {
            newHeight = 150;
        }

        contentPanel.getElement().getStyle().setProperty("maxHeight", newHeight + "px");
        final int nh = newHeight - 65;
        resultsPanel.getElement().getStyle().setProperty("maxHeight", nh + "px");
    }

    @Override
    public void onResize(ResizeEvent event) {
        resize(event.getHeight());
    }
}
