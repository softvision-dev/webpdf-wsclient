package net.webpdf.wsclient.documents.rest.documentmanager;

import net.webpdf.wsclient.documents.rest.RestWebServiceDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Document manager - session bounded
 */
public class RestWebServiceDocumentManager extends AbstractDocumentManager<RestWebServiceDocument> {
    /**
     * Initializes a document manager for the given {@link RestSession}.
     *
     * @param session The {@link RestSession} a document manager shall be created for.
     */
    public RestWebServiceDocumentManager(@NotNull RestSession<RestWebServiceDocument> session) {
        super(session);
    }

    @Override
    @NotNull
    protected RestWebServiceDocument createDocument(@Nullable String id) {
        return new RestWebServiceDocument(id);
    }
}
