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
package de.cismet.projecttracker.client.common.ui.profile;

import com.github.gwtbootstrap.client.ui.Label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;

/**
 * DOCUMENT ME!
 *
 * @author   dmeiers
 * @version  $Revision$, $Date$
 */
public class StatisticsPanel extends Composite {

    //~ Static fields/initializers ---------------------------------------------

    private static StatisticsPanelUiBinder uiBinder = GWT.create(StatisticsPanelUiBinder.class);

    //~ Instance fields --------------------------------------------------------

    @UiField
    FlowPanel mainPanel = new FlowPanel();
    @UiField
    HorizontalPanel unlockedDaysPnl = new HorizontalPanel();
    @UiField
    HorizontalPanel holidaysTakenPnl = new HorizontalPanel();
    @UiField
    HorizontalPanel holidaysPlannedPnl = new HorizontalPanel();

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StatisticsPanel object.
     */
    public StatisticsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        final BasicAsyncCallback<List<Date>> callback = new BasicAsyncCallback<List<Date>>() {

                @Override
                protected void afterExecution(final List<Date> result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (!result.isEmpty()) {
                            int count = 0;
                            VerticalPanel pnl = new VerticalPanel();
                            pnl.setStyleName("statistic_unlocked_days");
                            int colSize = result.size() / 3;
                            if ((result.size() % 3) != 0) {
                                colSize++;
                            }
                            for (final Date d : result) {
                                if (count == colSize) {
                                    count = 0;
                                    unlockedDaysPnl.add(pnl);
                                    pnl = new VerticalPanel();
                                    pnl.setStyleName("statistic_unlocked_days");
                                }
                                
//                                final Label l = new Label(DateHelper.formatDate(d));
                                Anchor l = new Anchor(DateHelper.formatDate(d));
                                l.addClickHandler(new ClickHandler() {
                                    @Override
                                    public void onClick(ClickEvent event) {
                                        Window.open(Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/ProjectTracker/ProjectTracker/ProjectTracker.html?day=" + d.getTime(), "tracker week", "location=0");
                                    }
                                });
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

        ProjectTrackerEntryPoint.getProjectService(true)
                .getUnlockedDays(ProjectTrackerEntryPoint.getInstance().getStaff(), callback);

        getHolidaysTaken();

        getHolidaysPlanned();
    }

    /**
     * DOCUMENT ME!
     */
    private void getHolidaysTaken() {
        final BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                @Override
                protected void afterExecution(final List<ActivityDTO> result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (result.size() > 0) {
                            int count = 0;
                            VerticalPanel pnl = new VerticalPanel();
                            pnl.setStyleName("statistic_unlocked_days");
                            int colSize = result.size() / 3;
                            if ((result.size() % 3) != 0) {
                                colSize++;
                            }
                            for (final ActivityDTO act : result) {
                                if (count == colSize) {
                                    count = 0;
                                    holidaysTakenPnl.add(pnl);
                                    pnl = new VerticalPanel();
                                    pnl.setStyleName("statistic_unlocked_days");
                                }
                                double wh = act.getWorkinghours();
                                double whPerDay = 0;
                                try {
                                    whPerDay = ProjectTrackerEntryPoint.getInstance()
                                                .getContractForStaff(act.getDay())
                                                .getWhow() / 5;
                                } catch (InvalidInputValuesException ex) {
                                    Logger.getLogger(StatisticsPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if (wh == 0) {
                                    wh = whPerDay;
                                }
                                final Date d = act.getDay();

                                final Label l = new Label(DateHelper.formatDate(d) + ": " + formatDays(wh / whPerDay)
                                                + " Days");
                                l.setStyleName("statistic_label");
                                pnl.add(l);
                                count++;
                            }
                            holidaysTakenPnl.add(pnl);
                        } else {
                            final Label l = new Label("no holidays planned");
                            l.setStyleName("statistic_label");
                            holidaysPlannedPnl.add(l);
                        }
                    }
                }
            };
        ProjectTrackerEntryPoint.getProjectService(true)
                .getVacationActivitiesTaken(new Date(), ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }

    /**
     * DOCUMENT ME!
     */
    private void getHolidaysPlanned() {
        final BasicAsyncCallback<List<ActivityDTO>> callback = new BasicAsyncCallback<List<ActivityDTO>>() {

                @Override
                protected void afterExecution(final List<ActivityDTO> result, final boolean operationFailed) {
                    if (!operationFailed) {
                        if (result.size() > 0) {
                            int count = 0;
                            VerticalPanel pnl = new VerticalPanel();
                            pnl.setStyleName("statistic_unlocked_days");
                            int colSize = result.size() / 3;
                            if ((result.size() % 3) != 0) {
                                colSize++;
                            }
                            for (final ActivityDTO act : result) {
                                if (count == colSize) {
                                    count = 0;
                                    holidaysPlannedPnl.add(pnl);
                                    pnl = new VerticalPanel();
                                    pnl.setStyleName("statistic_unlocked_days");
                                }
                                double wh = act.getWorkinghours();
                                double whPerDay = 0;
                                try {
                                    whPerDay = ProjectTrackerEntryPoint.getInstance()
                                                .getContractForStaff(act.getDay())
                                                .getWhow() / 5;
                                } catch (InvalidInputValuesException ex) {
                                    Logger.getLogger(StatisticsPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                if (wh == 0) {
                                    wh = whPerDay;
                                }
                                final Date d = act.getDay();

                                final Label l = new Label(DateHelper.formatDate(d) + ": " + formatDays(wh / whPerDay)
                                                + " Days");
                                l.setStyleName("statistic_label");
                                pnl.add(l);
                                count++;
                            }
                            holidaysPlannedPnl.add(pnl);
                        } else {
                            final Label l = new Label("no holidays planned");
                            l.setStyleName("statistic_label");
                            holidaysPlannedPnl.add(l);
                        }
                    }
                }
            };

        final Date d = new Date();
        DateHelper.addDays(d, 1);
        ProjectTrackerEntryPoint.getProjectService(true)
                .getVacationActivitiesPlanned(d, ProjectTrackerEntryPoint.getInstance().getStaff(), callback);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   d  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String formatDays(final double d) {
        return NumberFormat.getFormat("#.##").format(d);
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
