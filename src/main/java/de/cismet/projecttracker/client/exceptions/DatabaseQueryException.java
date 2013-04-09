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
package de.cismet.projecttracker.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DatabaseQueryException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DatabaseQueryException object.
     */
    public DatabaseQueryException() {
        super();
    }

    /**
     * Creates a new DatabaseQueryException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public DatabaseQueryException(final String message) {
        super(message);
    }

    /**
     * Creates a new DatabaseQueryException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public DatabaseQueryException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new DatabaseQueryException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public DatabaseQueryException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
