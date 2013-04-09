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
package de.cismet.projecttracker.client.common.ui.report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;

import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class StatisticsPanel extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static StatisticsPanelUiBinder uiBinder = GWT.create(StatisticsPanelUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    SpanElement totalHours;
    @UiField
    SpanElement staffCount;
    @UiField
    SpanElement activityCount;
    @UiField
    SpanElement firstDate;
    @UiField
    SpanElement lastDate;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StatisticsPanel object.
     *
     * @param  totalWh        DOCUMENT ME!
     * @param  staffCount     DOCUMENT ME!
     * @param  activityCount  DOCUMENT ME!
     * @param  firstDate      DOCUMENT ME!
     * @param  lastDate       DOCUMENT ME!
     */
    public StatisticsPanel(final double totalWh,
            final int staffCount,
            final int activityCount,
            final Date firstDate,
            final Date lastDate) {
        initWidget(uiBinder.createAndBindUi(this));
        this.totalHours.setInnerText(DateHelper.doubleToHours(totalWh));
        this.staffCount.setInnerText("" + staffCount);
        this.activityCount.setInnerText("" + activityCount);
        this.firstDate.setInnerText(DateHelper.formatDate(firstDate));
        this.lastDate.setInnerText(DateHelper.formatDate(lastDate));
    }

    //~ Inner Interfaces -------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    interface StatisticsPanelUiBinder extends UiBinder<Widget, StatisticsPanel> {
    }
}
