package de.cismet.projecttracker.server;

import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;
import de.cismet.projecttracker.report.ProjectTrackerReport;
import de.cismet.projecttracker.report.ReportManager;
import de.cismet.projecttracker.report.db.entities.Report;
import de.cismet.projecttracker.report.query.DBManager;
import de.cismet.projecttracker.utilities.LanguageBundle;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import org.apache.log4j.Logger;


/**
 * TODO: prufen, ob diese Funktionalität von der Klasse DocumentDownload abgedeckt werden kann
 * @author therter
 */
public class ReportDownload extends BasicServlet {
    private static final Logger logger = Logger.getLogger(ReportDownload.class);
    private ReportManager reportManager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        reportManager = new ReportManager(config.getServletContext().getRealPath("/"));
    }

   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String urlExtension = null;
        String id = request.getParameter("id");
        response.setContentType("text/html;charset=UTF-8");

        try {
            checkAdminPermission(request);
            if (request.getPathInfo() != null) {
                urlExtension = request.getPathInfo().substring(1);
            }

            if (urlExtension == null || urlExtension.equals("")) {
                String pluginName = request.getParameter("plugin");
                redirect(id, pluginName, request.getRequestURI(), response);
            } else {
                returnFile(id, response);
            }
        } catch (PermissionDenyException e) {
            PrintWriter out = response.getWriter();
            out.print("permission deny");
            out.close();
        } catch (NoSessionException e) {
            PrintWriter out = response.getWriter();
            out.print("session expired");
            out.close();
        }
    }


    private void redirect(String id, String pluginName, String uri, HttpServletResponse response) throws IOException {
            ProjectTrackerReport reportPlugin = reportManager.getReportByName( pluginName );
            String ext = "";
            if (reportPlugin != null) {
                ext = reportPlugin.getFileExtension();
            }
            String newUrl = uri + "/Report" + id + ext + "?id=" + id;

            response.sendRedirect( response.encodeRedirectURL( newUrl ) );
    }


    private void returnFile(String id, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        DBManager dbManager = new DBManager();

        try {
            long reportId = Long.parseLong( id );

            Report report = (Report)dbManager.getObject(Report.class, new Long(reportId));
            ProjectTrackerReport reportPlugin = reportManager.getReportByName( report.getGeneratorname() );

            if (reportPlugin != null && report != null) {
                response.setContentType(reportPlugin.getMIMEType());
                out.write(report.getReportdocument());
            } else {
                if (reportPlugin == null) {
                    logger.error("report plugin with name " + report.getGeneratorname() + " not found");
                    out.println( LanguageBundle.REPORT_PLUGIN_NOT_FOUND );
                } else if (report == null) {
                    logger.error("report with id " + reportId + " not found");
                    out.println( LanguageBundle.REPORT_NOT_FOUND );
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        } finally {
            if (dbManager != null) {
                dbManager.closeSession();
            }
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
