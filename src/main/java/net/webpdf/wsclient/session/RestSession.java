package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.documents.DocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.proxy.ProxyConfiguration;
import net.webpdf.wsclient.schema.beans.Token;
import net.webpdf.wsclient.schema.beans.User;
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
public class RestSession extends AbstractSession {

    private static final String INFO_PATH = "authentication/user/info/";
    private static final String LOGOUT_PATH = "authentication/user/logout/";
    private static final String LOGIN_PATH = "authentication/user/login/";
    @NotNull
    private final HttpClientBuilder httpClientBuilder;
    @NotNull
    private final DocumentManager documentManager = new DocumentManager(this);
    @Nullable
    private Token token;
    @Nullable
    private User user;
    @Nullable
    private CloseableHttpClient httpClient;

    /**
     * Creates new {@link RestSession} instance
     *
     * @param url        base url for webPDF server
     * @param tlsContext Container configuring a https session.
     * @throws ResultException a {@link ResultException}
     */
    RestSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, WebServiceProtocol.REST, tlsContext);
        this.dataFormat = DataFormat.JSON;

        RequestConfig clientConfig = RequestConfig.custom().setAuthenticationEnabled(true).build();
        this.httpClientBuilder = HttpClients.custom()
                .setDefaultRequestConfig(clientConfig)
                .setDefaultCredentialsProvider(this.credentialsProvider);
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
    public CloseableHttpClient getHttpClient() {
        return this.httpClient == null ? this.httpClient = this.httpClientBuilder.build() : this.httpClient;
    }

    /**
     * Returns the {@link DocumentManager} managing source and target document for this session.
     *
     * @return The {@link DocumentManager} managing source and target document for this session.
     */
    @NotNull
    public DocumentManager getDocumentManager() {
        return documentManager;
    }

    /**
     * Sets the {@link DataFormat} accepted by this session.
     *
     * @param dataFormat The {@link DataFormat} accepted by this session.
     */
    public void setDataFormat(@Nullable DataFormat dataFormat) {
        this.dataFormat = dataFormat;
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
     * Login into the server with an existing session token
     *
     * @param token the token to refresh the session with
     * @throws IOException HTTP access error
     */
    public void login(@Nullable Token token) throws IOException {

        this.token = token;

        try {
            this.token = HttpRestRequest.createRequest(this)
                    .buildRequest(HttpMethod.GET, LOGIN_PATH, null)
                    .executeRequest(Token.class);

            this.user = HttpRestRequest.createRequest(this)
                    .buildRequest(HttpMethod.GET, INFO_PATH, null)
                    .executeRequest(User.class);

            if (token != null) {
                this.documentManager.sync();
            }
        } catch (IOException ex) {
            this.token = null;
            throw ex;
        }
    }

    /**
     * Login into the server and get a token
     *
     * @throws IOException HTTP access error
     */
    public void login() throws IOException {
        login(null);
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
        this.user = null;
    }

    /**
     * Returns the {@link User} settings for the current session
     *
     * @return The {@link User} settings for the current session
     */
    @Nullable
    public User getUser() {
        return user;
    }
}
