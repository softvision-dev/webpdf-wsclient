package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import net.webpdf.wsclient.session.rest.documents.manager.DocumentManager;
import net.webpdf.wsclient.session.rest.documents.manager.RestWebServiceDocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.http.client.config.RequestConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * <p>
 * An instance of {@link RestWebServiceSession} establishes and manages a {@link WebServiceProtocol#REST} connection
 * with a webPDF server.
 * </p>
 * <p>
 * <b>Information:</b> A {@link RestWebServiceSession} provides simplified access to the uploaded {@link RestDocument}s
 * via a specialized {@link RestWebServiceDocumentManager}.
 * </p>
 */
@SuppressWarnings("unused")
public class RestWebServiceSession extends AbstractRestSession<RestWebServiceDocument> {


    /**
     * Creates a new {@link RestWebServiceSession} instance providing connection information, authorization objects and
     * a {@link RestWebServiceDocumentManager} for a webPDF server-client {@link RestSession}.
     *
     * @param url        The {@link URL} of the webPDF server
     * @param tlsContext The {@link TLSContext} used for this https session.
     *                   ({@code null} in case an unencrypted HTTP session shall be created.)
     * @throws ResultException Shall be thrown, in case establishing the session failed.
     */
    public RestWebServiceSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, tlsContext);

        RequestConfig clientConfig = RequestConfig.custom().setAuthenticationEnabled(true).build();
    }

    /**
     * Creates a new {@link RestWebServiceDocumentManager}.
     *
     * @return The created {@link RestWebServiceDocumentManager}.
     */
    @Override
    protected @NotNull DocumentManager<RestWebServiceDocument> createDocumentManager() {
        return new RestWebServiceDocumentManager(this);
    }

    /**
     * Creates a new {@link AdministrationManager} matching this {@link RestSession}.
     *
     * @return The created {@link AdministrationManager}.
     */
    @Override
    protected @NotNull AdministrationManager<RestWebServiceDocument> createAdministrationManager() {
        return new AdministrationManager<>(this);
    }

}
