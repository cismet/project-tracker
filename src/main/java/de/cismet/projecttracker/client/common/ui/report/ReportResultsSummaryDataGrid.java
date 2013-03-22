/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.github.gwtbootstrap.client.ui.CellTable;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageCell;
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
import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkPackageDTO;
import de.cismet.projecttracker.client.helper.DateHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author daniel
 */
public class ReportResultsSummaryDataGrid extends FlowPanel {

    private HashMap<StaffDTO, Set<ActivityDTO>> userMap = new HashMap<StaffDTO, Set<ActivityDTO>>();
    private HashMap<StaffDTO, Image> userIconMap = new HashMap<StaffDTO, Image>();
    private static String GRAVATAR_URL_PREFIX = "http://www.gravatar.com/avatar/";
    private HashMap<WorkPackageDTO, Set<ActivityDTO>> wpMap = new HashMap<WorkPackageDTO, Set<ActivityDTO>>();
    private HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>> detailSection = new HashMap<StaffSummaryEntry, Set<StaffSummaryEntry>>();
    private final Set<Integer> expandedStaffEntries = new HashSet<Integer>();
    CellTable<StaffSummaryEntry> grid = new CellTable<StaffSummaryEntry>();

    public ReportResultsSummaryDataGrid(HashMap<StaffDTO, Set<ActivityDTO>> userMap, HashMap<WorkPackageDTO, Set<ActivityDTO>> wpMap) {
        this.userMap = userMap;
        this.wpMap = wpMap;
        initializeColumns();
        grid.setWidth("100%", true);
        grid.setColumnWidth(staffIconColumn, 20.0,Unit.PX);
        grid.setColumnWidth(staffNameColumn, 100.0, Unit.PX);
        grid.setColumnWidth(wpNameColumn, 300.0, Unit.PX);
        grid.setColumnWidth(whColumn, 100.0, Unit.PX);
        grid.setHeaderBuilder(new CustomHeaderBuilder());
        grid.setTableBuilder(new ReportsResultSummaryTableBuilder());

        fill();
        this.add(grid);
    }
    /**
     * Column to expand friends list.
     */
    private Column<StaffSummaryEntry, String> staffIconColumn;
    private Column<StaffSummaryEntry, String> staffNameColumn;
    private Column<StaffSummaryEntry, String> wpNameColumn;
    private Column<StaffSummaryEntry, String> whColumn;

    private void initializeColumns() {
        staffNameColumn = new Column<StaffSummaryEntry, String>(new ClickableTextCell()) {
            @Override
            public String getValue(StaffSummaryEntry object) {
                return object.staffName;
            }
        };
        staffNameColumn.setFieldUpdater(new FieldUpdater<StaffSummaryEntry, String>() {
            @Override
            public void update(int index, StaffSummaryEntry object, String value) {
                if (expandedStaffEntries.contains(object.id)) {
                    expandedStaffEntries.remove(object.id);
                } else {
                    expandedStaffEntries.add(object.id);
                }
                // Redraw the modified row.
                grid.redrawRow(index);
            }
        });

        wpNameColumn = new Column<StaffSummaryEntry, String>(new TextCell()) {
            @Override
            public String getValue(StaffSummaryEntry object) {
                return object.wpName;
            }
        };

        whColumn = new Column<StaffSummaryEntry, String>(new TextCell()) {
            @Override
            public String getValue(StaffSummaryEntry object) {
                return DateHelper.doubleToHours(object.wh);
            }
        };
        whColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
    }

