package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.SessionAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.SessionContextSettings;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.RestWebService;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;

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
public class RestWebServiceSession extends AbstractRestSession<RestWebServiceDocument> {

    /**
     * <p>
     * Creates a new {@link RestWebServiceSession} instance providing connection information, authorization objects and
     * a {@link RestWebServiceDocumentManager} for a webPDF server-client {@link RestSession}.
     * </p>
     * <p>
     * <b>Be Aware:</b> Neither {@link SessionContext}, nor {@link SessionAuthProvider} are required to serve multiple
     * {@link Session}s at a time. It is expected to create a new {@link SessionContext} and {@link SessionAuthProvider}
     * per {@link Session} you create.
     * </p>
     *
     * @param serverContext The {@link SessionContext} initializing the {@link SessionContextSettings} of this
     *                      {@link RestSession}.
     * @param authProvider  The {@link SessionAuthProvider} for authentication/authorization of this {@link RestSession}.
     * @throws ResultException Shall be thrown, in case establishing the session failed.
     */
    public RestWebServiceSession(
            @NotNull SessionContext serverContext, @NotNull SessionAuthProvider authProvider) throws ResultException {
        super(serverContext, authProvider);
    }

    /**
     * This is a shortcut for {@link DocumentManager#uploadDocument(InputStream, String)} and uploads the given source
     * to the webPDF server.
     *
     * @param source The {@link InputStream} to upload to the webPDF server.
     * @return The uploaded {@link RestWebServiceDocument}.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @Override
    public @NotNull RestWebServiceDocument uploadDocument(@NotNull InputStream source, @NotNull String documentName)
            throws ResultException {
        return getDocumentManager().uploadDocument(source, documentName);
    }

    /**
     * This is a shortcut for {@link DocumentManager#uploadDocument(File)} and uploads the given source to the
     * webPDF server.
     *
     * @param source The {@link InputStream} to upload to the webPDF server.
     * @return The uploaded {@link RestWebServiceDocument}.
     * @throws ResultException Shall be thrown, should the upload have failed.
     */
    @Override
    public @NotNull RestWebServiceDocument uploadDocument(@NotNull File source) throws ResultException {
        return getDocumentManager().uploadDocument(source);
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

    /**
     * Creates a matching {@link RestWebService} instance to execute a webPDF operation for the current session.
     *
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @return A matching {@link RestWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link RestWebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T_WEBSERVICE extends RestWebService<?, ?, ?>> @NotNull T_WEBSERVICE createWSInstance(
            @NotNull WebServiceType webServiceType) throws ResultException {
        return (T_WEBSERVICE) WebServiceFactory.createInstance(this, webServiceType);
    }
}
