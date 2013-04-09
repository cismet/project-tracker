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
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LoginFailedException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new LoginFailedException object.
     */
    public LoginFailedException() {
        super();
    }

    /**
     * Creates a new LoginFailedException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public LoginFailedException(final String message) {
        super(message);
    }

    /**
     * Creates a new LoginFailedException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public LoginFailedException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new LoginFailedException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public LoginFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
