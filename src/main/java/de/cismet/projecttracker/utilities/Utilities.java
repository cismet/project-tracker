/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.utilities;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.cismet.projecttracker.server.ConfigurationManager;

/**
 * This class privides some static methods with basic functionality.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class Utilities {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(Utilities.class);
    private static final String LOG4J_CONFIG_FILE = "WEB-INF/config/log4j.properties";
    private static final String ADMIN_MAIL_ADDRESS = "sabine.trier@cismet.de";
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

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new Utilities object.
     */
    public Utilities() {
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     */
    private static synchronized void checkCollectedEmails() {
        final GregorianCalendar now = new GregorianCalendar();
        final List<String> keys = new ArrayList<String>(toSend.keySet());

        for (final String address : keys) {
            final EMailContent c = toSend.get(address);
            if (c.getTimeToSend().before(now)) {
                sendEmail(c.getAddress(), c.getSubject(), c.getBody());
                toSend.remove(address);
            }
        }
    }

    /**
     * initialize the LOG4J Logger.
     *
     * @param  applicationPath  DOCUMENT ME!
     */
    public static void initLogger(final String applicationPath) {
        PropertyConfigurator.configureAndWatch(applicationPath + LOG4J_CONFIG_FILE);
    }

    /**
     * Send a single email.
     *
     * @param  address  DOCUMENT ME!
     * @param  subject  DOCUMENT ME!
     * @param  body     DOCUMENT ME!
     */
    public static void sendEmail(final String address, final String subject, final String body) {
        fetchConfig();
        final Session session = Session.getDefaultInstance(fMailServerConfig, null);
        final MimeMessage message = new MimeMessage(session);
        try {
            // the "from" address may be set in code, or set in the
            // config file under "mail.from" ; here, the latter style is used
            // message.setFrom( new InternetAddress(aFromEmailAddr) );
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
            if (!address.equals(ADMIN_MAIL_ADDRESS)) {
                message.addRecipient(Message.RecipientType.BCC, new InternetAddress(ADMIN_MAIL_ADDRESS));
            }
            message.setSubject(subject);
//            message.setText(body);
            message.setContent(body, "text/html");
//            Transport.send(message);
        } catch (MessagingException ex) {
            logger.error("Cannot send email.", ex);
        }
    }

    /**
     * Send a single email.
     *
     * @param  address  DOCUMENT ME!
     * @param  subject  DOCUMENT ME!
     * @param  body     DOCUMENT ME!
     */
    public static synchronized void sendCollectedEmail(final String address, final String subject, final String body) {
        EMailContent mail = toSend.get(address);
        final GregorianCalendar time = new GregorianCalendar();
        time.add(GregorianCalendar.MINUTE, 5);

        if (mail == null) {
            mail = new EMailContent();
            mail.setAddress(address);
            mail.setSubject(subject);
            mail.setText(body);
            toSend.put(address, mail);
        } else {
            mail.setText(mail.getText() + "\n\n\n" + body);
        }

//        mail.setTimeToSend(time);
        mail.setTimeToSend(new GregorianCalendar());
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
