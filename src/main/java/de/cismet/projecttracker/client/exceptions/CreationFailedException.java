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
public class CreationFailedException extends Exception implements IsSerializable {

    public CreationFailedException() {
        super();
    }

    public CreationFailedException(String message) {
        super(message);
    }

    public CreationFailedException(Throwable cause) {
        super(cause);
    }

    public CreationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
