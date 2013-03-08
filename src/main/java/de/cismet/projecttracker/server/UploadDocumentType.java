package de.cismet.projecttracker.server;

import de.cismet.projecttracker.utilities.LanguageBundle;

/**
 *
 * @author therter
 */
public class UploadDocumentType {
    private long parentId = -1;
    private byte[] content;
    private String fileName;
    private String mimeType;
    private String docType;


    public String getErrors() {
        String errors = "";

        if ( content == null ) {
            errors += LanguageBundle.PAYLOAD_NOT_FOUND + "\n";
        }
        if (fileName == null) {
            errors += LanguageBundle.FILE_NAME_NOT_FOUND + "\n";
        }
        if (parentId == -1) {
            errors += LanguageBundle.PARENT_ID_NOT_FOUND + "\n";
        }
        if (parentId == -1) {
            errors += LanguageBundle.DOCUMENT_TYPE_NOT_FOUND + "\n";
        }

        if (errors.equals("")) {
            return null;
        } else {
            return errors;
        }
    }

    /**
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the parentId
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the docType
     */
    public String getDocType() {
        return docType;
    }

    /**
     * @param docType the docType to set
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }
}
