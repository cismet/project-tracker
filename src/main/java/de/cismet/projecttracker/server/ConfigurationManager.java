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
package de.cismet.projecttracker.server;

import javax.servlet.ServletContext;

/**
 * DOCUMENT ME!
 *
 * @author   daniel
 * @version  $Revision$, $Date$
 */
public class ConfigurationManager {

    //~ Static fields/initializers ---------------------------------------------

    private static ConfigurationManager instance;

    //~ Instance fields --------------------------------------------------------

    private ServletContext servletContext = null;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ConfigurationManager object.
     */
    private ConfigurationManager() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  context  DOCUMENT ME!
     */
    public void setContext(final ServletContext context) {
        servletContext = context;
    }
    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getConfBaseDir() {
        return servletContext.getInitParameter("confBaseDir");
    }
}
