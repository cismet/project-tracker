/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.profile;

import com.github.gwtbootstrap.client.ui.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.TaskNotice;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dmeiers
 */
public class StatisticsPanel extends Composite {

    private static StatisticsPanelUiBinder uiBinder = GWT.create(StatisticsPanelUiBinder.class);
    @UiField
    FlowPanel mainPanel = new FlowPanel();
    @UiField
    HorizontalPanel unlockedDaysPnl = new HorizontalPanel();
    @UiField
    HorizontalPanel holidaysTakenPnl = new HorizontalPanel();
    @UiField
    HorizontalPanel holidaysPlannedPnl = new HorizontalPanel();

    private void init() {
        BasicAsyncCallback<List<Date>> callback = new BasicAsyncCallback<List<Date>>() {
            @Override
            protected void afterExecution(List<Date> result, boolean operationFailed) {
                if (!operationFailed) {
                    if (!result.isEmpty()) {
                        int count = 0;
                        VerticalPanel pnl = new VerticalPanel();
                        pnl.setStyleName("statistic_unlocked_days");
                        for (Date d : result) {
                            if (count == 10) {
                                count = 0;
                                unlockedDaysPnl.add(pnl);
                                pnl = new VerticalPanel();
                                pnl.setStyleName("statistic_unlocked_days");
                            }
                            final Label l = new Label(DateHelper.formatDate(d));
                            l.setStyleName("statistic_label");
                            pnl.add(l);
                            count++;
                        }
                        unlockedDaysPnl.add(pnl);
                    } else {
                        mainPanel.add(new Label("all days are locked"));
                    }
                }
            }
        };

        ProjectTrackerEntryPoint.getProjectService(true).getUnlockedDays(ProjectTrackerEntryPoint.getInstance().getLoggedInStaff(), callback);

        getHolidaysTaken();

        getHolidaysPlanned();

        addMailButton();
    }

    //TODO remove method since it was just to test a feature
    private void addMailButton() {
//        final Button mailButton = new Button("send Test Mail");
//        mailButton.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//                //lets take some activites and send them per mail...
//                final StaffDTO staff = ProjectTrackerEntryPoint.getInstance().getLoggedInStaff();
//
//                final BasicAsyncCallback<ArrayList<ActivityDTO>> callback = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {
//                    @Override
//                    protected void afterExecution(ArrayList<ActivityDTO> result, boolean operationFailed) {
//                        BasicAsyncCallback<Void> cb = new BasicAsyncCallback<Void>();
//                        String text = "";
//                        for (ActivityDTO act : result) {
//                            TaskNotice tn = new TaskNotice(act);
//                            tn.setCloseButtonVisible(false);
//                            text += tn.toString() + "</br></br></br>";
//                        }
//                        ProjectTrackerEntryPoint.getProjectService(true).sendTestMail(text,result.get(0), cb);
//                    }
//                };
//
//                ProjectTrackerEntryPoint.getProjectService(true).getActivityByDay(staff, new Date(new Date().getYear(), 9, 15), callback);
//            }
//        });
//        mainPanel.add(mailButton);
    }

    interface StatisticsPanelUiBinder extends UiBinder<Widget, StatisticsPanel> {
    }

    public StatisticsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    private void getHolidaysTaken() {
        BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {
            @Override
            protected void afterExecution(List<ActivityDTO> result, boolean operationFailed) {
                if (!operationFailed) {
                    if (result.size() > 0) {
                        int count = 0;
                        VerticalPanel pnl = new VerticalPanel();
                        pnl.setStyleName("statistic_unlocked_days");
                        for (ActivityDTO act : result) {
                            if (count == 10) {
                                count = 0;
                                holidaysTakenPnl.add(pnl);
                                pnl = new VerticalPanel();
                                pnl.setStyleName("statistic_unlocked_days");

                            }
                            double wh = act.getWorkinghours();
                            double whPerDay = 0;
                            try {
                                whPerDay = ProjectTrackerEntryPoint.getInstance().getContractForStaff(act.getDay()).getWhow() / 5;
                            } catch (InvalidInputValuesException ex) {
                                Logger.getLogger(StatisticsPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (wh == 0) {
                                wh = whPerDay;
                            }
                            final Date d = act.getDay();

                            final Label l = new Label(DateHelper.formatDate(d) + ": " + wh / whPerDay + " Days");
                            l.setStyleName("statistic_label");
                            pnl.add(l);
                            count++;
                        }
                        holidaysTakenPnl.add(pnl);
                    } else {
                        Label l = new Label("no holidays planned");
                        l.setStyleName("statistic_label");
                        holidaysPlannedPnl.add(l);
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getVacationActivitiesTaken(new Date(), ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }

    private void getHolidaysPlanned() {
        BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {
            @Override
            protected void afterExecution(List<ActivityDTO> result, boolean operationFailed) {
                if (!operationFailed) {
                    if (result.size() > 0) {
                        int count = 0;
                        VerticalPanel pnl = new VerticalPanel();
                        pnl.setStyleName("statistic_unlocked_days");
                        for (ActivityDTO act : result) {
                            if (count == 10) {
                                count = 0;
                                holidaysPlannedPnl.add(pnl);
                                pnl = new VerticalPanel();
                                pnl.setStyleName("statistic_unlocked_days");
                            }
                            double wh = act.getWorkinghours();
                            double whPerDay = 0;
                            try {
                                whPerDay = ProjectTrackerEntryPoint.getInstance().getContractForStaff(act.getDay()).getWhow() / 5;
                            } catch (InvalidInputValuesException ex) {
                                Logger.getLogger(StatisticsPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (wh == 0) {
                                wh = whPerDay;
                            }
                            final Date d = act.getDay();

                            final Label l = new Label(DateHelper.formatDate(d) + ": " + wh / whPerDay + " Days");
                            l.setStyleName("statistic_label");
                            pnl.add(l);
                            count++;
                        }
                        holidaysPlannedPnl.add(pnl);
                    } else {
                        Label l = new Label("no holidays planned");
                        l.setStyleName("statistic_label");
                        holidaysPlannedPnl.add(l);
                    }
                }
            }
        };
        ProjectTrackerEntryPoint.getProjectService(true).getVacationActivitiesPlanned(new Date(), ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }
}