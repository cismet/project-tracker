/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.server;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.StaffExtern;
import de.cismet.projecttracker.report.query.DBManager;

import de.cismet.projecttracker.utilities.LanguageBundle;
import java.security.MessageDigest;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 * This servlet should used as basic class of all servlet classes. This class implements some basic methods for
 * permission handling.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class BasicServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(BasicServlet.class);

    //~ Methods ----------------------------------------------------------------

    /**
     * checks if the current user has admin rights.
     *
     * @param   request  DOCUMENT ME!
     *
     * @throws  PermissionDenyException  if the current user has no admin rights
     * @throws  NoSessionException       DOCUMENT ME!
     */
    protected void checkAdminPermission(final HttpServletRequest request) throws PermissionDenyException,
        NoSessionException {
        try {
            final SessionInformation sessionInfo = getCurrentSession(request);

            if (!sessionInfo.isAdmin()) {
                throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
            }
        } catch (NoSessionException e) {
            throw e;
        } catch (Throwable th) {
            throw new PermissionDenyException(LanguageBundle.ONLY_ALLOWED_FOR_ADMIN);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   request  DOCUMENT ME!
     *
     * @return  the session object of the current user
     *
     * @throws  NoSessionException  Will be thrown if no session was found
     */
    protected SessionInformation getCurrentSession(final HttpServletRequest request) throws NoSessionException {
        if (request.getSession() == null) {
            throw new NoSessionException();
        }

        final SessionInformation sessionInfo = (SessionInformation)request.getSession().getAttribute("sessionInfo");

        if (sessionInfo == null) {
            throw new NoSessionException();
        }

        return sessionInfo;
    }
    
    /**
     * Check the login data.
     *
     * @param   username   DOCUMENT ME!
     * @param   pasword    DOCUMENT ME!
     * @param   session    DOCUMENT ME!
     * @param   dbManager  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  LoginFailedException    DOCUMENT ME!
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    public Object checklogin(final String username,
            final String pasword,
            final HttpSession session,
            final DBManager dbManager) throws LoginFailedException, DataRetrievalException {
        try {
            final Session hibernateSession = dbManager.getSession();

            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());
            final byte[] sha1 = md.digest();

            final Staff staff = (Staff)hibernateSession.createCriteria(Staff.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("username", username),
                                    Restrictions.eq("password", sha1)))
                        .uniqueResult();

            if (staff == null) {
                final StaffExtern staffExtern = (StaffExtern)hibernateSession.createCriteria(StaffExtern.class)
                            .add(Restrictions.and(
                                        Restrictions.eq("username", username),
                                        Restrictions.eq("password", sha1)))
                            .uniqueResult();
//                final StaffExtern staffExtern = (StaffExtern)hibernateSession.createCriteria(StaffExtern.class)
//                            .add(Restrictions.eq("username", username))
//                            .uniqueResult();

                return staffExtern;
            }
            return staff;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        }
    }
    
    protected Staff getStaff(final String username, final DBManager dbManager) throws LoginFailedException, DataRetrievalException {
        try {
            final Session hibernateSession = dbManager.getSession();


            Staff staff = (Staff) hibernateSession.createCriteria(Staff.class).add(Restrictions.eq("username", username)).uniqueResult();

            return staff;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        }
    }
}
