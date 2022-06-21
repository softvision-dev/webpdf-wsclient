package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.documentmanager.DocumentManager;
import net.webpdf.wsclient.schema.beans.Token;
import net.webpdf.wsclient.schema.beans.User;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * <p>
 * A class implementing {@link RestSession} establishes and manages a {@link WebServiceProtocol#REST} connection with
 * a webPDF server.
 * </p>
 * <p>
 * <b>Information:</b> A {@link RestSession} provides simplified access to the uploaded {@link RestDocument}s via
 * a {@link DocumentManager}.
 * </p>
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} type used by this {@link RestSession}
 */
@SuppressWarnings("unused")
public interface RestSession<T_REST_DOCUMENT extends RestDocument> extends Session<T_REST_DOCUMENT> {

    /**
     * Returns the session {@link Token} of this {@link RestSession}.
     *
     * @return The session {@link Token} of this {@link RestSession}.
     */
    @Nullable Token getToken();

    /**
     * Returns the {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     *
     * @return The {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     */
    @NotNull CloseableHttpClient getHttpClient();

    /**
     * Returns the active {@link DocumentManager} of this {@link RestSession}.
     *
     * @return The active {@link DocumentManager} of this {@link RestSession}.
     */
    @NotNull DocumentManager<T_REST_DOCUMENT> getDocumentManager();

    /**
     * Login into the webPDF server and prepare a session {@link Token}.
     *
     * @throws IOException Shall be thrown in case of a HTTP access error.
     */
    void login() throws IOException;

    /**
     * Returns the {@link User} logged in via this {@link RestSession}.
     *
     * @return The {@link User} logged in via this {@link RestSession}.
     */
    @Nullable User getUser();

}