    private void fill() {
        final ArrayList<StaffSummaryEntry> tableEntries = new ArrayList<StaffSummaryEntry>();

        for (StaffDTO s : userMap.keySet()) {
            //retrieve the user icon
            String iconUrl="";
            if (s.getEmail() != null) {
                iconUrl = GRAVATAR_URL_PREFIX
                        + ProjectTrackerEntryPoint.getInstance().md5(s.getEmail())
                        + "?s=32";
            }
            double whPerstaff = 0;
            final Set<ActivityDTO> userSet = userMap.get(s);
            //calculate the first row
            if (!userSet.isEmpty()) {
                for (ActivityDTO act : userSet) {
                    whPerstaff += act.getWorkinghours();
                }
            }
            StaffSummaryEntry staffOverview = new StaffSummaryEntry(iconUrl,s.getFirstname() + s.getName(), "", whPerstaff);
            tableEntries.add(staffOverview);
            final HashSet<StaffSummaryEntry> wpOverview = new HashSet<StaffSummaryEntry>();
            //calculate the detail section
            for (WorkPackageDTO wp : wpMap.keySet()) {
                final Set<ActivityDTO> wpSet = new HashSet<ActivityDTO>(wpMap.get(wp));
                //intersect with the user set
                wpSet.retainAll(userSet);
                if (!wpSet.isEmpty()) {
                    double summarizedWorkingTime = 0;
                    for (ActivityDTO act : wpSet) {
                        summarizedWorkingTime += act.getWorkinghours();
                    }
                    wpOverview.add(new StaffSummaryEntry("","", wp.getName(), summarizedWorkingTime));
                }
            }
            detailSection.put(staffOverview, wpOverview);
            //if there is just one user show the wp details directly
            if (userMap.keySet().size() == 1) {
                expandedStaffEntries.add(staffOverview.id);
            }
        }
        grid.setPageSize(tableEntries.size());
        grid.setRowCount(tableEntries.size(), true);
        grid.setColumnWidth(staffNameColumn, 100, com.google.gwt.dom.client.Style.Unit.PX);
        grid.setRowData(0, tableEntries);
    }

    /**
     *
     * @author daniel
     */
    private class ReportsResultSummaryTableBuilder extends AbstractCellTableBuilder<StaffSummaryEntry> {

        private final String rowStyle;
        private final String cellStyle;

        /**
         *
         * @param cellTable
         */
        public ReportsResultSummaryTableBuilder() {
            super(grid);
            Style style = cellTable.getResources().style();
            rowStyle = style.evenRow();
            cellStyle = style.cell() + " report-table-no-border";
        }

        @Override
        protected void buildRowImpl(StaffSummaryEntry rowValue, int absRowIndex) {
            buildRow(rowValue, absRowIndex, false);

            // Display list of friends.
            if (expandedStaffEntries.contains(rowValue.id) && detailSection.containsKey(rowValue)) {
                Set<StaffSummaryEntry> workPackageEntry = detailSection.get(rowValue);
                for (StaffSummaryEntry entry : workPackageEntry) {
                    buildRow(entry, absRowIndex, true);
                }
            }
        }

        private void buildRow(StaffSummaryEntry rowValue, int absRowIndex, boolean wpFlag) {
            // Calculate the row styles.
            StringBuilder trClasses = new StringBuilder(rowStyle);
            if (wpFlag) {
                trClasses.append(" report-table-wpRow");
            }
            // Calculate the cell styles.
            String cellStyles = cellStyle;
            if (wpFlag) {
                cellStyles += " report-table-wpCell";
            }
            TableRowBuilder row = startRow();
            row.className(trClasses.toString());
            
            //staff name column
            TableCellBuilder td = row.startTD();
            td.className(cellStyles);
            if (!wpFlag) {
                SpanBuilder spB = td.startSpan();
                    ImageBuilder imgB = spB.startImage();
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
            td.text(DateHelper.doubleToHours(rowValue.wh));
            td.endTD();
            row.endTR();
        }
    }

    /**
     * Renders custom table header. Includes the staff name, workpackage name and summarized working hours
     */
    private class CustomHeaderBuilder extends AbstractHeaderOrFooterBuilder<StaffSummaryEntry> {

        private Header<String> checkBoxHeader = new TextHeader("Collapse");
        private Header<String> staffNameHeader = new TextHeader("Staff");
        private Header<String> wpHeader = new TextHeader("Workpackage");
        private Header<String> whHeader = new TextHeader("Hours");

        public CustomHeaderBuilder() {
            super(grid, false);
            setSortIconStartOfLine(false);
        }

        @Override
        protected boolean buildHeaderOrFooterImpl() {
            // header above the checkbox 
            TableRowBuilder tr = startRow();

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

            //workinghours header
            th = tr.startTH().colSpan(1);
            th.attribute("style", "text-align: right; padding-right: 20px;");
            th.className("report-table-whCol");
            th.text("Hours").endTH();

            return true;
        }
    }

    private final class StaffSummaryEntry {

        public String iconUrl;
        public final String staffName;
        public final String wpName;
        public final double wh;
        public final Integer id;

        public StaffSummaryEntry(String url, String staffName, String wpName, double wh) {
            this.iconUrl=url;
            this.staffName = staffName;
            this.wpName = wpName;
            this.wh = wh;
            this.id = Random.nextInt(Integer.MAX_VALUE);
        }
    }
}
