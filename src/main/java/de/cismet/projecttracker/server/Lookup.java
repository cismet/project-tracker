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
import de.cismet.projecttracker.client.dto.ProjectPeriodDTO;
import de.cismet.projecttracker.client.dto.WorkPackagePeriodDTO;

import org.apache.log4j.Logger;


import java.io.IOException;
import java.io.PrintWriter;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;
import static de.cismet.projecttracker.client.helper.DateHelper.isDateGreaterOrEqual;
import static de.cismet.projecttracker.client.helper.DateHelper.isDateLessOrEqual;
import de.cismet.projecttracker.report.db.entities.Project;
import de.cismet.projecttracker.report.db.entities.ProjectPeriod;

import de.cismet.projecttracker.report.db.entities.Staff;
import de.cismet.projecttracker.report.db.entities.StaffExtern;
import de.cismet.projecttracker.report.db.entities.WorkPackage;
import de.cismet.projecttracker.report.db.entities.WorkPackagePeriod;
import de.cismet.projecttracker.report.query.DBManager;

import de.cismet.projecttracker.utilities.DTOManager;
import java.text.SimpleDateFormat;
import java.util.Date;

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
                } else if (operation.equalsIgnoreCase("allProjects")) {
                    if (staff instanceof StaffExtern) {
                        response.setStatus(403);
                        logger.warn("invalid permission");
                        out.print("forbidden");
                        return;
                    }
                    
                    List<Project> prList = null;
                    final DTOManager dtoManager = new DTOManager();
                    
                    prList = getAllProjects(dbManager);

                    response.setCharacterEncoding("UTF-8");
                    final ObjectMapper mapper = new ObjectMapper();
                    final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
                    final String json = writer.writeValueAsString(dtoManager.clone(prList));
                    out.print(json);
                } else if (operation.equalsIgnoreCase("allWorkpackages")) {
                    final String day = request.getParameter("day");
                    Date dayDate = null;
                    List<WorkPackage> wpList = new ArrayList<WorkPackage>();
                    final DTOManager dtoManager = new DTOManager();

                    if (day != null) {
                        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        dayDate = formatter.parse(day);
                    } else {
                        response.setStatus(400);
                        logger.warn("missing parameter");
                        out.print("missing parameter");
                        return;
                    }
                    
                    if (staff instanceof StaffExtern) {
                        List<WorkPackage> tmpList = new ArrayList<WorkPackage>(((StaffExtern)staff).getWorkpackages());
                        DTOManager manager = new DTOManager();
                        
                        if (tmpList != null) {
                            for (WorkPackage wp : tmpList) {
                                if (wp.getWorkPackagePeriods() != null) {
                                    for (WorkPackagePeriod wpp : wp.getWorkPackagePeriods()) {
                                        if (isDayInWorkPackagePeriod(dayDate, (WorkPackagePeriodDTO)manager.clone(wpp))) {
                                            wpList.add(wp);
                                            break;
                                        }
                                    }
                                } else {
                                    wpList.add(wp);
                                }
                            }
                        }
                    } else {
                        wpList = getAllWorkPackages(dayDate, dbManager);
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

    private static boolean isDayInWorkPackagePeriod(final Date day, final WorkPackagePeriodDTO period) {
        return (isDateGreaterOrEqual(day, period.getFromdate())
                        && ((period.getTodate() == null) || isDateLessOrEqual(day, period.getTodate())));
    }
    
    /**
     * compares two calendar objects.
     *
     * @param   date1  DOCUMENT ME!
     * @param   date2  DOCUMENT ME!
     *
     * @return  true, if and only if date1 is less or equal to date2. The result only depends on the dates, which are
     *          contained in the given GregorianCalendar objects. The times will be ignored.
     */
    private static boolean isDateLessOrEqual(final Date date1, final Date date2) {
        final int firstDate = ((date1.getYear() + 1900) * 10000) + (date1.getMonth() * 100) + date1.getDate();
        final int secondDate = ((date2.getYear() + 1900) * 10000) + (date2.getMonth() * 100) + date2.getDate();

        return (firstDate <= secondDate);
    }

    /**
     * compares two calendar objects.
     *
     * @param   date1  DOCUMENT ME!
     * @param   date2  DOCUMENT ME!
     *
     * @return  true, if and only if date1 is less or equal to date2. The result only depends on the dates, which are
     *          contained in the given GregorianCalendar objects. The times will be ignored.
     */
    private static boolean isDateGreaterOrEqual(final Date date1, final Date date2) {
        final int firstDate = ((date1.getYear() + 1900) * 10000) + (date1.getMonth() * 100) + date1.getDate();
        final int secondDate = ((date2.getYear() + 1900) * 10000) + (date2.getMonth() * 100) + date2.getDate();

        return (firstDate >= secondDate);
    }

    
    /**
     * Determines all work packages, which fulfil the given criterias.
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
     * Determines all work packages, which fulfil the given criterias.
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
    private List<WorkPackage> getAllWorkPackages(final Date day,
            final DBManager dbManager) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get workpackages: ");
        }

        try {
            final List<WorkPackage> resultForDay = new ArrayList();
            final List<WorkPackage> result = (List<WorkPackage>)dbManager.getAllObjects(WorkPackage.class);
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " workpackages found");
            }
            
            DTOManager manager = new DTOManager();
            
            if (day != null) {
                for (WorkPackage wp : result) {
                    if (isProjectActive(day, wp.getProject(), manager)) {
                        if (wp.getWorkPackagePeriods() != null) {
                            for (WorkPackagePeriod wpp : wp.getWorkPackagePeriods()) {
                                if (isDayInWorkPackagePeriod(day, (WorkPackagePeriodDTO)manager.clone(wpp))) {
                                    resultForDay.add(wp);
                                    break;
                                }
                            }
                        } else {
                            resultForDay.add(wp);
                        }
                    }
                }
            } else {
                resultForDay.addAll(result);
            }

            return resultForDay;
        } catch (Exception e) {
            logger.error("Error while retrieving workpackages");
            return new ArrayList();
        }
    }
    
    private boolean isProjectActive(Date day, Project project, final DTOManager manager) {
        try {
            if (project != null && project.getProjectPeriods() != null) {
                for (ProjectPeriod pp : project.getProjectPeriods()) {
                    if (isDayInProjectPeriod(day, (ProjectPeriodDTO)manager.clone(pp))) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error while checking project periods", e);
        }        
        return false;
    }
    
    public static boolean isDayInProjectPeriod(final Date day, final ProjectPeriodDTO period) {
        return (isDateGreaterOrEqual(day, period.getFromdate())
                        && ((period.getTodate() == null) || isDateLessOrEqual(day, period.getTodate())));
    }
    
    /**
     * Determines all work packages, which fulfil the given criterias.
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
    private List<Project> getAllProjects(final DBManager dbManager) throws InvalidInputValuesException,
        DataRetrievalException,
        PermissionDenyException,
        NoSessionException {
        if (logger.isDebugEnabled()) {
            logger.debug("get workpackages: ");
        }

        try {
            final List<Project> result = (List<Project>)dbManager.getAllObjects(Project.class);
            if (logger.isDebugEnabled()) {
                logger.debug(result.size() + " projects found");
            }
            
            
            return result;
        } catch (Exception e) {
            logger.error("Error while retrieving workpackages");
            return new ArrayList();
        }
    }
}
