package net.webpdf.wsclient.documents;

public class RestDocument extends AbstractDocument {

    private String documentId;

    public RestDocument(String documentId) {
        super(null);
        this.documentId = documentId;
    }

    public String getSourceDocumentId() {
        return this.documentId;
    }
}
