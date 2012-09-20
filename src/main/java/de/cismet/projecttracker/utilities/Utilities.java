package de.cismet.projecttracker.utilities;

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
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
 *
 * @author therter
 */
public class Utilities {

    private static final Logger logger = Logger.getLogger(Utilities.class);
    private final static String LOG4J_CONFIG_FILE = "WEB-INF/config/log4j.properties";
    private final static String ADMIN_MAIL_ADDRESS = "sabine.trier@cismet.de";
    private static Properties fMailServerConfig = new Properties();
    private static final Map<String, EMailContent> toSend = new Hashtable<String, EMailContent>();
    private static final Timer sendTimer;

    static {
        sendTimer = new Timer(true);
        sendTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                checkCollectedEmails();
            }
        }, 300000, 60000);
    }

    public Utilities() {
    }

    private static synchronized void checkCollectedEmails() {
        GregorianCalendar now = new GregorianCalendar();
        List<String> keys = new ArrayList<String>(toSend.keySet());

        for (String address : keys) {
            EMailContent c = toSend.get(address);
            if (c.getTimeToSend().before(now)) {
                sendEmail(c.getAddress(), c.getSubject(), c.getBody());
                toSend.remove(address);
            }
        }
    }

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
//            message.addRecipient(Message.RecipientType.BCC, new InternetAddress(ADMIN_MAIL_ADDRESS));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
        } catch (MessagingException ex) {
            logger.error("Cannot send email.", ex);
        }
    }

    /**
     * Send a single email.
     */
    public static synchronized void sendCollectedEmail(String address, String subject, String body) {
        EMailContent mail = toSend.get(address);
        GregorianCalendar time = new GregorianCalendar();
        time.add(GregorianCalendar.MINUTE, 5);

        if (mail == null) {
            mail = new EMailContent();
            mail.setAddress(address);
            mail.setSubject(subject);
            mail.setBody(body);
            toSend.put(address, mail);
        } else {
            mail.setBody(mail.getBody() + "\n\n\n" + body);
        }

        mail.setTimeToSend(time);
    }

    /**
     * Open a specific text file containing mail server parameters, and populate a corresponding Properties object.
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
