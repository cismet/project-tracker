/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class EMailContent {

    //~ Instance fields --------------------------------------------------------

    private String address;
    private String subject;
    private String text;
    private GregorianCalendar timeToSend;
    private String prefix;
    private String suffix;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new EMailContent object.
     */
    public EMailContent() {
        // parse the html file...
        final InputStreamReader reader = new InputStreamReader(EMailContent.class.getResourceAsStream(
                    "/de/cismet/projecttracker/utilities/activityChangedMail.html"));
        final BufferedReader br = new BufferedReader(reader);
        StringBuilder file = null;
        String line;
        try {
            line = br.readLine();

            file = new StringBuilder();
            while (line != null) {
                file.append(line);
                line = br.readLine();
            }
        } catch (IOException ex) {
            Logger.getLogger(EMailContent.class.getName()).log(Level.SEVERE, null, ex);
        }

        final String[] splitFile = file.toString().split("<!--insert here-->");
        prefix = splitFile[0];
        suffix = splitFile[1];
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  address  the address to set
     */
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subject  the subject to set
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the body
     */
    public String getBody() {
        return prefix + text + suffix;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getText() {
        return text;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  text  DOCUMENT ME!
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the timeToSend
     */
    public GregorianCalendar getTimeToSend() {
        return timeToSend;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  timeToSend  the timeToSend to set
     */
    public void setTimeToSend(final GregorianCalendar timeToSend) {
        this.timeToSend = timeToSend;
    }
}
