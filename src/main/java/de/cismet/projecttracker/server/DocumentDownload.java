package de.cismet.projecttracker.server;

import de.cismet.projecttracker.client.exceptions.DataRetrievalException;
import de.cismet.projecttracker.client.exceptions.InvalidInputValuesException;
import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;
import de.cismet.projecttracker.report.db.entities.ContractDocument;
import de.cismet.projecttracker.report.db.entities.TravelDocument;
import de.cismet.projecttracker.report.query.DBManager;
import de.cismet.projecttracker.utilities.LanguageBundle;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 * A request to this servlet should have at least the following fields:
 * <strong>docType</strong> - the type of the parent object of the doocument that should be downloaded. Allowd values are: contract, travel <br />
 * <strong>id</strong> - the id of document that should be downloaded
 * @author therter
 */
public class DocumentDownload extends BasicServlet {
    private static final Logger logger = Logger.getLogger(DocumentDownload.class);
    private static final String HTML_TEMPLATE = "<html><head></head><body>%s</body></html>";



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
        String docType = request.getParameter("docType");
        response.setContentType("text/html;charset=UTF-8");

        try {
            checkAdminPermission(request);
            if (request.getPathInfo() != null) {
                urlExtension = request.getPathInfo().substring(1);
            }

            if (urlExtension == null || urlExtension.equals("")) {
                redirect(id, request.getRequestURI(), response, docType);
            } else {
                returnFile(id, response, docType);
            }
        } catch (PermissionDenyException e) {
            PrintWriter out = response.getWriter();
            out.print("permission deny");
            out.close();
        } catch (NoSessionException e) {
            PrintWriter out = response.getWriter();
            out.print("session expired");
            out.close();
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println(String.format(HTML_TEMPLATE, e.getMessage()));
            out.close();
        }
    } 


    private void redirect(String id, String uri, HttpServletResponse response, String docType) throws IOException, DataRetrievalException, InvalidInputValuesException {
        String docName;
        
        if (docType.equals("contract")) {
            docName = getContractDocument(id).getDocumentname();
        } else if (docType.equals("travel")) {
            docName = getTravelDocument(id).getDocumentname();
        } else throw new InvalidInputValuesException( LanguageBundle.DOCUMENT_NOT_FOUND );

        String newUrl = uri + "/" + docName + "?id=" + id + "&docType=" + docType;
        response.sendRedirect( response.encodeRedirectURL( newUrl ) );
    }


    private ContractDocument getContractDocument(String id) throws DataRetrievalException {
        DBManager dbManager = new DBManager();

        try {
            ContractDocument doc = (ContractDocument)dbManager.getObject(ContractDocument.class, new Long(id));

            if (doc != null) {
                return doc;
            }
            throw new DataRetrievalException( LanguageBundle.DOCUMENT_NOT_FOUND);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            throw new DataRetrievalException(t.getMessage());
        } finally {
            if (dbManager != null) {
                dbManager.closeSession();
            }
        }
    }

    private TravelDocument getTravelDocument(String id) throws DataRetrievalException {
        DBManager dbManager = new DBManager();

        try {
            TravelDocument doc = (TravelDocument)dbManager.getObject(TravelDocument.class, new Long(id));

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

    
    private void returnFile(String id, HttpServletResponse response, String docType) throws IOException, DataRetrievalException, InvalidInputValuesException {
        String mimeType;
        byte[] document;
        ServletOutputStream out = response.getOutputStream();

        if (docType.equals("contract")) {
            ContractDocument doc = getContractDocument(id);
            mimeType = doc.getMimetype();
            document = doc.getDocument();
        } else if (docType.equals("travel")) {
            TravelDocument doc = getTravelDocument(id);
            mimeType = doc.getMimetype();
            document = doc.getDocument();
        } else throw new InvalidInputValuesException( LanguageBundle.DOCUMENT_NOT_FOUND );

        if (mimeType != null) {
            response.setContentType(mimeType);
        }
        
        try {
            out.write( document );
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
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
