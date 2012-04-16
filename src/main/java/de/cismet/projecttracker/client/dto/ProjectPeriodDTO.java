/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import de.cismet.projecttracker.client.MessageConstants;
import de.cismet.projecttracker.client.helper.DateHelper;
import java.util.Date;

/**
 *
 * @author therter
 */
public class ProjectPeriodDTO extends BasicDTO<ProjectPeriodDTO> implements Comparable<ProjectPeriodDTO> {
    private ProjectDTO project;
    private Date fromdate;
    private Date todate;
    private Date asof;

    public ProjectPeriodDTO() {
    }

     

    public ProjectPeriodDTO(long id, ProjectDTO project, Date fromdate, Date todate, Date asof) {
        this.id = id;
        this.project = project;
        this.fromdate = fromdate;
        this.todate = todate;
        this.asof = asof;
    }

     

    /**
     * @return the project
     */
    public ProjectDTO getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(ProjectDTO project) {
        this.project = project;
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
    public ProjectPeriodDTO createCopy() {
        return new ProjectPeriodDTO(id, project, fromdate, todate, asof);
    }

    @Override
    public void reset(ProjectPeriodDTO obj) {
        this.id = obj.id;
        this.project = obj.project;
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
    public int compareTo(ProjectPeriodDTO o) {
        if (o == null || o.asof == null) {
            return -1;
        }
        if (asof == null) {
            return 1;
        }
        long diff = asof.getTime() - o.asof.getTime();
        
        return (int)Math.signum(diff);
    }
}
