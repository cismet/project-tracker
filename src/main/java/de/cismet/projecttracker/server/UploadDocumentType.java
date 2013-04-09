/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.projecttracker.server;

import de.cismet.projecttracker.utilities.LanguageBundle;

/**
 * DOCUMENT ME!
 *
 * @author   therter
 * @version  $Revision$, $Date$
 */
public class UploadDocumentType {

    //~ Instance fields --------------------------------------------------------

    private long parentId = -1;
    private byte[] content;
    private String fileName;
    private String mimeType;
    private String docType;

    //~ Methods ----------------------------------------------------------------

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getErrors() {
        String errors = "";

        if (content == null) {
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
     * DOCUMENT ME!
     *
     * @return  the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  content  the content to set
     */
    public void setContent(final byte[] content) {
        this.content = content;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  fileName  the fileName to set
     */
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  mimeType  the mimeType to set
     */
    public void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the parentId
     */
    public long getParentId() {
        return parentId;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  parentId  the parentId to set
     */
    public void setParentId(final long parentId) {
        this.parentId = parentId;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  the docType
     */
    public String getDocType() {
        return docType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  docType  the docType to set
     */
    public void setDocType(final String docType) {
        this.docType = docType;
    }
}
