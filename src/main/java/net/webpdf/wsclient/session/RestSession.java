package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.documents.DocumentManager;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.HttpMethod;
import net.webpdf.wsclient.http.HttpRestRequest;
import net.webpdf.wsclient.schema.beans.Token;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class RestSession extends AbstractSession {
    private static final String LOGOUT_PATH = "authentication/user/logout/";
    private static final String LOGIN_PATH = "authentication/user/login/";
    private Token token = new Token();
    private CloseableHttpClient httpClient;
    private DocumentManager documentManager = new DocumentManager(this);
    private boolean allowSelfSigned = true;
    private File trustStore = null;
    private String trustStorePassword = null;

    RestSession(URL url) throws ResultException {
        super(url, WebServiceProtocol.REST);
        this.dataFormat = DataFormat.JSON;

        RequestConfig clientConfig = RequestConfig.custom()
                                         .setAuthenticationEnabled(true)
                                         .build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                                                  .setDefaultRequestConfig(clientConfig)
                                                  .setDefaultCredentialsProvider(this.credentialsProvider);
        httpClient = httpClientBuilder.build();
    }

    /**
     * Returns the session {@link Token} of this session.
     *
     * @return The session {@link Token} of this session.
     */
    public Token getToken() {
        return this.token;
    }

    /**
     * Returns the {@link CloseableHttpClient} connected to the server via this session.
     *
     * @return The {@link CloseableHttpClient} connected to the server via this session.
     */
    public CloseableHttpClient getHttpClient() {
        return this.httpClient;
    }

    /**
     * Returns the {@link DocumentManager} managing source and target document for this session.
     *
     * @return The {@link DocumentManager} managing source and target document for this session.
     */
    public DocumentManager getDocumentManager() {
        return documentManager;
    }

    /**
     * Sets the {@link DataFormat} accepted by this session.
     *
     * @param dataFormat The {@link DataFormat} accepted by this session.
     */
    public void setDataFormat(DataFormat dataFormat) {
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
     * Login into the server and get a token
     *
     * @throws IOException HTTP access error
     */
    public void login() throws IOException {

        this.token = HttpRestRequest.createRequest(this)
                         .buildRequest(HttpMethod.GET, LOGIN_PATH, null)
                         .executeRequest(Token.class);
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
    }
}
