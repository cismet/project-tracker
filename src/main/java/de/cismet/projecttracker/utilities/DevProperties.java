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
package de.cismet.projecttracker.utilities;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class DevProperties {

    //~ Static fields/initializers ---------------------------------------------

    private static DevProperties instance;

    //~ Instance fields --------------------------------------------------------

    private boolean devMode;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DevProperties object.
     */
    private DevProperties() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static DevProperties getInstance() {
        if (instance == null) {
            instance = new DevProperties();
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public boolean isDevMode() {
        return devMode;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  devMode  DOCUMENT ME!
     */
    public void setDevMode(final boolean devMode) {
        this.devMode = devMode;
    }
}
