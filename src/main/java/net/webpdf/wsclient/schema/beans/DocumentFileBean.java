package net.webpdf.wsclient.schema.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import net.webpdf.wsclient.schema.extraction.info.DocumentType;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@SuppressWarnings({"unused"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "documentFile")
public class DocumentFileBean implements Serializable {

    @Nullable
    private String documentId;
    @Nullable
    private String parentDocumentId;
    private long fileSize;
    @Nullable
    private String mimeType;
    @Nullable
    private String fileName;
    @Nullable
    private String fileExtension;
    private int fileTypeId;
    @Nullable
    private String fileTypeGroups;
    @Nullable
    private String fileLastModified;
    private boolean isFileLocked;
    @Nullable
    private DocumentType metadata;
    @Nullable
    private ExceptionBean error = new ExceptionBean();

    /**
     * Returns the document ID of this document.
     *
     * @return The document ID of this document.
     */
    @XmlElement
    @Nullable
    public String getDocumentId() {
        return this.documentId;
    }

    /**
     * Sets the document ID of this document.
     *
     * @param documentId The document ID of this document.
     */
    public void setDocumentId(@Nullable String documentId) {
        this.documentId = documentId;
    }

    /**
     * Returns the document ID of the parent document.
     *
     * @return The document ID of the parent document.
     */
    @XmlElement
    @Nullable
    public String getParentDocumentId() {
        return this.parentDocumentId;
    }

    /**
     * Sets the document ID of the parent document.
     *
     * @param parentDocumentId The document ID of the parent document.
     */
    public void setParentDocumentId(@Nullable String parentDocumentId) {
        this.parentDocumentId = parentDocumentId;
    }

    /**
     * Returns the file size of this document.
     *
     * @return The file size of this document.
     */
    @XmlElement
    public long getFileSize() {
        return this.fileSize;
    }

    /**
     * Sets the file size of this document.
     *
     * @param fileSize The file size of this document.
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Returns the MIME type of this document.
     *
     * @return The MIME type of this document.
     */
    @XmlElement
    @Nullable
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * Sets the MIME type of this document.
     *
     * @param mimeType The MIME type of this document.
     */
    public void setMimeType(@Nullable String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Returns the file name of this document.
     *
     * @return the file name of this document.
     */
    @XmlElement
    @Nullable
    public String getFileName() {
        return this.fileName;
    }

    /**
     * Sets the file name of this document.
     *
     * @param documentName The file name of this document.
     */
    public void setFileName(@Nullable String documentName) {
        this.fileName = documentName;
    }

    /**
     * Returns the file extension of this document.
     *
     * @return The file extension of this document.
     */
    @XmlElement
    @Nullable
    public String getFileExtension() {
        return this.fileExtension;
    }

    /**
     * Sets the file extension of this document.
     *
     * @param documentExtension The file extension of this document.
     */
    public void setFileExtension(@Nullable String documentExtension) {
        this.fileExtension = documentExtension;
    }

    /**
     * Returns the file lock of this document.
     *
     * @return The file lock of this document.
     */
    @XmlElement
    public boolean isIsFileLocked() {
        return this.isFileLocked;
    }

    /**
     * Sets the file lock of this document.
     *
     * @param isFileLocked The file lock of this document.
     */
    public void setIsFileLocked(boolean isFileLocked) {
        this.isFileLocked = isFileLocked;
    }

    /**
     * Returns the file type id of this document.
     *
     * @return The file type id of this document.
     */
    @XmlElement
    public int getFileTypeId() {
        return this.fileTypeId;
    }

    /**
     * Sets the file type id of this document.
     *
     * @param fileTypeId The file type id of this document.
     */
    public void setFileTypeId(int fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    /**
     * Returns the file type group of this document.
     *
     * @return The file type group of this document.
     */
    @XmlElement
    @Nullable
    public String getFileTypeGroups() {
        return this.fileTypeGroups;
    }

    /**
     * Sets the file type group of this document.
     *
     * @param setFileTypeGroups The file type group of this document.
     */
    public void setFileTypeGroups(@Nullable String setFileTypeGroups) {
        this.fileTypeGroups = setFileTypeGroups;
    }

    /**
     * Returns the last known modification date of this document.
     *
     * @return The last known modification date of this document.
     */
    @XmlElement
    @Nullable
    public String getFileLastModified() {
        return this.fileLastModified;
    }

    /**
     * Sets the modification date of this document.
     *
     * @param fileLastModified The modification date of this document.
     */
    public void setFileLastModified(@Nullable String fileLastModified) {
        this.fileLastModified = fileLastModified;
    }

    /**
     * Return the metadata of this document.
     *
     * @return The metadata of this document.
     */
    @XmlElement
    @Nullable
    public DocumentType getMetadata() {
        return this.metadata;
    }

    /**
     * Sets the metadata of this document.
     *
     * @param metadata The metadata of this document.
     */
    public void setMetadata(@Nullable DocumentType metadata) {
        this.metadata = metadata;
    }

    /**
     * Returns the stored error, that was caused by the creation attempt of this document.
     *
     * @return The error, caused by the attempted creation of this document.
     */
    @XmlElement
    @Nullable
    public ExceptionBean getError() {
        return error;
    }

    /**
     * Sets the error, that was caused by the creation of this document.
     *
     * @param error The error, that was caused by the creation of this document.
     */
    public void setError(@Nullable ExceptionBean error) {
        this.error = error;
    }
}
