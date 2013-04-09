/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.server;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;

import de.cismet.projecttracker.report.ProjectTrackerReport;
import de.cismet.projecttracker.report.ReportManager;
import de.cismet.projecttracker.report.db.entities.Report;
import de.cismet.projecttracker.report.query.DBManager;

import de.cismet.projecttracker.utilities.LanguageBundle;

/**
 * TODO: prufen, ob diese Funktionalität von der Klasse DocumentDownload abgedeckt werden kann
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class ReportDownload extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(ReportDownload.class);

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

    //~ Instance fields --------------------------------------------------------

    private ReportManager reportManager;

    //~ Methods ----------------------------------------------------------------

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        reportManager = new ReportManager(config.getServletContext().getRealPath("/"));
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
        String urlExtension = null;
        final String id = request.getParameter("id");
        response.setContentType("text/html;charset=UTF-8");

        try {
            checkAdminPermission(request);
            if (request.getPathInfo() != null) {
                urlExtension = request.getPathInfo().substring(1);
            }

            if ((urlExtension == null) || urlExtension.equals("")) {
                final String pluginName = request.getParameter("plugin");
                redirect(id, pluginName, request.getRequestURI(), response);
            } else {
                returnFile(id, response);
            }
        } catch (PermissionDenyException e) {
            final PrintWriter out = response.getWriter();
            out.print("permission deny");
            out.close();
        } catch (NoSessionException e) {
            final PrintWriter out = response.getWriter();
            out.print("session expired");
            out.close();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id          DOCUMENT ME!
     * @param   pluginName  DOCUMENT ME!
     * @param   uri         DOCUMENT ME!
     * @param   response    DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void redirect(final String id,
            final String pluginName,
            final String uri,
            final HttpServletResponse response) throws IOException {
        final ProjectTrackerReport reportPlugin = reportManager.getReportByName(pluginName);
        String ext = "";
        if (reportPlugin != null) {
            ext = reportPlugin.getFileExtension();
        }
        final String newUrl = uri + "/Report" + id + ext + "?id=" + id;

        response.sendRedirect(response.encodeRedirectURL(newUrl));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id        DOCUMENT ME!
     * @param   response  DOCUMENT ME!
     *
     * @throws  IOException  DOCUMENT ME!
     */
    private void returnFile(final String id, final HttpServletResponse response) throws IOException {
        final ServletOutputStream out = response.getOutputStream();
        final DBManager dbManager = new DBManager();

        try {
            final long reportId = Long.parseLong(id);

            final Report report = (Report)dbManager.getObject(Report.class, new Long(reportId));
            final ProjectTrackerReport reportPlugin = reportManager.getReportByName(report.getGeneratorname());

            if ((reportPlugin != null) && (report != null)) {
                response.setContentType(reportPlugin.getMIMEType());
                out.write(report.getReportdocument());
            } else {
                if (reportPlugin == null) {
                    logger.error("report plugin with name " + report.getGeneratorname() + " not found");
                    out.println(LanguageBundle.REPORT_PLUGIN_NOT_FOUND);
                } else if (report == null) {
                    logger.error("report with id " + reportId + " not found");
                    out.println(LanguageBundle.REPORT_NOT_FOUND);
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
}
