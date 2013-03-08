package de.cismet.projecttracker.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This exception should be thrown, if the user want to create oir update an activity,
 * but the projectcomponent of this activity is overbooked. The message of instances of this
 * exception should contain the name of the overbooked work package.
 *
 * @author therter
 */
public class FullStopException extends Exception implements IsSerializable {
    public FullStopException() {
        super();
    }

    public FullStopException(String message) {
        super(message);
    }

    public FullStopException(Throwable cause) {
        super(cause);
    }

    public FullStopException(String message, Throwable cause) {
        super(message, cause);
    }
}
