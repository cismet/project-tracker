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
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TaskNoticeListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.ProjectDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.listener.BasicRollbackCallback;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TaskNotice extends Composite implements ClickHandler {

    //~ Static fields/initializers ---------------------------------------------

// private static final String MAIN_STYLES = "alert-message block-message timebox";
    private static final String MAIN_STYLES = "alert alert-block timebox";
    private static final String[] ADDITIONAL_PROJECT_STYLES = {
            "proj1",
            "proj2",
            "proj3",
            "proj4",
            "proj5",
            "proj6",
            "proj7"
        };
    private static final HashMap<Long, Integer> projectStyle = new HashMap<Long, Integer>();

    static {
        projectStyle.put((long)1, 0);
        projectStyle.put((long)2, 1);
        projectStyle.put((long)18, 2);
        projectStyle.put((long)21, 3);
        projectStyle.put((long)20, 4);
        projectStyle.put((long)10, 5);
    }

    //~ Instance fields --------------------------------------------------------

    protected HTML lab;
    protected ActivityDTO activity;
    private FlowPanel mainPanel = new FlowPanel();
    private Label close = new Label("x");
    private List<TaskDeleteListener> listener = new ArrayList<TaskDeleteListener>();
    private List<TaskNoticeListener> taskListener = new ArrayList<TaskNoticeListener>();
    private boolean deleteButtonDisabled;
    private boolean redBorder;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TaskNotice object.
     *
     * @param  activity  DOCUMENT ME!
     */
    public TaskNotice(final ActivityDTO activity) {
        this(activity, false);
    }

    /**
     * Creates a new TaskNotice object.
     *
     * @param  activity              DOCUMENT ME!
     * @param  deleteButtonDisabled  DOCUMENT ME!
     */
    public TaskNotice(final ActivityDTO activity, final boolean deleteButtonDisabled) {
        this.deleteButtonDisabled = deleteButtonDisabled;
        this.activity = activity;
        init();
        initWidget(mainPanel);
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
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

    /**
     * DOCUMENT ME!
     */
    private void setColour() {
        if ((activity.getWorkPackage() != null) && (activity.getWorkPackage().getProject() != null)) {
            final List<ProjectDTO> proj = ProjectTrackerEntryPoint.getInstance().getProjects();
            if (proj != null) {
                final long projectId = activity.getWorkPackage().getProject().getId();
                Integer index = projectStyle.get(projectId);

                if (index == null) {
                    index = 6;
                }

                if ((index < 0) || (index >= ADDITIONAL_PROJECT_STYLES.length)) {
                    index = ADDITIONAL_PROJECT_STYLES.length - 1;
                }
                final String a = mainPanel.getStyleName();

                if ((activity.getWorkinghours() == 0.0) && redBorder) {
                    mainPanel.setStyleName(MAIN_STYLES + " red-border " + ADDITIONAL_PROJECT_STYLES[index]);
                } else {
                    mainPanel.setStyleName(MAIN_STYLES + " " + ADDITIONAL_PROJECT_STYLES[index]);
                }
            } else {
                final Timer t = new Timer() {

                        @Override
                        public void run() {
                            setColour();
                        }
                    };

                t.schedule(2000);
            }
        }
    }
    /**
     * private void setColourOld() { int index = proj.indexOf(activity.getWorkPackage().getProject()); if (index < 0 ||
     * index >= ADDITIONAL_PROJECT_STYLES.length) { index = ADDITIONAL_PROJECT_STYLES.length - 1; } String a =
     * mainPanel.getStyleName(); if (activity.getWorkinghours() == 0.0) { mainPanel.setStyleName(MAIN_STYLES + "
     * red-border " + ADDITIONAL_PROJECT_STYLES[index]); } else { mainPanel.setStyleName(MAIN_STYLES + " " +
     * ADDITIONAL_PROJECT_STYLES[index]); } }.
     *
     * @return  DOCUMENT ME!
     */
    protected String getTextFromActivity() {
        final String desc = getDesccription(activity.getDescription());
        final StringBuilder text = new StringBuilder();

        if ((activity.getWorkCategory() != null) && (activity.getWorkCategory().getId() == WorkCategoryDTO.TRAVEL)) {
            text.append("Travel: ").append(activity.getWorkPackage().getAbbreviation());
        } else {
            text.append(activity.getWorkPackage().getAbbreviation());
        }

        final double hours = Math.round(activity.getWorkinghours() * 100) / 100.0;
        text.append("<br />").append(desc);

        if ((hours != 0.0) && (hours != -1.0) && !deleteButtonDisabled) {
            text.append("<br />").append(DateHelper.doubleToHours(hours)).append(" hours");
        }

        return text.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param   desc  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getDesccription(final String desc) {
        if (desc == null) {
            return "";
        }
        final int i = desc.indexOf("(@");

        if (i != -1) {
            final String result = desc.substring(0, i);
            return result;
        } else {
            return desc;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    protected String getTooltipTextFromActivity() {
        final StringBuilder text = new StringBuilder(activity.getWorkPackage().getProject().getName());
        text.append("\n").append(activity.getWorkPackage().getName());

        if (activity.getDescription() != null) {
            text.append("\n").append(activity.getDescription());
        }

        return text.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Widget getMouseHandledWidget() {
        return lab;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ActivityDTO getActivity() {
        return activity;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  activity  DOCUMENT ME!
     */
    public void setActivity(final ActivityDTO activity) {
        this.activity = activity;
        refresh();
        fireActivityModified();
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        lab.setHTML(getTextFromActivity());
        mainPanel.setTitle(getTooltipTextFromActivity());
        lab.setTitle(getTooltipTextFromActivity());
        setColour();
    }

    /**
     * DOCUMENT ME!
     */
    public void save() {
        final BasicRollbackCallback<ActivityDTO> callback = new BasicRollbackCallback<ActivityDTO>(activity) {

                @Override
                protected void afterExecution(final ActivityDTO result, final boolean operationFailed) {
                    if (!operationFailed) {
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).saveActivity(activity, callback);
    }

    @Override
    public void onClick(final ClickEvent event) {
        if (event.getSource() == close) {
            final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getStaff();
            final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                    @Override
                    protected void afterExecution(final Boolean result, final boolean operationFailed) {
                        if (!operationFailed) {
                            if (!result) {
                                deleteTask();
                            }
                        }
                    }
                };
            ProjectTrackerEntryPoint.getProjectService(true)
                    .isDayLocked(activity.getDay(), activity.getStaff(), callback);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void deleteTask() {
        final BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                @Override
                protected void afterExecution(final Void result, final boolean operationFailed) {
                    if (!operationFailed) {
                        fireActivityDelete();
                    } else {
                    }
                }
            };

        ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(activity, callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addListener(final TaskDeleteListener l) {
        listener.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeListener(final TaskDeleteListener l) {
        listener.remove(l);
    }

    /**
     * DOCUMENT ME!
     */
    private void fireActivityDelete() {
        for (final TaskDeleteListener l : listener) {
            l.taskDelete(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void addTaskNoticeListener(final TaskNoticeListener l) {
        taskListener.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeTaskNoticeListener(final TaskNoticeListener l) {
        taskListener.remove(l);
    }

    /**
     * DOCUMENT ME!
     */
    private void fireActivityModified() {
        for (final TaskNoticeListener l : taskListener) {
            l.taskChanged(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    public void setDeleteButtonEnabled(final boolean aFlag) {
        deleteButtonDisabled = aFlag;
        refresh();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    public void setRedBorderEnabled(final boolean aFlag) {
        redBorder = aFlag;
        refresh();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TaskNotice) {
            if (((TaskNotice)obj).getActivity().getId() == getActivity().getId()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = (37 * hash) + ((this.activity != null) ? (int)this.activity.getId() : 0);
        return hash;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    public void setCloseButtonVisible(final boolean aFlag) {
        close.setVisible(aFlag);
    }
}
