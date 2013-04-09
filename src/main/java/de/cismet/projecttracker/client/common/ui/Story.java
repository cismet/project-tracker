/**
 * *************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 * 
* ... and it just works.
 * 
***************************************************
 */
package de.cismet.projecttracker.client.common.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeNoticeListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.types.TimePeriod;

/**
 * DOCUMENT ME!
 *
 * @version $Revision$, $Date$
 */
public class Story extends Composite implements ClickHandler, TaskDeleteListener, TimeNoticeListener {

    //~ Static fields/initializers ---------------------------------------------
    private static StoryUiBinder uiBinder = GWT.create(StoryUiBinder.class);
    private static final String DOW_TOOLTIP_SUFFX = "\n(click to add a new time slot)";
    //~ Instance fields --------------------------------------------------------
    @UiField
    FlowPanel monday;
    @UiField
    FlowPanel tuesday;
    @UiField
    FlowPanel wednesday;
    @UiField
    FlowPanel thursday;
    @UiField
    FlowPanel friday;
    @UiField
    FlowPanel saturday;
    @UiField
    FlowPanel sunday;
    @UiField
    AbsolutePanel boundaryPanel;
    @UiField
    Label mondayLab;
    @UiField
    Label tuesdayLab;
    @UiField
    Label wednesdayLab;
    @UiField
    Label thursdayLab;
    @UiField
    Label fridayLab;
    @UiField
    Label saturdayLab;
    @UiField
    Label sundayLab;
//    @UiField
    Button addTask = new Button();
    private FlowPanel[] daysOfWeek = new FlowPanel[7];
    private HashMap<FlowPanel, List<TimeNotice>> taskMap = new HashMap<FlowPanel, List<TimeNotice>>();
    private Date firstDayOfWeek = new Date();
    private List<TimeStoryListener> listener = new ArrayList<TimeStoryListener>();

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new Story object.
     */
    public Story() {
        initWidget(uiBinder.createAndBindUi(this));

        daysOfWeek[0] = sunday;
        daysOfWeek[1] = monday;
        daysOfWeek[2] = tuesday;
        daysOfWeek[3] = wednesday;
        daysOfWeek[4] = thursday;
        daysOfWeek[5] = friday;
        daysOfWeek[6] = saturday;

        mondayLab.addClickHandler(this);
        tuesdayLab.addClickHandler(this);
        wednesdayLab.addClickHandler(this);
        thursdayLab.addClickHandler(this);
        fridayLab.addClickHandler(this);
        saturdayLab.addClickHandler(this);
        sundayLab.addClickHandler(this);

        mondayLab.setStyleName("TimeHeader");
        tuesdayLab.setStyleName("TimeHeader");
        wednesdayLab.setStyleName("TimeHeader");
        thursdayLab.setStyleName("TimeHeader");
        fridayLab.setStyleName("TimeHeader");
        saturdayLab.setStyleName("TimeHeader");
        sundayLab.setStyleName("TimeHeader");

        mondayLab.setText("Monday");
        tuesdayLab.setText("Tuesday");
        wednesdayLab.setText("Wednesday");
        thursdayLab.setText("Thursday");
        fridayLab.setText("Friday");
        saturdayLab.setText("Saturday");
        sundayLab.setText("Sunday");

        for (final FlowPanel columnPanel : daysOfWeek) {
            taskMap.put(columnPanel, new ArrayList<TimeNotice>());
        }
    }

    //~ Methods ----------------------------------------------------------------
    @Override
    public void taskDelete(final Object source) {
        if (source instanceof TimeNotice) {
            final TimeNotice time = (TimeNotice) source;

            for (final FlowPanel tmpPanel : taskMap.keySet()) {
                final List<TimeNotice> tmpList = taskMap.get(tmpPanel);
                if (tmpList.contains(time)) {
                    tmpList.remove(time);
                    tmpPanel.remove(time);
                    break;
                }
            }

            final TimeStoryEvent e = new TimeStoryEvent(this, false, time.getStart());
            for (final TimeStoryListener tmp : listener) {
                tmp.timeNoticeDeleted(e);
            }
        }

        ProjectTrackerEntryPoint.getInstance().validateSize();
    }

