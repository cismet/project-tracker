package de.cismet.projecttracker.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This exception should be thrown, if there are some problems with the clone or merge method of the DTOManager.
 * @author therter
 */
public class InvalidInputValuesException extends Exception implements IsSerializable {

    public InvalidInputValuesException() {
        super();
    }

    public InvalidInputValuesException(String message) {
        super(message);
    }

    public InvalidInputValuesException(Throwable cause) {
        super(cause);
    }

    public InvalidInputValuesException(String message, Throwable cause) {
        super(message, cause);
    }
}
