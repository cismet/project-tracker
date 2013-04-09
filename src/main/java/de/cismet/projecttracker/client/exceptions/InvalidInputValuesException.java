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
 * This exception should be thrown, if there are some problems with the clone or merge method of the DTOManager.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class InvalidInputValuesException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new InvalidInputValuesException object.
     */
    public InvalidInputValuesException() {
        super();
    }

    /**
     * Creates a new InvalidInputValuesException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public InvalidInputValuesException(final String message) {
        super(message);
    }

    /**
     * Creates a new InvalidInputValuesException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public InvalidInputValuesException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new InvalidInputValuesException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public InvalidInputValuesException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
