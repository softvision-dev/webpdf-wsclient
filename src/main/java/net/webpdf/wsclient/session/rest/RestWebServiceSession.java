package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.documents.rest.RestWebServiceDocument;
import net.webpdf.wsclient.documents.rest.documentmanager.RestWebServiceDocumentManager;
import net.webpdf.wsclient.documents.rest.documentmanager.DocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class RestWebServiceSession extends AbstractRestSession<RestWebServiceDocument> {

    /**
     * Creates new {@link AbstractRestSession} instance
     *
     * @param url        base url for webPDF server
     * @param tlsContext Container configuring a https session.
     * @throws ResultException a {@link ResultException}
     */
    public RestWebServiceSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, tlsContext);
    }

    /**
     * Creates a new {@link DocumentManager} matching the given document type.
     *
     * @return The created {@link DocumentManager}.
     */
    @Override
    @NotNull
    protected DocumentManager<RestWebServiceDocument> createDocumentManager() {
        return new RestWebServiceDocumentManager(this);
    }
}
