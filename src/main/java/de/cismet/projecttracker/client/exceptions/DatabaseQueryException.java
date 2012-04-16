/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.cismet.projecttracker.client.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author therter
 */
public class DatabaseQueryException extends Exception implements IsSerializable {

    public DatabaseQueryException() {
        super();
    }

    public DatabaseQueryException(String message) {
        super(message);
    }

    public DatabaseQueryException(Throwable cause) {
        super(cause);
    }

    public DatabaseQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
