package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.session.administration.AdministrationManager;
import net.webpdf.wsclient.session.documents.rest.RestDocument;
import net.webpdf.wsclient.session.documents.rest.manager.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.credentials.token.SessionToken;
import net.webpdf.wsclient.session.credentials.token.Token;
import net.webpdf.wsclient.session.credentials.token.TokenProvider;
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
     * Returns the active {@link AdministrationManager} of this {@link RestSession}.
     *
     * @return The active {@link AdministrationManager} of this {@link RestSession}.
     */
    @NotNull AdministrationManager<T_REST_DOCUMENT> getAdministrationManager();

    /**
     * Login into the webPDF server and prepare a session {@link Token}.
     *
     * @throws IOException Shall be thrown in case of a HTTP access error.
     */
    void login() throws IOException;

    /**
     * Login into the server using the given {@link Token}.
     *
     * @param token The {@link Token} to provide a session for.
     * @throws IOException Shall be thrown in case of a HTTP access error.
     */
    void login(@Nullable Token token) throws IOException;

    /**
     * Login into the server using the given {@link TokenProvider}.
     *
     * @param tokenProvider The {@link TokenProvider} to provide a session for.
     * @throws IOException HTTP access error.
     */
    void login(@Nullable TokenProvider<?> tokenProvider) throws IOException;

    /**
     * <p>
     * Refreshes the {@link RestSession} and prevents it from expiring. Also refreshes the currently set
     * {@link SessionToken}.
     * </p>
     * <p>
     * <b>Important:</b> This may only be used to refresh {@link SessionToken}s, attempts to refresh {@link Session}s
     * based on other {@link Token} types in this manner, shall result in a {@link Error#FORBIDDEN_TOKEN_REFRESH}
     * error.
     * </p>
     *
     * @throws ResultException Shall be thrown, when refreshing the session failed.
     */
    void refresh() throws ResultException;

    /**
     * Returns the {@link User} logged in via this {@link RestSession}.
     *
     * @return The {@link User} logged in via this {@link RestSession}.
     */
    @Nullable User getUser();

}
