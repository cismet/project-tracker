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
package de.cismet.projecttracker.client.common.ui.report;

import com.github.gwtbootstrap.client.ui.CheckBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.*;
import de.cismet.projecttracker.client.dto.BasicDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ReportFilterPanel extends Composite implements ChangeHandler,
    ClickHandler,
    ValueChangeHandler<Date>,
    KeyUpHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static ReportFilterPanelUiBinder uiBinder = GWT.create(ReportFilterPanelUiBinder.class);
    public static final String WORKPACKAGE_KEY = "WP";
    public static final String PROJECT_KEY = "PROJECT";
    public static final String DATE_CHECKBOX_KEY = "DATE_CHECK";
    public static final String OLD_PROJECTS_KEY = "OLD_PROJECTS_CHECK";
    public static final String STAFF_KEY = "STAFF";
    public static final String DATE_FROM_KEY = "FROM";
    public static final String DATE_TO_KEY = "TO";
    public static final String DESC_KEY = "DESCRIPTION";

    //~ Instance fields --------------------------------------------------------

    @UiField
    TextBox description;
    @UiField
    ListBox project;
    @UiField(provided = true)
    com.github.gwtbootstrap.client.ui.ListBox workpackages = new com.github.gwtbootstrap.client.ui.ListBox(true);
    @UiField(provided = true)
    com.github.gwtbootstrap.client.ui.ListBox users = new com.github.gwtbootstrap.client.ui.ListBox(true);
    @UiField
    HorizontalPanel datepickerPanel;
    @UiField
    HTMLPanel statisticWrapper;
    StaffDTO loggedInUser;
    @UiField
    CheckBox dateFilterCB;
    @UiField
    CheckBox oldProjectFilterCB;
    private List<ProjectDTO> projects;
    private List<ReportSearchParamListener> listeners = new LinkedList<ReportSearchParamListener>();
    Timer t = new Timer() {

            @Override
            public void run() {
                fireSearchParamsChanged();
            }
        };

    private String wp = null;
    private String staff = null;
    private String from = null;
    private String to = null;
    private String desc = null;
    private String lastProject = null;
    private String dateCheck = null;
    private String oldProjectsCheck = null;
    private boolean userInitialised = false;
    private boolean projectsInitialised = false;
    private boolean localConfigLoaded = false;
    private final Storage storage = Storage.getLocalStorageIfSupported();

    private List<StaffDTO> userList = new ArrayList<StaffDTO>();
    private WeekDatePicker periodFrom = new WeekDatePicker();
    private Button periodFromButton = new Button("<i class='icon-calendar'></i>", this);
    private WeekDatePicker periodTo = new WeekDatePicker();
    private Button periodToButton = new Button("<i class='icon-calendar'></i>", this);
    private boolean isFromPickerVisible = false;
    private boolean isToPickerVisible = false;
    private long lastInvocation = -1;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportFilterPanel object.
     */
    public ReportFilterPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
        project.addChangeHandler(this);
        workpackages.addChangeHandler(this);

        // set the default selection for project and wp...
        workpackages.setItemSelected(0, true);
        int index = 0;
        for (int i = 0; i < project.getItemCount(); i++) {
            if (project.getValue(i).equals("-1")) {
                index = i;
                break;
            }
        }
        project.setItemSelected(index, true);
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), project);
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void onChange(final ChangeEvent event) {
        if (event.getSource() == project) {
            initWorkpackage();
        }
        fireSearchParamsChanged();
    }

    @Override
    public void onClick(final ClickEvent event) {
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
        } else if (event.getSource() == dateFilterCB) {
            fireSearchParamsChanged();
        } else if (event.getSource() == oldProjectFilterCB) {
            initProjects();
        }
    }

    @Override
    public void onValueChange(final ValueChangeEvent<Date> event) {
        fireSearchParamsChanged();
    }

    @Override
    public void onKeyUp(final KeyUpEvent event) {
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

    /**
     * DOCUMENT ME!
     */
    private void init() {
        if (storage != null) {
            wp = storage.getItem("searchWp");
            staff = storage.getItem("searchStaff");
            from = storage.getItem("searchFrom");
            to = storage.getItem("searchTo");
            desc = storage.getItem("searchDesc");
            lastProject = storage.getItem("searchProject");
            dateCheck = storage.getItem("searchDateCheck");
            oldProjectsCheck = storage.getItem("searchOldProjects");
        }
        loggedInUser = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff();
        projects = ProjectTrackerEntryPoint.getInstance().getProjects();
        if ((projects != null) && projects.isEmpty()) {
            projects = null;
        }
//        travel.setText("Travel: ");
        initProjects();
        project.addItem("* all Elements", "" + -1);
        initUsers();
        users.setStyleName("report-filter-user-list");
        users.addChangeHandler(this);
        dateFilterCB.addClickHandler(this);
        oldProjectFilterCB.addClickHandler(this);
        periodFrom.setFormat("dd.mm.yyyy");
        periodFrom.setAutoClose(true);
        periodFrom.setStyleName("report-datePicker");
        periodFrom.setWeekStart(1);
        // StartDate yesterday
        final Date startDate = new Date();
        DateHelper.addDays(startDate, -1);
        periodFrom.setValue(startDate);
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

        loadConfigurationfromStorage();
    }

    /**
     * DOCUMENT ME!
     */
    private void loadConfigurationfromStorage() {
        if ((storage != null) && projectsInitialised && userInitialised && !localConfigLoaded) {
            localConfigLoaded = true;

            if ((oldProjectsCheck != null) && oldProjectsCheck.equalsIgnoreCase("true")) {
                oldProjectFilterCB.setValue(true);
                initProjects();
            }

            if (lastProject != null) {
                for (int i = 0; i < this.project.getItemCount(); ++i) {
                    if (this.project.getValue(i).equals(lastProject)) {
                        this.project.setSelectedIndex(i);
                        initWorkpackage();
                    }
                }
            }

            if ((desc != null) && !desc.isEmpty()) {
                description.setText(desc);
            }

            if ((dateCheck != null) && dateCheck.equalsIgnoreCase("true")) {
                if (!dateFilterCB.getValue()) {
//                    datepickerPanel.setVisible(true);
//                    Document.get().createClickEvent(0, 0, 0, 0, 0, isToPickerVisible, isToPickerVisible, isToPickerVisible, isToPickerVisible)
//                    dateFilterCB.fireEvent(ClickEvent.getType());
//                    ProjectTrackerEntryPoint.outputBox("click");
                    dateFilterCB.setValue(true, true);
                    ProjectTrackerEntryPoint.toggle();
//                    dateFilterCB.fireEvent(new CheckBoxClickEvent());
                }

                if (to != null) {
                    try {
                        final Date toDate = DateHelper.parseString(to, null);
                        periodTo.setValue(toDate);
                    } catch (IllegalArgumentException ex) {
                        // nothing to do
                    }
                }

                if (from != null) {
                    try {
                        final Date fromDate = DateHelper.parseString(from, null);
                        periodFrom.setValue(fromDate);
                    } catch (IllegalArgumentException ex) {
                        // nothing to do
                    }
                }
            }
            if (wp != null) {
                final String[] wps = IdStringToArray(wp);
                Arrays.sort(wps);

                for (int i = 0; i < workpackages.getItemCount(); ++i) {
                    final String value = workpackages.getValue(i);
                    final boolean isSelected = Arrays.binarySearch(wps, value) >= 0;

                    workpackages.setItemSelected(i, isSelected);
                }
            }

            if (staff != null) {
                final String[] staffArray = IdStringToArray(staff);
                Arrays.sort(staffArray);

                for (int i = 0; i < users.getItemCount(); ++i) {
                    final String value = users.getValue(i);
                    final boolean isSelected = Arrays.binarySearch(staffArray, value) >= 0;

                    users.setItemSelected(i, isSelected);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   idString  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String[] IdStringToArray(final String idString) {
        final List<String> list = new ArrayList<String>();
        final String[] splitted = idString.split(",");

        for (final String token : splitted) {
            list.add(token);
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * DOCUMENT ME!
     */
    private void initProjects() {
        if (projects == null) {
            final BasicAsyncCallback<ArrayList<ProjectDTO>> callback = new BasicAsyncCallback<ArrayList<ProjectDTO>>() {

                    @Override
                    protected void afterExecution(final ArrayList<ProjectDTO> result, final boolean operationFailed) {
                        project.clear();
                        for (final ProjectDTO tmp : result) {
                            final ProjectPeriodDTO period = tmp.determineMostRecentPeriod();
                            // TODO do not use the current day..
                            if ((period == null) || oldProjectFilterCB.getValue()
                                        || DateHelper.isDayInProjectPeriod(new Date(), period)) {
                                project.addItem(tmp.getName(), "" + tmp.getId());
                            }
                        }
                        ProjectTrackerEntryPoint.getInstance().setProjects(result);
                        projects = result;

                        initWorkpackage();
                        projectsInitialised = true;
                        loadConfigurationfromStorage();
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true).getAllProjectsFull(callback);
        } else {
            project.clear();
            for (final ProjectDTO tmp : projects) {
                final ProjectPeriodDTO period = tmp.determineMostRecentPeriod();
                // TODO do not use the current day..
                if ((period == null) || oldProjectFilterCB.getValue()
                            || DateHelper.isDayInProjectPeriod(new Date(), period)) {
                    project.addItem(tmp.getName(), "" + tmp.getId());
                }
            }
            initWorkpackage();
            projectsInitialised = true;
            loadConfigurationfromStorage();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initWorkpackage() {
        final ProjectDTO selectedProject = getSelectedProject();
        workpackages.clear();
        workpackages.addItem("* all WorkPackages", "-1");
        if (selectedProject != null) {
            if (selectedProject.getWorkPackages() != null) {
                final WorkPackageDTO[] wps = selectedProject.getWorkPackages()
                            .toArray(new WorkPackageDTO[selectedProject.getWorkPackages().size()]);
                Arrays.sort(wps);
                for (final WorkPackageDTO tmp : wps) {
                    final WorkPackagePeriodDTO period = tmp.determineMostRecentPeriod();

//                    if (period == null || DateHelper.isDayInWorkPackagePeriod(day, period)) {
                    if (tmp.getName() != null ) {
                        if (period != null && period.getTodate() != null) {
                            if ( oldProjectFilterCB.getValue() || DateHelper.isDateGreaterOrEqual(period.getTodate(), new Date()) ) {
                                workpackages.addItem(extractWorkpackageName(tmp), String.valueOf(tmp.getId()));
                                setTotalHours(tmp);
                            }
                        } else {
                            workpackages.addItem(extractWorkpackageName(tmp), String.valueOf(tmp.getId()));
                            setTotalHours(tmp);
                        }
                    }
                }

                for (int i = 0; i < workpackages.getItemCount(); i++) {
                    final String itemText = workpackages.getItemText(i);
                    if (itemText.toUpperCase().startsWith("WP")) {
                        workpackages.getElement()
                                .getElementsByTagName("option")
                                .getItem(i)
                                .setAttribute("disabled", "disabled");
                    }
                }
            }
        } else {
            // we have selected the all project...
            for (final ProjectDTO proj : ProjectTrackerEntryPoint.getInstance().getProjects()) {
//                if ((proj.getWorkPackages() != null) && !proj.getWorkPackages().isEmpty()) {
//                    workpackages.addItem("-- " + proj.getName() + " --", String.valueOf(-2));
//                }
                for (final WorkPackageDTO wp : proj.getWorkPackages()) {
                    final WorkPackagePeriodDTO period = wp.determineMostRecentPeriod();

//                    if (period == null || DateHelper.isDayInWorkPackagePeriod(day, period)) {
                    if (wp.getName() != null ) {
                        if (period != null && period.getTodate() != null) {
                            if ( oldProjectFilterCB.getValue() || DateHelper.isDateGreaterOrEqual(period.getTodate(), new Date()) ) {
                                workpackages.addItem(extractWorkpackageName(wp), String.valueOf(wp.getId()));
                                setTotalHours(wp);
                            }
                        } else {
                            workpackages.addItem(extractWorkpackageName(wp), String.valueOf(wp.getId()));
                            setTotalHours(wp);
                        }
                    }
                }
            }
            for (int i = 0; i < workpackages.getItemCount(); i++) {
                final String itemText = workpackages.getItemText(i);
                if (itemText.toUpperCase().startsWith("WP") || itemText.startsWith("--")) {
                    workpackages.getElement()
                            .getElementsByTagName("option")
                            .getItem(i)
                            .setAttribute("disabled", "disabled");
                }
            }
            workpackages.setSelectedIndex(0);
        }
    }
    
    private void setTotalHours(final WorkPackageDTO wp) {
        final BasicAsyncCallback<Double> cb = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result,
                        final boolean operationFailed) {
                    int index = -1;
                    
                    for (int i = 0; i < workpackages.getItemCount(); ++i) {
                        if (workpackages.getValue(i).equals(String.valueOf(wp.getId()))) {
                            index = i;
                            break;
                        }
                    }
                    
                    if (index != -1) {
                        workpackages.setItemText(index, extractWorkpackageName(wp) + " (" + DateHelper.doubleToHours(result) + ")");
                    }
                }
            };
        List<WorkPackageDTO> wpList = new ArrayList<WorkPackageDTO>();
        wpList.add(wp);
        ProjectTrackerEntryPoint.getProjectService(false).getHoursSumForActivites(wpList, null, null, null, null, cb);
    }    

    /**
     * DOCUMENT ME!
     */
    private void initUsers() {
        if (false) { // every one should be able to make searches over all users
            final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
            users.addItem(staff.getFirstname() + " " + staff.getName(), staff.getId() + "");
            users.setSelectedIndex(0);
            // fire the change event since setSelected item does not
            Scheduler.get().scheduleDeferred(new Command() {

                    @Override
                    public void execute() {
                        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), users);
                    }
                });
            users.setEnabled(false);
            userList.add(staff);
            userInitialised = true;
        } else {
            final BasicAsyncCallback<ArrayList<StaffDTO>> callback = new BasicAsyncCallback<ArrayList<StaffDTO>>() {

                    @Override
                    protected void afterExecution(final ArrayList<StaffDTO> result, final boolean operationFailed) {
                        users.addItem("* all Users", "-1");
                        users.setSelectedIndex(-1);
                        Collections.sort(result);
                        for (final StaffDTO staff : result) {
                            users.addItem(staff.getFirstname() + " " + staff.getName(), staff.getId() + "");
                        }
                        users.setSelectedIndex(0);
                        // fire the change event since setSelected item does not
                        Scheduler.get().scheduleDeferred(new Command() {

                                @Override
                                public void execute() {
                                    DomEvent.fireNativeEvent(Document.get().createChangeEvent(), users);
                                }
                            });
                        userList = result;
                        userInitialised = true;
                        loadConfigurationfromStorage();
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true).getCurrentEmployees(callback);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ProjectDTO getSelectedProject() {
        final String value = project.getValue(project.getSelectedIndex());
        if (value.equals("-1")) {
            // the all option is selected return null
            return null;
        }
        return getProjectById(Long.parseLong(value));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ProjectDTO getProjectById(final long id) {
        for (final ProjectDTO tmp : projects) {
            if (tmp.getId() == id) {
                return tmp;
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private List<WorkPackageDTO> getSelectedWorkpackages() {
        ArrayList<WorkPackageDTO> selectedItems = null;
        for (int i = 0; i < workpackages.getItemCount(); i++) {
            if (workpackages.isItemSelected(i)) {
                final String value = workpackages.getValue(i);
                if (value.equals("-1")) {
                    // the all workpackage element is selected so return null
                    return null;
                } else {
                    final long wpId = Long.parseLong(value);
                    final WorkPackageDTO wp = getWorkpackageById(wpId);
                    if (selectedItems == null) {
                        selectedItems = new ArrayList<WorkPackageDTO>();
                    }
                    selectedItems.add(wp);
                }
            }
        }
        return selectedItems;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private WorkPackageDTO getWorkpackageById(final long id) {
        final ProjectDTO proj = getSelectedProject();
        if (proj == null) {
            for (final ProjectDTO p : ProjectTrackerEntryPoint.getInstance().getProjects()) {
                for (final WorkPackageDTO tmp : p.getWorkPackages()) {
                    if (tmp.getId() == id) {
                        return tmp;
                    }
                }
            }
        } else {
            for (final WorkPackageDTO tmp : proj.getWorkPackages()) {
                if (tmp.getId() == id) {
                    return tmp;
                }
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   workpackage  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static String extractWorkpackageName(final WorkPackageDTO workpackage) {
        WorkPackageDTO tmp = workpackage.getWorkPackage();
        String prefix = "";

        while (tmp != null) {
            prefix += "  ";
            tmp = tmp.getWorkPackage();
        }

        return prefix + workpackage.getName();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addSearchParamListener(final ReportSearchParamListener l) {
        this.listeners.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeSearchParamListener(final ReportSearchParamListener l) {
        listeners.remove(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  p  DOCUMENT ME!
     */
    public void setStatisticsPanel(final Composite p) {
        statisticWrapper.clear();
        statisticWrapper.add(p);
    }

    /**
     * DOCUMENT ME!
     */
    private void fireSearchParamsChanged() {
        for (final ReportSearchParamListener l : listeners) {
            l.searchParamsChanged();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private StaffDTO getSelectedUser() {
        try {
            final long value = Long.parseLong(users.getValue(users.getSelectedIndex()));
            if (value == -1) {
                // search for all users, return null
                return null;
            }
            for (final StaffDTO staff : userList) {
                if (staff.getId() == value) {
                    return staff;
                }
            }
        } catch (NumberFormatException e) {
            // should not happen
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public HashMap<String, Object> getSearchParams() {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        List<WorkPackageDTO> workpackages = getSelectedWorkpackages();
//        final List<WorkPackageDTO wp = getSelectedWorkpackage();
        if ((workpackages == null) || workpackages.isEmpty()) {
            // we have to check what project is selected...
            final ProjectDTO proj = getSelectedProject();
            /*
             * if the selected Proj is not null we have to search for all Workpackages of this Project. Otherwise (which
             * means all project are relevant) we have to do nothing
             */
            if (proj != null) {
                if (workpackages == null) {
                    workpackages = new ArrayList<WorkPackageDTO>();
                }
                workpackages.addAll(proj.getWorkPackages());
            }
        }
        map.put(WORKPACKAGE_KEY, workpackages);
        map.put(PROJECT_KEY, getSelectedProject());
        map.put(OLD_PROJECTS_KEY, oldProjectFilterCB.getValue());
        map.put(DATE_CHECKBOX_KEY, dateFilterCB.getValue());

        final List<StaffDTO> searchStaff = new ArrayList<StaffDTO>();
        final List<StaffDTO> selectedUsers = getSelectedUsers();
        if (selectedUsers != null) {
            searchStaff.addAll(selectedUsers);
        }
        map.put(STAFF_KEY, searchStaff);

        if (dateFilterCB.getValue()) {
            map.put(DATE_FROM_KEY, periodFrom.getValue());
            map.put(DATE_TO_KEY, periodTo.getValue());
        }

        map.put(DESC_KEY, description.getText());

        return map;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private ArrayList<StaffDTO> getSelectedUsers() {
        final ArrayList<StaffDTO> selectedItems = new ArrayList<StaffDTO>();
        for (int i = 0; i < users.getItemCount(); i++) {
            if (users.isItemSelected(i)) {
                final long value = Long.parseLong(users.getValue(i));
                if (value == -1) {
                    // search for all users, return null
                    return null;
                }
                for (final StaffDTO staff : userList) {
                    if (staff.getId() == value) {
                        selectedItems.add(staff);
                        break;
                    }
                }
            }
        }
        return selectedItems;
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (ProjectTrackerEntryPoint.getInstance().getLoggedInStaff() != loggedInUser) {
            userList.clear();
            users.clear();
            users.setEnabled(true);
            initUsers();
        }
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface ReportFilterPanelUiBinder extends UiBinder<Widget, ReportFilterPanel> {
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private class CheckBoxClickEvent extends ClickEvent {
    }
}
