package de.cismet.projecttracker.client.dto;

import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.helper.DateHelper;
import java.util.Date;

/**
 *
 * @author therter
 */
public class WorkPackagePeriodDTO extends BasicDTO<WorkPackagePeriodDTO> implements Comparable<Object>  {
    private WorkPackageDTO workPackage;
    private Date fromdate;
    private Date todate;
    private Date asof;

    public WorkPackagePeriodDTO() {
    }



    public WorkPackagePeriodDTO(long id, WorkPackageDTO workPackage, Date fromdate, Date todate, Date asof) {
        this.id = id;
        this.workPackage = workPackage;
        this.fromdate = fromdate;
        this.todate = todate;
        this.asof = asof;
    }



    /**
     * @return the workPackage
     */
    public WorkPackageDTO getWorkPackage() {
        return workPackage;
    }

    /**
     * @param workPackage the workPackage to set
     */
    public void setWorkPackage(WorkPackageDTO workPackage) {
        this.workPackage = workPackage;
    }

    /**
     * @return the fromdate
     */
    public Date getFromdate() {
        return fromdate;
    }

    /**
     * @param fromdate the fromdate to set
     */
    public void setFromdate(Date fromdate) {
        this.fromdate = fromdate;
    }

    /**
     * @return the todate
     */
    public Date getTodate() {
        return todate;
    }

    /**
     * @param todate the todate to set
     */
    public void setTodate(Date todate) {
        this.todate = todate;
    }

    /**
     * @return the asof
     */
    public Date getAsof() {
        return asof;
    }

    /**
     * @param asof the asof to set
     */
    public void setAsof(Date asof) {
        this.asof = asof;
    }

    @Override
    public WorkPackagePeriodDTO createCopy() {
        return new WorkPackagePeriodDTO(id, workPackage, fromdate, todate, asof);
    }

    @Override
    public void reset(WorkPackagePeriodDTO obj) {
        this.id = obj.id;
        this.workPackage = obj.workPackage;
        this.fromdate = obj.fromdate;
        this.todate = obj.todate;
        this.asof = obj.asof;
    }
    @Override

    public String toString() {
        MessageConstants messages = getMessagesInstance();

        //the MessageConstants instance is only available in the client mode
        if (messages != null) {
            return messages.periodMessage(DateHelper.formatDateTime(asof), DateHelper.formatDate(fromdate), DateHelper.formatDate(todate));
        } else {
            return super.toString();
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof WorkPackagePeriodDTO) {
            long result = asof.getTime() - ((WorkPackagePeriodDTO)o).asof.getTime();
            return  (int)Math.signum(result);
        } else if (o instanceof Date) {
            long result = asof.getTime() - ((Date)o).getTime();
            return  (int)Math.signum(result);
        } else {
            return 0;
        }
    }
}
