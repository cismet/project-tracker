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

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeNoticeListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.listener.BasicRollbackCallback;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TimeNotice extends Composite implements ChangeHandler, ClickHandler {

    //~ Instance fields --------------------------------------------------------

    private final FlowPanel mainPanel = new FlowPanel();
    private final ActivityDTO start;
    private ActivityDTO end;
    private final ControlGroup startCtrlGroup = new ControlGroup();
    private final ControlGroup endCtrlGroup = new ControlGroup();
    private final TextBox startTime = new TextBox();
    private final TextBox endTime = new TextBox();
    private final Label close = new Label("x");
    private final List<TaskDeleteListener> listener = new ArrayList<TaskDeleteListener>();
    private final List<TimeNoticeListener> timeListener = new ArrayList<TimeNoticeListener>();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TimeNotice object.
     *
     * @param  start  DOCUMENT ME!
     * @param  end    DOCUMENT ME!
     */
    public TimeNotice(final ActivityDTO start, final ActivityDTO end) {
        this.start = start;
        this.end = end;
        init();
        initWidget(mainPanel);
        mainPanel.setStyleName("time-notice");
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        startTime.setMaxLength(5);
        endTime.setMaxLength(5);
        startTime.setStyleName("inlineComponent time-input");
        endTime.setStyleName("inlineComponent time-input");
        final KeyUpHandler timeValidator = new KeyUpHandler() {

                @Override
                public void onKeyUp(final KeyUpEvent event) {
                    final String s;
                    final ControlGroup ctrlGroup;
                    if (event.getSource() == startTime) {
                        s = startTime.getText();
                        ctrlGroup = startCtrlGroup;
                    } else {
                        s = endTime.getText();
                        ctrlGroup = endCtrlGroup;
                    }

                    if ((s != null) && !s.isEmpty() && !isTimeInputValid(s)) {
                        ctrlGroup.setType(ControlGroupType.ERROR);
                    } else {
                        ctrlGroup.setType(ControlGroupType.NONE);
                    }
                }
            };
        startTime.addKeyUpHandler(timeValidator);
        endTime.addKeyUpHandler(timeValidator);
        startCtrlGroup.addStyleName("time-notice-control");
        endCtrlGroup.addStyleName("time-notice-control");
        startCtrlGroup.add(startTime);
        endCtrlGroup.add(endTime);
        close.setStyleName("close pull-right closeButton");
        close.addClickHandler(this);
        mainPanel.add(close);
        mainPanel.add(startCtrlGroup);
        mainPanel.add(endCtrlGroup);
//        mainPanel.add(startTime);
//        mainPanel.add(endTime);
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
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            changeTimeNotice(eventSource);
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(start.getDay(), start.getStaff(), callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  eventSource  DOCUMENT ME!
     */
    private void changeTimeNotice(final Object eventSource) {
        ActivityDTO activityToSave = null;
        String newDate = null;
        boolean endAtNextDay = false;
        boolean createNewActivity = false;

        if (eventSource == startTime) {
            activityToSave = start;
            if (!isTimeInputValid(startTime.getText())) {
                if (start != null) {
                    startTime.setText(DateHelper.formatTime(start.getDay()));
                } else {
                    startTime.setText("");
                }
                startCtrlGroup.setType(ControlGroupType.NONE);
                return;
            }
            newDate = parseTimeInput(startTime.getText());
            startTime.setText(newDate);
            // check if the time lies in the interval 24:00 - 4 and set the date correctly
            if (!startTime.getText().equals("")) {
                try {
                    final Date startDate = DateHelper.parseString(startTime.getText(),
                            DateTimeFormat.getFormat("HH:mm"));
                    if (startDate.getHours() < 4) {
                        endAtNextDay = true;
                    }
                } catch (IllegalArgumentException e) {
                    ((TextBox)eventSource).setText(DateHelper.formatTime(activityToSave.getDay()));
                    return;
                }
            }
        } else if (eventSource == endTime) {
            activityToSave = end;
            if (!endTime.getText().isEmpty() && !isTimeInputValid(endTime.getText())) {
                if (end != null) {
                    endTime.setText(DateHelper.formatTime(end.getDay()));
                } else {
                    endTime.setText("");
                }
                endCtrlGroup.setType(ControlGroupType.NONE);
                return;
            }
            newDate = parseTimeInput(endTime.getText());
            endTime.setText(newDate);

            if (!endTime.getText().equals("")) {
                try {
                    final Date endDate = DateHelper.parseString(newDate, DateTimeFormat.getFormat("HH:mm"));
                    final Date startDate = DateHelper.parseString(startTime.getText(),
                            DateTimeFormat.getFormat("HH:mm"));

                    if (endDate.before(startDate)) {
                        if (endDate.getHours() > 3) {
                            ProjectTrackerEntryPoint.outputBox(
                                "The end time must not be after 3:59 a.m. at the next day.");
                            ((TextBox)eventSource).setText(DateHelper.formatTime(activityToSave.getDay()));

                            return;
                        }
                        endAtNextDay = true;
                    }

                    if (activityToSave == null) {
                        end = new ActivityDTO();
                        end.setKindofactivity(ActivityDTO.END_OF_DAY);
                        end.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
                        end.setDay((Date)start.getDay().clone());
                        activityToSave = end;
                        createNewActivity = true;
                    }
                } catch (IllegalArgumentException e) {
                    ((TextBox)eventSource).setText(DateHelper.formatTime(activityToSave.getDay()));
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
                    final BasicAsyncCallback<Void> deleteCallback = new BasicAsyncCallback<Void>() {

                            @Override
                            protected void afterExecution(final Void result, final boolean operationFailed) {
                                if (!operationFailed) {
                                    end = null;
                                    fireTimeChanged();
                                } else {
                                    endTime.setText(DateHelper.formatTime(end.getDay()));
                                }
                            }
                        };

                    ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(end, deleteCallback);
                    return;
                }
            } else {
                callback = new BasicRollbackCallback<ActivityDTO>(activityToSave) {

                        @Override
                        protected void afterExecution(final ActivityDTO result, final boolean operationFailed) {
                            if (operationFailed) {
                                if (eventSource == endTime) {
                                    if (end != null) {
                                        endTime.setText(DateHelper.formatTime(end.getDay()));
                                    } else {
                                        endTime.setText("");
                                    }
                                } else {
                                    if (start != null) {
                                        startTime.setText(DateHelper.formatTime(start.getDay()));
                                    } else {
                                        startTime.setText("");
                                    }
                                }
                            }
                        }
                    };
            }
        } else {
            createCallback = new BasicAsyncCallback<Long>() {

                    @Override
                    protected void afterExecution(final Long result, final boolean operationFailed) {
                        if (!operationFailed) {
                            end.setId(result);
                        } else {
                            endTime.setText("");
                        }
                    }
                };
        }

        try {
            Date time = DateHelper.parseString(newDate, DateTimeFormat.getFormat("HH:mm"));
            final Date dayOfActivity = activityToSave.getDay();
            if (endAtNextDay) {
                if (DateHelper.isSameDay(start.getDay(), dayOfActivity)) {
                    DateHelper.addDays(dayOfActivity, 1);
                }
            } else if (!endAtNextDay) {
                if (eventSource == startTime) {
                    if ((dayOfActivity.getHours() < 4) && (time.getHours() >= 4)) {
                        DateHelper.addDays(dayOfActivity, -1);
                    }
                }
                if (eventSource == endTime) {
                    if (!DateHelper.isSameDay(start.getDay(), dayOfActivity)) {
                        DateHelper.addDays(dayOfActivity, -1);
                    }
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
            ((TextBox)eventSource).setText(DateHelper.formatTime(activityToSave.getDay()));
        }
    }

    /**
     * checks if s matches a time expression, if not checks if the insertet text can be interpreted as hours and returns
     * a time expression representing the.
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String parseTimeInput(String s) {
        if (!s.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
            if (s.matches("[01][0-9]|2[0-3]")) {
                s += ":00";
            } else if (s.matches("[0-9]|2[0-3]")) {
                s = "0" + s + ":00";
            }
        }
        return s;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   s  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private boolean isTimeInputValid(final String s) {
        // check if the inserted text is a full time expression
        boolean isValid = s.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]");

        // check if the inserted text can be interpreted as as hours
        if (!isValid) {
            isValid = s.matches("[01][0-9]|2[0-3]") || s.matches("[0-9]|2[0-3]");
        }
        return isValid;
    }

    @Override
    public void onClick(final ClickEvent event) {
        final Object eventSource = event.getSource();
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {

                @Override
                protected void afterExecution(final Boolean result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result) {
                            deleteTimeNotice(eventSource);
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true).isDayLocked(start.getDay(), start.getStaff(), callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  eventSource  DOCUMENT ME!
     */
    private void deleteTimeNotice(final Object eventSource) {
        if (eventSource == close) {
            final BasicAsyncCallback<Void> callback = new BasicAsyncCallback<Void>() {

                    @Override
                    protected void afterExecution(final Void result, final boolean operationFailed) {
                        if (!operationFailed) {
                            fireActivityDelete();
                        } else {
                        }
                    }
                };

            final BasicAsyncCallback<Void> endCallback = new BasicAsyncCallback<Void>() {

                    @Override
                    protected void afterExecution(final Void result, final boolean operationFailed) {
                        if (!operationFailed) {
                            ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(start, callback);
                        }
                    }
                };

            if (end != null) {
                ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(end, endCallback);
            } else {
                ProjectTrackerEntryPoint.getProjectService(true).deleteActivity(start, callback);
            }
        }
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
     *
     * @param  l  DOCUMENT ME!
     */
    public void addTimeNoticeListener(final TimeNoticeListener l) {
        timeListener.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  l  DOCUMENT ME!
     */
    public void removeTimeNoticeListener(final TimeNoticeListener l) {
        timeListener.remove(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public double getHours() {
        if (end != null) {
            return DateHelper.substract(start.getDay(), end.getDay());
        } else {
            return 0.0;
        }
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
     */
    private void fireTimeChanged() {
        for (final TimeNoticeListener l : timeListener) {
            l.timeChanged(this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getStart() {
        return start.getDay();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getEnd() {
        if (end == null) {
            return null;
        }
        return end.getDay();
    }

    /**
     * DOCUMENT ME!
     *
     * @param  aFlag  DOCUMENT ME!
     */
    public void setEnabled(final boolean aFlag) {
        close.setVisible(aFlag);
        startTime.setEnabled(aFlag);
        endTime.setEnabled(aFlag);
    }
}
