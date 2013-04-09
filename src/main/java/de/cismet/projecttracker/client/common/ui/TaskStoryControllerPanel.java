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

import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class TaskStoryControllerPanel extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static TaskStoryControllerUiBinder uiBinder = GWT.create(TaskStoryControllerUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    TaskStoryController monday;

    @UiField
    TaskStoryController tuesday;

    @UiField
    TaskStoryController wednesday;

    @UiField
    TaskStoryController thursday;

    @UiField
    TaskStoryController friday;

    @UiField
    TaskStoryController saturday;

    @UiField
    TaskStoryController sunday;

    @UiField
    AbsolutePanel boundaryPanel;
    private Date firstDayOfWeek = new Date();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new TaskStoryControllerPanel object.
     */
    public TaskStoryControllerPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @param  firstDayOfWeek  DOCUMENT ME!
     * @param  caller          DOCUMENT ME!
     * @param  time            DOCUMENT ME!
     */
    public void initialise(final Date firstDayOfWeek, final TaskStory caller, final Story time) {
        this.firstDayOfWeek = firstDayOfWeek;

        monday.setDay(calcDateForDay(0));
        tuesday.setDay(calcDateForDay(1));
        wednesday.setDay(calcDateForDay(2));
        thursday.setDay(calcDateForDay(3));
        friday.setDay(calcDateForDay(4));
        saturday.setDay(calcDateForDay(5));
        sunday.setDay(calcDateForDay(6));

        monday.setTaskStory(caller);
        tuesday.setTaskStory(caller);
        wednesday.setTaskStory(caller);
        thursday.setTaskStory(caller);
        friday.setTaskStory(caller);
        saturday.setTaskStory(caller);
        sunday.setTaskStory(caller);

        monday.setStory(time);
        tuesday.setStory(time);
        wednesday.setStory(time);
        thursday.setStory(time);
        friday.setStory(time);
        saturday.setStory(time);
        sunday.setStory(time);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   day  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private Date calcDateForDay(final int day) {
        final Date newDate = (Date)firstDayOfWeek.clone();
        DateHelper.addDays(newDate, day);

        return newDate;
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface TaskStoryControllerUiBinder extends UiBinder<Widget, TaskStoryControllerPanel> {
    }
}
