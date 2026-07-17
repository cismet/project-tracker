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

import de.cismet.projecttracker.client.ProjectTrackerEntryPoint;
import org.apache.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.io.Serializable;

import java.security.MessageDigest;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import de.cismet.projecttracker.client.dto.ActivityDTO;
import de.cismet.projecttracker.client.dto.StaffDTO;
import de.cismet.projecttracker.client.dto.WorkCategoryDTO;
import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.exceptions.LoginFailedException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;

import de.cismet.projecttracker.report.db.entities.Activity;
import de.cismet.projecttracker.report.db.entities.Project;
import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.WorkCategory;
import de.cismet.projecttracker.report.db.entities.WorkPackage;
import de.cismet.projecttracker.report.query.DBManager;
import de.cismet.projecttracker.utilities.DBManagerWrapper;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import org.hibernate.criterion.Criterion;

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
        String staffName = request.getParameter("staff");
        final DBManager dbManager = new DBManager(ConfigurationManager.getInstance().getConfBaseDir());
        final ServletOutputStream out = response.getOutputStream();
        
        if (staffName == null) {
            staffName = username;
        }

        try {
            final Staff user = checklogin(username, password, request.getSession(), dbManager);
            
            if (user != null && !username.equals(staffName)) {
                boolean isAdmin = (((Staff)user).getPermissions() & ProjectTrackerEntryPoint.ADMIN_PERMISSION) == ProjectTrackerEntryPoint.ADMIN_PERMISSION;
                
                if (!isAdmin) {
                    response.setStatus(403);
                    logger.warn("invalid permission");
                    out.print("forbidden");
                    return;
                }
            } else if (user == null) {
                response.setStatus(403);
                logger.warn("invalid username/password");
                out.print("forbidden");
                return;
            }
            
            Staff staff = null;
            
            if (username.equals(staffName)) {
                staff = user;
            } else {
                staff = getStaff(staffName, dbManager);
            }

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
                } else if (operation.toLowerCase().equals("addslot")) {
                    final String von = request.getParameter("von");
                    final String bis = request.getParameter("bis");

                    if ((von != null) && (bis != null)) {
                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        final Date from = formatter.parse(von);
                        final Date until = formatter.parse(bis);

                        if (!((from.getYear() == until.getYear())
                                        && (from.getMonth() == until.getMonth())
                                        && (from.getDate() == until.getDate()))) {
                            response.setStatus(400);
                            out.print("The 2 dates are on different days");
                            return;
                        }

                        addSlot(staff, from, until, dbManager, response);
                    } else {
                        response.setStatus(400);
                        out.print("von or bis parameter not found");
                    }
                } else if (operation.toLowerCase().equals("lock")) {
                    final String day = request.getParameter("day");
                    
                    if (day != null) {
                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        final Date dayDate = formatter.parse(day);

                        addLock(dayDate, staff, dbManager, response);
                    } else {
                        response.setStatus(400);
                        out.print("day parameter not found");
                    }
                } else if (operation.toLowerCase().equals("unlock")) {
                    final String day = request.getParameter("day");
                    
                    if (day != null) {
                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        final Date dayDate = formatter.parse(day);

                        unlock(dayDate, staff, dbManager, response);
                   } else {
                        response.setStatus(400);
                        out.print("day parameter not found");
                    }
                } else if (operation.toLowerCase().equals("addactivity")) {
                    final String day = request.getParameter("day");
                    final String workpackage = request.getParameter("workpackage");
                    final String project = request.getParameter("project");
                    final String workingHoursString = request.getParameter("workingHours");
                    final String description = request.getParameter("description");
                    Double workingHours = 0.0;
                    final Session hibernateSession = dbManager.getSession();
                    WorkPackage wp = null;
                    Project pr = null;
                    
                    if (project != null) {
                        pr = (Project)hibernateSession.createCriteria(Project.class).add(Restrictions.eq("name", project))
                                    .uniqueResult();

                        if (pr == null) {
                            response.setStatus(400);
                            out.print("Project is not valid");
                            logger.warn("Project is not valid " + project);
                            return;
                        }
                    }

                    if (workpackage != null) {
                        if (pr != null) {
                            wp = (WorkPackage)hibernateSession.createCriteria(WorkPackage.class)
                                        .add(Restrictions.eq("name", workpackage))
                                        .add(Restrictions.eq("project", pr))
                                        .uniqueResult();
                        } else {
                            wp = (WorkPackage)hibernateSession.createCriteria(WorkPackage.class)
                                        .add(Restrictions.eq("name", workpackage))
                                        .uniqueResult();
                        }
                    }
                    
                    if (wp == null) {
                        response.setStatus(400);
                        logger.warn("Workpackage is not valid: " + workpackage);
                        out.print("workpackage is not valid");
                        return;
                    }
                    
                    if (workingHoursString != null) {
                        try {
                            workingHours = Double.parseDouble(workingHoursString);
                        } catch (NumberFormatException e) {
                            response.setStatus(400);
                            logger.warn("working hours is not valid: " + workingHours);
                            out.print("working hours is not valid");
                            return;
                        }
                    }
                    
                    if (description == null || description.equals("")) {
                        response.setStatus(400);
                        logger.warn("description is not valid: " + workingHours);
                        out.print("description is not valid");
                        return;
                    }
                
                    if (day != null) {
//                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        final Date dayDate = formatter.parse(day);

                        addActivity(staff, dayDate, wp, workingHours, description, dbManager, response);
                    } else {
                        response.setStatus(400);
                        out.print("day parameter not found");
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
     * Adds a complete time slot.
     *
     * @param   staff      DOCUMENT ME!
     * @param   from       DOCUMENT ME!
     * @param   until      DOCUMENT ME!
     * @param   dbManager  DOCUMENT ME!
     * @param   response   DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void addSlot(final Staff staff,
            final Date from,
            final Date until,
            final DBManager dbManager,
            final HttpServletResponse response) throws Exception {
        final Activity actStart = new Activity();
        final Activity actEnd = new Activity();
        Serializable idFrom = null;
        Serializable idUntil = null;

        try {
            actStart.setKindofactivity(ActivityDTO.BEGIN_OF_DAY);
            actStart.setStaff(staff);
            actStart.setDay(from);
            actStart.setDescription("quickBooking");

            idFrom = dbManager.createObject(actStart);

            actEnd.setKindofactivity(ActivityDTO.END_OF_DAY);
            actEnd.setStaff(staff);
            actEnd.setDay(until);

            idUntil = dbManager.createObject(actEnd);
        } catch (Exception e) {
            if (idFrom != null) {
                dbManager.deleteObject(actStart);
            }
            if (idUntil != null) {
                dbManager.deleteObject(actEnd);
            }

            throw new Exception("the time slot has a conflict with an existing slot.");
        }

        refreshModification(staff, dbManager);
        return;
    }

    /**
     * Adds a complete time slot.
     *
     * @param   staff      DOCUMENT ME!
     * @param   from       DOCUMENT ME!
     * @param   until      DOCUMENT ME!
     * @param   dbManager  DOCUMENT ME!
     * @param   response   DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private void addActivity(final Staff staff,
            final Date day,
            final WorkPackage workpackage,
            final double workingHours,
            final String description,
            final DBManager dbManager,
            final HttpServletResponse response) throws Exception {
        final Activity activity = new Activity();
        Serializable id = null;

        try {
            activity.setKindofactivity(ActivityDTO.ACTIVITY);
            activity.setStaff(staff);
            activity.setDay(day);
            activity.setWorkCategory(new WorkCategory(WorkCategoryDTO.WORK));
            activity.setWorkPackage(workpackage);
            activity.setWorkinghours(workingHours);
            activity.setDescription(description);

            id = dbManager.createObject(activity);
        } catch (Exception e) {
            if (id != null) {
                dbManager.deleteObject(id);
            }

            throw new Exception("the time slot has a conflict with an existing slot.");
        }

        refreshModification(staff, dbManager);
        return;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dbManager  DOCUMENT ME!
     * @param   day        DOCUMENT ME!
     * @param   staff      DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Boolean isDayLocked(final DBManager dbManager, final Date day, final Staff staff) {
        if (day == null) {
            // favourite tasks have null as day
            return false;
        }
        // TODO: change Time Restriction to same day and not same time...
        final Date d = new Date(day.getTime());
        d.setHours(5);
        d.setMinutes(0);
        d.setSeconds(0);

        final Criteria crit = dbManager.getSession()
                    .createCriteria(Activity.class)
                    .add(Restrictions.and(
                                Restrictions.eq("staff", staff),
                                Restrictions.and(
                                    Restrictions.eq("kindofactivity", ActivityDTO.LOCKED_DAY),
                                    Restrictions.eq("day", d))))
                    .setMaxResults(1);
        final Activity lockedDayActivity = (Activity)crit.uniqueResult();

        if (lockedDayActivity != null) {
            return true;
        }
        return false;
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
    private void addLock(final Date day, final Staff staff, final DBManager dbManager, final HttpServletResponse response) {
        final Activity act = new Activity();
        act.setKindofactivity(ActivityDTO.LOCKED_DAY);
        act.setStaff(staff);
        act.setDay(day);

        dbManager.createObject(act);
        refreshModification(staff, dbManager);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param  staff      DOCUMENT ME!
     * @param  dbManager  DOCUMENT ME!
     * @param  response   DOCUMENT ME!
     */
    private void unlock(final Date day, final Staff staff, final DBManager dbManager, final HttpServletResponse response) {
        final GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(day);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        day.setHours(0);
        day.setMinutes(0);
        day.setSeconds(0);
        
        final Criterion dateRestriction = Restrictions.and(Restrictions.ge("day", day),
                Restrictions.lt("day", cal.getTime()));
        final List lockActivities = dbManager.getSession()
            .createCriteria(Activity.class)
            .add(Restrictions.eq("staff", staff))
            .add(Restrictions.eq("kindofactivity", ActivityDTO.LOCKED_DAY))
            .add(dateRestriction)
            .addOrder(Order.desc("day"))
            .list();
        
        for (Object o : lockActivities) {
            dbManager.deleteObject(o);
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
    @Override
    public Staff checklogin(final String username,
            final String pasword,
            final HttpSession session,
            final DBManager dbManager) throws LoginFailedException, DataRetrievalException {
        try {
            final Session hibernateSession = dbManager.getSession();

            final MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(pasword.getBytes());

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
