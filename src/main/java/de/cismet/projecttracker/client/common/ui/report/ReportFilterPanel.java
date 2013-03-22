/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import de.cismet.projecttracker.client.common.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author daniel
 */
public class ReportFilterPanel extends Composite implements ChangeHandler, ClickHandler, ValueChangeHandler<Date>, KeyUpHandler {

    private static ReportFilterPanelUiBinder uiBinder = GWT.create(ReportFilterPanelUiBinder.class);
    @UiField
    TextBox description;
    @UiField
    ListBox project;
    @UiField
    ListBox workpackage;
    @UiField
    ListBox users;
    @UiField
    HorizontalPanel datepickerPanel;
    @UiField
    HTMLPanel statisticWrapper;
    private List<ProjectDTO> projects;
    private List<ReportSearchParamListener> listeners = new LinkedList<ReportSearchParamListener>();
    public static final String WORKPACKAGE_KEY = "WP";
    public static final String STAFF_KEY = "STAFF";
    public static final String DATE_FROM_KEY = "FROM";
    public static final String DATE_TO_KEY = "TO";
    public static final String DESC_KEY = "DESCRIPTION";
    private List<StaffDTO> userList = new ArrayList<StaffDTO>();
    private WeekDatePicker periodFrom = new WeekDatePicker();
    private Button periodFromButton = new Button("<i class='icon-calendar'></i>", this);
    private WeekDatePicker periodTo = new WeekDatePicker();
    private Button periodToButton = new Button("<i class='icon-calendar'></i>", this);
    private boolean isFromPickerVisible = false;
    private boolean isToPickerVisible = false;
    private long lastInvocation = -1;
    Timer t = new Timer() {

        @Override
        public void run() {
            fireSearchParamsChanged();
        }
    };

    @Override
    public void onChange(ChangeEvent event) {
        if (event.getSource() == project) {
            initWorkpackage();
        }
        fireSearchParamsChanged();
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == periodFromButton) {
            if (isFromPickerVisible) {
                isFromPickerVisible = false;
                periodFrom.hide();
            } else {
                isFromPickerVisible = true;
                periodFrom.show();
            }
        } else if (event.getSource() == periodToButton) {
            if (isToPickerVisible) {
                isToPickerVisible = false;
                periodTo.hide();
            } else {
                isToPickerVisible = true;
                periodTo.show();
            }
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<Date> event) {
        fireSearchParamsChanged();
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        final long time = System.currentTimeMillis();
        if (lastInvocation > 0) {
            final double msBetweenInvocations = time - lastInvocation;
            if (msBetweenInvocations < 500) {
                t.cancel();
            }
        }
        lastInvocation = time;
        t.schedule(800);
        
    }

    interface ReportFilterPanelUiBinder extends UiBinder<Widget, ReportFilterPanel> {
    }

