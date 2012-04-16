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
public class ReportNotFoundException extends Exception implements IsSerializable {

    public ReportNotFoundException() {
        super();
    }

    public ReportNotFoundException(String message) {
        super(message);
    }

    public ReportNotFoundException(Throwable cause) {
        super(cause);
    }

    public ReportNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
