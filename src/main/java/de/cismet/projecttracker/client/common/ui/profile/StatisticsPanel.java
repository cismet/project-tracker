/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.profile;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author dmeiers
 */
public class StatisticsPanel extends Composite {

    private static StatisticsPanelUiBinder uiBinder = GWT.create(StatisticsPanelUiBinder.class);
    @UiField
    FlowPanel mainPanel = new FlowPanel();

    private void init() {
        BasicAsyncCallback<List<Date>> callback = new BasicAsyncCallback<List<Date>>() {

            @Override
            protected void afterExecution(List<Date> result, boolean operationFailed) {
                if(!operationFailed){
                    if(!result.isEmpty()){
                        final VerticalPanel pnl = new VerticalPanel();
                        for(Date d: result){
                            final Label l = new Label(DateHelper.formatDate(d));
                            pnl.add(l);
                        }
                        mainPanel.add(pnl);
                    }else{
                        mainPanel.add(new Label("all days are locked"));
                    }
                }
            }
            
        };
        
        ProjectTrackerEntryPoint.getProjectService(true).getUnlockedDays(ProjectTrackerEntryPoint.getInstance().getLoggedInStaff(), callback);
    }

    interface StatisticsPanelUiBinder extends UiBinder<Widget, StatisticsPanel> {
    }

    public StatisticsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }
}
