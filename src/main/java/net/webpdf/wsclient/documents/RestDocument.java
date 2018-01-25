package net.webpdf.wsclient.documents;

public class RestDocument extends AbstractDocument {

    private String documentId;

    /**
     * Manages a REST document with the given document ID.
     *
     * @param documentId The document ID of the managed REST document.
     */
    public RestDocument(String documentId) {
        super(null);
        this.documentId = documentId;
    }

    /**
     * Returns the document ID of the managed REST document.
     *
     * @return The document ID of the managed REST document.
     */
    public String getSourceDocumentId() {
        return this.documentId;
    }
}
