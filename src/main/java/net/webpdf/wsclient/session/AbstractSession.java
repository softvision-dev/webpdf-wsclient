package net.webpdf.wsclient.session;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.rest.AbstractRestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * An instance of {@link AbstractSession} establishes and manages a {@link WebServiceProtocol} connection
 * with a webPDF server.
 * </p>
 */
public abstract class AbstractSession implements Session {

    private final @NotNull AuthProvider authProvider;
    private final @NotNull String basePath;
    private final @NotNull WebServiceProtocol webServiceProtocol;
    private final @NotNull URI baseUrl;
    private final @Nullable TLSContext tlsContext;
    private final @NotNull AtomicReference<ProxyConfiguration> proxy = new AtomicReference<>();
    private final @NotNull AtomicInteger skewTime = new AtomicInteger(1);

    /**
     * Creates a new {@link AbstractRestSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link Session}.
     *
     * @param url                The {@link URL} of the webPDF server
     * @param webServiceProtocol The {@link WebServiceProtocol} used for this {@link Session}.
     * @param tlsContext         The {@link TLSContext} used for this https {@link Session}.
     *                           ({@code null} in case an unencrypted HTTP {@link Session} shall be created.)
     * @param authProvider       The {@link AuthProvider} for authentication/authorization of this {@link Session}.
     * @throws ResultException Shall be thrown, in case establishing the {@link Session} failed.
     */
    public AbstractSession(@NotNull URL url, @NotNull WebServiceProtocol webServiceProtocol,
            @Nullable TLSContext tlsContext, @NotNull AuthProvider authProvider) throws ResultException {
        this.authProvider = authProvider;
        this.webServiceProtocol = webServiceProtocol;
        this.tlsContext = tlsContext;
        this.basePath = webServiceProtocol.equals(WebServiceProtocol.SOAP) ? "soap/" : "rest/";
        String toUrl = url.toString();
        if (!toUrl.endsWith("/")) {
            toUrl = toUrl + "/";
        }
        // determine the base URL
        try {
            URIBuilder uriBuilder = new URIBuilder(toUrl);
            this.baseUrl = uriBuilder.setUserInfo(null).build();
        } catch (URISyntaxException ex) {
            throw new ClientResultException(Error.INVALID_URL, ex);
        }
    }

    /**
     * Provides {@link AuthMaterial} for the authorization of the {@link Session}´s requests, using the
     * {@link Session}´s {@link AuthProvider}.
     *
     * @return {@link AuthMaterial} for the authorization of the {@link Session}´s requests.
     * @throws ResultException Shall be thrown, should the determination of {@link AuthMaterial} fail.
     */
    public synchronized @NotNull AuthMaterial getAuthMaterial() throws ResultException {
        return this.authProvider.provide(this);
    }

    /**
     * Returns the currently set {@link TLSContext}. ({@code null} in case this is representing an HTTP {@link Session})
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
        return proxy.get();
    }

    /**
     * Sets a {@link ProxyConfiguration} for this {@link Session}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set for this {@link Session}.
     * @throws ResultException Shall be thrown, when resolving the {@link ProxyConfiguration} failed.
     */
    @Override
    public void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        this.proxy.set(proxy);
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
            throw new ClientResultException(Error.INVALID_URL, ex);
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
            throw new ClientResultException(Error.INVALID_URL, ex);
        }
    }

    /**
     * <p>
     * Returns the {@link Session}´s skew time.<br>
     * The skew time helps to avoid using expired tokens. The returned value (in seconds) is subtracted from the
     * expiry time to avoid issues possibly caused by transfer delays.<br>
     * It can not be guaranteed, but is recommended, that custom implementations of {@link AuthProvider} handle this
     * accordingly.
     * </p>
     *
     * @return The {@link Session}´s token refresh skew time in seconds.
     */
    @Override
    public int getSkewTime() {
        return this.skewTime.get();
    }

    /**
     * <p>
     * Sets the {@link Session}´s skew time.<br>
     * The skew time helps to avoid using expired tokens. The returned value (in seconds) is subtracted from the
     * expiry time to avoid issues possibly caused by transfer delays.<br>
     * It can not be guaranteed, but is recommended, that custom implementations of {@link AuthProvider} handle this
     * accordingly.
     * </p>
     *
     * @param skewTime The {@link Session}´s token refresh skew time in seconds.
     */
    @Override
    public void setSkewTime(int skewTime) {
        this.skewTime.set(skewTime);
    }

}
