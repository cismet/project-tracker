package de.cismet.projecttracker.server;

import de.cismet.projecttracker.client.exceptions.NoSessionException;
import de.cismet.projecttracker.client.exceptions.PermissionDenyException;
import de.cismet.projecttracker.client.exceptions.PersistentLayerException;
import de.cismet.projecttracker.report.db.entities.Contract;
import de.cismet.projecttracker.report.db.entities.ContractDocument;
import de.cismet.projecttracker.report.db.entities.Travel;
import de.cismet.projecttracker.report.db.entities.TravelDocument;
import de.cismet.projecttracker.report.query.DBManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

/**
 *
 * Requests to this servlet should have at least the following fields: <br />
 * <strong>filePayload</strong> - contains the uploaded file <br />
 * <strong>docType</strong> - contains the object type, the uploaded file should assigned to. Valid values are: contract, travel <br />
 * <strong>documentId</strong> the id of the object, the uploaded file should be assigned to<br />
 * @author therter
 */
public class DocumentUpload extends BasicServlet {
    private static final Logger logger = Logger.getLogger(DocumentUpload.class);
    private static final ResourceBundle CONFIG = ResourceBundle.getBundle("de.cismet.projecttracker.MIMETypeMapping");

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            checkAdminPermission(request);

            if (request.getMethod().equals("POST")) {
                UploadDocumentType uploadDoc = getFileContent(request);

                String errors = uploadDoc.getErrors();

                if (errors == null) {
                    if ( uploadDoc.getDocType().equals("contract") ) {
                        saveContractDocument(uploadDoc);
                    } else if ( uploadDoc.getDocType().equals("travel") ) {
                        saveTravelDocument(uploadDoc);
                    }
                } else {
                    logger.error( errors );
                    out.print( errors );
                }
            } else {
                logger.error("wrong method");
                out.print("wrong method");
            }
        } catch (PermissionDenyException e) {
            out.print("permission deny");
        } catch (NoSessionException e) {
            out.print("session expired");
        } catch (Exception e) {
            out.append(e.getMessage());
        } finally {
            out.close();
        }
    }


    /**
     * get content of the payload section of the request
     *
     * @param request the http request object
     * @return the content of the file that was uploaded
     * @throws java.lang.Exception
     */
    private UploadDocumentType getFileContent(HttpServletRequest request) throws Exception {
        UploadDocumentType uploadDocument = new UploadDocumentType();
        DiskFileItemFactory itemFactory = new DiskFileItemFactory();
        ServletFileUpload fileUpload = new ServletFileUpload(itemFactory);
        List list = fileUpload.parseRequest(request);
        ByteArrayOutputStream fileContent = null;

        for (Object tmp : list) {
            if (tmp instanceof DiskFileItem) {
                DiskFileItem item = (DiskFileItem)tmp;
                logger.debug("fieldName: " + item.getFieldName());

                if (item.getFieldName().equals("filePayload")) {
                    logger.debug("file payload found: ");
                    InputStream stream = item.getInputStream();
                    byte[] buffer = new byte[256];
                    int count;
                    fileContent = new ByteArrayOutputStream();

                    // read file content
                    while ( ( count = stream.read(buffer, 0, buffer.length) ) != -1 ) {
                        fileContent.write(buffer, 0, count);
                    }

                    uploadDocument.setContent( fileContent.toByteArray() );
                    uploadDocument.setFileName( item.getName() );
                    uploadDocument.setMimeType( getMIMEType(item) );
                } else if ( item.getFieldName().equals("parentId") ) {
                    uploadDocument.setParentId( Long.parseLong( item.getString() ) );
                } else if ( item.getFieldName().equals("docType") ) {
                    uploadDocument.setDocType( item.getString() );
                }
            }
        }

        return uploadDocument;
    }


    private String getMIMEType(DiskFileItem item) {
        String contentType = null;
        String fileName = item.getName();

        //determine the MIME type of the document
        if ( fileName.lastIndexOf(".") != -1 && !fileName.endsWith(".") ) {
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            try {
                contentType = CONFIG.getString(fileExtension);
            } catch (MissingResourceException e) {
                // nothing to do
            }
        }

        if (contentType == null) {
            contentType = item.getContentType();
        }

        return contentType;
    }


    private void saveContractDocument(UploadDocumentType uploadDoc) throws PersistentLayerException {
        DBManager dbManager = new DBManager();
        ContractDocument doc = new ContractDocument();
        Contract tmpContract = new Contract();

        tmpContract.setId(uploadDoc.getParentId());
        doc.setDocument(uploadDoc.getContent());
        doc.setDocumentname(uploadDoc.getFileName());
        doc.setMimetype(uploadDoc.getMimeType());
        doc.setContract( tmpContract );

        try {
            dbManager.saveObject(doc);
        } catch (Exception t) {
            logger.error("Error", t);
            throw new PersistentLayerException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
        }
    }


    private void saveTravelDocument(UploadDocumentType uploadDoc) throws PersistentLayerException {
        DBManager dbManager = new DBManager();
        TravelDocument doc = new TravelDocument();
        Travel tmpTravel = new Travel();

        tmpTravel.setId( uploadDoc.getParentId() );
        doc.setDocument( uploadDoc.getContent() );
        doc.setDocumentname( uploadDoc.getFileName() );
        doc.setMimetype( uploadDoc.getMimeType() );
        doc.setTravel( tmpTravel );

        try {
            dbManager.saveObject(doc);
        } catch (Exception t) {
            logger.error("Error", t);
            throw new PersistentLayerException(t.getMessage(), t);
        } finally {
            dbManager.closeSession();
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
