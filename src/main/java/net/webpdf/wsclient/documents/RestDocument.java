package net.webpdf.wsclient.documents;

import org.jetbrains.annotations.Nullable;

public class RestDocument extends AbstractDocument {

    @Nullable
    private String documentId;

    /**
     * Manages a REST document with the given document ID.
     *
     * @param documentId The document ID of the managed REST document.
     */
    public RestDocument(@Nullable String documentId) {
        super(null);
        this.documentId = documentId;
    }

    /**
     * Returns the document ID of the managed REST document.
     *
     * @return The document ID of the managed REST document.
     */
    @Nullable
    public String getSourceDocumentId() {
        return this.documentId;
    }

}
