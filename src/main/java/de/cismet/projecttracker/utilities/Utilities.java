package de.cismet.projecttracker.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * This class privides some static methods with basic functionality.
 * @author therter
 */
public class Utilities {
    private static final Logger logger = Logger.getLogger(Utilities.class);
    private final static String LOG4J_CONFIG_FILE = "WEB-INF/config/log4j.properties";
    private static Properties fMailServerConfig = new Properties();


    /**
     * initialize the LOG4J Logger
     */
    public static void initLogger(String applicationPath) {
        PropertyConfigurator.configureAndWatch(applicationPath + LOG4J_CONFIG_FILE);
    }


    /**
     * Send a single email.
     */
    public static void sendEmail(String address, String subject, String body) {
        fetchConfig();
        Session session = Session.getDefaultInstance(fMailServerConfig, null);
        MimeMessage message = new MimeMessage(session);
        try {
            //the "from" address may be set in code, or set in the
            //config file under "mail.from" ; here, the latter style is used
            //message.setFrom( new InternetAddress(aFromEmailAddr) );
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException ex) {
            logger.error("Cannot send email.", ex);
        }
    }


    /**
     * Open a specific text file containing mail server
     * parameters, and populate a corresponding Properties object.
     */
    private static void fetchConfig() {
        InputStream input = null;

        try {
            input = Utilities.class.getResourceAsStream("/de/cismet/projecttracker/utilities/EMail.properties");
            fMailServerConfig.load(input);
        } catch (IOException e) {
            logger.error("Cannot open and load mail server properties file.", e);
        }
    }
}
