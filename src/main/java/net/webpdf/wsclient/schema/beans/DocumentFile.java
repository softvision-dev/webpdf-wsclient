package net.webpdf.wsclient.schema.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.schema.extraction.info.DocumentType;
import net.webpdf.wsclient.session.Session;
import org.apache.http.HttpEntity;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * <p>
 * An instance of {@link DocumentFile} provides information about a {@link Document} uploaded to a {@link Session}
 * of the webPDF server.
 * </p>
 * <p>
 * Those information include the referencable document ID of the {@link Document}, the {@link Document}´s size, name,
 * extension MIME type etc.
 * </p>
 * <p>
 * The class {@link DocumentFile} serves as a data transfer object {@link HttpEntity} for such documents. The class
 * {@link Document} wraps such {@link DocumentFile}s for easier handling by the webPDF wsclient.
 * </p>
 */
@SuppressWarnings({"unused"})
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "documentFile")
public class DocumentFile implements Serializable {

    private @Nullable String documentId;
    private @Nullable String parentDocumentId;
    private long fileSize;
    private @Nullable String mimeType;
    private @Nullable String fileName;
    private @Nullable String fileExtension;
    private int fileTypeId;
    private @Nullable String fileTypeGroups;
    private @Nullable String fileLastModified;
    private boolean isFileLocked;
    private @Nullable DocumentType metadata;
    private @Nullable Failure error = new Failure();

    /**
     * Returns the document ID of this document.
     *
     * @return The document ID of this document.
     */
    @XmlElement
    public @Nullable String getDocumentId() {
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
    public @Nullable String getParentDocumentId() {
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
    public @Nullable String getMimeType() {
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
    public @Nullable String getFileName() {
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
    public @Nullable String getFileExtension() {
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
    public @Nullable String getFileTypeGroups() {
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
    public @Nullable String getFileLastModified() {
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
     * Return the {@link DocumentType} metadata of this document.
     *
     * @return The {@link DocumentType} metadata of this document.
     */
    @XmlElement
    public @Nullable DocumentType getMetadata() {
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
     * Returns the stored {@link Failure}, that was caused by the creation attempt of this document.
     *
     * @return The {@link Failure}, caused by the attempted creation of this document.
     */
    @XmlElement
    public @Nullable Failure getError() {
        return error;
    }

    /**
     * Sets the {@link Failure}, that was caused by the creation of this document.
     *
     * @param error The {@link Failure}, that was caused by the creation of this document.
     */
    public void setError(@Nullable Failure error) {
        this.error = error;
    }

}
