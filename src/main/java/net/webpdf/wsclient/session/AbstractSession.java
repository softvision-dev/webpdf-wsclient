package net.webpdf.wsclient.session;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.connection.ServerContext;
import net.webpdf.wsclient.session.connection.ServerContextSettings;
import net.webpdf.wsclient.session.connection.https.AlwaysTrustManager;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.rest.AbstractRestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * An instance of {@link AbstractSession} establishes and manages a {@link WebServiceProtocol} connection
 * with a webPDF server.
 * </p>
 */
public abstract class AbstractSession implements Session {

    private static final TrustManager[] TRUST_ALL = new TrustManager[]{new AlwaysTrustManager()};
    private final @NotNull WebServiceProtocol webServiceProtocol;
    private final @NotNull ServerContextSettings serverContext;
    private final @NotNull AuthProvider authProvider;
    private final @NotNull String basePath;
    private final @NotNull URI baseUrl;
    private final @NotNull AtomicReference<SSLContext> sslContext = new AtomicReference<>();

    /**
     * Creates a new {@link AbstractRestSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link Session}.
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used for this {@link Session}.
     * @param serverContext      The {@link ServerContext} initializing the {@link ServerContextSettings} of this
     *                           {@link Session}.
     * @param authProvider       The {@link AuthProvider} for authentication/authorization of this {@link Session}.
     * @throws ResultException Shall be thrown, in case establishing the {@link Session} failed.
     */
    public AbstractSession(@NotNull WebServiceProtocol webServiceProtocol, @NotNull ServerContext serverContext,
            @NotNull AuthProvider authProvider) throws ResultException {
        this.serverContext = new ServerContextSettings(serverContext);
        this.authProvider = authProvider;
        this.webServiceProtocol = webServiceProtocol;
        this.basePath = webServiceProtocol.equals(WebServiceProtocol.SOAP) ? "soap/" : "rest/";
        String toUrl = this.serverContext.getUrl().toString();
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
    @Override
    public synchronized @NotNull AuthMaterial getAuthMaterial() throws ResultException {
        return this.authProvider.provide(this);
    }

    /**
     * Returns the {@link WebServiceProtocol} this {@link Session} is using.
     *
     * @return The {@link WebServiceProtocol} this {@link Session} is using.
     */
    @Override
    public @NotNull WebServiceProtocol getWebServiceProtocol() {
        return this.webServiceProtocol;
    }

    /**
     * Returns the {@link ServerContextSettings} used for this session.
     *
     * @return The {@link ServerContextSettings} used for this session.
     */
    @Override
    public @NotNull ServerContextSettings getServerContext() {
        return this.serverContext;
    }

    /**
     * Returns an {@link URI} pointing to the webservice interface of the session.
     *
     * @param subPath The location of the webservice interface on the webPDF server.
     * @return an {@link URI} pointing to the webservice interface of the session.
     * @throws ResultException a {@link ResultException}
     */
    @Override
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
    @Override
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
     * Returns (and initializes) the {@link Session}´s {@link SSLContext}.
     * </p>
     * <p>
     * <b>Information:</b> Actually this is not exactly a "SSL" context, but a "TLS" context.
     * TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is no longer
     * supported by the webPDF wsclient, as it is obsolete and insecure.
     * </p>
     *
     * @return The resulting {@link SSLContext}.
     */
    public @Nullable SSLContext getTLSContext() throws ResultException {
        TLSContext tlsContext = getServerContext().getTlsContext();
        if (sslContext.get() != null) {
            return sslContext.get();
        }
        if (tlsContext == null) {
            return null;
        }
        try {
            if (tlsContext.getTrustStore() != null || tlsContext.isAllowSelfSigned()) {
                this.sslContext.set(tlsContext.getTrustStore() != null ?
                        new SSLContextBuilder()
                                .setProtocol(tlsContext.getTlsProtocol().getName())
                                .loadTrustMaterial(
                                        tlsContext.getTrustStore(),
                                        tlsContext.getTrustStorePassword() != null ?
                                                tlsContext.getTrustStorePassword().toCharArray() :
                                                null, null
                                )
                                .build() :
                        new SSLContextBuilder()
                                .setProtocol(tlsContext.getTlsProtocol().getName())
                                .build());
                if (tlsContext.isAllowSelfSigned()) {
                    this.sslContext.get().init(new KeyManager[0], TRUST_ALL, new SecureRandom());
                }
            } else {
                this.sslContext.set(SSLContexts.createDefault());
            }
        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 IOException ex) {
            throw new ClientResultException(Error.TLS_INITIALIZATION_FAILURE, ex);
        }
        return this.sslContext.get();
    }

}
