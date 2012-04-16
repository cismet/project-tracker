package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ReportDTO;



/**
 * A ListItem implementation that wraps ReportDTO objects
 *
 * @author therter
 */
public class ReportListItem extends ListItem {
    private ReportDTO report;

    public ReportListItem(){
    }

    public ReportListItem(ReportDTO report) {
        super("" + report.getId(), report.getName());
        this.report = report;
    }

    /**
     * @return the report
     */
    public ReportDTO getReport() {
        return report;
    }

    /**
     * @param report the report to set
     */
    public void setReport(ReportDTO report) {
        this.report = report;
        setId( "" + report.getId() );
        setName( report.getName() );
    }

    @Override
    public int compareTo(ListItem o) {
        if (o instanceof ReportListItem) {
//            long result = report.getCreationtime().getTime() - ((ReportListItem)o).getReport().getCreationtime().getTime();
//            return  (int)Math.signum(result);
            return getName().compareTo(o.getName());
        } else {
            return super.compareTo(o);
        }
    }
}
