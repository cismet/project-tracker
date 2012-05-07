/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskNoticeListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.listener.BasicRollbackCallback;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author therter
 */
public class TaskNotice extends Composite implements ClickHandler {

    private static final String MAIN_STYLES = "alert-message block-message timebox";
    private static final String[] ADDITIONAL_PROJECT_STYLES = {"proj1", "proj2", "proj3", "proj4", "proj5", "proj6", "proj7"};
    private static final HashMap<Long, Integer> projectStyle = new HashMap<Long, Integer>();
    protected HTML lab;
    protected ActivityDTO activity;
    private FlowPanel mainPanel = new FlowPanel();
    private Label close = new Label("x");
    private List<TaskDeleteListener> listener = new ArrayList<TaskDeleteListener>();
    private List<TaskNoticeListener> taskListener = new ArrayList<TaskNoticeListener>();
    private boolean deleteButtonDisabled;
    private boolean redBorder;

    static {
        projectStyle.put((long) 1, 0);
        projectStyle.put((long) 2, 1);
        projectStyle.put((long) 18, 2);
        projectStyle.put((long) 21, 3);
        projectStyle.put((long) 20, 4);
        projectStyle.put((long) 10, 5);
    }

    public TaskNotice(ActivityDTO activity) {
        this(activity, false);
    }

    public TaskNotice(ActivityDTO activity, boolean deleteButtonDisabled) {
        this.deleteButtonDisabled = deleteButtonDisabled;
        this.activity = activity;
        init();
        initWidget(mainPanel);
    }

    private void init() {
        lab = new HTML();

        if (!deleteButtonDisabled) {
            close.setStyleName("close pull-right closeButton");
            close.addClickHandler(this);
            mainPanel.add(close);
            this.redBorder = true;
        } else {
            this.redBorder = false;
        }
        mainPanel.add(lab);
        refresh();
    }

    private void setColour() {
        if (activity.getWorkPackage() != null && activity.getWorkPackage().getProject() != null) {
            List<ProjectDTO> proj = ProjectTrackerEntryPoint.getInstance().getProjects();
            if (proj != null) {
                long projectId = activity.getWorkPackage().getProject().getId();
                Integer index = projectStyle.get(projectId);

                if (index == null) {
                    index = 6;
                }

                if (index < 0 || index >= ADDITIONAL_PROJECT_STYLES.length) {
                    index = ADDITIONAL_PROJECT_STYLES.length - 1;
                }
                String a = mainPanel.getStyleName();

                if (activity.getWorkinghours() == 0.0 && redBorder) {
                    mainPanel.setStyleName(MAIN_STYLES + " red-border " + ADDITIONAL_PROJECT_STYLES[index]);
                } else {
                    mainPanel.setStyleName(MAIN_STYLES + " " + ADDITIONAL_PROJECT_STYLES[index]);
                }
            } else {
                Timer t = new Timer() {

                    @Override
                    public void run() {
                        setColour();
                    }
                };

                t.schedule(2000);
            }
        }
    }

//    private void setColourOld() {
//        int index = proj.indexOf(activity.getWorkPackage().getProject());
//
//        if (index < 0 || index >= ADDITIONAL_PROJECT_STYLES.length) {
//            index = ADDITIONAL_PROJECT_STYLES.length - 1;
//        }
//        String a = mainPanel.getStyleName();
//
//        if (activity.getWorkinghours() == 0.0) {
//            mainPanel.setStyleName(MAIN_STYLES + " red-border " + ADDITIONAL_PROJECT_STYLES[index]);
//        } else {
//            mainPanel.setStyleName(MAIN_STYLES + " " + ADDITIONAL_PROJECT_STYLES[index]);
//        }
//    }
    protected String getTextFromActivity() {
        String desc = getDesccription(activity.getDescription());
        StringBuilder text = new StringBuilder();

        if (activity.getWorkCategory() != null && activity.getWorkCategory().getId() == WorkCategoryDTO.TRAVEL) {
            text.append("Travel: ").append(activity.getWorkPackage().getAbbreviation());
        } else {
            text.append(activity.getWorkPackage().getAbbreviation());
        }

        double hours = Math.round(activity.getWorkinghours() * 100) / 100.0;
        text.append("<br />").append(desc);

        if (hours != 0.0 && !deleteButtonDisabled) {
            text.append("<br />").append(DateHelper.doubleToHours(hours)).append(" hours");
        }

        return text.toString();
    }

    protected String getDesccription(String desc) {
        if (desc == null) {
            return "";
        }
        int i = desc.indexOf("(@");

        if (i != -1) {
            String result = desc.substring(0, i);
            return result;
        } else {
            return desc;
        }
    }

    protected String getTooltipTextFromActivity() {
        StringBuilder text = new StringBuilder(activity.getWorkPackage().getProject().getName());
        text.append("\n").append(activity.getWorkPackage().getName());

        if (activity.getDescription() != null) {
            text.append("\n").append(activity.getDescription());
        }

        return text.toString();
    }

    public Widget getMouseHandledWidget() {
        return lab;
    }

    public ActivityDTO getActivity() {
        return activity;
    }

    public void setActivity(ActivityDTO activity) {
        this.activity = activity;
        refresh();
        fireActivityModified();
    }

    public void refresh() {
        lab.setHTML(getTextFromActivity());
        mainPanel.setTitle(getTooltipTextFromActivity());
        lab.setTitle(getTooltipTextFromActivity());
        setColour();
    }

    public void save() {
        BasicRollbackCallback<ActivityDTO> callback = new BasicRollbackCallback<ActivityDTO>(activity) {

            @Override
            protected void afterExecution(ActivityDTO result, boolean operationFailed) {
                if (!operationFailed) {
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).saveActivity(activity, callback);
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getSource() == close) {
            BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                @Override
                protected void afterExecution(Void result, boolean operationFailed) {
                    if (!operationFailed) {
                        fireActivityDelete();
                    } else {
                    }
                }
            };

            ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(activity, callback);
        }
    }

    public void addListener(TaskDeleteListener l) {
        listener.add(l);
    }

    public void removeListener(TaskDeleteListener l) {
        listener.remove(l);
    }

    private void fireActivityDelete() {
        for (TaskDeleteListener l : listener) {
            l.taskDelete(this);
        }
    }

    public void addTaskNoticeListener(TaskNoticeListener l) {
        taskListener.add(l);
    }

    public void removeTaskNoticeListener(TaskNoticeListener l) {
        taskListener.remove(l);
    }

    private void fireActivityModified() {
        for (TaskNoticeListener l : taskListener) {
            l.taskChanged(this);
        }
    }

    public void setDeleteButtonEnabled(boolean aFlag) {
        deleteButtonDisabled = aFlag;
        refresh();
    }

    public void setRedBorderEnabled(boolean aFlag) {
        redBorder = aFlag;
        refresh();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskNotice) {
            if (((TaskNotice) obj).getActivity().getId() == getActivity().getId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.activity != null ? (int) this.activity.getId() : 0);
        return hash;
    }
}
