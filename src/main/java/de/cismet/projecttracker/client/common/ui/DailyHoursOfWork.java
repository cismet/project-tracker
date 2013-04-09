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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import java.util.Date;
import java.util.List;

import de.cismet.projecttracker.client.common.ui.event.TaskStoryEvent;
import de.cismet.projecttracker.client.common.ui.event.TimeStoryEvent;
import de.cismet.projecttracker.client.common.ui.listener.TaskStoryListener;
import de.cismet.projecttracker.client.common.ui.listener.TimeStoryListener;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DailyHoursOfWork extends Composite implements TimeStoryListener, TaskStoryListener {

    //~ Static fields/initializers ---------------------------------------------

    private static DailyHoursOfWorkUiBinder uiBinder = GWT.create(DailyHoursOfWorkUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    HTML monday;
    @UiField
    HTML tuesday;
    @UiField
    HTML wednesday;
    @UiField
    HTML thursday;
    @UiField
    HTML friday;
    @UiField
    HTML saturday;
    @UiField
    HTML sunday;
    @UiField
    AbsolutePanel boundaryPanel;
    private Date firstDayOfWeek = new Date();
    private Story story;
    private TaskStory taskStory;
    private HTML[] days;
    private boolean initialised = false;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DailyHoursOfWork object.
     */
    public DailyHoursOfWork() {
        initWidget(uiBinder.createAndBindUi(this));
        days = new HTML[7];

        days[0] = sunday;
        days[1] = monday;
        days[2] = tuesday;
        days[3] = wednesday;
        days[4] = thursday;
        days[5] = friday;
        days[6] = saturday;
    }

    //~ Methods ----------------------------------------------------------------

    @Override
    public void timeNoticeCreated(final TimeStoryEvent e) {
        fillLabel(e.getDay().getDay());
        ;
    }

    @Override
    public void timeNoticeChanged(final TimeStoryEvent e) {
        fillLabel(e.getDay().getDay());
        ;
    }

    @Override
    public void timeNoticeDeleted(final TimeStoryEvent e) {
        fillLabel(e.getDay().getDay());
        ;
    }

    @Override
    public void taskNoticeCreated(final TaskStoryEvent e) {
        fillLabel(e.getDay().getDay());
        ;
    }

    @Override
    public void taskNoticeChanged(final TaskStoryEvent e) {
        fillLabel(e.getDay().getDay());
        ;
    }

    @Override
    public void taskNoticeDeleted(final TaskStoryEvent e) {
        fillLabel(e.getDay().getDay());
        ;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  firstDayOfWeek  DOCUMENT ME!
     * @param  time            DOCUMENT ME!
     * @param  taskStory       DOCUMENT ME!
     */
    public void initialise(final Date firstDayOfWeek, final Story time, final TaskStory taskStory) {
        this.firstDayOfWeek = firstDayOfWeek;
        this.story = time;
        this.taskStory = taskStory;
        final Date day = (Date)firstDayOfWeek.clone();
        monday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        tuesday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        wednesday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        thursday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        friday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        saturday.setHTML(getText(day.getDay()));
        DateHelper.addDays(day, 1);
        sunday.setHTML(getText(day.getDay()));

        if (!initialised) {
            story.addTimeStoryListener(this);
            taskStory.addTaskStoryListener(this);
            initialised = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  day  DOCUMENT ME!
     */
    private void fillLabel(final int day) {
        days[day].setHTML(getText(day));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   day  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String getText(final int day) {
        double hours = story.getTimeForDay(day);
        double hoursWorked = 0.0;
        final List<TaskNotice> tasks = taskStory.getTasksForDay(day);

        for (final TaskNotice tmp : tasks) {
            if ((tmp.getActivity() != null) && (tmp.getActivity().getWorkPackage() != null)
                        && (tmp.getActivity().getWorkPackage().getProject() != null)) {
                if ((tmp.getActivity().getWorkPackage().getId() == ActivityDTO.PAUSE_ID)
                            || (tmp.getActivity().getWorkPackage().getId() == ActivityDTO.SPARE_TIME_ID)) {
                    hours -= tmp.getActivity().getWorkinghours();
                } else if ((tmp.getActivity().getWorkPackage().getId() != ActivityDTO.HOLIDAY_ID)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.ILLNESS_ID)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.SPECIAL_HOLIDAY_ID)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.LECTURE_ID)
                            && (tmp.getActivity().getWorkPackage().getId() != ActivityDTO.Travel_ID)) {
                    hoursWorked += tmp.getActivity().getWorkinghours();
                }
            }
        }

        return DateHelper.doubleToHours(hours) + " (" + DateHelper.doubleToHours(hours - hoursWorked) + ")";
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface DailyHoursOfWorkUiBinder extends UiBinder<Widget, DailyHoursOfWork> {
    }
}
