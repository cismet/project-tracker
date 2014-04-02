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

import com.github.gwtbootstrap.client.ui.CellTable;

import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.builder.shared.ImageBuilder;
import com.google.gwt.dom.builder.shared.SpanBuilder;
import com.google.gwt.dom.builder.shared.TableCellBuilder;
import com.google.gwt.dom.builder.shared.TableRowBuilder;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.AbstractCellTable.Style;
import com.google.gwt.user.cellview.client.AbstractCellTableBuilder;
import com.google.gwt.user.cellview.client.AbstractHeaderOrFooterBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ReportResultsSummaryDataGrid extends FlowPanel {

    //~ Static fields/initializers ---------------------------------------------

    private static String GRAVATAR_URL_PREFIX = "http://www.gravatar.com/avatar/";
    private static String WP_SUMMARY_HEADER = "Workpackage Summary";
    private static String MONTH_SUMMARY_HEADER = "Month Summary";

    //~ Instance fields --------------------------------------------------------

    CellTable<StaffSummaryEntry> grid = new CellTable<StaffSummaryEntry>();
    final ArrayList<StaffSummaryEntry> tableEntries = new ArrayList<StaffSummaryEntry>();
    private HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>> wpdetailSection =
        new HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>>();
    private HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>> monthDetailSection =
        new HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>>();
    private final Set<Integer> expandedStaffWPEntries = new HashSet<Integer>();
    private final Set<Integer> expandedStaffMonthEntries = new HashSet<Integer>();
    /** Columns to expand friends list. */
    private Column<StaffSummaryEntry, String> staffIconColumn;
    private Column<StaffSummaryEntry, String> staffNameColumn;
    private Column<StaffSummaryEntry, String> wpNameColumn;
    private Column<StaffSummaryEntry, String> whColumn;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportResultsSummaryDataGrid object.
     */
    public ReportResultsSummaryDataGrid() {
//        this.userMap = userMap;
//        this.wpMap = wpMap;
        initializeColumns();
        grid.setWidth("100%", true);
        grid.setColumnWidth(staffIconColumn, 20.0, Unit.PX);
        grid.setColumnWidth(staffNameColumn, 100.0, Unit.PX);
        grid.setColumnWidth(wpNameColumn, 300.0, Unit.PX);
        grid.setColumnWidth(whColumn, 100.0, Unit.PX);
        grid.setHeaderBuilder(new CustomHeaderBuilder());
        grid.setTableBuilder(new ReportsResultSummaryTableBuilder());

//        fill();
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
                    // handling for workpackage summary
                    if (expandedStaffWPEntries.contains(object.id)) {
                        expandedStaffWPEntries.remove(object.id);
                    } else {
                        if (object.staffName.equals(WP_SUMMARY_HEADER)) {
                            expandedStaffWPEntries.add(object.id);
                        }
                    }
                    // handling for monthly summary
                    if (expandedStaffMonthEntries.contains(object.id)) {
                        expandedStaffMonthEntries.remove(object.id);
                    } else {
                        if (object.staffName.equals(MONTH_SUMMARY_HEADER)) {
                            expandedStaffMonthEntries.add(object.id);
                        }
                    }
                    // Redraw the modified row.
                    grid.redrawRow(index);
                }
            });

        wpNameColumn = new Column<StaffSummaryEntry, String>(new TextCell()) {

                @Override
                public String getValue(final StaffSummaryEntry object) {
                    return object.wpName;
                }
            };

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
     *
     * @param  staffOverview  DOCUMENT ME!
     * @param  wpOverview     DOCUMENT ME!
     * @param  monthOverview  DOCUMENT ME!
     */
    void addStaffEntry(final StaffSummaryEntry staffOverview,
            final HashSet<StaffSummaryEntry> wpOverview,
            final HashSet<StaffSummaryEntry> monthOverview) {
        tableEntries.add(staffOverview);
        final StaffSummaryEntry staffWPOverview;
        staffWPOverview = new StaffSummaryEntry(null, WP_SUMMARY_HEADER, "", 0);
        tableEntries.add(staffWPOverview);
        wpdetailSection.put(staffWPOverview, wpOverview);
        final StaffSummaryEntry staffMonthOverview = new StaffSummaryEntry(null, MONTH_SUMMARY_HEADER, "", 0);
        tableEntries.add(staffMonthOverview);
        monthDetailSection.put(staffMonthOverview, monthOverview);
        if (wpdetailSection.keySet().size() == 1) {
            expandedStaffMonthEntries.add(staffMonthOverview.id);
            expandedStaffWPEntries.add(staffWPOverview.id);
        } else {
            expandedStaffMonthEntries.clear();
            expandedStaffWPEntries.clear();
        }
        refreshTable();
    }

    /**
     * DOCUMENT ME!
     */
    private void refreshTable() {
        grid.setPageSize(tableEntries.size());
        grid.setRowCount(tableEntries.size(), true);
        grid.setColumnWidth(staffNameColumn, 100, com.google.gwt.dom.client.Style.Unit.PX);
        grid.setRowData(0, tableEntries);
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @author   daniel
     * @version  $Revision$, $Date$
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

            // Display workpackage summary if corresponding node is expanded.
            if (expandedStaffWPEntries.contains(rowValue.id) && wpdetailSection.containsKey(rowValue)) {
                final Set<StaffSummaryEntry> workPackageEntry = wpdetailSection.get(rowValue);
                for (final StaffSummaryEntry entry : workPackageEntry) {
                    buildRow(entry, absRowIndex, true);
                }
            }
            // Display month summary if corresponding node is expanded.
            if (expandedStaffMonthEntries.contains(rowValue.id) && monthDetailSection.containsKey(rowValue)) {
                final Set<StaffSummaryEntry> workPackageEntry = monthDetailSection.get(rowValue);
                for (final StaffSummaryEntry entry : workPackageEntry) {
                    buildRow(entry, absRowIndex, true);
                }
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @param  rowValue     DOCUMENT ME!
         * @param  absRowIndex  DOCUMENT ME!
         * @param  wpFlag       DOCUMENT ME!
         */
        private void buildRow(final StaffSummaryEntry rowValue, final int absRowIndex, final boolean wpFlag) {
            // Calculate the row styles.
            final StringBuilder trClasses = new StringBuilder(rowStyle);
            if (wpFlag) {
                trClasses.append(" report-table-wpRow");
            }
            if (rowValue.staffName.equals(WP_SUMMARY_HEADER) || rowValue.staffName.equals(MONTH_SUMMARY_HEADER)) {
                trClasses.append(" report-table-detail-header");
            }
            // Calculate the cell styles.
            String cellStyles = cellStyle;
            if (wpFlag) {
                cellStyles += " report-table-wpCell";
            }
            if (rowValue.staffName.equals(WP_SUMMARY_HEADER) || rowValue.staffName.equals(MONTH_SUMMARY_HEADER)) {
                cellStyles += " report-table-detailHeaderCell";
            }
            final TableRowBuilder row = startRow();
            row.className(trClasses.toString());

            // staff name column
            TableCellBuilder td = row.startTD();
            td.className(cellStyles);
            if (!wpFlag) {
                SpanBuilder spB = td.startSpan();
                if (rowValue.iconUrl != null) {
                    final ImageBuilder imgB = spB.startImage();
                    imgB.src(rowValue.iconUrl);
                    imgB.className("report-table-staffIcon");
                    spB.endImage();
                }
                spB.endSpan();
                spB = td.startSpan();
                if (rowValue.staffName.equals(WP_SUMMARY_HEADER) || rowValue.staffName.equals(MONTH_SUMMARY_HEADER)) {
                    spB.attribute("style", "margin-left:60px;");
                }
                renderCell(spB, createContext(1), staffNameColumn, rowValue);
                spB.endSpan();
            }
            td.endTD();

            // WP column.
            td = row.startTD();
            td.className(cellStyles);
            if (wpFlag) {
                renderCell(td, createContext(1), wpNameColumn, rowValue);
            } else {
                td.text("");
            }
            td.endTD();

            // workinghours column.
            td = row.startTD();
            td.className(cellStyles + " report-table-whCol");
            final NumberFormat df = NumberFormat.getFormat("#.##");
            td.text((rowValue.wh == 0) ? ""
                                       : (DateHelper.doubleToHours(rowValue.wh) + " (" + df.format(rowValue.wh) + ")"));
            td.endTD();
            row.endTR();
        }
    }

    /**
     * Renders custom table header. Includes the staff name, workpackage name and summarized working hours
     *
     * @version  $Revision$, $Date$
     */
    private class CustomHeaderBuilder extends AbstractHeaderOrFooterBuilder<StaffSummaryEntry> {

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
}
