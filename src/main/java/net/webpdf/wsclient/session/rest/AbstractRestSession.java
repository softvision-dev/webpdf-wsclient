package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.ServerContext;
import net.webpdf.wsclient.session.connection.ServerContextSettings;
import net.webpdf.wsclient.session.connection.http.HttpAuthorizationHandler;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.User;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
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
    private final @Nullable User user;
    private final @NotNull CloseableHttpClient httpClient;
    private final @NotNull DocumentManager<T_REST_DOCUMENT> documentManager = createDocumentManager();
    private final @NotNull AdministrationManager<T_REST_DOCUMENT> administrationManager = createAdministrationManager();

    /**
     * Creates a new {@link AbstractRestSession} instance providing connection information, authorization objects and
     * a {@link DocumentManager} for a webPDF server-client {@link RestSession}.
     *
     * @param serverContext The {@link ServerContext} initializing the {@link ServerContextSettings} of this
     *                      {@link RestSession}.
     * @param authProvider  The {@link AuthProvider} for authentication/authorization of this {@link RestSession}.
     * @throws ResultException Shall be thrown, in case establishing the {@link RestSession} failed.
     */
    public AbstractRestSession(
            @NotNull ServerContext serverContext, @NotNull AuthProvider authProvider) throws ResultException {
        super(WebServiceProtocol.REST, serverContext, authProvider);
        RequestConfig clientConfig = RequestConfig.custom().setAuthenticationEnabled(true).build();
        HttpAuthorizationHandler requestAuthorization = new HttpAuthorizationHandler(this);
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(clientConfig)
                .addExecInterceptorAfter(ChainElement.REDIRECT.name(),
                        requestAuthorization.getExecChainHandlerName(), requestAuthorization);
        if (getServerContext().getProxy() != null) {
            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(
                    getServerContext().getProxy().getHost()));
        }
        if (getServerContext().getTlsContext() != null) {
            LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    getServerContext().getTlsContext().getSslContext(), (hostname, session) -> true);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();
            httpClientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager(registry));
        }
        this.httpClient = httpClientBuilder.build();
        this.user = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, INFO_PATH, null)
                .executeRequest(User.class);
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
     * Returns the {@link User} logged in via this {@link RestSession}.
     *
     * @return The {@link User} logged in via this {@link RestSession}.
     */
    @Override
    public @Nullable User getUser() {
        return user;
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
            HttpRestRequest.createRequest(this)
                    .buildRequest(HttpMethod.GET, LOGOUT_PATH, null)
                    .executeRequest(Object.class);
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