package de.cismet.projecttracker.server;

import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;
import de.cismet.projecttracker.utilities.LanguageBundle;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * This servlet should used as basic class of all servlet classes.
 * This class implements some basic methods for permission handling.
 *
 * @author therter
 */
public class BasicServlet extends HttpServlet {

    /**
     * checks if the current user has admin rights
     * @throws PermissionDenyException if the current user has no admin rights
     */
    protected void checkAdminPermission(HttpServletRequest request) throws PermissionDenyException, NoSessionException {
        try {
            SessionInformation sessionInfo = getCurrentSession(request);

            if ( !sessionInfo.isAdmin() ) {
                throw new PermissionDenyException( LanguageBundle.ONLY_ALLOWED_FOR_ADMIN );
            }
        } catch (NoSessionException e) {
            throw e;
        } catch (Throwable th) {
            throw new PermissionDenyException( LanguageBundle.ONLY_ALLOWED_FOR_ADMIN );
        }
    }

    /**
     * @param request
     * @return the session object of the current user
     * @throws NoSessionException Will be thrown if no session was found
     */
    protected SessionInformation getCurrentSession(HttpServletRequest request) throws NoSessionException {
        if (request.getSession() == null) {
            throw new NoSessionException();
        }

        SessionInformation sessionInfo = (SessionInformation)request.getSession().getAttribute("sessionInfo");

        if (sessionInfo == null) {
            throw new NoSessionException();
        }

        return sessionInfo;
    }
}
