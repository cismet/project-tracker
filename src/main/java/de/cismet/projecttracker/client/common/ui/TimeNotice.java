/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeNoticeListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.listener.BasicRollbackCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author therter
 */
public class TimeNotice extends Composite implements ChangeHandler, ClickHandler {

    private FlowPanel mainPanel = new FlowPanel();
    private ActivityDTO start;
    private ActivityDTO end;
    private TextBox startTime = new TextBox();
    private TextBox endTime = new TextBox();
    private Label close = new Label("x");
    private List<TaskDeleteListener> listener = new ArrayList<TaskDeleteListener>();
    private List<TimeNoticeListener> timeListener = new ArrayList<TimeNoticeListener>();

    public TimeNotice(ActivityDTO start, ActivityDTO end) {
        this.start = start;
        this.end = end;
        init();
        initWidget(mainPanel);
        mainPanel.setStyleName("time-notice");
    }

    private void init() {
        startTime.setMaxLength(5);
        endTime.setMaxLength(5);
        startTime.setStyleName("inlineComponent time-input");
        endTime.setStyleName("inlineComponent time-input");
        close.setStyleName("close pull-right closeButton");
        close.addClickHandler(this);
        mainPanel.add(close);
        mainPanel.add(startTime);
        mainPanel.add(endTime);
        startTime.setText(DateHelper.formatTime(start.getDay()));
        if (end != null) {
            endTime.setText(DateHelper.formatTime(end.getDay()));
        } else {
            endTime.setText("");
        }

        startTime.addChangeHandler(this);
        endTime.addChangeHandler(this);
    }

    @Override
    public void onChange(final ChangeEvent event) {
        final Object eventSource = event.getSource();
        BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) {
                if (!operationFailed) {
                    if (!result || ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                        changeTimeNotice(eventSource);
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(start.getDay(), start.getStaff(), callback);
    }

    private void changeTimeNotice(Object eventSource) {
        ActivityDTO activityToSave = null;
        String newDate = null;
        boolean endAtNextDay = false;
        boolean createNewActivity = false;

        if (eventSource == startTime) {
            activityToSave = start;
            newDate = startTime.getText();
        } else if (eventSource == endTime) {
            activityToSave = end;
            newDate = endTime.getText();

            if (!endTime.getText().equals("")) {
                try {
                    Date endDate = DateHelper.parseString(newDate, DateTimeFormat.getFormat("HH:mm"));
                    Date startDate = DateHelper.parseString(startTime.getText(), DateTimeFormat.getFormat("HH:mm"));

                    if (endDate.before(startDate)) {
                        if (endDate.getHours() > 3) {
                            ProjectTrackerEntryPoint.outputBox("The end time must not be after 3:59 a.m. at the next day.");
                            ((TextBox) eventSource).setText(DateHelper.formatTime(activityToSave.getDay()));

                            return;
                        }
                        endAtNextDay = true;
                    }

                    if (activityToSave == null) {
                        end = new ActivityDTO();
                        end.setKindofactivity(ActivityDTO.END_OF_DAY);
                        end.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
                        end.setDay((Date) start.getDay().clone());
                        activityToSave = end;
                        createNewActivity = true;
                    }
                } catch (IllegalArgumentException e) {
                    ((TextBox) eventSource).setText(DateHelper.formatTime(activityToSave.getDay()));
                    return;
                }
            } else {
                activityToSave = null;
            }
        }
        BasicRollbackCallback<ActivityDTO> callback = null;
        BasicAsyncCallback<Long> createCallback = null;

        if (!createNewActivity) {
            if (activityToSave == null) {
                if (end != null) {
                    BasicAsyncCallback<Void> deleteCallback = new BasicAsyncCallback<Void>() {

                        @Override
                        protected void afterExecution(Void result, boolean operationFailed) {
                            if (!operationFailed) {
                                end = null;
                            }
                        }
                    };

                    ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(end, deleteCallback);
                    return;
                }
            } else {
                callback = new BasicRollbackCallback<ActivityDTO>(activityToSave) {

                    @Override
                    protected void afterExecution(ActivityDTO result, boolean operationFailed) {
                        if (!operationFailed) {
                        }
                    }
                };
            }
        } else {
            createCallback = new BasicAsyncCallback<Long>() {

                @Override
                protected void afterExecution(Long result, boolean operationFailed) {
                    if (!operationFailed) {
                        end.setId(result);
                    }
                }
            };
        }

        try {
            Date time = DateHelper.parseString(newDate, DateTimeFormat.getFormat("HH:mm"));
            Date dayOfActivity = activityToSave.getDay();
            if (endAtNextDay) {
                if (DateHelper.isSameDay(start.getDay(), dayOfActivity)) {
                    DateHelper.addDays(dayOfActivity, 1);
                }
            } else if (!endAtNextDay) {
                if (!DateHelper.isSameDay(start.getDay(), dayOfActivity)) {
                    DateHelper.addDays(dayOfActivity, -1);
                }
            }
            time = DateHelper.createDateObject(dayOfActivity, time);
            activityToSave.setDay(time);
            if (!createNewActivity) {
                ProjectTrackerEntryPoint.getProjectService(true).saveActivity(activityToSave, callback);
            } else {
                ProjectTrackerEntryPoint.getProjectService(true).createActivity(activityToSave, createCallback);
            }
            fireTimeChanged();
        } catch (IllegalArgumentException e) {
            ((TextBox) eventSource).setText(DateHelper.formatTime(activityToSave.getDay()));
        }
    }

    @Override
    public void onClick(final ClickEvent event) {
        final Object eventSource = event.getSource();
        BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

            @Override
            protected void afterExecution(Boolean result, boolean operationFailed) {
                if (!operationFailed) {
                    if (!result || ProjectTrackerEntryPoint.getInstance().isAdmin()) {
                        deleteTimeNotice(eventSource);
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(start.getDay(), start.getStaff(), callback);
    }

    private void deleteTimeNotice(Object eventSource) {
        if (eventSource == close) {
            BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                @Override
                protected void afterExecution(Void result, boolean operationFailed) {
                    if (!operationFailed) {
                        fireActivityDelete();
                    } else {
                    }
                }
            };

            BasicAsyncCallback<Void> endCallback = new BasicAsyncCallback<Void>() {

                @Override
                protected void afterExecution(Void result, boolean operationFailed) {
                    if (!operationFailed) {
                    }
                }
            };

            if (end != null) {
                ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(end, endCallback);
            }
            ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(start, callback);
        }
    }

    public void addListener(TaskDeleteListener l) {
        listener.add(l);
    }

    public void removeListener(TaskDeleteListener l) {
        listener.remove(l);
    }

    public void addTimeNoticeListener(TimeNoticeListener l) {
        timeListener.add(l);
    }

    public void removeTimeNoticeListener(TimeNoticeListener l) {
        timeListener.remove(l);
    }

    public double getHours() {
        if (end != null) {
            return DateHelper.substract(start.getDay(), end.getDay());
        } else {
            return 0.0;
        }
    }

    private void fireActivityDelete() {
        for (TaskDeleteListener l : listener) {
            l.taskDelete(this);
        }
    }

    private void fireTimeChanged() {
        for (TimeNoticeListener l : timeListener) {
            l.timeChanged(this);
        }
    }

    public Date getStart() {
        return start.getDay();
    }

    public Date getEnd() {
        return end.getDay();
    }
}
