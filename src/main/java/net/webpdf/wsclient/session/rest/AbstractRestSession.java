package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.connection.http.HttpAuthorizationProvider;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.manager.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.access.token.Token;
import net.webpdf.wsclient.session.access.token.SessionToken;
import net.webpdf.wsclient.schema.beans.User;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.ChainElement;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

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
        extends AbstractSession implements RestSession<T_REST_DOCUMENT> {

    private static final @NotNull String INFO_PATH = "authentication/user/info/";
    private static final @NotNull String LOGOUT_PATH = "authentication/user/logout/";
    private static final @NotNull String LOGIN_PATH = "authentication/user/login/";
    private static final @NotNull String REFRESH_PATH = "authentication/user/refresh/";
    private final @NotNull HttpClientBuilder httpClientBuilder;
    private final @Nullable User user;
    private final @NotNull CloseableHttpClient httpClient;
    private final @NotNull AtomicReference<Token> token = new AtomicReference<>(new SessionToken());
    private final @NotNull DocumentManager<T_REST_DOCUMENT> documentManager = createDocumentManager();
    private final @NotNull AdministrationManager<T_REST_DOCUMENT> administrationManager = createAdministrationManager();

    /**
     * Creates a new {@link AbstractRestSession} instance providing connection information, authorization objects and
     * a {@link DocumentManager} for a webPDF server-client {@link RestSession}.
     *
     * @param url         The {@link URL} of the webPDF server
     * @param tlsContext  The {@link TLSContext} used for this https {@link RestSession}.
     *                    ({@code null} in case an unencrypted HTTP {@link RestSession} shall be created.)
     * @param credentials The {@link Credentials} used for authorization of this session.
     * @throws ResultException Shall be thrown, in case establishing the {@link RestSession} failed.
     */
    public AbstractRestSession(@NotNull URL url, @Nullable TLSContext tlsContext, Credentials credentials)
            throws ResultException {
        super(url, WebServiceProtocol.REST, tlsContext, credentials);

        RequestConfig clientConfig = RequestConfig.custom().setAuthenticationEnabled(true).build();
        HttpAuthorizationProvider authorizationProvider = new HttpAuthorizationProvider(this);
        httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(clientConfig)
                .setDefaultCredentialsProvider(getCredentialsProvider())
                .addExecInterceptorAfter(ChainElement.REDIRECT.name(),
                        authorizationProvider.getExecChainHandlerName(), authorizationProvider);
        if (getTlsContext() != null) {
            LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    getTlsContext().getSslContext(), (hostname, session) -> true);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();
            httpClientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager(registry));
        }
        this.httpClient = this.httpClientBuilder.build();
        if (getCredentials() == null || getCredentials() instanceof UsernamePasswordCredentials) {
            this.token.set(HttpRestRequest.createRequest(this)
                    .buildRequest(HttpMethod.GET, LOGIN_PATH, null)
                    .executeRequest(SessionToken.class));
        }
        this.user = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, INFO_PATH, null)
                .executeRequest(User.class);
    }

    /**
     * Returns the session {@link Token} of this {@link RestSession}.
     *
     * @return The session {@link Token} of this {@link RestSession}.
     */
    @Override
    public @Nullable Token getToken() {
        return this.token.get();
    }

    /**
     * Returns the {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     *
     * @return The {@link CloseableHttpClient} connected to the webPDF server via this {@link RestSession}.
     */
    @Override
    public @NotNull CloseableHttpClient getHttpClient() {
        return this.httpClient;
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
     * Returns the active {@link AdministrationManager} of this {@link RestSession}.
     *
     * @return The active {@link AdministrationManager} of this {@link RestSession}.
     */
    @Override
    public @NotNull AdministrationManager<T_REST_DOCUMENT> getAdministrationManager() {
        return administrationManager;
    }

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
    @Override
    public synchronized void refresh() throws ResultException {
        if (this.token.get() instanceof SessionToken) {
            this.token.set(((SessionToken) this.token.get()).provideRefreshToken());
            this.token.set(HttpRestRequest.createRequest(this)
                    .buildRequest(HttpMethod.POST, REFRESH_PATH, null)
                    .executeRequest(SessionToken.class));
        } else {
            throw new ClientResultException(Error.FORBIDDEN_TOKEN_REFRESH);
        }
    }

    /**
     * Returns the {@link User} logged in via this {@link RestSession}.
     *
     * @return The {@link User} logged in via this {@link RestSession}.
     */
    @Override
    public @Nullable User getUser() {
        return user;
    }

    /**
     * Set a {@link ProxyConfiguration} for this {@link RestSession}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set for this {@link RestSession}.
     * @throws ResultException Shall be thrown, when resolving the {@link ProxyConfiguration} failed.
     */
    @Override
    public synchronized void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        super.setProxy(proxy);
        if (proxy != null) {
            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy.getHost()));
        }
    }

    /**
     * Close the {@link RestSession}.
     *
     * @throws ResultException Shall be thrown, if closing the {@link RestSession} failed.
     */
    @Override
    public void close() throws ResultException {
        ResultException resultException = null;
        try {
            if (!this.token.get().getToken().isEmpty()) {
                HttpRestRequest.createRequest(this)
                        .buildRequest(HttpMethod.GET, LOGOUT_PATH, null)
                        .executeRequest(Object.class);
            }
        } finally {
            try {
                this.httpClient.close();
            } catch (IOException ex) {
                resultException = new ClientResultException(Error.HTTP_IO_ERROR, ex);
            }
        }
        if (resultException != null) {
            throw resultException;
        }
    }

    /**
     * Creates a new {@link DocumentManager} matching this {@link RestSession}.
     *
     * @return The created {@link DocumentManager}.
     */
    protected abstract @NotNull DocumentManager<T_REST_DOCUMENT> createDocumentManager();

    /**
     * Creates a new {@link AdministrationManager} matching this {@link RestSession}.
     *
     * @return The created {@link AdministrationManager}.
     */
    protected abstract @NotNull AdministrationManager<T_REST_DOCUMENT> createAdministrationManager();

}
