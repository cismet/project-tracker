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
import de.cismet.projecttracker.client.helper.DateHelper;
import java.util.Date;

/**
 *
 * @author daniel
 */
public class StatisticsPanel extends Composite{
     private static StatisticsPanelUiBinder uiBinder = GWT.create(StatisticsPanelUiBinder.class);
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
     
    interface StatisticsPanelUiBinder extends UiBinder<Widget, StatisticsPanel> {
    }

    public StatisticsPanel(double totalWh, int staffCount, int activityCount, Date firstDate, Date lastDate) {
        initWidget(uiBinder.createAndBindUi(this));
        this.totalHours.setInnerText(DateHelper.doubleToHours(totalWh));
        this.staffCount.setInnerText(""+staffCount);
        this.activityCount.setInnerText(""+activityCount);
        this.firstDate.setInnerText(DateHelper.formatDate(firstDate));
        this.lastDate.setInnerText(DateHelper.formatDate(lastDate));
    }
    
}