    public ReportFilterPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
        project.addChangeHandler(this);
        workpackage.addChangeHandler(this);
    }

    private void init() {
        List<ProjectDTO> result = ProjectTrackerEntryPoint.getInstance().getProjects();
//        travel.setText("Travel: ");
        if (result == null) {
            BasicAsyncCallback<ArrayList<ProjectDTO>> callback = new BasicAsyncCallback<ArrayList<ProjectDTO>>() {
                @Override
                protected void afterExecution(ArrayList<ProjectDTO> result, boolean operationFailed) {
                    for (ProjectDTO tmp : result) {
                        ProjectPeriodDTO period = tmp.determineMostRecentPeriod();
                        //TODO do not use the current day..
                        if (period == null || DateHelper.isDayInProjectPeriod(new Date(), period)) {
                            project.addItem(tmp.getName(), "" + tmp.getId());
                        }
                    }
                    ProjectTrackerEntryPoint.getInstance().setProjects(result);
                    projects = result;
                    initWorkpackage();
                }
            };

            ProjectTrackerEntryPoint.getProjectService(true).getAllProjectsFull(callback);
        } else {
            for (ProjectDTO tmp : result) {
                ProjectPeriodDTO period = tmp.determineMostRecentPeriod();
                //TODO do not use the current day..
                if (period == null || DateHelper.isDayInProjectPeriod(new Date(), period)) {
                    project.addItem(tmp.getName(), "" + tmp.getId());
                }
            }
            projects = result;
            initWorkpackage();
        }
        project.addItem("* all Elements", "" + -1);
        initUsers();
        periodFrom.setFormat("dd.mm.yyyy");
        periodFrom.setAutoClose(true);
        periodFrom.setStyleName("report-datePicker");
        periodFrom.setWeekStart(1);
        //StartDate 01.01.2012
        periodFrom.setValue(new Date(112, 0, 1));
        periodFrom.addValueChangeHandler(this);
        periodFromButton.setWidth("10px;");
        periodFromButton.addStyleName("btn");
        periodFromButton.addStyleName("report-datePicker-button");
        periodFromButton.setTitle("Click to select the first day of search period");
        periodTo.setFormat("dd.mm.yyyy");
        periodTo.setAutoClose(true);
        periodTo.setStyleName("report-datePicker");
        periodTo.setWeekStart(1);
        periodTo.addValueChangeHandler(this);
        periodToButton.setWidth("10px;");
        periodToButton.addStyleName("btn");
        periodToButton.addStyleName("report-datePicker-button");
        periodToButton.setTitle("Click to select the last day of search period");
        final Label fromLbl = new Label("from:");
        fromLbl.addStyleName("report-lbl");
        final Label toLbl = new Label("to:");
        toLbl.addStyleName("report-lbl");
        datepickerPanel.add(fromLbl);
        datepickerPanel.add(periodFrom);
        datepickerPanel.add(periodFromButton);
        datepickerPanel.add(toLbl);
        datepickerPanel.add(periodTo);
        datepickerPanel.add(periodToButton);

        description.setStyleName("report-filter-textfield");
        description.addKeyUpHandler(this);
//        description.addChangeHandler(this);
    }

    private void initWorkpackage() {
        ProjectDTO selectedProject = getSelectedProject();
        workpackage.clear();
        workpackage.addItem("* all WorkPackages", "-1");
        if (selectedProject != null) {
            if (selectedProject.getWorkPackages() != null) {
                WorkPackageDTO[] wps = selectedProject.getWorkPackages().toArray(new WorkPackageDTO[selectedProject.getWorkPackages().size()]);
                Arrays.sort(wps);
                for (WorkPackageDTO tmp : wps) {
                    WorkPackagePeriodDTO period = tmp.determineMostRecentPeriod();

//                    if (period == null || DateHelper.isDayInWorkPackagePeriod(day, period)) {
                    if (tmp.getName() != null) {
                        workpackage.addItem(extractWorkpackageName(tmp), String.valueOf(tmp.getId()));
                    }
                }

                for (int i = 0; i < workpackage.getItemCount(); i++) {
                    final String itemText = workpackage.getItemText(i);
                    if (itemText.toUpperCase().startsWith("WP")) {
                        workpackage.getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled", "disabled");
                    }
                }
            }
        } else {
            // we have selected the all project...
            for (ProjectDTO proj : ProjectTrackerEntryPoint.getInstance().getProjects()) {
                for (WorkPackageDTO wp : proj.getWorkPackages()) {
                    WorkPackagePeriodDTO period = wp.determineMostRecentPeriod();

//                    if (period == null || DateHelper.isDayInWorkPackagePeriod(day, period)) {
                    if (wp.getName() != null) {
                        workpackage.addItem(extractWorkpackageName(wp), String.valueOf(wp.getId()));
                    }

                    for (int i = 0; i < workpackage.getItemCount(); i++) {
                        final String itemText = workpackage.getItemText(i);
                        if (itemText.toUpperCase().startsWith("WP")) {
                            workpackage.getElement().getElementsByTagName("option").getItem(i).setAttribute("disabled", "disabled");
                        }
                    }
                }
            }
//            workpackage.setSelectedIndex(-1);
        }
    }

    private void initUsers() {
        BasicAsyncCallback<ArrayList<StaffDTO>> callback = new BasicAsyncCallback<ArrayList<StaffDTO>>() {
            @Override
            protected void afterExecution(ArrayList<StaffDTO> result, boolean operationFailed) {
                users.addItem("* all Users", "-1");
                users.setSelectedIndex(-1);
                int i = 0;
                int index = 0;
                Collections.sort(result);
                StaffDTO loggedInStaff = ProjectTrackerEntryPoint.getInstance().getStaff();
                for (StaffDTO staff : result) {
                    users.addItem(staff.getFirstname() + " " + staff.getName(), staff.getId() + "");
                    if (staff.getId() == loggedInStaff.getId()) {
                        index = i;
                    }
                    ++i;
                }
                users.setSelectedIndex(index);
                //fire the change event since setSelected item does not
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), users);
                    }
                });
                userList = result;
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).getCurrentEmployees(callback);
        users.addChangeHandler(this);
    }

    private ProjectDTO getSelectedProject() {
        String value = project.getValue(project.getSelectedIndex());
        if (value.equals("-1")) {
            //the all option is selected return null
            return null;
        }
        return getProjectById(Long.parseLong(value));
    }

    private ProjectDTO getProjectById(long id) {
        for (ProjectDTO tmp : projects) {
            if (tmp.getId() == id) {
                return tmp;
            }
        }
        return null;
    }

    private WorkPackageDTO getSelectedWorkpackage() {
        String value = workpackage.getValue(workpackage.getSelectedIndex());
        if (value.equals("-1")) {
            //the all option is selected so return null
            return null;
        }
        return getWorkpackageById(Long.parseLong(value));
    }

    private WorkPackageDTO getWorkpackageById(long id) {
        final ProjectDTO proj = getSelectedProject();
        if (proj == null) {
            for (ProjectDTO p : ProjectTrackerEntryPoint.getInstance().getProjects()) {
                for (WorkPackageDTO tmp : p.getWorkPackages()) {
                    if (tmp.getId() == id) {
                        return tmp;
                    }
                }
            }
        } else {
            for (WorkPackageDTO tmp : proj.getWorkPackages()) {
                if (tmp.getId() == id) {
                    return tmp;
                }
            }
        }
        return null;
    }

    private static String extractWorkpackageName(WorkPackageDTO workpackage) {
        WorkPackageDTO tmp = workpackage.getWorkPackage();
        String prefix = "";

        while (tmp != null) {
            prefix += "  ";
            tmp = tmp.getWorkPackage();
        }

        return prefix + workpackage.getName();
    }

    public void addSearchParamListener(ReportSearchParamListener l) {
        this.listeners.add(l);
    }

    public void removeSearchParamListener(ReportSearchParamListener l) {
        listeners.remove(l);
    }
    
    public void setStatisticsPanel(Composite p){
        statisticWrapper.clear();
        statisticWrapper.add(p);
    }
    private void fireSearchParamsChanged() {
        for (ReportSearchParamListener l : listeners) {
            l.searchParamsChanged();
        }
    }

    private StaffDTO getSelectedUser() {
        try {
            long value = Long.parseLong(users.getValue(users.getSelectedIndex()));
            if (value == -1) {
                //search for all users, return null
                return null;
            }
            for (StaffDTO staff : userList) {
                if (staff.getId() == value) {
                    return staff;
                }
            }
        } catch (NumberFormatException e) {
            //should not happen
        }

        return null;
    }

    public HashMap<String, Object> getSearchParams() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        final List<WorkPackageDTO> workpackages = new LinkedList<WorkPackageDTO>();
        final WorkPackageDTO wp = getSelectedWorkpackage();
        if (wp != null) {
            workpackages.add(wp);
        } else {
            //we have to check what project is selected...
            final ProjectDTO proj = getSelectedProject();
            /*
             * if the selected Proj is not null we have to search for all Workpackages of this Project. 
             * Otherwise (which means all project are relevant) we have to do nothing  
             */
            if (proj != null) {
                workpackages.addAll(proj.getWorkPackages());
            }
        }
        map.put(WORKPACKAGE_KEY, workpackages);

        final List<StaffDTO> searchStaff = new ArrayList<StaffDTO>();
        final StaffDTO staff = getSelectedUser();
        if (staff != null) {
            searchStaff.add(staff);
        }
        map.put(STAFF_KEY, searchStaff);

        map.put(DATE_FROM_KEY, periodFrom.getValue());
        map.put(DATE_TO_KEY, periodTo.getValue());

        map.put(DESC_KEY, description.getText());

        return map;
    }
}
