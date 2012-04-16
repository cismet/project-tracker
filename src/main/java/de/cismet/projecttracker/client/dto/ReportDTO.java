/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.dto;

import de.cismet.projecttracker.client.helper.DateHelper;
import java.util.Date;

/**
 *
 * @author therter
 */
public class ReportDTO extends BasicDTO<BasicDTO>{
    private String name;
    private String generatorname;
    private StaffDTO staff;
    private Date creationtime;
    private Date fromdate;
    private Date todate;

    public ReportDTO() {
    }


     
    public ReportDTO(long id, String name, String generatorname, StaffDTO staff, Date creationtime, Date fromdate, Date todate) {
        this.id = id;
        this.name = name;
        this.generatorname = generatorname;
        this.staff = staff;
        this.creationtime = creationtime;
        this.fromdate = fromdate;
        this.todate = todate;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the generatorname
     */
    public String getGeneratorname() {
        return generatorname;
    }

    /**
     * @param generatorname the generatorname to set
     */
    public void setGeneratorname(String generatorname) {
        this.generatorname = generatorname;
    }

    /**
     * @return the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * @param staff the staff to set
     */
    public void setStaff(StaffDTO staff) {
        this.staff = staff;
    }

    /**
     * @return the creationtime
     */
    public Date getCreationtime() {
        return creationtime;
    }

    /**
     * @param creationtime the creationtime to set
     */
    public void setCreationtime(Date creationtime) {
        this.creationtime = creationtime;
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
     *
     * @return an isntance of the type ReportShort, that contains the same data as this object
     */
    @Override
    public BasicDTO createCopy() {
        return new ReportDTO(id, name, generatorname, staff, creationtime, fromdate, todate);
    }

    /**
     * copies the content of the given object in this object
     * @param obj an instance of the type ReportShort
     */
    @Override
    public void reset(BasicDTO obj) {
        ReportDTO rep = (ReportDTO)obj;
        this.id = rep.id;
        this.name = rep.name;
        this.generatorname = rep.generatorname;
        this.staff = rep.staff;
        this.creationtime = rep.creationtime;
        this.fromdate = rep.fromdate;
        this.todate = rep.todate;
    }

    @Override
    public String toString() {
        //todo: String auslagern
        return name + " erstellt am: " + DateHelper.formatDate(creationtime);
    }
}
