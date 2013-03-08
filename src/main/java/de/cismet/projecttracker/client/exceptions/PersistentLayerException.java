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
public class PersistentLayerException extends Exception implements IsSerializable {

    public PersistentLayerException() {
        super();
    }

    public PersistentLayerException(String message) {
        super(message);
    }

    public PersistentLayerException(Throwable cause) {
        super(cause);
    }

    public PersistentLayerException(String message, Throwable cause) {
        super(message, cause);
    }
}
