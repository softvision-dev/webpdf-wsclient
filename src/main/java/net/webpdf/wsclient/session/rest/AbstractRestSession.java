package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.documents.rest.documentmanager.RestWebServiceDocumentManager;
import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.documentmanager.DocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.proxy.ProxyConfiguration;
import net.webpdf.wsclient.schema.beans.Token;
import net.webpdf.wsclient.schema.beans.UserCredentialsBean;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.DataFormat;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;

@SuppressWarnings("unused")
public abstract class AbstractRestSession<T_REST_DOCUMENT extends RestDocument>
        extends AbstractSession<T_REST_DOCUMENT> implements RestSession<T_REST_DOCUMENT> {

    private static final String INFO_PATH = "authentication/user/info/";
    private static final String LOGOUT_PATH = "authentication/user/logout/";
    private static final String LOGIN_PATH = "authentication/user/login/";
    @NotNull

    private final HttpClientBuilder httpClientBuilder;
    @Nullable
    private Token token = new Token();
    @Nullable
    private UserCredentialsBean userCredentials = new UserCredentialsBean();
    @Nullable
    private CloseableHttpClient httpClient;
    private final DocumentManager<T_REST_DOCUMENT> documentManager = createDocumentManager();

    /**
     * Creates new {@link AbstractRestSession} instance
     *
     * @param url        base url for webPDF server
     * @param tlsContext Container configuring a https session.
     * @throws ResultException a {@link ResultException}
     */
    public AbstractRestSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, WebServiceProtocol.REST, tlsContext);
        setDataFormat(DataFormat.JSON);

        RequestConfig clientConfig = RequestConfig.custom().setAuthenticationEnabled(true).build();
        this.httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(clientConfig)
                .setDefaultCredentialsProvider(getCredentialsProvider());
        if (getTlsContext() != null) {
            httpClientBuilder.setSSLContext(getTlsContext().getSslContext());
        }
    }

    /**
     * Returns the session {@link Token} of this session.
     *
     * @return The session {@link Token} of this session.
     */
    @Nullable
    @Override
    public Token getToken() {
        return this.token;
    }

    /**
     * Set a {@link ProxyConfiguration}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set.
     * @throws ResultException Shall be thrown, when resolving the proxy failed.
     */
    @Override
    public void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        super.setProxy(proxy);
        if (proxy != null) {
            httpClientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy.getHost()));
        }
    }

    /**
     * Returns the {@link CloseableHttpClient} connected to the server via this session.
     *
     * @return The {@link CloseableHttpClient} connected to the server via this session.
     */
    @NotNull
    @Override
    public CloseableHttpClient getHttpClient() {
        return this.httpClient == null ? this.httpClient = this.httpClientBuilder.build() : this.httpClient;
    }

    /**
     * Returns the {@link UserCredentialsBean} for the currently logged in user
     *
     * @return The {@link UserCredentialsBean} for the currently logged in user
     */
    @Nullable
    @Override
    public UserCredentialsBean getUserCredentials() {
        return userCredentials;
    }

    /**
     * Returns the {@link RestWebServiceDocumentManager} managing source and target document for this session.
     *
     * @return The {@link RestWebServiceDocumentManager} managing source and target document for this session.
     */
    @Override
    public @NotNull DocumentManager<T_REST_DOCUMENT> getDocumentManager() {
        return documentManager;
    }

    /**
     * Login into the server with an existing session token
     *
     * @param token the token to refresh the session with
     * @throws IOException HTTP access error
     */
    @Override
    public void login(@Nullable Token token) throws IOException {
        this.token = token;
        login();
    }

    /**
     * Login into the server and get a token
     *
     * @throws IOException HTTP access error
     */
    @Override
    public void login() throws IOException {

        this.token = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, LOGIN_PATH, null)
                .executeRequest(Token.class);

        this.userCredentials = HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, INFO_PATH, null)
                .executeRequest(UserCredentialsBean.class);
    }

    /**
     * Close session on webPDF server
     *
     * @throws java.io.IOException a {@link java.io.IOException}
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
     * Logout from the server
     *
     * @throws IOException HTTP access error
     */
    private void logout() throws IOException {

        HttpRestRequest.createRequest(this)
                .buildRequest(HttpMethod.GET, LOGOUT_PATH, null)
                .executeRequest(Object.class);

        this.token = null;
        this.userCredentials = null;
    }

    /**
     * Creates a new {@link DocumentManager} matching the given document type.
     *
     * @return The created {@link DocumentManager}.
     */
    @NotNull
    protected abstract DocumentManager<T_REST_DOCUMENT> createDocumentManager();
}
