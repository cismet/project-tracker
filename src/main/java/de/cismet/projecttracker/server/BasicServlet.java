/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;

import de.cismet.projecttracker.utilities.LanguageBundle;

/**
 * This servlet should used as basic class of all servlet classes. This class implements some basic methods for
 * permission handling.
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class BasicServlet extends HttpServlet {

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
}
