/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import de.cismet.projecttracker.client.dto.ReportDTO;

/**
 * A ListItem implementation that wraps ReportDTO objects.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ReportListItem extends ListItem {

    //~ Instance fields --------------------------------------------------------

    private ReportDTO report;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportListItem object.
     */
    public ReportListItem() {
    }

    /**
     * Creates a new ReportListItem object.
     *
     * @param  report  DOCUMENT ME!
     */
    public ReportListItem(final ReportDTO report) {
        super("" + report.getId(), report.getName());
        this.report = report;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the report
     */
    public ReportDTO getReport() {
        return report;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  report  the report to set
     */
    public void setReport(final ReportDTO report) {
        this.report = report;
        setId("" + report.getId());
        setName(report.getName());
    }

    @Override
    public int compareTo(final ListItem o) {
        if (o instanceof ReportListItem) {
//            long result = report.getCreationtime().getTime() - ((ReportListItem)o).getReport().getCreationtime().getTime();
//            return  (int)Math.signum(result);
            return getName().compareTo(o.getName());
        } else {
            return super.compareTo(o);
        }
    }
}
