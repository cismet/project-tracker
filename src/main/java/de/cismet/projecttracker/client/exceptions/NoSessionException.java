package de.cismet.projecttracker.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This exception will be thrown, if the user has no valid Session.
 * 
 * @author therter
 */
public class NoSessionException extends Exception implements IsSerializable {
    public NoSessionException() {
        super();
    }

    public NoSessionException(String message) {
        super(message);
    }

    public NoSessionException(Throwable cause) {
        super(cause);
    }

    public NoSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
