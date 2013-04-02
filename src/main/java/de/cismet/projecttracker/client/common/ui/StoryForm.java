package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.*;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StoryForm extends Composite implements ChangeHandler, KeyUpHandler {

    private static TaskFormUiBinder uiBinder = GWT.create(TaskFormUiBinder.class);
    private static WorkCategoryDTO travelCatagory = null;
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
    private DialogBox form;
    private TaskStory caller;
    private Date day = new Date();
    private List<ProjectDTO> projects;
    private boolean modification = false;
    private TaskNotice tn;

    @Override
    public void onChange(ChangeEvent event) {
        if (event.getSource() == project) {
            initWorkpackage();
        }
    }

    interface TaskFormUiBinder extends UiBinder<Widget, StoryForm> {
    }

    public StoryForm(DialogBox form, TaskStory tb, Date day) {
        this.form = form;
        this.caller = tb;
        this.day = day;
        initWidget(uiBinder.createAndBindUi(this));
        init();
        project.addChangeHandler(this);
        setDefaultButton();
    }

    public StoryForm(DialogBox form, TaskStory tb, TaskNotice tn) {
        this.form = form;
        this.caller = tb;
        this.tn = tn;
        this.modification = true;
        this.day = tn.getActivity().getDay();
        initWidget(uiBinder.createAndBindUi(this));
        init();
        description.setText(tn.getActivity().getDescription());
        if (tn.getActivity().getWorkCategory() != null
                && tn.getActivity().getWorkCategory().getId() == WorkCategoryDTO.TRAVEL) {
            travel.setValue(true);
        } else {
            travel.setValue(false);
        }
        duration.setText(DateHelper.doubleToHours(tn.getActivity().getWorkinghours()));
        setProjectById(tn.getActivity().getWorkPackage().getProject().getId());
        initWorkpackage();
        setWPById(tn.getActivity().getWorkPackage().getId());
        //todo set project und workpackage
        project.addChangeHandler(this);
        button.setText("Save");
        setDefaultButton();
    }

    private void setDefaultButton() {
        button.setFocus(true);
        description.addKeyUpHandler(this);
        duration.addKeyUpHandler(this);
        project.addKeyUpHandler(this);
        workpackage.addKeyUpHandler(this);
    }

    @UiHandler("button")
    void onButtonClick(ClickEvent event) {
        if (travel.getValue() && travelCatagory == null) {
            BasicAsyncCallback<WorkCategoryDTO> callback = new BasicAsyncCallback<WorkCategoryDTO>() {
                @Override
                protected void afterExecution(WorkCategoryDTO result, boolean operationFailed) {
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

    private void save() {
        double workinghours = 0.0;

        try {
            if (duration.getText() != null && !duration.getText().equals("")) {
                workinghours = DateHelper.hoursToDouble(duration.getText());
            }
        } catch (NumberFormatException e) {
            ProjectTrackerEntryPoint.outputBox("The duration is not valid");
        }
        final ActivityDTO newActivity = (modification ? tn.getActivity().createCopy() : new ActivityDTO());
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
            BasicAsyncCallback<ActivityDTO> callback = new BasicAsyncCallback<ActivityDTO>() {
                @Override
                protected void afterExecution(ActivityDTO result, boolean operationFailed) {
                    if (!operationFailed) {
                        tn.setActivity(newActivity);
                        form.hide();
                    }
                }
            };

            ProjectTrackerEntryPoint.getProjectService(true).saveActivity(newActivity, callback);
        } else {
            BasicAsyncCallback<Long> callback = new BasicAsyncCallback<Long>() {
                @Override
                protected void afterExecution(Long result, boolean operationFailed) {
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

    @UiHandler("cancelButton")
    void onButtoClick(ClickEvent event) {
        form.hide();
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

                        if (period == null || DateHelper.isDayInProjectPeriod(day, period)) {
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

                if (period == null || DateHelper.isDayInProjectPeriod(day, period)) {
                    project.addItem(tmp.getName(), "" + tmp.getId());
                }
            }
            projects = result;
            initWorkpackage();
        }
    }

    private void initWorkpackage() {
        ProjectDTO selectedProject = getSelectedProject();
        if (selectedProject != null) {
            workpackage.clear();

            if (selectedProject.getWorkPackages() != null) {
                WorkPackageDTO[] wps = selectedProject.getWorkPackages().toArray(new WorkPackageDTO[selectedProject.getWorkPackages().size()]);
                Arrays.sort(wps);
                //for the abwesenheits package we want urlaub,krank, pause at the beginning
                if (selectedProject.getName().equals("Abwesenheit")) {
                    int alreadyAdded = 0;
                    for (int i = 0; i < wps.length; i++) {
                        WorkPackageDTO wp = wps[i];
                        if (wp.getId() == ActivityDTO.HOLIDAY_ID || wp.getId() == ActivityDTO.ILLNESS_ID || wp.getId() == ActivityDTO.PAUSE_ID) {
                            WorkPackageDTO tmp = wps[alreadyAdded];
                            wps[alreadyAdded] = wp;
                            wps[i] = tmp;
                            alreadyAdded++;
                        }
                    }
                }
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
        }
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

    private WorkPackageDTO getSelectedWorkpackage() {
        String value = workpackage.getValue(workpackage.getSelectedIndex());

        return getWorkpackageById(Long.parseLong(value));
    }

    private WorkPackageDTO getWorkpackageById(long id) {
        for (WorkPackageDTO tmp : getSelectedProject().getWorkPackages()) {
            if (tmp.getId() == id) {
                return tmp;
            }
        }
        return null;
    }

    private ProjectDTO getSelectedProject() {
        String value = project.getValue(project.getSelectedIndex());

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

    private void setProjectById(long id) {
        for (int i = 0; i < project.getItemCount(); ++i) {
            if (project.getValue(i).equals("" + id)) {
                project.setSelectedIndex(i);
            }
        }
    }

    private void setWPById(long id) {
        for (int i = 0; i < workpackage.getItemCount(); ++i) {
            if (workpackage.getValue(i).equals("" + id)) {
                workpackage.setSelectedIndex(i);
            }
        }
    }

    @Override
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == 13) {
            save();
        }
    }
}