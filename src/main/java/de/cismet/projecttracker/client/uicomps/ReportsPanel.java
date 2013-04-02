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
import de.cismet.projecttracker.client.common.ui.report.ReportResultPanel;
import de.cismet.projecttracker.client.common.ui.report.ReportSearchParamListener;
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
    private ReportResultPanel resultsPanel;
    private ReportFilterPanel filterPanel;

    public ReportsPanel() {
        init();
        initWidget(mainPanel);
        setStyleName("content");
    }

    @Override
    public void menuChangeEvent(MenuEvent e) {
        if (e.getNumber() == TopPanel.REPORTS) {
            RootPanel.get("contentId").clear();
            RootPanel.get("contentId").add(this);
            filterPanel.refresh();
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
        resultsPanel = new ReportResultPanel(filterPanel);
    }

    @Override
    public void searchParamsChanged() {
        resultsPanel.refresh();
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
