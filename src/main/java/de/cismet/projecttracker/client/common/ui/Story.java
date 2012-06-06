package de.cismet.projecttracker.client.common.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.listener.TaskDeleteListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeNoticeListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.types.TimePeriod;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class Story extends Composite implements ClickHandler, TaskDeleteListener, TimeNoticeListener {

    private FlowPanel[] daysOfWeek = new FlowPanel[7]; 
    private static StoryUiBinder uiBinder = GWT.create(StoryUiBinder.class);
    private HashMap<FlowPanel, List<TimeNotice>> taskMap = new HashMap<FlowPanel, List<TimeNotice>>();
    private Date firstDayOfWeek = new Date();
    private List<TimeStoryListener> listener = new ArrayList<TimeStoryListener>();

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

    @Override
    public void taskDelete(Object source) {
         if (source instanceof TimeNotice) {
            TimeNotice time = (TimeNotice)source;
            
            for (FlowPanel tmpPanel : taskMap.keySet()) {
                List<TimeNotice> tmpList = taskMap.get(tmpPanel);
                if (tmpList.contains(time)) {
                    tmpList.remove(time);
                    tmpPanel.remove(time);
                    break;
                }
            }

            TimeStoryEvent e = new TimeStoryEvent(this, false, time.getStart());
            for (TimeStoryListener tmp : listener) {
                tmp.timeNoticeDeleted(e);
            }
        }
         
         ProjectTrackerEntryPoint.getInstance().validateSize();
    }

    @Override
    public void timeChanged(Object source) {
        TimeStoryEvent e = new TimeStoryEvent(this, false, ((TimeNotice)source).getStart());
        for (TimeStoryListener tmp : listener) {
            tmp.timeNoticeChanged(e);
        }
    }




    interface StoryUiBinder extends UiBinder<Widget, Story> {
    }

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
        
        for (FlowPanel columnPanel : daysOfWeek) {
            taskMap.put(columnPanel, new ArrayList<TimeNotice>());
        }
    }

    public void addTask(ActivityDTO start, ActivityDTO end, FlowPanel columnPanel) {
        TimeNotice widget = new TimeNotice(start, end);
        widget.addListener(this);
        widget.addTimeNoticeListener(this);
        widget.addStyleName("alert-message block-message info timebox");
        columnPanel.add(widget);
        taskMap.get(columnPanel).add(widget);
        ProjectTrackerEntryPoint.getInstance().validateSize();
    }
    
    public void setTimes(Date firstDayOfWeek, List<ActivityDTO> activities) {
        this.firstDayOfWeek = firstDayOfWeek;
        setDates();
        removeAllTasks();
        Collections.sort(activities);
        Date day = null;
        ActivityDTO begin = null;
        ActivityDTO end = null;
        
        for (ActivityDTO tmp : activities) {
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
            
            if (tmp.getKindofactivity() == ActivityDTO.BEGIN_OF_DAY ) {
                if (begin != null && end != null) {
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
    
    private void setDates() {
        mondayLab.setTitle(DateHelper.formatDate(firstDayOfWeek));
        Date tmpDay = (Date)firstDayOfWeek.clone();
        DateHelper.addDays( tmpDay, 1);
        tuesdayLab.setTitle(DateHelper.formatDate(tmpDay));
        DateHelper.addDays( tmpDay, 1);
        wednesdayLab.setTitle(DateHelper.formatDate(tmpDay));
        DateHelper.addDays( tmpDay, 1);
        thursdayLab.setTitle(DateHelper.formatDate(tmpDay));
        DateHelper.addDays( tmpDay, 1);
        fridayLab.setTitle(DateHelper.formatDate(tmpDay));
        DateHelper.addDays( tmpDay, 1);
        saturdayLab.setTitle(DateHelper.formatDate(tmpDay));
        DateHelper.addDays( tmpDay, 1);
        sundayLab.setTitle(DateHelper.formatDate(tmpDay));
    }
    
//    @UiHandler("addTask")
    void onAddTaskClick(ClickEvent event) {
//        DialogBox form = new DialogBox();
//        form.setWidget(new TaskForm(form, this));
//        form.center();
    }

    private void removeAllTasks() {
        for (FlowPanel tmp : daysOfWeek) {
            List<TimeNotice> list = taskMap.get(tmp);
            for (TimeNotice widget : list) {
                tmp.remove(widget);
            }
            list.clear();
        }
    }
    
    public void addTask(int day, ActivityDTO begin, ActivityDTO end) {
        this.addTask(begin, end, daysOfWeek[day]);
    }

    @Override
    public void onClick(ClickEvent event) {
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

    public double getTimeForDay(int day) {
        FlowPanel test = daysOfWeek[day];
        List<TimeNotice> timeList = taskMap.get( test );
        double sum = 0.0;
        
        for (TimeNotice tmp : timeList) {
            sum += tmp.getHours();
        }
        
        return sum;
    }
    
    private void addTime(final Date day) {
        List<TimeNotice> timeList = this.taskMap.get(this.daysOfWeek[day.getDay()]);
        if (timeList.isEmpty()) {
            BasicAsyncCallback<TimePeriod> callback = new BasicAsyncCallback<TimePeriod>() {

                @Override
                protected void afterExecution(TimePeriod result, boolean operationFailed) {
                    if (!operationFailed) {
                        Date start = result.getStart();
                        Date end = result.getEnd();
                        
                        if (start == null) {
                            //todo: sinvolleren Defaultwert nutzen
                            start = new Date(1900, 1, 1, 10, 0, 0);
                        }
                        
                        if (end == null) {
                            end = new Date();
                            
                            if (end.before(start)) {
                                end = new Date( ((Date)start.clone()).getTime() + 60000 );
                            }
                        }
                        
                        addTimeStartEnd(day, start, end);
                    }
                }
            };
            
            ProjectTrackerEntryPoint.getProjectService(true).getStartEndOfWork(ProjectTrackerEntryPoint.getInstance().getStaff(),  day, callback);
        } else {
            Date max = null;
            
            for (TimeNotice tmp : timeList) {
                if ( max == null || (tmp.getEnd() != null && tmp.getEnd().after(max)) ) {
                    max = tmp.getEnd();
                }
            }
            
            max = new Date(max.getTime() + 60000);
            timeList.get(timeList.size() - 1).getStart();
            addTimeStartEnd(day, max, new Date());
        }
    }
    
    private void addTimeStartEnd(final Date day, final Date startTime, final Date endTime) {
        final ActivityDTO start = new ActivityDTO();
        final ActivityDTO end = new ActivityDTO();
        
        if (startTime.getHours() >= 4) {
            start.setDay(DateHelper.createDateObject(day, startTime));
            end.setDay(DateHelper.createDateObject(day, endTime));
        } else {
            start.setDay(DateHelper.createDateObject(day, new Date(2010,01,01,04,01,00)));
            end.setDay(DateHelper.createDateObject(day, endTime));
            
            if (end.getDay().before(start.getDay())) {
                end.setDay(DateHelper.createDateObject(day, start.getDay()));
                end.setDay( new Date(end.getDay().getTime() + 60000) );
            }
        }
        
        
        start.setKindofactivity(ActivityDTO.BEGIN_OF_DAY);
        end.setKindofactivity(ActivityDTO.END_OF_DAY);
        
        start.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
        end.setStaff(ProjectTrackerEntryPoint.getInstance().getStaff());
        
        BasicAsyncCallback<Long> startCallback = new BasicAsyncCallback<Long>() {

            @Override
            protected void afterExecution(Long result, boolean operationFailed) {
                if (!operationFailed) {
                    start.setId(result);
                }
            }

        };
        
        BasicAsyncCallback<Long> endCallback = new BasicAsyncCallback<Long>() {

            @Override
            protected void afterExecution(Long result, boolean operationFailed) {
                if (!operationFailed) {
                    end.setId(result);
                    addTask(day.getDay(), start, end);
                    TimeStoryEvent e = new TimeStoryEvent(Story.this, true, day);
                    fireTimeNoticeCreated(e);
                }
            }

        };
        
        ProjectTrackerEntryPoint.getProjectService(true).createActivity(start, startCallback);        
        ProjectTrackerEntryPoint.getProjectService(true).createActivity(end, endCallback);
    }
    
    private Date calcDateForDay(int day) {
        Date newDate = (Date)firstDayOfWeek.clone();
        DateHelper.addDays(newDate, day);
        
        return newDate;
    }    
    
    private void fireTimeNoticeCreated(TimeStoryEvent e) {
        for (TimeStoryListener l : listener) {
            l.timeNoticeCreated(e);
        }
    }
    
    public void addTimeStoryListener(TimeStoryListener l) {
        listener.add(l);
    }
    
    public void removeTimeStoryListener(TimeStoryListener l) {
        listener.remove(l);
    }
}
