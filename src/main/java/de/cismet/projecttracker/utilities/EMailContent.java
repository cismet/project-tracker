/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.utilities;

import java.util.GregorianCalendar;

/**
 *
 * @author therter
 */
public class EMailContent {
    private String address;
    private String subject;
    private String body;
    private GregorianCalendar timeToSend;

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the timeToSend
     */
    public GregorianCalendar getTimeToSend() {
        return timeToSend;
    }

    /**
     * @param timeToSend the timeToSend to set
     */
    public void setTimeToSend(GregorianCalendar timeToSend) {
        this.timeToSend = timeToSend;
    }
}
