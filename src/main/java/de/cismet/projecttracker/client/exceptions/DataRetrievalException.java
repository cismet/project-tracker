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
public class DataRetrievalException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new DataRetrievalException object.
     */
    public DataRetrievalException() {
        super();
    }

    /**
     * Creates a new DataRetrievalException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public DataRetrievalException(final String message) {
        super(message);
    }

    /**
     * Creates a new DataRetrievalException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public DataRetrievalException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new DataRetrievalException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public DataRetrievalException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
