/**
 * *************************************************
 *
 * cismet GmbH, Saarbruecken, Germany
 * 
* ... and it just works.
 * 
***************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.github.gwtbootstrap.client.ui.CellTable;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.builder.shared.ImageBuilder;
import com.google.gwt.dom.builder.shared.SpanBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.TextHeader;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author daniel
 * @version $Revision$, $Date$
 */
public class ReportResultsSummaryDataGrid extends FlowPanel {

    //~ Static fields/initializers ---------------------------------------------
    private static String GRAVATAR_URL_PREFIX = "http://www.gravatar.com/avatar/";
    private static String WP_SUMMARY_HEADER = "Workpackage Summary";
    private static String MONTH_SUMMARY_HEADER = "Month Summary";
    //~ Instance fields --------------------------------------------------------
    CellTable<StaffSummaryEntry> grid = new CellTable<StaffSummaryEntry>();
    private HashMap<StaffDTO, Set<ActivityDTO>> userMap = new HashMap<StaffDTO, Set<ActivityDTO>>();
    private HashMap<StaffDTO, Image> userIconMap = new HashMap<StaffDTO, Image>();
    private HashMap<WorkPackageDTO, Set<ActivityDTO>> wpMap = new HashMap<WorkPackageDTO, Set<ActivityDTO>>();
    private HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>> detailSection =
            new HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>>();
    private final Set<Integer> expandedStaffEntries = new HashSet<Integer>();
    private final Set<Integer> expandedStaffMonthEntries = new HashSet<Integer>();
    /**
     * Column to expand friends list.
     */
    private Column<StaffSummaryEntry, String> staffIconColumn;
    private Column<StaffSummaryEntry, String> staffNameColumn;
    private Column<StaffSummaryEntry, String> wpNameColumn;
    private Column<StaffSummaryEntry, String> whColumn;

    //~ Constructors -----------------------------------------------------------
    /**
     * Creates a new ReportResultsSummaryDataGrid object.
     *
     * @param userMap DOCUMENT ME!
     * @param wpMap DOCUMENT ME!
     */
    public ReportResultsSummaryDataGrid(final HashMap<StaffDTO, Set<ActivityDTO>> userMap,
            final HashMap<WorkPackageDTO, Set<ActivityDTO>> wpMap) {
        this.userMap = userMap;
        this.wpMap = wpMap;
        initializeColumns();
        grid.setWidth("100%", true);
        grid.setColumnWidth(staffIconColumn, 20.0, Unit.PX);
        grid.setColumnWidth(staffNameColumn, 100.0, Unit.PX);
        grid.setColumnWidth(wpNameColumn, 300.0, Unit.PX);
        grid.setColumnWidth(whColumn, 100.0, Unit.PX);
        grid.setHeaderBuilder(new CustomHeaderBuilder());
        grid.setTableBuilder(new ReportsResultSummaryTableBuilder());

        fill();
        this.add(grid);
    }

    //~ Methods ----------------------------------------------------------------
    /**
     * DOCUMENT ME!
     */
    private void initializeColumns() {
        staffNameColumn = new Column<StaffSummaryEntry, String>(new ClickableTextCell()) {
            @Override
            public String getValue(final StaffSummaryEntry object) {
                return object.staffName;
            }
        };
        staffNameColumn.setFieldUpdater(new FieldUpdater<StaffSummaryEntry, String>() {
            @Override
            public void update(final int index, final StaffSummaryEntry object, final String value) {
                if (expandedStaffEntries.contains(object.id)) {
                    expandedStaffEntries.remove(object.id);
                } else {
                    expandedStaffEntries.add(object.id);
                }
                // Redraw the modified row.
                grid.redrawRow(index);
            }
        });

        wpNameColumn = new Column<StaffSummaryEntry, String>(new ClickableTextCell()) {
            @Override
            public String getValue(final StaffSummaryEntry object) {
                return object.wpName;
            }
        };
        wpNameColumn.setFieldUpdater(new FieldUpdater<StaffSummaryEntry, String>() {
            @Override
            public void update(int index, StaffSummaryEntry object, String value) {
                if (expandedStaffMonthEntries.contains(object.id)) {
                    expandedStaffMonthEntries.remove(object.id);
                } else {
                    if (object.wpName.equals(this)) {
                        expandedStaffMonthEntries.add(object.id);
                    }
                }
                grid.redrawRow(index);
            }
        });

        whColumn = new Column<StaffSummaryEntry, String>(new TextCell()) {
            @Override
            public String getValue(final StaffSummaryEntry object) {
                return DateHelper.doubleToHours(object.wh);
            }
        };
        whColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    }

