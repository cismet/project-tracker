/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cismet.projecttracker.client.ImageConstants;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.report.ReportResultPanel;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ContractDTO;
import de.cismet.projecttracker.client.dto.ProjectCategoryDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.helper.GUIHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.utilities.TaskFiller;
import java.util.Collections;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class StoryForm extends Composite implements ChangeHandler, KeyUpHandler, ClickHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static StoryFormUiBinder uiBinder = GWT.create(StoryFormUiBinder.class);
    private static WorkCategoryDTO travelCatagory = null;

    //~ Instance fields --------------------------------------------------------

    @UiField
    TextBox description;
    @UiField
    com.github.gwtbootstrap.client.ui.TextBox duration;
    @UiField
    Button saveButton;
    @UiField
    Button fillTaskBtn;
    @UiField
    Button cancelButton;
    @UiField
    ListBox project;
    @UiField
    ListBox workpackage;
    @UiField
    CheckBox travel;
    @UiField
    SimpleCheckBox wpDateFilterCB;
    @UiField
    ControlGroup durationCtrlGroup;
    @UiField
    SpanElement hoursLeft;
    private DialogBox form;
    private TaskStory caller;
    private Story story;
    private Date day = new Date();
    private List<ProjectDTO> projects;
    private boolean modification = false;
    private TaskNotice tn;
    private final Storage storage = Storage.getLocalStorageIfSupported();
    private String currentProject;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StoryForm object.
     *
     * @param  form  DOCUMENT ME!
     * @param  tb    DOCUMENT ME!
     * @param  s     DOCUMENT ME!
     * @param  tn    DOCUMENT ME!
     */
    public StoryForm(final DialogBox form, final TaskStory tb, final Story s, final TaskNotice tn) {
        this.form = form;
        this.caller = tb;
        this.tn = tn;
        tn.getActivity().getDay();
        this.story = s;
        this.modification = true;
        this.day = tn.getActivity().getDay();
        initWidget(uiBinder.createAndBindUi(this));
        init();
        description.setText(tn.getActivity().getDescription());
        if ((tn.getActivity().getWorkCategory() != null)
                    && (tn.getActivity().getWorkCategory().getId() == WorkCategoryDTO.TRAVEL)) {
            travel.setValue(true);
        } else {
            travel.setValue(false);
        }
        duration.setText(DateHelper.doubleToHours(tn.getActivity().getWorkinghours()));
        setProjectById(tn.getActivity().getWorkPackage().getProject().getId());
        initWorkpackage();
        setWPById(tn.getActivity().getWorkPackage().getId());
        // todo set project und workpackage
        project.addChangeHandler(this);
        workpackage.addChangeHandler(this);
        saveButton.setText("Save");
        setDefaultButton();
        configFillButton();
        configDurationBox();
        setProjectHoursLeft();
    }

    /**
     * Creates a new StoryForm object.
     *
     * @param  form  DOCUMENT ME!
     * @param  tb    DOCUMENT ME!
     * @param  s     DOCUMENT ME!
     * @param  day   DOCUMENT ME!
     */
    public StoryForm(final DialogBox form, final TaskStory tb, final Story s, final Date day) {
        this.form = form;
        this.caller = tb;
        this.day = day;
        this.story = s;
        initWidget(uiBinder.createAndBindUi(this));
        init();
        project.addChangeHandler(this);
        workpackage.addChangeHandler(this);
        setDefaultButton();
        configFillButton();
        configDurationBox();
        setProjectHoursLeft();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void configDurationBox() {
        duration.addKeyUpHandler(new KeyUpHandler() {

                @Override
                public void onKeyUp(final KeyUpEvent event) {
                    validateDuration();
                }
            });
        duration.addValueChangeHandler(new ValueChangeHandler<String>() {

                @Override
                public void onValueChange(final ValueChangeEvent<String> event) {
                    validateDuration();
                }
            });
    }
    
    private void setProjectHoursLeft() {
        final BasicAsyncCallback<Double> cb = new BasicAsyncCallback<Double>() {

                @Override
                protected void afterExecution(final Double result,
                        final boolean operationFailed) {
                    hoursLeft.setInnerText("");
                    if (operationFailed) {
                        return;
                    }
                    if ((result == null)) {
                        return;
                    }

                    hoursLeft.setInnerText("(Total Hours: " + DateHelper.doubleToHours(result) + ")");
                }
            };
        List<WorkPackageDTO> wpList = new ArrayList<WorkPackageDTO>();
        wpList.add(getSelectedWorkpackage());
        ProjectTrackerEntryPoint.getProjectService(false).getHoursSumForActivites(wpList, null, null, null, null, cb);
    }

    /**
     * DOCUMENT ME!
     */
    private void validateDuration() {
        final String durationText = duration.getText();
        boolean isValid = false;

        if (durationText != null) {
            if ((ProjectTrackerEntryPoint.getInstance().getStaff().getPermissions()
                            & ProjectTrackerEntryPoint.ORDER_PERMISSION) == ProjectTrackerEntryPoint.ORDER_PERMISSION) {
                isValid = durationText.matches("-?[0-9:]*");
            } else {
                isValid = durationText.matches("[0-9:]*");
            }
        }

        if (!isValid) {
            durationCtrlGroup.setType(ControlGroupType.ERROR);
            saveButton.setEnabled(false);
        } else {
            durationCtrlGroup.setType(ControlGroupType.NONE);
            saveButton.setEnabled(true);
        }
    }

    @Override
    public void onClick(final ClickEvent event) {
        if (event.getSource() == wpDateFilterCB) {
            initWorkpackage();
        }
    }

    @Override
    public void onChange(final ChangeEvent event) {
        if (event.getSource() == project) {
            if (storage != null && project != null && project.getSelectedItemText() != null) {
                storage.setItem("activityProject", project.getSelectedItemText());
            }
            initWorkpackage();
            setProjectHoursLeft();
        } else if (event.getSource() == workpackage) {
            setProjectHoursLeft();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setDefaultButton() {
        saveButton.setFocus(true);
        description.addKeyUpHandler(this);
        duration.addKeyUpHandler(this);
        project.addKeyUpHandler(this);
        workpackage.addKeyUpHandler(this);
    }

    /**
     * DOCUMENT ME!
     */
    private void configFillButton() {
        fillTaskBtn.setHTML("<img src='" + ImageConstants.INSTANCE.magicFiller().getURL() + "' />");
        fillTaskBtn.setStyleName("btn pull-left");
        fillTaskBtn.getElement().getStyle().setProperty("padding", "8px 12px");
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("saveButton")
    void onButtonClick(final ClickEvent event) {
        if (travel.getValue() && (travelCatagory == null)) {
            final BasicAsyncCallback<WorkCategoryDTO> callback = new BasicAsyncCallback<WorkCategoryDTO>() {

                    @Override
                    protected void afterExecution(final WorkCategoryDTO result, final boolean operationFailed) {
                        if (!operationFailed) {
                            if (travelCatagory == null) {
                                travelCatagory = result;
                                save();
                            }
                        }
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true).getWorkCategory(WorkCategoryDTO.TRAVEL, callback);
            return;
        }
        save();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("fillTaskBtn")
    void onFillButtonClick(final ClickEvent event) {
        double timeToFill = TaskFiller.getTimeToFill(day, null, caller, story);
        try {
            if ((duration.getText() != null) && !duration.getText().isEmpty()) {
                timeToFill += DateHelper.hoursToDouble(duration.getText());
            }
        } catch (NumberFormatException e) {
        } finally {
            if (timeToFill < 0) {
                ProjectTrackerEntryPoint.outputBox("The fill operation would set a negative time for this task.");
                return;
            } else {
                duration.setText(DateHelper.doubleToHours(timeToFill));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void save() {
        double workinghours = 0.0;

        try {
            String durationAsText = duration.getText();

            if ((durationAsText != null) && !durationAsText.equals("")) {
                if ((ProjectTrackerEntryPoint.getInstance().getStaff().getPermissions()
                                & ProjectTrackerEntryPoint.ORDER_PERMISSION)
                            == ProjectTrackerEntryPoint.ORDER_PERMISSION) {
                    final boolean startWithMinus = durationAsText.startsWith("-");

                    if (startWithMinus) {
                        durationAsText = durationAsText.substring(1);
                    }
                    workinghours = DateHelper.hoursToDouble(durationAsText);

                    if (startWithMinus) {
                        workinghours *= -1;
                    }
                } else {
                    workinghours = DateHelper.hoursToDouble(durationAsText);
                }
            }
        } catch (NumberFormatException e) {
            ProjectTrackerEntryPoint.outputBox("The duration is not valid");
            return;
        }
        
        if (description.getText().trim().equals("") && !getSelectedProject().getName().equals("Abwesenheit") 
                && !getSelectedWorkpackage().getName().equalsIgnoreCase("Teambesprechung")
                && !getSelectedProject().getName().equals("Abgleich Zeitkonto") ) {
            ProjectTrackerEntryPoint.outputBox("An empty description is not allowed.");
            return;
        }
        
        String workpackageName = getSelectedWorkpackage().getName().trim().replaceAll("^\\d*_", "");
        String descriptionText = description.getText().trim().replaceAll("^\\d*_", "");
        
        if (similarity(workpackageName, descriptionText) > 0.5) {
            ProjectTrackerEntryPoint.outputBox("The description and the name of the workpackage are too similar.");
            return;
        }

        final ActivityDTO newActivity = (modification ? tn.getActivity().createCopy() : new ActivityDTO());

        newActivity.setDay(day);
        newActivity.setWorkPackage(getSelectedWorkpackage());
        newActivity.setDescription(description.getText());
        newActivity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
        newActivity.setKindofactivity(ActivityDTO.ACTIVITY);
        newActivity.setWorkinghours(workinghours);
        if (newActivity.getWorkPackage() != null) {
            final WorkPackageDTO wp = newActivity.getWorkPackage();

            final WorkPackagePeriodDTO period = wp.determineMostRecentPeriod();
            if (!((period == null) || DateHelper.isDayInWorkPackagePeriod(day, period))) {
                ProjectTrackerEntryPoint.outputBox(
                    "Can not drop this activity here since the workpackage is out of date ( "
                            + DateHelper.formatDate(period.getFromdate())
                            + " - "
                            + DateHelper.formatDate(period.getTodate())
                            + " Your administrators advice is: "
                            + wp.getExpirationDescription());
                return;
            }
        }
        if (travel.getValue()) {
            newActivity.setWorkCategory(travelCatagory);
        } else {
            newActivity.setWorkCategory(null);
        }

        if (modification) {
            final BasicAsyncCallback<ActivityDTO> callback = new BasicAsyncCallback<ActivityDTO>() {

                    @Override
                    protected void afterExecution(final ActivityDTO result, final boolean operationFailed) {
                        if (!operationFailed) {
                            tn.setActivity(newActivity);
                            form.hide();
                        }
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true).saveActivity(newActivity, callback);
        } else {
            final BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {

                    @Override
                    protected void afterExecution(final Long result, final boolean operationFailed) {
                        if (!operationFailed) {
                            newActivity.setId(result);
                            caller.addTask(newActivity);
                            form.hide();
                        }
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true).createActivity(newActivity, callback);
        }
    }
    
    private double similarity(String s1, String s2) {
        return 1 - levenshtein(s1, s2) / Math.max(s1.length(), s2.length());
    }
    
    /**
     * levenshtein algorithm
     * 
     * @param s1 first string to compare
     * @param s2 second string to compare
     * 
     * @return the levenshtein distance
     */
    private double levenshtein(String s1, String s2) {
        // init
        int m = s1.length();
        int n = s2.length();
        int[][] d = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            d[i] = new int[n];
            d[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            d[0][j] = j;
        }

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1];
                } else {
                    d[i][j] = Math.min(Math.min(
                                d[i - 1][j] + 1, // a deletion
                                d[i][j - 1] + 1), // an insertion
                                d[i - 1][j - 1] + 1 // a substitution
                    );
                }
            }
        }
        
        return d[m][n];
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("cancelButton")
    void onButtoClick(final ClickEvent event) {
        form.hide();
    }

    /**
     * DOCUMENT ME!
     */
    private void init() {
        if (storage != null) {
            currentProject = storage.getItem("activityProject");
        }
        wpDateFilterCB.setValue(true);
        wpDateFilterCB.setTitle(
            " if selected, only WorkPackages which are in the current period of the project, are shown");
        wpDateFilterCB.addClickHandler(this);
        final List<ProjectDTO> result = ProjectTrackerEntryPoint.getInstance().getProjects();
//        travel.setText("Travel: ");
        ContractDTO contract = null;
        try {
            contract = ProjectTrackerEntryPoint.getInstance().getContractForStaff(day);
        } catch (InvalidInputValuesException ex) {
            Logger.getLogger(StoryForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        final String companyName = (contract == null) ? "" : contract.getCompany().getName();
        if (result == null) {
            final BasicAsyncCallback<ArrayList<ProjectDTO>> callback = new BasicAsyncCallback<ArrayList<ProjectDTO>>() {

                    @Override
                    protected void afterExecution(final ArrayList<ProjectDTO> result, final boolean operationFailed) {
                        int index = 0;
                        int selectedProjectIndex = 0;
                        
                        for (final ProjectDTO tmp : result) {
                            final ProjectPeriodDTO period = tmp.determineMostRecentPeriod();

                            if (GUIHelper.canUserBillToProject(companyName, tmp)
                                        && ((period == null) || DateHelper.isDayInProjectPeriod(day, period))) {
                                project.addItem(tmp.getName(), "" + tmp.getId());
                                
                                if (tmp.getName().equals(currentProject)) {
                                    selectedProjectIndex = index;
                                }
                                
                                ++index;
                            }
                        }
                        ProjectTrackerEntryPoint.getInstance().setProjects(result);
                        projects = result;
                        project.setSelectedIndex(selectedProjectIndex);
                        initWorkpackage();
                    }
                };

            ProjectTrackerEntryPoint.getProjectService(true).getAllProjectsFull(callback);
        } else {
            int index = 0;
            int selectedProjectIndex = 0;
            
            for (final ProjectDTO tmp : result) {
                final ProjectPeriodDTO period = tmp.determineMostRecentPeriod();

                if (GUIHelper.canUserBillToProject(companyName, tmp)
                            && ((period == null) || DateHelper.isDayInProjectPeriod(day, period))) {
                    project.addItem(tmp.getName(), "" + tmp.getId());
                    
                    if (tmp.getName().equals(currentProject)) {
                        selectedProjectIndex = index;
                    }
                    
                    ++index;
                }
            }
            projects = result;
            project.setSelectedIndex(selectedProjectIndex);
            initWorkpackage();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void initWorkpackage() {
        final ProjectDTO selectedProject = getSelectedProject();
        if (selectedProject != null) {
            workpackage.clear();

            if (selectedProject.getWorkPackages() != null) {
                final WorkPackageDTO[] wps = selectedProject.getWorkPackages()
                            .toArray(new WorkPackageDTO[selectedProject.getWorkPackages().size()]);
                Arrays.sort(wps);
                // for the abwesenheits package we want urlaub,krank, pause at the beginning
                if (selectedProject.getName().equals("Abwesenheit")) {
                    int alreadyAdded = 0;
                    for (int i = 0; i < wps.length; i++) {
                        final WorkPackageDTO wp = wps[i];
                        if ((wp.getId() == ActivityDTO.HOLIDAY_ID) || (wp.getId() == ActivityDTO.ILLNESS_ID)
                                    || (wp.getId() == ActivityDTO.PAUSE_ID)) {
                            final WorkPackageDTO tmp = wps[alreadyAdded];
                            wps[alreadyAdded] = wp;
                            wps[i] = tmp;
                            alreadyAdded++;
                        }
                    }
                }
                for (final WorkPackageDTO tmp : wps) {
                    final WorkPackagePeriodDTO period = tmp.determineMostRecentPeriod();

                    if (wpDateFilterCB.getValue()) {
                        if ((period == null) || DateHelper.isDayInWorkPackagePeriod(day, period)) {
                            if (tmp.getName() != null) {
                                workpackage.addItem(extractWorkpackageName(tmp), String.valueOf(tmp.getId()));
                            }
                        }
                    } else {
                        if (tmp.getName() != null) {
                            workpackage.addItem(extractWorkpackageName(tmp), String.valueOf(tmp.getId()));
                        }
                    }
                }

                for (int i = 0; i < workpackage.getItemCount(); i++) {
                    final String itemText = workpackage.getItemText(i);
                    if (itemText.toUpperCase().startsWith("WP")) {
                        workpackage.getElement()
                                .getElementsByTagName("option")
                                .getItem(i)
                                .setAttribute("disabled", "disabled");
                    }
                }
            }
        }
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
     * @return  DOCUMENT ME!
     */
    private WorkPackageDTO getSelectedWorkpackage() {
        final String value = workpackage.getValue(workpackage.getSelectedIndex());

        return getWorkpackageById(Long.parseLong(value));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private WorkPackageDTO getWorkpackageById(final long id) {
        for (final WorkPackageDTO tmp : getSelectedProject().getWorkPackages()) {
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
    private ProjectDTO getSelectedProject() {
        final String value = project.getValue(project.getSelectedIndex());

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
     * @param  id  DOCUMENT ME!
     */
    private void setProjectById(final long id) {
        for (int i = 0; i < project.getItemCount(); ++i) {
            if (project.getValue(i).equals("" + id)) {
                project.setSelectedIndex(i);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  id  DOCUMENT ME!
     */
    private void setWPById(final long id) {
        for (int i = 0; i < workpackage.getItemCount(); ++i) {
            if (workpackage.getValue(i).equals("" + id)) {
                workpackage.setSelectedIndex(i);
            }
        }
    }

    @Override
    public void onKeyUp(final KeyUpEvent event) {
        if (event.getNativeKeyCode() == 13) {
            save();
        }
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface StoryFormUiBinder extends UiBinder<Widget, StoryForm> {
    }
}
