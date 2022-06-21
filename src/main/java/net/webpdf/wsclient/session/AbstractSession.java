package net.webpdf.wsclient.session;

import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.session.rest.AbstractRestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.session.proxy.ProxyConfiguration;
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
 * <p>
 * An instance of {@link AbstractSession} establishes and manages a {@link WebServiceProtocol} connection
 * with a webPDF server.
 * </p>
 *
 * @param <T_DOCUMENT> The {@link Document} type used by this {@link Session}.
 */
public abstract class AbstractSession<T_DOCUMENT extends Document> implements Session<T_DOCUMENT> {

    private final @NotNull CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private final @NotNull DataFormat dataFormat;
    private final @NotNull String basePath;
    private final @NotNull WebServiceProtocol webServiceProtocol;
    private final @NotNull URI baseUrl;
    private final @Nullable AuthScope authScope;
    private final @Nullable TLSContext tlsContext;
    private @Nullable Credentials credentials;
    private @Nullable ProxyConfiguration proxy;

    /**
     * Creates a new {@link AbstractRestSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link Session}.
     *
     * @param url                The {@link URL} of the webPDF server
     * @param webServiceProtocol The {@link WebServiceProtocol} used for this {@link Session}.
     * @param tlsContext         The {@link TLSContext} used for this https {@link Session}.
     *                           ({@code null} in case an unencrypted HTTP {@link Session} shall be created.)
     * @throws ResultException Shall be thrown, in case establishing the {@link Session} failed.
     */
    public AbstractSession(@NotNull URL url, @NotNull WebServiceProtocol webServiceProtocol,
            @Nullable TLSContext tlsContext) throws ResultException {
        // The used protocol determines the data format.
        switch (webServiceProtocol) {
            case SOAP:
                this.dataFormat = DataFormat.XML;
                break;
            case REST:
            default:
                this.dataFormat = DataFormat.JSON;
        }
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
     * Returns the currently set {@link TLSContext}. ({@code null} in case this is representing a HTTP {@link Session})
     *
     * @return The currently set {@link TLSContext}.
     */
    @Override
    public @Nullable TLSContext getTlsContext() {
        return tlsContext;
    }

    /**
     * Returns the currently set {@link ProxyConfiguration}.
     *
     * @return The currently set {@link ProxyConfiguration}.
     */
    @Override
    public @Nullable ProxyConfiguration getProxy() {
        return proxy;
    }

    /**
     * Sets a {@link ProxyConfiguration} for this {@link Session}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set for this {@link Session}.
     * @throws ResultException Shall be thrown, when resolving the {@link ProxyConfiguration} failed.
     */
    @Override
    public void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        this.proxy = proxy;
    }

    /**
     * Returns the {@link WebServiceProtocol} this {@link Session} is using.
     *
     * @return The {@link WebServiceProtocol} this {@link Session} is using.
     */
    public @NotNull WebServiceProtocol getWebServiceProtocol() {
        return this.webServiceProtocol;
    }

    /**
     * Returns an {@link URI} pointing to the webservice interface of the session.
     *
     * @param subPath The location of the webservice interface on the webPDF server.
     * @return an {@link URI} pointing to the webservice interface of the session.
     * @throws ResultException a {@link ResultException}
     */
    public @NotNull URI getURI(@NotNull String subPath) throws ResultException {
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
    public @NotNull URI getURI(@NotNull String subPath, List<NameValuePair> parameters) throws ResultException {
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
     * Returns the {@link DataFormat} accepted by this {@link Session}.
     *
     * @return The {@link DataFormat} accepted by this {@link Session}.
     */
    public @NotNull DataFormat getDataFormat() {
        return dataFormat;
    }

    /**
     * Returns the {@link Credentials} authorizing this session.
     *
     * @return The {@link Credentials} authorizing this session.
     */
    public @Nullable Credentials getCredentials() {
        return credentials;
    }

    /**
     * Sets the {@link Credentials} authorizing this session.
     *
     * @param userCredentials The {@link Credentials} authorizing this session.
     */
    public void setCredentials(@Nullable Credentials userCredentials) {
        // set new credentials
        if (userCredentials != null) {
            this.credentials = userCredentials;
            this.credentialsProvider.setCredentials(this.authScope, this.credentials);
        }
    }

    /**
     * Extract and deduce the {@link UsernamePasswordCredentials} from a given user info String.
     *
     * @param userInfo The user info String containing the {@link UsernamePasswordCredentials}, separated by ":".
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
     * Returns the currently registered {@link CredentialsProvider}.
     *
     * @return The currently registered {@link CredentialsProvider}.
     */
    public @NotNull CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }

}
