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

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.AlertBlock;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.base.AlertBase;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.event.ShowEvent;
import com.github.gwtbootstrap.client.ui.event.ShowHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.common.ui.LoadingSpinner;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import de.cismet.projecttracker.client.listener.BasicAsyncCallback;
import de.cismet.projecttracker.client.utilities.TimeCalculator;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ReportResultPanel extends Composite {

    //~ Instance fields --------------------------------------------------------

    AccordionGroup activityAccordionPanel = new AccordionGroup();

    private LargeReportResultAlert sizeAlert = new LargeReportResultAlert(this);
    private FlowPanel mainPanel = new FlowPanel();
    final Timer showLongResultalertTimer = new Timer() {

            @Override
            public void run() {
                showSizeAlert();
            }
        };

    private FlowPanel activityList = new FlowPanel();
    private FlowPanel activityDetailPanel = new FlowPanel();
    private FlowPanel summaryPanel = new FlowPanel();
    private ReportFilterPanel filterPanel;
    private LoadingSpinner loadSpinner = new LoadingSpinner();
    private int users = 0;
    private double hoursInTotal = 0;
    private int activityCount = 0;
    private Date firstActivity = null;
    private Date lastActivity = null;
    private int detailActivityLoaded = 100;
    private ArrayList<ActivityDTO> lastSearchResult;
    private ProcessActivitiesIncrementalCommand processCommand;
    private boolean synchFlag = false;
    private boolean addFlag = true;
    private boolean timerStarted = false;
    private Timer synchTimer = new Timer() {

            @Override
            public void run() {
                if (synchFlag) {
                    this.schedule(100);
                } else {
                    refresh();
                    timerStarted = false;
                    addFlag = true;
                }
            }
        };

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportResultPanel object.
     *
     * @param  filterPanel  DOCUMENT ME!
     */
    public ReportResultPanel(final ReportFilterPanel filterPanel) {
        initWidget(mainPanel);
        this.filterPanel = filterPanel;
        init();
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private void init() {
        activityAccordionPanel.addShowHandler(new ShowHandler() {

                @Override
                public void onShow(final ShowEvent showEvent) {
                    activityDetailPanel.add(activityList);
                }
            });
        loadSpinner.setStyleName("report-result-lbl");
        mainPanel.setStyleName("report-results-area verticalScroller");
        mainPanel.add(summaryPanel);
        activityAccordionPanel.setHeading("Show single activites");
        activityAccordionPanel.setDefaultOpen(false);
        activityAccordionPanel.add(activityDetailPanel);
        final NavLink loadMoreLink = new NavLink("load next 100 activities");
        loadMoreLink.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(final ClickEvent event) {
                    final int newMax = detailActivityLoaded + 100;
                    Scheduler.get().scheduleIncremental(new Scheduler.RepeatingCommand() {

                            @Override
                            public boolean execute() {
                                while ((detailActivityLoaded <= newMax) && (lastSearchResult != null)
                                            && (lastSearchResult.size() >= detailActivityLoaded)) {
                                    final ActivityDTO activity = lastSearchResult.get(detailActivityLoaded);
                                    detailActivityLoaded++;
                                    activityList.add(new ReportTaskNotice(activity));
                                    return true;
                                }
                                return false;
                            }
                        });
                }
            });
        activityAccordionPanel.add(loadMoreLink);
    }

    /**
     * DOCUMENT ME!
     */
    private void showLoadSpinner() {
        mainPanel.clear();
        mainPanel.add(loadSpinner);
    }

    /**
     * DOCUMENT ME!
     */
    public void refresh() {
        if (synchFlag) {
            if (!timerStarted) {
                addFlag = false;
                synchTimer.schedule(100);
            }
        } else {
            synchFlag = true;
            hoursInTotal = 0;
            activityCount = 0;
            firstActivity = null;
            lastActivity = null;
            users = 0;
            activityList.clear();
            detailActivityLoaded = 100;
            lastSearchResult = null;
            showLoadSpinner();

            final HashMap<String, Object> params = filterPanel.getSearchParams();
            final List<WorkPackageDTO> workpackages = (List<WorkPackageDTO>)params.get(
                    ReportFilterPanel.WORKPACKAGE_KEY);
            final ArrayList<StaffDTO> staff = (ArrayList<StaffDTO>)params.get(
                    ReportFilterPanel.STAFF_KEY);
            Date from = null;
            Date to = null;
            if (params.containsKey(ReportFilterPanel.DATE_FROM_KEY)) {
                from = (Date)params.get(ReportFilterPanel.DATE_FROM_KEY);
            }
            if (params.containsKey(ReportFilterPanel.DATE_TO_KEY)) {
                to = (Date)params.get(ReportFilterPanel.DATE_TO_KEY);
            }
            final String descr = (String)params.get(ReportFilterPanel.DESC_KEY);

            showLongResultalertTimer.schedule(5 * 1000);
            final BasicAsyncCallback<ArrayList<ActivityDTO>> cb = new BasicAsyncCallback<ArrayList<ActivityDTO>>() {

                    @Override
                    protected void afterExecution(final ArrayList<ActivityDTO> result,
                            final boolean operationFailed) {
                        lastSearchResult = result;

                        if (operationFailed) {
                            showAlert(false, AlertType.ERROR, "Error during acticity search");
                            return;
                        }
                        if ((result == null) || result.isEmpty()) {
                            showAlert(false, AlertType.INFO, "No corresponding activites can be found");
                            return;
                        }

                        processCommand = new ProcessActivitiesIncrementalCommand(result);
                        Scheduler.get().scheduleIncremental(processCommand);
                    }
                };
            // call the service..
            ProjectTrackerEntryPoint.getProjectService(true).getActivites(workpackages, staff, from, to, descr, cb);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  close  DOCUMENT ME!
     * @param  type   DOCUMENT ME!
     * @param  text   DOCUMENT ME!
     */
    private void showAlert(final boolean close, final AlertType type, final String text) {
        final AlertBlock message = new AlertBlock(true);
        final AlertBase b = (AlertBase)message;
        final Timer closeAlertTimer = new Timer() {

                @Override
                public void run() {
                    b.close();
                }
            };
        b.setAnimation(true);
        mainPanel.clear();
        b.setClose(close);
        b.setType(type);
        b.setText(text);
        mainPanel.add(message);
        closeAlertTimer.schedule(3 * 1000);
        synchFlag = false;
        addFlag = true;
    }

    /**
     * DOCUMENT ME!
     */
    private void showSizeAlert() {
        sizeAlert = new LargeReportResultAlert(this);
        mainPanel.add(sizeAlert);
    }

    /**
     * DOCUMENT ME!
     */
    private void clearUI() {
        mainPanel.clear();
    }

    /**
     * DOCUMENT ME!
     */
    public void abort() {
        if (processCommand != null) {
            processCommand.cancel();
            Scheduler.get().scheduleDeferred(new Command() {

                    @Override
                    public void execute() {
                        clearUI();
                    }
                });
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void generateAndPropagateStatisticsPanel() {
        final StatisticsPanel statPan = new StatisticsPanel(
                hoursInTotal,
                users,
                activityCount,
                firstActivity,
                lastActivity);
        filterPanel.setStatisticsPanel(statPan);
    }

    /**
     * DOCUMENT ME!
     *
     * @param  stuffSummary    DOCUMENT ME!
     * @param  staffWpList     DOCUMENT ME!
     * @param  staffMonthList  DOCUMENT ME!
     */
    private void fillSummaryPanel(final HashMap<StaffDTO, Double> stuffSummary,
            final HashMap<StaffDTO, HashMap<WorkPackageDTO, Double>> staffWpList,
            final HashMap<StaffDTO, HashMap<YearMonthKey, Double>> staffMonthList) {
        final ReportResultsSummaryDataGrid table = new ReportResultsSummaryDataGrid();
        for (final StaffDTO staff : staffWpList.keySet()) {
            final Double whPerStaff = stuffSummary.get(staff);
            final HashMap<WorkPackageDTO, Double> wpSum = staffWpList.get(staff);
            final HashMap<YearMonthKey, Double> montSum = staffMonthList.get(staff);

            // create SummaryEntry for Staff
            String iconUrl = "";
            if (staff.getEmail() != null) {
                iconUrl = ProjectTrackerEntryPoint.GRAVATAR_URL_PREFIX
                            + ProjectTrackerEntryPoint.getInstance().md5(staff.getEmail())
                            + "?s=32";
            }
            final StaffSummaryEntry staffOverview = new StaffSummaryEntry(
                    iconUrl,
                    staff.getFirstname()
                            + staff.getName(),
                    "",
                    whPerStaff);

            // create List of WP summaries
            final HashSet<StaffSummaryEntry> wpOverview = new HashSet<StaffSummaryEntry>();
            final HashSet<StaffSummaryEntry> monthOverview = new HashSet<StaffSummaryEntry>();
            for (final WorkPackageDTO wp : wpSum.keySet()) {
                final double sumWH = wpSum.get(wp);
                wpOverview.add(new StaffSummaryEntry(null, "", wp.getName(), sumWH));
            }

            final ArrayList<YearMonthKey> list = new ArrayList<YearMonthKey>();
            list.addAll(montSum.keySet());
            Collections.sort(list);
            for (final YearMonthKey key : list) {
                final double sumMonth = montSum.get(key);
                monthOverview.add(new StaffSummaryEntry(
                        "",
                        "",
                        key.getYear()
                                + " - "
                                + DateHelper.NAME_OF_MONTH[key.getMonth()],
                        sumMonth));
            }
            table.addStaffEntry(staffOverview, wpOverview, monthOverview);
        }
        summaryPanel.clear();
        summaryPanel.add(table);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    private final class ProcessActivitiesIncrementalCommand implements RepeatingCommand {

        //~ Instance fields ----------------------------------------------------

        final HashMap<StaffDTO, Double> stuffSummary = new HashMap<StaffDTO, Double>();
        final HashMap<StaffDTO, HashMap<WorkPackageDTO, Double>> wpSummaries =
            new HashMap<StaffDTO, HashMap<WorkPackageDTO, Double>>();
        final HashMap<StaffDTO, HashMap<YearMonthKey, Double>> monthSummaries =
            new HashMap<StaffDTO, HashMap<YearMonthKey, Double>>();

        private boolean cancelFlag;
        private Boolean clearVis = true;
        private Boolean stat = true;
        private Boolean table = true;
        private int index = 0;
        private ArrayList<ActivityDTO> result;
        private boolean isCancelled = false;
        private boolean hasStarted = false;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new ProcessActivitiesIncrementalCommand object.
         *
         * @param  result  DOCUMENT ME!
         */
        public ProcessActivitiesIncrementalCommand(final ArrayList<ActivityDTO> result) {
            this.result = result;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * Creates a new ProcessActivitiesIncrementalCommand object.
         *
         * @return  DOCUMENT ME!
         */
        @Override
        public boolean execute() {
            if (!hasStarted) {
                hasStarted = true;
            }
            if ((result == null) || result.isEmpty()) {
                isCancelled = true;
                return false;
            }

            if (cancelFlag) {
                isCancelled = true;
                mainPanel.clear();
                return false;
            }

            while (index < (result.size())) {
                final ActivityDTO activity = result.get(index);
                index++;
                final StaffDTO staff = activity.getStaff();
                final WorkPackageDTO wp = activity.getWorkPackage();
                final double wh = TimeCalculator.getWorkingHoursForActivity(activity);
                if ((wp == null) || (staff == null)) {
                    return true;
                }
                hoursInTotal += TimeCalculator.getWorkingHoursForActivity(activity);
                activityCount++;
                // find the earliest activity...
                if (firstActivity == null) {
                    firstActivity = activity.getDay();
                } else {
                    if (activity.getDay().before(firstActivity)) {
                        firstActivity = activity.getDay();
                    }
                }

                // fin the latest activity
                if (lastActivity == null) {
                    lastActivity = activity.getDay();
                } else {
                    if (activity.getDay().after(lastActivity)) {
                        lastActivity = activity.getDay();
                    }
                }
                // add the hours to the staff summary
                Double whPerStaff = 0d;
                if (stuffSummary.containsKey(staff)) {
                    whPerStaff = stuffSummary.get(staff);
                }
                whPerStaff += wh;
                stuffSummary.put(staff, whPerStaff);

                // add the workingtime to the wp Summary
                final HashMap<WorkPackageDTO, Double> wpMap;
                if (!wpSummaries.containsKey(staff)) {
                    wpMap = new HashMap<WorkPackageDTO, Double>();
                } else {
                    wpMap = wpSummaries.get(staff);
                }
                Double wpSum;
                if (wpMap.containsKey(wp)) {
                    wpSum = wpMap.get(wp);
                    wpSum += wh;
                } else {
                    wpSum = wh;
                }
                wpMap.put(wp, wpSum);
                wpSummaries.put(staff, wpMap);

                // add the workingTime to the month summary
                final HashMap<YearMonthKey, Double> monthMap;
                if (!monthSummaries.containsKey(staff)) {
                    monthMap = new HashMap<YearMonthKey, Double>();
                } else {
                    monthMap = monthSummaries.get(staff);
                }
                Double monthSum;
                final YearMonthKey yearMonthKey = new YearMonthKey(
                        DateHelper.getYear(activity.getDay()),
                        activity.getDay().getMonth());
                if (monthMap.containsKey(yearMonthKey)) {
                    monthSum = monthMap.get(yearMonthKey);
                    monthSum += wh;
                } else {
                    monthSum = wh;
                }
                monthMap.put(yearMonthKey, monthSum);
                monthSummaries.put(staff, monthMap);

                // create a task notice for the show single activites section
                if (index <= 100) {
                    activityList.add(new ReportTaskNotice(activity));
                }

                return true;
            }

            if (stat) {
                stat = false;
                users = stuffSummary.keySet().size();
                generateAndPropagateStatisticsPanel();
                return true;
            }

            if (table) {
                table = false;
//                fillSummaryPanel(stuffSummary, wpSummaries, monthSummaries);
                return true;
            }

            if (clearVis && addFlag) {
                clearVis = false;
                showLongResultalertTimer.cancel();
                mainPanel.clear();
//                mainPanel.remove(ReportResultPanel.this.loadSpinner);
//                sizeAlert.close();
//                mainPanel.remove(sizeAlert);
                fillSummaryPanel(stuffSummary, wpSummaries, monthSummaries);
                mainPanel.add(summaryPanel);
                mainPanel.add(activityAccordionPanel);
                return true;
            }
            synchFlag = false;
            return false;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isAbortFlag() {
            return cancelFlag;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public boolean isCancelled() {
            return isCancelled;
        }

        /**
         * DOCUMENT ME!
         */
        public void reset() {
            this.cancelFlag = false;
        }

        /**
         * DOCUMENT ME!
         */
        public void cancel() {
            this.cancelFlag = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version  $Revision$, $Date$
     */
    public final class YearMonthKey implements Comparable<YearMonthKey> {

        //~ Instance fields ----------------------------------------------------

        private int month;
        private int year;

        //~ Constructors -------------------------------------------------------

        /**
         * Creates a new YearMonth object.
         *
         * @param  year   DOCUMENT ME!
         * @param  month  DOCUMENT ME!
         */
        public YearMonthKey(final int year, final int month) {
            this.year = year;
            this.month = month;
        }

        //~ Methods ------------------------------------------------------------

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getMonth() {
            return month;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  month  DOCUMENT ME!
         */
        public void setMonth(final int month) {
            this.month = month;
        }

        /**
         * DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public int getYear() {
            return year;
        }

        /**
         * DOCUMENT ME!
         *
         * @param  year  DOCUMENT ME!
         */
        public void setYear(final int year) {
            this.year = year;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof YearMonthKey) {
                final YearMonthKey ym = (YearMonthKey)o;
                if ((ym.getMonth() == this.month) && (ym.getYear() == this.year)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = (31 * hash) + this.month;
            hash = (31 * hash) + this.year;
            return hash;
        }

        @Override
        public int compareTo(final YearMonthKey t) {
            if (t.getYear() < this.getYear()) {
                return 1;
            } else if (t.getYear() > this.getYear()) {
                return -1;
            } else {
                if (t.getMonth() < this.getMonth()) {
                    return 1;
                } else if (t.getMonth() > this.getMonth()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
}
