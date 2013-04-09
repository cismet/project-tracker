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
package de.cismet.projecttracker.server;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.io.PrintWriter;

import java.security.MessageDigest;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;

import de.cismet.projecttracker.report.db.entities.Staff;

import de.cismet.projecttracker.utilities.DBManagerWrapper;
import de.cismet.projecttracker.utilities.LanguageBundle;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class LoginServlet extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(LoginServlet.class);

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param   request   servlet request
     * @param   response  servlet response
     *
     * @throws  ServletException  if a servlet-specific error occurs
     * @throws  IOException       if an I/O error occurs
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param   request   servlet request
     * @param   response  servlet response
     *
     * @throws  ServletException  if a servlet-specific error occurs
     * @throws  IOException       if an I/O error occurs
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return  a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    } // </editor-fold>

    //~ Methods ----------------------------------------------------------------

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param   request   servlet request
     * @param   response  servlet response
     *
     * @throws  ServletException  if a servlet-specific error occurs
     * @throws  IOException       if an I/O error occurs
     */
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        final String username = request.getParameter("username");
        final String password = request.getParameter("password");
//        ServletOutputStream out  = response.getOutputStream();

        try {
            login(username, password, request.getSession());
//            out.print("ok");
        } catch (Exception e) {
            logger.error("login error", e);
//            response.sendRedirect( response.encodeRedirectURL("ProjectTracker.html") );
        } finally {
//            out.close();
        }
        response.sendRedirect(response.encodeRedirectURL("ProjectTracker.html"));
    }

    /**
     * {@inheritDoc}
     */
    public void login(final String username, final String pasword, final HttpSession session)
            throws LoginFailedException, DataRetrievalException {
        Session hibernateSession = null;
        final DBManagerWrapper dbManager = new DBManagerWrapper();

        try {
            hibernateSession = dbManager.getSession();
            if (logger.isDebugEnabled()) {
                logger.debug(username + " sends login request");
            }
            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());

            final Staff staff = (Staff)hibernateSession.createCriteria(Staff.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("username", username),
                                    Restrictions.eq("password", md.digest())))
                        .uniqueResult();

            if (staff != null) {
                final SessionInformation sessionInfo = new SessionInformation();
                sessionInfo.setCurrentUser(staff);
                session.setAttribute("sessionInfo", sessionInfo);
            } else {
                throw new LoginFailedException(LanguageBundle.LOGIN_FAILED);
            }
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }
}