    /**
     * DOCUMENT ME!
     */
    private void fill() {
        final ArrayList<StaffSummaryEntry> tableEntries = new ArrayList<StaffSummaryEntry>();

        for (final StaffDTO s : userMap.keySet()) {
            // retrieve the user icon
            String iconUrl = "";
            if (s.getEmail() != null) {
                iconUrl = GRAVATAR_URL_PREFIX
                        + ProjectTrackerEntryPoint.getInstance().md5(s.getEmail())
                        + "?s=32";
            }
            double whPerstaff = 0;
            final Set<ActivityDTO> userSet = userMap.get(s);
            // calculate the first row, genreate the month map
            final HashMap<YearMonth, Set<ActivityDTO>> monthMap = new HashMap<YearMonth, Set<ActivityDTO>>();
            if (!userSet.isEmpty()) {
                for (final ActivityDTO act : userSet) {
                    whPerstaff += act.getWorkinghours();
                    final YearMonth ym = new YearMonth(DateHelper.getYear(act.getDay()), act.getDay().getMonth());
                    Set<ActivityDTO> monthSet = monthMap.get(ym);
                    if (monthSet == null) {
                        monthSet = new HashSet<ActivityDTO>();
                    }
                    monthSet.add(act);
                    monthMap.put(ym, monthSet);
                }
            }
            final StaffSummaryEntry staffOverview = new StaffSummaryEntry(
                    iconUrl,
                    s.getFirstname()
                    + s.getName(),
                    "",
                    whPerstaff);
            tableEntries.add(staffOverview);
            final HashSet<StaffSummaryEntry> wpOverview = new HashSet<StaffSummaryEntry>();
            // calculate the detail section
            for (final WorkPackageDTO wp : wpMap.keySet()) {
                final Set<ActivityDTO> wpSet = new HashSet<ActivityDTO>(wpMap.get(wp));
                // intersect with the user set
                wpSet.retainAll(userSet);
                if (!wpSet.isEmpty()) {
                    double summarizedWorkingTime = 0;
                    for (final ActivityDTO act : wpSet) {
                        summarizedWorkingTime += act.getWorkinghours();
                    }
                    wpOverview.add(new StaffSummaryEntry("", "", wp.getName(), summarizedWorkingTime));
                }
            }
            wpOverview.add(new StaffSummaryEntry("", "", "", 0));
            final ArrayList<YearMonth> list = new ArrayList<YearMonth>();
            list.addAll(monthMap.keySet());
            Collections.sort(list);
            for (final YearMonth ym : list) {
                final Set<ActivityDTO> monthSet = monthMap.get(ym);
                double monthWH = 0;
                for (final ActivityDTO act : monthSet) {
                    monthWH += act.getWorkinghours();
                }
                wpOverview.add(new StaffSummaryEntry(
                        "",
                        "",
                        ym.getYear()
                        + " - "
                        + DateHelper.NAME_OF_MONTH[ym.getMonth()],
                        monthWH));
            }
            detailSection.put(staffOverview, wpOverview);
            // if there is just one user show the wp details directly
            if (userMap.keySet().size() == 1) {
                expandedStaffEntries.add(staffOverview.id);
            }
        }
        grid.setPageSize(tableEntries.size());
        grid.setRowCount(tableEntries.size(), true);
        grid.setColumnWidth(staffNameColumn, 100, com.google.gwt.dom.client.Style.Unit.PX);
        grid.setRowData(0, tableEntries);
    }

    //~ Inner Classes ----------------------------------------------------------
    /**
     * DOCUMENT ME!
     *
     * @author daniel
     * @version $Revision$, $Date$
     */
    private class ReportsResultSummaryTableBuilder extends AbstractCellTableBuilder<StaffSummaryEntry> {

        //~ Instance fields ----------------------------------------------------
        private final String rowStyle;
        private final String cellStyle;

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new ReportsResultSummaryTableBuilder object.
         */
        public ReportsResultSummaryTableBuilder() {
            super(grid);
            final Style style = cellTable.getResources().style();
            rowStyle = style.evenRow();
            cellStyle = style.cell() + " report-table-no-border";
        }

