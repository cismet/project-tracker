/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This exception will be thrown, if the user has no valid Session.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class NoSessionException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new NoSessionException object.
     */
    public NoSessionException() {
        super();
    }

    /**
     * Creates a new NoSessionException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public NoSessionException(final String message) {
        super(message);
    }

    /**
     * Creates a new NoSessionException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public NoSessionException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new NoSessionException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public NoSessionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
