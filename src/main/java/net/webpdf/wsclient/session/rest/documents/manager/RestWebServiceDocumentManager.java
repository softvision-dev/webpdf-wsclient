package net.webpdf.wsclient.session.rest.documents.manager;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.rest.RestSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @throws ResultException Shall be thrown, should creating the document fail.
     */
    @Override
    protected @NotNull RestWebServiceDocument createDocument(@Nullable String documentId) throws ResultException {
        if (documentId == null) {
            throw new ClientResultException(Error.INVALID_DOCUMENT);
        }

        return new RestWebServiceDocument(documentId);
    }

}
