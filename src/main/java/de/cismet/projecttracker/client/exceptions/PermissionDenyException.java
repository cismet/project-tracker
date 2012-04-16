package de.cismet.projecttracker.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author therter
 */
public class PermissionDenyException extends Exception implements IsSerializable {

    public PermissionDenyException() {
        super();
    }

    public PermissionDenyException(String message) {
        super(message);
    }

    public PermissionDenyException(Throwable cause) {
        super(cause);
    }

    public PermissionDenyException(String message, Throwable cause) {
        super(message, cause);
    }
}
