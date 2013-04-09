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

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;

import de.cismet.projecttracker.report.db.entities.ContractDocument;
import de.cismet.projecttracker.report.db.entities.TravelDocument;
import de.cismet.projecttracker.report.query.DBManager;

import de.cismet.projecttracker.utilities.LanguageBundle;

/**
 * A request to this servlet should have at least the following fields: <strong>docType</strong> - the type of the
 * parent object of the doocument that should be downloaded. Allowd values are: contract, travel<br />
 * <strong>id</strong> - the id of document that should be downloaded
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class DocumentDownload extends BasicServlet {

    //~ Static fields/initializers ---------------------------------------------

    private static final Logger logger = Logger.getLogger(DocumentDownload.class);
    private static final String HTML_TEMPLATE = "<html><head></head><body>%s</body></html>";

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
        String urlExtension = null;
        final String id = request.getParameter("id");
        final String docType = request.getParameter("docType");
        response.setContentType("text/html;charset=UTF-8");

        try {
            checkAdminPermission(request);
            if (request.getPathInfo() != null) {
                urlExtension = request.getPathInfo().substring(1);
            }

            if ((urlExtension == null) || urlExtension.equals("")) {
                redirect(id, request.getRequestURI(), response, docType);
            } else {
                returnFile(id, response, docType);
            }
        } catch (PermissionDenyException e) {
            final PrintWriter out = response.getWriter();
            out.print("permission deny");
            out.close();
        } catch (NoSessionException e) {
            final PrintWriter out = response.getWriter();
            out.print("session expired");
            out.close();
        } catch (Exception e) {
            final PrintWriter out = response.getWriter();
            out.println(String.format(HTML_TEMPLATE, e.getMessage()));
            out.close();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id        DOCUMENT ME!
     * @param   uri       DOCUMENT ME!
     * @param   response  DOCUMENT ME!
     * @param   docType   DOCUMENT ME!
     *
     * @throws  IOException                  DOCUMENT ME!
     * @throws  DataRetrievalException       DOCUMENT ME!
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    private void redirect(final String id, final String uri, final HttpServletResponse response, final String docType)
            throws IOException, DataRetrievalException, InvalidInputValuesException {
        String docName;

        if (docType.equals("contract")) {
            docName = getContractDocument(id).getDocumentname();
        } else if (docType.equals("travel")) {
            docName = getTravelDocument(id).getDocumentname();
        } else {
            throw new InvalidInputValuesException(LanguageBundle.DOCUMENT_NOT_FOUND);
        }

        final String newUrl = uri + "/" + docName + "?id=" + id + "&docType=" + docType;
        response.sendRedirect(response.encodeRedirectURL(newUrl));
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    private ContractDocument getContractDocument(final String id) throws DataRetrievalException {
        final DBManager dbManager = new DBManager(ConfigurationManager.getInstance().getConfBaseDir());

        try {
            final ContractDocument doc = (ContractDocument)dbManager.getObject(ContractDocument.class, new Long(id));

            if (doc != null) {
                return doc;
            }
            throw new DataRetrievalException(LanguageBundle.DOCUMENT_NOT_FOUND);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            throw new DataRetrievalException(t.getMessage());
        } finally {
            if (dbManager != null) {
                dbManager.closeSession();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  DataRetrievalException  DOCUMENT ME!
     */
    private TravelDocument getTravelDocument(final String id) throws DataRetrievalException {
        final DBManager dbManager = new DBManager(ConfigurationManager.getInstance().getConfBaseDir());

        try {
            final TravelDocument doc = (TravelDocument)dbManager.getObject(TravelDocument.class, new Long(id));

            if (doc != null) {
                return doc;
            }
            throw new DataRetrievalException(LanguageBundle.DOCUMENT_NOT_FOUND);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            throw new DataRetrievalException(t.getMessage());
        } finally {
            if (dbManager != null) {
                dbManager.closeSession();
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   id        DOCUMENT ME!
     * @param   response  DOCUMENT ME!
     * @param   docType   DOCUMENT ME!
     *
     * @throws  IOException                  DOCUMENT ME!
     * @throws  DataRetrievalException       DOCUMENT ME!
     * @throws  InvalidInputValuesException  DOCUMENT ME!
     */
    private void returnFile(final String id, final HttpServletResponse response, final String docType)
            throws IOException, DataRetrievalException, InvalidInputValuesException {
        String mimeType;
        byte[] document;
        final ServletOutputStream out = response.getOutputStream();

        if (docType.equals("contract")) {
            final ContractDocument doc = getContractDocument(id);
            mimeType = doc.getMimetype();
            document = doc.getDocument();
        } else if (docType.equals("travel")) {
            final TravelDocument doc = getTravelDocument(id);
            mimeType = doc.getMimetype();
            document = doc.getDocument();
        } else {
            throw new InvalidInputValuesException(LanguageBundle.DOCUMENT_NOT_FOUND);
        }

        if (mimeType != null) {
            response.setContentType(mimeType);
        }

        try {
            out.write(document);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            out.close();
        }
    }
}
