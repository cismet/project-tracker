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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;

import java.security.MessageDigest;

import java.util.Date;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;

import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.query.DBManager;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class QuickBooking extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(QuickBooking.class);
    private static final String PRESENT_RESPONSE = "anwesend";
    private static final String ABSENT_RESPONSE = "abwesend";

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
        final String operation = request.getParameter("operation");
        final DBManager dbManager = new DBManager();
        final ServletOutputStream out = response.getOutputStream();

        try {
            final Staff staff = checklogin(username, password, request.getSession(), dbManager);

            if (staff != null) {
                if (operation.equals("changeStatus")) {
                    changeStatus(staff, dbManager);
                } else if (operation.toLowerCase().equals("kommen")) {
                    addCome(staff, dbManager, response);
                } else if (operation.toLowerCase().equals("gehen")) {
                    addGo(staff, dbManager, response);
                } else if (operation.toLowerCase().equals("status")) {
                    final String status = status(staff, dbManager);

                    if (status != null) {
                        out.print(status);
                    } else {
                        out.print(ABSENT_RESPONSE);
                    }
                } else {
                    response.setStatus(400);
                    out.print("No valid operation found.");
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
     * DOCUMENT ME!
     *
     * @param  staff      DOCUMENT ME!
     * @param  dbManager  DOCUMENT ME!
     */
    private void changeStatus(final Staff staff, final DBManager dbManager) {
        final Object lastAct = dbManager.getSession()
                    .createCriteria(Activity.class)
                    .add(Restrictions.eq("staff", staff))
                    .add(Restrictions.or(
                                Restrictions.eq("kindofactivity", ActivityDTO.BEGIN_OF_DAY),
                                Restrictions.eq("kindofactivity", ActivityDTO.END_OF_DAY)))
                    .addOrder(Order.desc("day"))
                    .setMaxResults(1)
                    .uniqueResult();

        if (lastAct instanceof Activity) {
            final Activity act = new Activity();
            act.setKindofactivity((((Activity)lastAct).getKindofactivity() % 2) + 1);
            act.setStaff(staff);
            act.setDay(new Date());

            dbManager.createObject(act);
            refreshModification(staff, dbManager);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff      DOCUMENT ME!
     * @param  dbManager  DOCUMENT ME!
     */
    private void refreshModification(final Staff staff, final DBManager dbManager) {
        staff.setLastmodification(new Date());
        dbManager.saveObject(staff);
    }

    /**
     * DOCUMENT ME!
     *
     * @param   staff      DOCUMENT ME!
     * @param   dbManager  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private String status(final Staff staff, final DBManager dbManager) {
        final Object lastAct = dbManager.getSession()
                    .createCriteria(Activity.class)
                    .add(Restrictions.eq("staff", staff))
                    .add(Restrictions.or(
                                Restrictions.eq("kindofactivity", ActivityDTO.BEGIN_OF_DAY),
                                Restrictions.eq("kindofactivity", ActivityDTO.END_OF_DAY)))
                    .addOrder(Order.desc("day"))
                    .setMaxResults(1)
                    .uniqueResult();

        if (lastAct instanceof Activity) {
            if (((Activity)lastAct).getKindofactivity() == ActivityDTO.BEGIN_OF_DAY) {
                return PRESENT_RESPONSE;
            } else {
                return ABSENT_RESPONSE;
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff      DOCUMENT ME!
     * @param  dbManager  DOCUMENT ME!
     * @param  response   DOCUMENT ME!
     */
    private void addCome(final Staff staff, final DBManager dbManager, final HttpServletResponse response) {
        final String currStatus = status(staff, dbManager);
        if (currStatus.equals(ABSENT_RESPONSE)) {
            final Object lastAct = dbManager.getSession()
                        .createCriteria(Activity.class)
                        .add(Restrictions.eq("staff", staff))
                        .add(Restrictions.or(
                                    Restrictions.eq("kindofactivity", ActivityDTO.BEGIN_OF_DAY),
                                    Restrictions.eq("kindofactivity", ActivityDTO.END_OF_DAY)))
                        .addOrder(Order.desc("day"))
                        .setMaxResults(1)
                        .uniqueResult();
            if (lastAct instanceof Activity) {
                final Activity tmp = (Activity)lastAct;
                final Date helper = new Date();
                final Date d = new Date(helper.getYear(),
                        helper.getMonth(),
                        helper.getDate(),
                        helper.getHours(),
                        helper.getMinutes());
                if (tmp.getDay().before(d)) {
                    final Activity act = new Activity();
                    act.setKindofactivity(ActivityDTO.BEGIN_OF_DAY);
                    act.setStaff(staff);
                    act.setDay(new Date());

                    dbManager.createObject(act);
                    refreshModification(staff, dbManager);
                    return;
                }
            }
        }

        response.setStatus(400);
        final ServletOutputStream out;
        try {
            out = response.getOutputStream();
            out.print("The last activity is already a come booking.");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(QuickBooking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param  staff      DOCUMENT ME!
     * @param  dbManager  DOCUMENT ME!
     * @param  response   DOCUMENT ME!
     */
    private void addGo(final Staff staff, final DBManager dbManager, final HttpServletResponse response) {
        final String stat = status(staff, dbManager);
        if (stat.equals(PRESENT_RESPONSE)) {
            final Activity act = new Activity();
            act.setKindofactivity(ActivityDTO.END_OF_DAY);
            act.setStaff(staff);
            act.setDay(new Date());

            dbManager.createObject(act);
            refreshModification(staff, dbManager);
            return;
        }

        response.setStatus(400);
        final ServletOutputStream out;
        try {
            out = response.getOutputStream();
            out.print("The last activity is already a go booking");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(QuickBooking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * DOCUMENT ME!
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
    public Staff checklogin(final String username,
            final String pasword,
            final HttpSession session,
            final DBManager dbManager) throws LoginFailedException, DataRetrievalException {
        try {
            final Session hibernateSession = dbManager.getSession();

            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());

//            Staff staff = (Staff) hibernateSession.createCriteria(Staff.class).add(Restrictions.eq("username", username)).uniqueResult();
            final Staff staff = (Staff)hibernateSession.createCriteria(Staff.class)
                        .add(Restrictions.and(
                                    Restrictions.eq("username", username),
                                    Restrictions.eq("password", md.digest())))
                        .uniqueResult();

            return staff;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        }
    }
}
