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
 *
 * @author therter
 */
public class EMailContent {

    private String address;
    private String subject;
    private String text;
    private GregorianCalendar timeToSend;
    private String prefix;
    private String suffix;

    public EMailContent() {
        //parse the html file...
        InputStreamReader reader = new InputStreamReader(EMailContent.class.getResourceAsStream("/de/cismet/projecttracker/utilities/activityChangedMail.html"));
        BufferedReader br = new BufferedReader(reader);
        StringBuilder file = null;
         String line;
        try {
            line = br.readLine();
       
        file = new StringBuilder();
        while(line != null){
            file.append(line);
            line = br.readLine();
        }
         } catch (IOException ex) {
            Logger.getLogger(EMailContent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String[] splitFile = file.toString().split("<!--insert here-->");
        prefix = splitFile[0];
        suffix = splitFile[1];
        
    }

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
        return prefix+text+suffix;
    }

    public String getText(){
        return text;
    }
    
    public void setText(String text){
        this.text = text;
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
