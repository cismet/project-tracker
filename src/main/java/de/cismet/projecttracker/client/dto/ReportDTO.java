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
package de.cismet.projecttracker.client.dto;

import java.util.Date;

import de.cismet.projecttracker.client.helper.DateHelper;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ReportDTO extends BasicDTO<BasicDTO> {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private String generatorname;
    private StaffDTO staff;
    private Date creationtime;
    private Date fromdate;
    private Date todate;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportDTO object.
     */
    public ReportDTO() {
    }

    /**
     * Creates a new ReportDTO object.
     *
     * @param  id             DOCUMENT ME!
     * @param  name           DOCUMENT ME!
     * @param  generatorname  DOCUMENT ME!
     * @param  staff          DOCUMENT ME!
     * @param  creationtime   DOCUMENT ME!
     * @param  fromdate       DOCUMENT ME!
     * @param  todate         DOCUMENT ME!
     */
    public ReportDTO(final long id,
            final String name,
            final String generatorname,
            final StaffDTO staff,
            final Date creationtime,
            final Date fromdate,
            final Date todate) {
        this.id = id;
        this.name = name;
        this.generatorname = generatorname;
        this.staff = staff;
        this.creationtime = creationtime;
        this.fromdate = fromdate;
        this.todate = todate;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the name
     */
    public String getName() {
        return name;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  name  the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the generatorname
     */
    public String getGeneratorname() {
        return generatorname;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  generatorname  the generatorname to set
     */
    public void setGeneratorname(final String generatorname) {
        this.generatorname = generatorname;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the staff
     */
    public StaffDTO getStaff() {
        return staff;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff  the staff to set
     */
    public void setStaff(final StaffDTO staff) {
        this.staff = staff;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the creationtime
     */
    public Date getCreationtime() {
        return creationtime;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  creationtime  the creationtime to set
     */
    public void setCreationtime(final Date creationtime) {
        this.creationtime = creationtime;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the fromdate
     */
    public Date getFromdate() {
        return fromdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fromdate  the fromdate to set
     */
    public void setFromdate(final Date fromdate) {
        this.fromdate = fromdate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the todate
     */
    public Date getTodate() {
        return todate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  todate  the todate to set
     */
    public void setTodate(final Date todate) {
        this.todate = todate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  an isntance of the type ReportShort, that contains the same data as this object
     */
    @Override
    public BasicDTO createCopy() {
        return new ReportDTO(id, name, generatorname, staff, creationtime, fromdate, todate);
    }

    /**
     * copies the content of the given object in this object.
     *
     * @param  obj  an instance of the type ReportShort
     */
    @Override
    public void reset(final BasicDTO obj) {
        final ReportDTO rep = (ReportDTO)obj;
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
        // todo: String auslagern
        return name + " erstellt am: " + DateHelper.formatDate(creationtime);
    }
}
