/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.cismet.projecttracker.server;

import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;
import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.query.DBManager;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author therter
 */
public class QuickBooking extends BasicServlet {

    private static final Logger logger = Logger.getLogger(QuickBooking.class);
    private static final String PRESENT_RESPONSE = "anwesend";
    private static final String ABSENT_RESPONSE = "abwesend";

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String operation = request.getParameter("operation");
        DBManager dbManager = new DBManager();
        ServletOutputStream out = response.getOutputStream();

        try {
            Staff staff = checklogin(username, password, request.getSession(), dbManager);

            if (staff != null) {
                if (operation.equals("changeStatus")) {
                    changeStatus(staff, dbManager);
                } else if (operation.toLowerCase().equals("kommen")) {
                    addCome(staff, dbManager, response);
                } else if (operation.toLowerCase().equals("gehen")) {
                    addGo(staff, dbManager, response);
                } else if (operation.toLowerCase().equals("status")) {
                    String status = status(staff, dbManager);

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

    private void changeStatus(Staff staff, DBManager dbManager) {
        Object lastAct = dbManager.getSession().createCriteria(Activity.class).add(Restrictions.eq("staff", staff)).add(Restrictions.or(Restrictions.eq("kindofactivity", ActivityDTO.BEGIN_OF_DAY), Restrictions.eq("kindofactivity", ActivityDTO.END_OF_DAY))).addOrder(Order.desc("day")).setMaxResults(1).uniqueResult();

        if (lastAct instanceof Activity) {
            Activity act = new Activity();
            act.setKindofactivity((((Activity) lastAct).getKindofactivity() % 2) + 1);
            act.setStaff(staff);
            act.setDay(new Date());

            dbManager.createObject(act);
            refreshModification(staff, dbManager);
        }
    }

    private void refreshModification(Staff staff, DBManager dbManager) {
        staff.setLastmodification(new Date());
        dbManager.saveObject(staff);
    }

    private String status(Staff staff, DBManager dbManager) {
        Object lastAct = dbManager.getSession().createCriteria(Activity.class).add(Restrictions.eq("staff", staff)).add(Restrictions.or(Restrictions.eq("kindofactivity", ActivityDTO.BEGIN_OF_DAY), Restrictions.eq("kindofactivity", ActivityDTO.END_OF_DAY))).addOrder(Order.desc("day")).setMaxResults(1).uniqueResult();

        if (lastAct instanceof Activity) {
            if (((Activity) lastAct).getKindofactivity() == ActivityDTO.BEGIN_OF_DAY) {
                return PRESENT_RESPONSE;
            } else {
                return ABSENT_RESPONSE;
            }
        }

        return null;
    }

    private void addCome(Staff staff, DBManager dbManager, HttpServletResponse response) {
        final String currStatus = status(staff, dbManager);
        if (currStatus.equals(ABSENT_RESPONSE)) {
            Object lastAct = dbManager.getSession().createCriteria(Activity.class).add(Restrictions.eq("staff", staff)).add(Restrictions.or(Restrictions.eq("kindofactivity", ActivityDTO.BEGIN_OF_DAY), Restrictions.eq("kindofactivity", ActivityDTO.END_OF_DAY))).addOrder(Order.desc("day")).setMaxResults(1).uniqueResult();
            if (lastAct instanceof Activity) {
                Activity tmp = (Activity) lastAct;
                final Date helper = new Date();
                final Date d = new Date(helper.getYear(), helper.getMonth(), helper.getDate(), helper.getHours(), helper.getMinutes());
                if (tmp.getDay().before(d)) {
                    Activity act = new Activity();
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
        ServletOutputStream out;
        try {
            out = response.getOutputStream();
            out.print("The last activity is already a come booking.");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(QuickBooking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addGo(Staff staff, DBManager dbManager, HttpServletResponse response) {
        final String stat = status(staff, dbManager);
        if (stat.equals(PRESENT_RESPONSE)) {
            Activity act = new Activity();
            act.setKindofactivity(ActivityDTO.END_OF_DAY);
            act.setStaff(staff);
            act.setDay(new Date());

            dbManager.createObject(act);
            refreshModification(staff, dbManager);
            return;
        }
        
        response.setStatus(400);
        ServletOutputStream out;
        try {
            out = response.getOutputStream();
            out.print("The last activity is already a go booking");
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(QuickBooking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Staff checklogin(String username, String pasword, HttpSession session, DBManager dbManager) throws LoginFailedException, DataRetrievalException {
        try {
            Session hibernateSession = dbManager.getSession();

            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());

//            Staff staff = (Staff) hibernateSession.createCriteria(Staff.class).add(Restrictions.eq("username", username)).uniqueResult();
            Staff staff = (Staff)hibernateSession.createCriteria(Staff.class).add(Restrictions.and(Restrictions.eq("username", username), Restrictions.eq("password", md.digest()))).uniqueResult();

            return staff;
        } catch (Throwable t) {
            logger.error("Error:", t);
            throw new DataRetrievalException(t.getMessage(), t);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}