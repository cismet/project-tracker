/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.client.common.ui.report;

import com.google.gwt.user.client.Random;

/**
 * A StaffSummaryEntry containts the data that represents a row of the data grid.
 *
 * @version  $Revision$, $Date$
 */
public class StaffSummaryEntry {

    //~ Instance fields --------------------------------------------------------

    public String iconUrl;
    public final String staffName;
    public final String wpName;
    public final double wh;
    public final Integer id;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new StaffSummaryEntry object.
     *
     * @param  url        DOCUMENT ME!
     * @param  staffName  DOCUMENT ME!
     * @param  wpName     DOCUMENT ME!
     * @param  wh         DOCUMENT ME!
     */
    public StaffSummaryEntry(final String url, final String staffName, final String wpName, final double wh) {
        this.iconUrl = url;
        this.staffName = staffName;
        this.wpName = wpName;
        this.wh = wh;
        this.id = Random.nextInt(Integer.MAX_VALUE);
    }
}
