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
 * This exception should be thrown, if the user want to create oir update an activity, but the projectcomponent of this
 * activity is overbooked. The message of instances of this exception should contain the name of the overbooked work
 * package.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class FullStopException extends Exception implements IsSerializable {

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new FullStopException object.
     */
    public FullStopException() {
        super();
    }

    /**
     * Creates a new FullStopException object.
     *
     * @param  message  DOCUMENT ME!
     */
    public FullStopException(final String message) {
        super(message);
    }

    /**
     * Creates a new FullStopException object.
     *
     * @param  cause  DOCUMENT ME!
     */
    public FullStopException(final Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new FullStopException object.
     *
     * @param  message  DOCUMENT ME!
     * @param  cause    DOCUMENT ME!
     */
    public FullStopException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
