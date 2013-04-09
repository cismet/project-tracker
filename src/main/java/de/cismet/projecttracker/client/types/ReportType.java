/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.types;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class represents a report generator.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ReportType implements IsSerializable {

    //~ Instance fields --------------------------------------------------------

    private String name;
    private boolean userRelated;
    private boolean notUserRelated;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new ReportType object.
     */
    public ReportType() {
    }

    /**
     * Creates a new ReportType object.
     *
     * @param  name            DOCUMENT ME!
     * @param  userRelated     DOCUMENT ME!
     * @param  notUserRelated  DOCUMENT ME!
     */
    public ReportType(final String name, final boolean userRelated, final boolean notUserRelated) {
        this.name = name;
        this.userRelated = userRelated;
        this.notUserRelated = notUserRelated;
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
     * @return  the userRelated
     */
    public boolean isUserRelated() {
        return userRelated;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  userRelated  the userRelated to set
     */
    public void setUserRelated(final boolean userRelated) {
        this.userRelated = userRelated;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the notUserRelated
     */
    public boolean isNotUserRelated() {
        return notUserRelated;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  notUserRelated  the notUserRelated to set
     */
    public void setNotUserRelated(final boolean notUserRelated) {
        this.notUserRelated = notUserRelated;
    }
}