        //~ Methods ------------------------------------------------------------
        @Override
        protected void buildRowImpl(final StaffSummaryEntry rowValue, final int absRowIndex) {
            buildRow(rowValue, absRowIndex, false);

            // Display list of friends.
            if (expandedStaffEntries.contains(rowValue.id) && detailSection.containsKey(rowValue)) {
                buildRow(new StaffSummaryEntry("", "", WP_SUMMARY_HEADER, 0),absRowIndex,true);
                buildRow(new StaffSummaryEntry("", "", MONTH_SUMMARY_HEADER, 0),absRowIndex,true);
                
//                final Set<StaffSummaryEntry> workPackageEntry = detailSection.get(rowValue);
//                for (final StaffSummaryEntry entry : workPackageEntry) {
//                    buildRow(entry, absRowIndex, true);
//                }
//                if(expandedStaffMonthEntries.contains(rowValue.id && det))
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param rowValue DOCUMENT ME!
         * @param absRowIndex DOCUMENT ME!
         * @param wpFlag DOCUMENT ME!
         */
        private void buildRow(final StaffSummaryEntry rowValue, final int absRowIndex, final boolean wpFlag) {
            // Calculate the row styles.
            final StringBuilder trClasses = new StringBuilder(rowStyle);
            if (wpFlag) {
                trClasses.append(" report-table-wpRow");
            }
            // Calculate the cell styles.
            String cellStyles = cellStyle;
            if (wpFlag) {
                cellStyles += " report-table-wpCell";
            }
            final TableRowBuilder row = startRow();
            row.className(trClasses.toString());

            // staff name column
            TableCellBuilder td = row.startTD();
            td.className(cellStyles);
            if (!wpFlag) {
                SpanBuilder spB = td.startSpan();
                final ImageBuilder imgB = spB.startImage();
                imgB.src(rowValue.iconUrl);
                imgB.className("report-table-staffIcon");
                spB.endImage();
                spB.endSpan();
                spB = td.startSpan();
//                    spB.text(rowValue.staffName);
                renderCell(spB, createContext(1), staffNameColumn, rowValue);
                spB.endSpan();
            }
            td.endTD();

            // WP column.
            td = row.startTD();
            td.className(cellStyles);
            if (wpFlag) {
                td.text(rowValue.wpName);
            } else {
                td.text("");
            }
            td.endTD();

            // workinghours column.
            td = row.startTD();
            td.className(cellStyles + " report-table-whCol");
            td.text((rowValue.wh == 0) ? "" : DateHelper.doubleToHours(rowValue.wh));
            td.endTD();
            row.endTR();
        }
    }

    /**
     * Renders custom table header. Includes the staff name, workpackage name and summarized working hours
     *
     * @version $Revision$, $Date$
     */
    private class CustomHeaderBuilder extends AbstractHeaderOrFooterBuilder<StaffSummaryEntry> {

        //~ Instance fields ----------------------------------------------------
        private Header<String> checkBoxHeader = new TextHeader("Collapse");
        private Header<String> staffNameHeader = new TextHeader("Staff");
        private Header<String> wpHeader = new TextHeader("Workpackage");
        private Header<String> whHeader = new TextHeader("Hours");

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new CustomHeaderBuilder object.
         */
        public CustomHeaderBuilder() {
            super(grid, false);
            setSortIconStartOfLine(false);
        }

        //~ Methods ------------------------------------------------------------
        @Override
        protected boolean buildHeaderOrFooterImpl() {
            // header above the checkbox
            final TableRowBuilder tr = startRow();

            /*
             * staff name header
             */
            TableCellBuilder th = tr.startTH().colSpan(1);
            enableColumnHandlers(th, staffNameColumn);
            th.className("report-table-staffCol");
            th.text("Staff").endTH();

//            th = tr.startTH().colSpan(1);
//            th.className("report-table-wpCol");
//            th.text("Staff").endTH();

            // workpackageheader.
            th = tr.startTH().colSpan(1);
            th.className("report-table-wpCol");
            th.text("WorkPackage").endTH();

            // workinghours header
            th = tr.startTH().colSpan(1);
            th.attribute("style", "text-align: right; padding-right: 20px;");
            th.className("report-table-whCol");
            th.text("Hours").endTH();

            return true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @version $Revision$, $Date$
     */
    private final class YearMonth implements Comparable<YearMonth> {

        //~ Instance fields ----------------------------------------------------
        private int month;
        private int year;

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new YearMonth object.
         *
         * @param year DOCUMENT ME!
         * @param month DOCUMENT ME!
         */
        public YearMonth(final int year, final int month) {
            this.year = year;
            this.month = month;
        }

        //~ Methods ------------------------------------------------------------
        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int getMonth() {
            return month;
        }

        /**
         * DOCUMENT ME!
         *
         * @param month DOCUMENT ME!
         */
        public void setMonth(final int month) {
            this.month = month;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int getYear() {
            return year;
        }

        /**
         * DOCUMENT ME!
         *
         * @param year DOCUMENT ME!
         */
        public void setYear(final int year) {
            this.year = year;
        }

        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof YearMonth) {
                final YearMonth ym = (YearMonth) o;
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
        public int compareTo(final YearMonth t) {
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

    /**
     * DOCUMENT ME!
     *
     * @version $Revision$, $Date$
     */
    private final class StaffSummaryEntry {

        //~ Instance fields ----------------------------------------------------
        public String iconUrl;
        public final String staffName;
        public final String wpName;
        public final double wh;
        public final Integer id;

        //~ Constructors -------------------------------------------------------
        /**
         * Creates a new StaffSummaryEntry object.
         *
         * @param url DOCUMENT ME!
         * @param staffName DOCUMENT ME!
         * @param wpName DOCUMENT ME!
         * @param wh DOCUMENT ME!
         */
        public StaffSummaryEntry(final String url, final String staffName, final String wpName, final double wh) {
            this.iconUrl = url;
            this.staffName = staffName;
            this.wpName = wpName;
            this.wh = wh;
            this.id = Random.nextInt(Integer.MAX_VALUE);
        }
    }
}
