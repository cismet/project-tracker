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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.apache.log4j.Logger;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.io.PrintWriter;

import java.security.MessageDigest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;

import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.StaffExtern;
import de.cismet.projecttracker.report.db.entities.WorkPackage;
import de.cismet.projecttracker.report.query.DBManager;

import de.cismet.projecttracker.utilities.DTOManager;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class Lookup extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(Lookup.class);

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

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
        IOException {
        resp.addHeader("Access-Control-Allow-Origin", "*");                                // NOI18N
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS"); // NOI18N
        resp.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");     // NOI18N

        super.service(req, resp); // To change body of generated methods, choose Tools | Templates.
    }

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
        final String operation = request.getParameter("operation");
        final DBManager dbManager = new DBManager(ConfigurationManager.getInstance().getConfBaseDir());
        final PrintWriter out = response.getWriter();

        try {
            final Object staff = checklogin(username, password, request.getSession(), dbManager);

            if (staff != null) {
                if (operation.equalsIgnoreCase("searchableWorkPackages")) {
                    List<WorkPackage> wpList = null;
                    final DTOManager dtoManager = new DTOManager();

                    if (staff instanceof StaffExtern) {
                        wpList = new ArrayList<WorkPackage>(((StaffExtern)staff).getWorkpackages());
                    } else {
                        wpList = getAllWorkPackages((Staff)staff, dbManager);
                    }

                    response.setCharacterEncoding("UTF-8");
                    final ObjectMapper mapper = new ObjectMapper();
                    final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                    final String json = writer.writeValueAsString(dtoManager.clone(wpList));
                    out.print(json);
                } else {
                    response.setStatus(400);
                    out.print("no valid operation");
                }
            } else {
                response.setStatus(400);
                out.print("The username/password is not correct.");
            }
        } catch (Exception e) {
            logger.error("login error", e);
            e.printStackTrace();
            response.setStatus(400);
            out.print(e.getMessage());
        } finally {
            dbManager.closeSession();
            out.close();
        }
    }

    /**
     * Determines all activities, which fulfil the given criterias.
     *
     * @param   user       staff the staff, whose activities should be retrieved
     * @param   dbManager  DOCUMENT ME!
     *
     * @return  all activities, which fulfil the given criterias
     *
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     * @throws  DataRetrievalException       DOCUMENT ME!
     * @throws  PermissionDenyException      DOCUMENT ME!
     * @throws  NoSessionException           DOCUMENT ME!
     */
    private List<WorkPackage> getAllWorkPackages(final Staff user,
            final DBManager dbManager) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get workpackages: ");
        }

        try {
            final List<WorkPackage> result = (List<WorkPackage>)dbManager.getAllObjects(WorkPackage.class);
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " workpackages found");
            }

            return result;
        } catch (Exception e) {
            logger.error("Error while retrieving workpackages");
            return new ArrayList();
        }
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
}
