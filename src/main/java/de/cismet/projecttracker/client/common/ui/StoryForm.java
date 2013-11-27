/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
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

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @version  $Revision$, $Date$
 */
public class StoryForm extends Composite implements ChangeHandler, KeyUpHandler, ClickHandler {

    //~ Static fields/initializers ---------------------------------------------

    private static TaskFormUiBinder uiBinder = GWT.create(TaskFormUiBinder.class);
    private static WorkCategoryDTO travelCatagory = null;

    //~ Instance fields --------------------------------------------------------

    @UiField
    TextBox description;
    @UiField
    TextBox duration;
    @UiField
    Button button;
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
    private DialogBox form;
    private TaskStory caller;
    private Date day = new Date();
    private List<ProjectDTO> projects;
    private boolean modification = false;
    private TaskNotice tn;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StoryForm object.
     *
     * @param  form  DOCUMENT ME!
     * @param  tb    DOCUMENT ME!
     * @param  day   DOCUMENT ME!
     */
    public StoryForm(final DialogBox form, final TaskStory tb, final Date day) {
        this.form = form;
        this.caller = tb;
        this.day = day;
        initWidget(uiBinder.createAndBindUi(this));
        init();
        project.addChangeHandler(this);
        setDefaultButton();
    }

    /**
     * Creates a new StoryForm object.
     *
     * @param  form  DOCUMENT ME!
     * @param  tb    DOCUMENT ME!
     * @param  tn    DOCUMENT ME!
     */
    public StoryForm(final DialogBox form, final TaskStory tb, final TaskNotice tn) {
        this.form = form;
        this.caller = tb;
        this.tn = tn;
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
        button.setText("Save");
        setDefaultButton();
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void onClick(final ClickEvent event) {
        if (event.getSource() == wpDateFilterCB) {
            initWorkpackage();
            ;
        }
    }

    @Override
    public void onChange(final ChangeEvent event) {
        if (event.getSource() == project) {
            initWorkpackage();
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setDefaultButton() {
        button.setFocus(true);
        description.addKeyUpHandler(this);
        duration.addKeyUpHandler(this);
        project.addKeyUpHandler(this);
        workpackage.addKeyUpHandler(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  event  DOCUMENT ME!
     */
    @UiHandler("button")
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
     */
    private void save() {
        double workinghours = 0.0;

        try {
            if ((duration.getText() != null) && !duration.getText().equals("")) {
                workinghours = DateHelper.hoursToDouble(duration.getText());
            }
        } catch (NumberFormatException e) {
            ProjectTrackerEntryPoint.outputBox("The duration is not valid");
        }
        final ActivityDTO newActivity = (modification ? tn.getActivity().createCopy() : new ActivityDTO());
        if (newActivity.getWorkPackage() != null) {
            final ProjectDTO project = newActivity.getWorkPackage().getProject();
            if (project != null) {
                final ProjectPeriodDTO period = project.determineMostRecentPeriod();
                if (!((period == null) || DateHelper.isDayInProjectPeriod(day, period))) {
                    ProjectTrackerEntryPoint.outputBox(
                        "Can not drop this activity here since the workpackage is out of date ( "
                                + DateHelper.formatDate(period.getFromdate())
                                + " - "
                                + DateHelper.formatDate(period.getTodate()));
                    return;
                }
            }
        }
        newActivity.setDay(day);
        newActivity.setWorkPackage(getSelectedWorkpackage());
        newActivity.setDescription(description.getText());
        newActivity.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
        newActivity.setKindofactivity(ActivityDTO.ACTIVITY);
        newActivity.setWorkinghours(workinghours);
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
        wpDateFilterCB.setValue(true);
        wpDateFilterCB.setTitle(
            " if selected, only WorkPackages which are in the current period of the project, are shown");
        wpDateFilterCB.addClickHandler(this);
        final List<ProjectDTO> result = ProjectTrackerEntryPoint.getInstance().getProjects();
//        travel.setText("Travel: ");
        if (result == null) {
            final BasicAsyncCallback<ArrayList<ProjectDTO>> callback = new BasicAsyncCallback<ArrayList<ProjectDTO>>() {

                    @Override
                    protected void afterExecution(final ArrayList<ProjectDTO> result, final boolean operationFailed) {
                        for (final ProjectDTO tmp : result) {
                            final ProjectPeriodDTO period = tmp.determineMostRecentPeriod();

                            if ((period == null) || DateHelper.isDayInProjectPeriod(day, period)) {
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
            for (final ProjectDTO tmp : result) {
                final ProjectPeriodDTO period = tmp.determineMostRecentPeriod();

                if ((period == null) || DateHelper.isDayInProjectPeriod(day, period)) {
                    project.addItem(tmp.getName(), "" + tmp.getId());
                }
            }
            projects = result;
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
    interface TaskFormUiBinder extends UiBinder<Widget, StoryForm> {
    }
}