    @Override
    public void timeChanged(final Object source) {
        final TimeStoryEvent e = new TimeStoryEvent(this, false, ((TimeNotice) source).getStart());
        for (final TimeStoryListener tmp : listener) {
            tmp.timeNoticeChanged(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param start DOCUMENT ME!
     * @param end DOCUMENT ME!
     * @param columnPanel DOCUMENT ME!
     */
    public void addTask(final ActivityDTO start, final ActivityDTO end, final FlowPanel columnPanel) {
        final TimeNotice widget = new TimeNotice(start, end);
        widget.addListener(this);
        widget.addTimeNoticeListener(this);
        widget.addStyleName("alert alert-block info timebox");
        columnPanel.add(widget);
        taskMap.get(columnPanel).add(widget);
        ProjectTrackerEntryPoint.getInstance().validateSize();
    }

    /**
     * DOCUMENT ME!
     *
     * @param firstDayOfWeek DOCUMENT ME!
     * @param activities DOCUMENT ME!
     */
    public void setTimes(final Date firstDayOfWeek, final List<ActivityDTO> activities) {
        this.firstDayOfWeek = firstDayOfWeek;
        setDates();
        removeAllTasks();
        Collections.sort(activities);
        Date day = null;
        ActivityDTO begin = null;
        ActivityDTO end = null;

        for (final ActivityDTO tmp : activities) {
            if (day == null) {
                day = tmp.getDay();
            } else if (!DateHelper.isSameDaySV(day, tmp.getDay())) {
                if (begin != null) {
                    addTask(day.getDay(), begin, end);
                }
                day = tmp.getDay();
                begin = null;
                end = null;
            }

            if (tmp.getKindofactivity() == ActivityDTO.BEGIN_OF_DAY) {
                if ((begin != null) && (end != null)) {
                    addTask(day.getDay(), begin, end);
                    end = null;
                }
                begin = tmp;
            } else if (tmp.getKindofactivity() == ActivityDTO.END_OF_DAY) {
                end = tmp;
            }
        }
//        if (begin != null && end != null) {
        if (begin != null) {
            addTask(day.getDay(), begin, end);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void setDates() {
        mondayLab.setTitle("\t" + DateHelper.formatDate(firstDayOfWeek) + DOW_TOOLTIP_SUFFX);
        final Date tmpDay = (Date) firstDayOfWeek.clone();
        DateHelper.addDays(tmpDay, 1);
        tuesdayLab.setTitle("\t" + DateHelper.formatDate(tmpDay) + DOW_TOOLTIP_SUFFX);
        DateHelper.addDays(tmpDay, 1);
        wednesdayLab.setTitle("\t" + DateHelper.formatDate(tmpDay) + DOW_TOOLTIP_SUFFX);
        DateHelper.addDays(tmpDay, 1);
        thursdayLab.setTitle("\t" + DateHelper.formatDate(tmpDay) + DOW_TOOLTIP_SUFFX);
        DateHelper.addDays(tmpDay, 1);
        fridayLab.setTitle("\t" + DateHelper.formatDate(tmpDay) + DOW_TOOLTIP_SUFFX);
        DateHelper.addDays(tmpDay, 1);
        saturdayLab.setTitle("\t" + DateHelper.formatDate(tmpDay) + DOW_TOOLTIP_SUFFX);
        DateHelper.addDays(tmpDay, 1);
        sundayLab.setTitle("\t" + DateHelper.formatDate(tmpDay) + DOW_TOOLTIP_SUFFX);
    }

    /**
     * DOCUMENT ME!
     *
     * @param event DOCUMENT ME!
     *
     * @UiHandler ("addTask")
     */
    void onAddTaskClick(final ClickEvent event) {
//        DialogBox form = new DialogBox();
//        form.setWidget(new TaskForm(form, this));
//        form.center();
    }

    /**
     * DOCUMENT ME!
     */
    private void removeAllTasks() {
        for (final FlowPanel tmp : daysOfWeek) {
            final List<TimeNotice> list = taskMap.get(tmp);
            for (final TimeNotice widget : list) {
                tmp.remove(widget);
            }
            list.clear();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param day DOCUMENT ME!
     * @param begin DOCUMENT ME!
     * @param end DOCUMENT ME!
     */
    public void addTask(final int day, final ActivityDTO begin, final ActivityDTO end) {
        this.addTask(begin, end, daysOfWeek[day]);
    }

    @Override
    public void onClick(final ClickEvent event) {
        if (event.getSource() == mondayLab) {
            addTime(calcDateForDay(0));
        } else if (event.getSource() == tuesdayLab) {
            addTime(calcDateForDay(1));
        } else if (event.getSource() == wednesdayLab) {
            addTime(calcDateForDay(2));
        } else if (event.getSource() == thursdayLab) {
            addTime(calcDateForDay(3));
        } else if (event.getSource() == fridayLab) {
            addTime(calcDateForDay(4));
        } else if (event.getSource() == saturdayLab) {
            addTime(calcDateForDay(5));
        } else if (event.getSource() == sundayLab) {
            addTime(calcDateForDay(6));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param day DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public double getTimeForDay(final int day) {
        final FlowPanel test = daysOfWeek[day];
        final List<TimeNotice> timeList = taskMap.get(test);
        double sum = 0.0;

        for (final TimeNotice tmp : timeList) {
            sum += tmp.getHours();
        }

        return sum;
    }

    /**
     * DOCUMENT ME!
     *
     * @param day DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List<TimeNotice> getTimeNoticesForDay(final int day) {
        return taskMap.get(daysOfWeek[day]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param day DOCUMENT ME!
     */
    private void addTime(final Date day) {
        final BasicAsyncCallback<Boolean> callback = new BasicAsyncCallback<Boolean>() {
            @Override
            protected void afterExecution(final Boolean result, final boolean operationFailed) {
                if (!operationFailed) {
                    if (!result) {
                        final List<TimeNotice> timeList = Story.this.taskMap.get(
                                Story.this.daysOfWeek[day.getDay()]);
                        if (timeList.isEmpty()) {
                            final BasicAsyncCallback<TimePeriod> callback = new BasicAsyncCallback<TimePeriod>() {
                                @Override
                                protected void afterExecution(final TimePeriod result,
                                        final boolean operationFailed) {
                                    if (!operationFailed) {
                                        Date start = result.getStart();
                                        Date end = result.getEnd();

                                        if (start == null) {
                                            // todo: sinvolleren Defaultwert nutzen
                                            start = new Date();//new Date(1900, 1, 1, 10, 0, 0);
                                        }

//                                        if (end == null) {
//                                            end = new Date();
//                                        }

                                        addTimeStartEnd(day, start, end);
                                    }
                                }
                            };

                            ProjectTrackerEntryPoint.getProjectService(true)
                                    .getStartEndOfWork(ProjectTrackerEntryPoint.getInstance().getStaff(),
                                    day,
                                    callback);
                        } else {
                            Date max = null;

                            for (final TimeNotice tmp : timeList) {
                                if ((max == null) || ((tmp.getEnd() != null) && tmp.getEnd().after(max))) {
                                    max = tmp.getEnd();
                                }
                            }

                            max = new Date(max.getTime() + 60000);
                            timeList.get(timeList.size() - 1).getStart();
                            addTimeStartEnd(day, max, null);
                        }
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true)
                .isDayLocked(day, ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param day DOCUMENT ME!
     * @param startTime DOCUMENT ME!
     * @param endTime DOCUMENT ME!
     */
    private void addTimeStartEnd(final Date day, final Date startTime, final Date endTime) {
        final ActivityDTO start = new ActivityDTO();
        final ActivityDTO end = (endTime == null ? null : new ActivityDTO());

        if (startTime.getHours() >= 4) {
            start.setDay(DateHelper.createDateObject(day, startTime));
            if (end != null) {
                end.setDay(DateHelper.createDateObject(day, endTime));
            }
        } else {
            start.setDay(DateHelper.createDateObject(day, new Date(2010, 01, 01, 04, 01, 00)));
            if (end != null) {
                end.setDay(DateHelper.createDateObject(day, endTime));
            
                if (end.getDay().before(start.getDay())) {
                    end.setDay(DateHelper.createDateObject(day, start.getDay()));
                    end.setDay(new Date(end.getDay().getTime() + 60000));
                }
            }
        }

        start.setKindofactivity(ActivityDTO.BEGIN_OF_DAY);
        start.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());

        final BasicAsyncCallback<Long> startCallback = new BasicAsyncCallback<Long>() {
            @Override
            protected void afterExecution(final Long result, final boolean operationFailed) {
                if (!operationFailed) {
                    start.setId(result);
                    
                    if (end == null) {
                        addTask(day.getDay(), start, end);
                        final TimeStoryEvent e = new TimeStoryEvent(Story.this, true, day);
                        fireTimeNoticeCreated(e);
                    }
                }
            }
        };

        final BasicAsyncCallback<Long> endCallback = new BasicAsyncCallback<Long>() {
            @Override
            protected void afterExecution(final Long result, final boolean operationFailed) {
                if (!operationFailed) {
                    end.setId(result);
                    addTask(day.getDay(), start, end);
                    final TimeStoryEvent e = new TimeStoryEvent(Story.this, true, day);
                    fireTimeNoticeCreated(e);
                }
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).createActivity(start, startCallback);

        if (end != null) {
            end.setKindofactivity(ActivityDTO.END_OF_DAY);
            end.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
            ProjectTrackerEntryPoint.getProjectService(true).createActivity(end, endCallback);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param day DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Date calcDateForDay(final int day) {
        final Date newDate = (Date) firstDayOfWeek.clone();
        DateHelper.addDays(newDate, day);

        return newDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    private void fireTimeNoticeCreated(final TimeStoryEvent e) {
        for (final TimeStoryListener l : listener) {
            l.timeNoticeCreated(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void addTimeStoryListener(final TimeStoryListener l) {
        listener.add(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param l DOCUMENT ME!
     */
    public void removeTimeStoryListener(final TimeStoryListener l) {
        listener.remove(l);
    }

    //~ Inner Interfaces -------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @version $Revision$, $Date$
     */
    interface StoryUiBinder extends UiBinder<Widget, Story> {
    }
}