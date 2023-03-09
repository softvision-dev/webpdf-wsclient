package net.webpdf.wsclient.session.rest.documents;

import net.webpdf.wsclient.session.rest.RestSession;
import org.jetbrains.annotations.NotNull;

/**
 * An instance of {@link RestWebServiceDocumentManager} allows to monitor and interact with the
 * {@link RestWebServiceDocument}s uploaded to a {@link RestSession} of the webPDF server.
 */
public class RestWebServiceDocumentManager extends AbstractDocumentManager<RestWebServiceDocument> {

    /**
     * Initializes a {@link RestWebServiceDocumentManager} for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a {@link RestWebServiceDocumentManager} shall be created for.
     */
    public RestWebServiceDocumentManager(@NotNull RestSession<RestWebServiceDocument> session) {
        super(session);
    }

    /**
     * Creates a new {@link RestWebServiceDocument} for the given document ID.
     *
     * @param documentId The document ID a matching {@link RestWebServiceDocument} shall be created for.
     * @return The created {@link RestWebServiceDocument}.
     */
    @Override
    protected @NotNull RestWebServiceDocument createDocument(@NotNull String documentId) {
        return new RestWebServiceDocument(new RestWebServiceDocumentState(documentId, this));
    }

    /**
     * Requests access to the internal {@link RestDocumentState}.
     *
     * @param document The {@link RestDocument} to request access for.
     * @return The internal {@link RestDocumentState}.
     */
    @Override
    protected @NotNull RestWebServiceDocumentState accessInternalState(@NotNull RestWebServiceDocument document) {
        return document.accessInternalState();
    }

}
