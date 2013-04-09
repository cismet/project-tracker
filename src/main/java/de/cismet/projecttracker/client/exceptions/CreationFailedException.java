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
public class CreationFailedException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new CreationFailedException object.
     */
    public CreationFailedException() {
        super();
    }

    /**
     * Creates a new CreationFailedException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public CreationFailedException(final String message) {
        super(message);
    }

    /**
     * Creates a new CreationFailedException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public CreationFailedException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new CreationFailedException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public CreationFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
