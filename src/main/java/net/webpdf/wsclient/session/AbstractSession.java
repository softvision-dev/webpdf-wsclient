package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.documents.DocumentManager;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Session class which contains connection and authorization objects and a {@link DocumentManager} instance
 */
abstract class AbstractSession implements Session {

    @NotNull
    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    @Nullable
    DataFormat dataFormat = DataFormat.JSON;
    @NotNull
    private String basePath;
    @NotNull
    private WebServiceProtocol webServiceProtocol;
    @NotNull
    private URI baseUrl;
    @Nullable
    private AuthScope authScope;
    @Nullable
    private Credentials credentials;
    @Nullable
    private TLSContext tlsContext;

    /**
     * Creates new {@link AbstractSession} instance
     *
     * @param url                base url for webPDF server
     * @param webServiceProtocol protocol for Web service operation
     * @param tlsContext         Container configuring a https session.
     * @throws ResultException a {@link ResultException}
     */
    AbstractSession(@NotNull URL url, @NotNull WebServiceProtocol webServiceProtocol, @Nullable TLSContext tlsContext) throws ResultException {
        this.webServiceProtocol = webServiceProtocol;
        this.tlsContext = tlsContext;
        this.basePath = webServiceProtocol.equals(WebServiceProtocol.SOAP) ? "soap/" : "rest/";
        String toUrl = url.toString();
        if (!toUrl.endsWith("/")) {
            toUrl = toUrl + "/";
        }

        try {
            // get the URL
            URIBuilder uriBuilder = new URIBuilder(toUrl);
            String userInfo = uriBuilder.getUserInfo();
            this.baseUrl = uriBuilder.setUserInfo(null).build();
            this.authScope = new AuthScope(this.baseUrl.getHost(), this.baseUrl.getPort());

            // extract user info from the URL
            if (userInfo != null) {
                extractUserInfo(userInfo);
            }

            // set credentials based on the URL settings
            if (this.credentials != null) {
                this.credentialsProvider.setCredentials(this.authScope, this.credentials);
            }
        } catch (URISyntaxException ex) {
            throw new ResultException(Result.build(Error.INVALID_URL, ex));
        }
    }

    /**
     * Returns the currently set TLS context.
     *
     * @return The currently set TLS context.
     */
    @Override
    @Nullable
    public TLSContext getTlsContext() {
        return tlsContext;
    }

    /**
     * Returns the {@link WebServiceProtocol} this session is using.
     *
     * @return The {@link WebServiceProtocol} this session is using.
     */
    @NotNull
    public WebServiceProtocol getWebServiceProtocol() {
        return this.webServiceProtocol;
    }

    /**
     * Extract and deduce the user credentials from a given user info String.
     *
     * @param userInfo The user info String containing the user name and password, separated by ":".
     */
    private void extractUserInfo(@Nullable String userInfo) {

        String name = "";
        String password = "";

        if (userInfo != null) {
            String[] credentials = userInfo.split(":");

            if (credentials.length >= 1) {
                name = credentials[0];
            }

            if (credentials.length >= 2) {
                password = credentials[1];
            }
        }

        this.credentials = new UsernamePasswordCredentials(name, password);
    }

    /**
     * Returns an {@link URI} pointing to the webservice interface of the session.
     *
     * @param subPath The location of the webservice interface on the webPDF server.
     * @return an {@link URI} pointing to the webservice interface of the session.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public URI getURI(@NotNull String subPath) throws ResultException {
        try {
            return new URIBuilder(this.baseUrl)
                       .setPath(this.baseUrl.getPath() + this.basePath + subPath)
                       .build();
        } catch (URISyntaxException ex) {
            throw new ResultException(Result.build(Error.INVALID_URL, ex));
        }
    }

    /**
     * Returns an {@link URI} pointing to the webservice interface of the session.
     *
     * @param subPath    The location of the webservice interface on the webPDF server.
     * @param parameters Additional Get parameters.
     * @return an {@link URI} pointing to the webservice interface of the session.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public URI getURI(@NotNull String subPath, List<NameValuePair> parameters) throws ResultException {
        try {
            return new URIBuilder(this.baseUrl)
                       .setPath(this.baseUrl.getPath() + this.basePath + subPath)
                       .addParameters(parameters)
                       .build();
        } catch (URISyntaxException ex) {
            throw new ResultException(Result.build(Error.INVALID_URL, ex));
        }
    }

    /**
     * Returns the {@link Credentials} authorizing this session.
     *
     * @return The {@link Credentials} authorizing this session.
     */
    @Nullable
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the {@link Credentials} authorizing this session.
     *
     * @param credentials The {@link Credentials} authorizing this session.
     */
    public void setCredentials(@Nullable Credentials credentials) {
        // set new credentials
        if (credentials != null) {
            this.credentials = credentials;
            this.credentialsProvider.setCredentials(this.authScope, this.credentials);
        }
    }

    /**
     * Returns the {@link DataFormat} accepted by this session.
     *
     * @return The {@link DataFormat} accepted by this session.
     */
    @Nullable
    public DataFormat getDataFormat() {
        return dataFormat;
    }
}
