package net.webpdf.wsclient.schema.beans;


import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings({"unused", "WeakerAccess"})
@XmlRootElement
public class DocumentFileBean {
    private String documentId;
    private String parentDocumentId;
    private long fileSize;
    private String mimeType;
    private String fileName;
    private String fileExtension;
    private int fileTypeId;
    private String fileTypeGroups;
    private String fileLastModified;
    private long numberOfPages;
    private boolean hasPermissionPassword;
    private boolean hasOpenPassword;
    private boolean isFileLocked;
    private boolean hasSignature;
    private String pdfaLevel;
    private String formType;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getParentDocumentId() {
        return parentDocumentId;
    }

    public void setParentDocumentId(String parentDocumentId) {
        this.parentDocumentId = parentDocumentId;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String documentName) {
        this.fileName = documentName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String documentExtension) {
        this.fileExtension = documentExtension;
    }

    public long getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(long numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public boolean isHasPermissionPassword() {
        return hasPermissionPassword;
    }

    public void setHasPermissionPassword(boolean hasPermissionPassword) {
        this.hasPermissionPassword = hasPermissionPassword;
    }

    public boolean isHasOpenPassword() {
        return hasOpenPassword;
    }

    public void setHasOpenPassword(boolean hasOpenPassword) {
        this.hasOpenPassword = hasOpenPassword;
    }

    public boolean isIsFileLocked() {
        return this.isFileLocked;
    }

    public void setIsFileLocked(boolean isFileLocked) {
        this.isFileLocked = isFileLocked;
    }

    public boolean isHasSignature() {
        return hasSignature;
    }

    public void setHasSignature(boolean hasSignature) {
        this.hasSignature = hasSignature;
    }

    public String getPdfaLevel() {
        return pdfaLevel;
    }

    public void setPdfaLevel(String pdfaLevel) {
        this.pdfaLevel = pdfaLevel;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public int getFileTypeId() {
        return fileTypeId;
    }

    public void setFileTypeId(int fileTypeId) {
        this.fileTypeId = fileTypeId;
    }

    public String getFileTypeGroups() {
        return this.fileTypeGroups;
    }

    public void setFileTypeGroups(String setFileTypeGroups) {
        this.fileTypeGroups = setFileTypeGroups;
    }

    public String getFileLastModified() {
        return this.fileLastModified;
    }

    public void setFileLastModified(String fileLastModified) {
        this.fileLastModified = fileLastModified;
    }

}
