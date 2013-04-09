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
public class PermissionDenyException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new PermissionDenyException object.
     */
    public PermissionDenyException() {
        super();
    }

    /**
     * Creates a new PermissionDenyException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public PermissionDenyException(final String message) {
        super(message);
    }

    /**
     * Creates a new PermissionDenyException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public PermissionDenyException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new PermissionDenyException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public PermissionDenyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
