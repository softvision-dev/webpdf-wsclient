package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.openapi.AuthUserCertificates;
import net.webpdf.wsclient.openapi.AuthUserCredentials;
import net.webpdf.wsclient.openapi.KeyStorePassword;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.SessionContextSettings;
import net.webpdf.wsclient.session.connection.http.HttpAuthorizationHandler;
import net.webpdf.wsclient.session.rest.administration.AdministrationManager;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.tools.SerializeHelper;
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
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

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
    private static final @NotNull String CERTIFICATES_PATH = "authentication/user/certificates/";
    private final @Nullable AuthUserCredentials user;
    private final @NotNull CloseableHttpClient httpClient;
    private final @NotNull DocumentManager<T_REST_DOCUMENT> documentManager = createDocumentManager();
    private final @NotNull AdministrationManager<T_REST_DOCUMENT> administrationManager = createAdministrationManager();
    private @Nullable AuthUserCertificates certificates;

    /**
     * <p>
     * Creates a new {@link AbstractRestSession} instance providing connection information, authorization objects and
     * a {@link DocumentManager} for a webPDF server-client {@link RestSession}.
     * </p>
     * <p>
     * <b>Be Aware:</b> Neither {@link SessionContext}, nor {@link AuthProvider} are required to serve multiple
     * {@link Session}s at a time. It is expected to create a new {@link SessionContext} and {@link AuthProvider}
     * per {@link Session} you create.
     * </p>
     *
     * @param serverContext The {@link SessionContext} initializing the {@link SessionContextSettings} of this
     *                      {@link RestSession}.
     * @param authProvider  The {@link AuthProvider} for authentication/authorization of this {@link RestSession}.
     * @throws ResultException Shall be thrown, in case establishing the {@link RestSession} failed.
     */
    public AbstractRestSession(
            @NotNull SessionContext serverContext, @NotNull AuthProvider authProvider) throws ResultException {
        super(WebServiceProtocol.REST, serverContext, authProvider);
        RequestConfig clientConfig = RequestConfig.custom().setAuthenticationEnabled(true).build();
        HttpAuthorizationHandler requestAuthorization = new HttpAuthorizationHandler(this);
        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(clientConfig)
                .addExecInterceptorAfter(ChainElement.REDIRECT.name(),
                        requestAuthorization.getExecChainHandlerName(), requestAuthorization);
        if (getSessionContext().getProxy() != null) {
            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(
                    getSessionContext().getProxy().getHost()));
        }
        if (serverContext.getTlsContext() != null) {
            LayeredConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    serverContext.getTlsContext().create(), (hostname, session) -> true);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();
            httpClientBuilder.setConnectionManager(new PoolingHttpClientConnectionManager(registry));
        }
        this.httpClient = httpClientBuilder.build();
        this.user = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, INFO_PATH)
                .executeRequest(AuthUserCredentials.class);
        this.certificates = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, CERTIFICATES_PATH)
                .executeRequest(AuthUserCertificates.class);
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
     * Returns the {@link AuthUserCredentials} logged in via this {@link RestSession}.
     *
     * @return The {@link AuthUserCredentials} logged in via this {@link RestSession}.
     */
    @Override
    public @Nullable AuthUserCredentials getUser() {
        return user;
    }

    /**
     * Returns the {@link AuthUserCertificates} of the currently logged-in user of this {@link RestSession}.
     *
     * @return The {@link AuthUserCertificates} of the currently logged-in user of this {@link RestSession}.
     */
    @Override
    public @Nullable AuthUserCertificates getCertificates() {
        return certificates;
    }

    /**
     * Updates the {@link KeyStorePassword}s for specific keystore and returns the
     * {@link AuthUserCertificates} for the currently logged-in user afterward.
     *
     * @param keystoreName     The name of the keystore to be updated.
     * @param keyStorePassword The {@link KeyStorePassword} to unlock the certificates with.
     * @return The {@link AuthUserCertificates} of the logged-in user in this {@link RestSession}.
     * @throws ResultException Shall be thrown, if the request failed.
     */
    @Override
    public @Nullable AuthUserCertificates updateCertificates(
            String keystoreName, KeyStorePassword keyStorePassword
    ) throws ResultException {
        this.certificates = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.PUT, "authentication/user/certificates/passwords/" + keystoreName,
                        prepareHttpEntity(keyStorePassword))
                .executeRequest(AuthUserCertificates.class);

        return this.certificates;
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
                    .buildRequest(HttpMethod.GET, LOGOUT_PATH)
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
     * Prepares a {@link HttpEntity} for internal requests to the webPDF server.
     *
     * @param parameter The parameters, that shall be used for the request.
     * @param <T>       The parameter type (data transfer object/bean) that shall be used.
     * @return The resulting state of the data transfer object.
     * @throws ResultException Shall be thrown, should the {@link HttpEntity} creation fail.
     */
    private <T> @NotNull HttpEntity prepareHttpEntity(@NotNull T parameter) throws ResultException {
        try {
            return new StringEntity(SerializeHelper.toJSON(parameter),
                    ContentType.create(DataFormat.JSON.getMimeType(), StandardCharsets.UTF_8));
        } catch (UnsupportedCharsetException ex) {
            throw new ClientResultException(Error.XML_OR_JSON_CONVERSION_FAILURE, ex);
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