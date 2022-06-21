package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.documentmanager.DocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.schema.beans.Token;
import net.webpdf.wsclient.schema.beans.User;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.proxy.ProxyConfiguration;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

/**
 * <p>
 * An instance of {@link AbstractRestSession} establishes and manages a {@link WebServiceProtocol#REST} connection
 * with a webPDF server.
 * </p>
 * <p>
 * <b>Information:</b> A {@link RestSession} provides simplified access to the uploaded {@link RestDocument}s via
 * a {@link DocumentManager}.
 * </p>
 *
 * @param <T_REST_DOCUMENT> The {@link RestDocument} type used by this {@link RestSession}.
 */
public abstract class AbstractRestSession<T_REST_DOCUMENT extends RestDocument>
        extends AbstractSession<T_REST_DOCUMENT> implements RestSession<T_REST_DOCUMENT> {

    private static final @NotNull String INFO_PATH = "authentication/user/info/";
    private static final @NotNull String LOGOUT_PATH = "authentication/user/logout/";
    private static final @NotNull String LOGIN_PATH = "authentication/user/login/";
    private final @NotNull HttpClientBuilder httpClientBuilder;
    private @Nullable Token token = new Token();
    private @Nullable User userCredentials = new User();
    private @Nullable CloseableHttpClient httpClient;
    private final @NotNull DocumentManager<T_REST_DOCUMENT> documentManager = createDocumentManager();

    /**
     * Creates a new {@link AbstractRestSession} instance providing connection information, authorization objects and
     * a {@link DocumentManager} for a webPDF server-client {@link RestSession}.
     *
     * @param url        The {@link URL} of the webPDF server
     * @param tlsContext The {@link TLSContext} used for this https {@link RestSession}.
     *                   ({@code null} in case an unencrypted HTTP {@link RestSession} shall be created.)
     * @throws ResultException Shall be thrown, in case establishing the {@link RestSession} failed.
     */
    public AbstractRestSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, WebServiceProtocol.REST, tlsContext);

        RequestConfig clientConfig = RequestConfig.custom().setAuthenticationEnabled(true).build();
        this.httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(clientConfig)
                .setDefaultCredentialsProvider(getCredentialsProvider());
        if (getTlsContext() != null) {
            httpClientBuilder.setSSLContext(getTlsContext().getSslContext());
        }
    }

    /**
     * Returns the session {@link Token} of this {@link RestSession}.
     *
     * @return The session {@link Token} of this {@link RestSession}.
     */
    @Override
    public @Nullable Token getToken() {
        return this.token;
    }

    /**
     * Returns the {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     *
     * @return The {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     */
    @Override
    public @NotNull CloseableHttpClient getHttpClient() {
        return this.httpClient == null ? this.httpClient = this.httpClientBuilder.build() : this.httpClient;
    }

    /**
     * Returns the active {@link DocumentManager} of this {@link RestSession}.
     *
     * @return The active {@link DocumentManager} of this {@link RestSession}.
     */
    @Override
    public @NotNull DocumentManager<T_REST_DOCUMENT> getDocumentManager() {
        return documentManager;
    }

    /**
     * Login into the webPDF server and prepare a session {@link Token}.
     *
     * @throws IOException Shall be thrown in case of a HTTP access error.
     */
    @Override
    public void login() throws IOException {

        this.token = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, LOGIN_PATH, null)
                .executeRequest(Token.class);

        this.userCredentials = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, INFO_PATH, null)
                .executeRequest(User.class);
    }

    /**
     * Returns the {@link User} logged in via this {@link RestSession}.
     *
     * @return The {@link User} logged in via this {@link RestSession}.
     */
    @Override
    public @Nullable User getUser() {
        return userCredentials;
    }

    /**
     * Set a {@link ProxyConfiguration} for this {@link RestSession}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set for this {@link RestSession}.
     * @throws ResultException Shall be thrown, when resolving the {@link ProxyConfiguration} failed.
     */
    @Override
    public void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        super.setProxy(proxy);
        if (proxy != null) {
            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy.getHost()));
        }
    }

    /**
     * Close the {@link RestSession}.
     *
     * @throws java.io.IOException Shall be thrown, if closing the {@link RestSession} failed.
     */
    @Override
    public void close() throws IOException {
        try {
            if (this.token != null && !this.token.getToken().isEmpty()) {
                logout();
            }
        } catch (ResultException ex) {
            throw new IOException("Unable to logout from server", ex);
        } finally {
            if (this.httpClient != null) {
                this.httpClient.close();
            }
        }
    }

    /**
     * Logout from the {@link RestSession}.
     *
     * @throws IOException Shall be thrown in case of a HTTP access error.
     */
    private void logout() throws IOException {

        HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, LOGOUT_PATH, null)
                .executeRequest(Object.class);

        this.token = null;
        this.userCredentials = null;
    }

    /**
     * Creates a new {@link DocumentManager} matching this {@link RestSession}.
     *
     * @return The created {@link DocumentManager}.
     */
    protected abstract @NotNull DocumentManager<T_REST_DOCUMENT> createDocumentManager();

}
