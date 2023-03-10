package net.webpdf.wsclient.session;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.SessionContextSettings;
import net.webpdf.wsclient.session.rest.AbstractRestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * <p>
 * An instance of {@link AbstractSession} establishes and manages a {@link WebServiceProtocol} connection
 * with a webPDF server.
 * </p>
 */
public abstract class AbstractSession implements Session {

    private final @NotNull WebServiceProtocol webServiceProtocol;
    private final @NotNull SessionContextSettings serverContext;
    private final @NotNull AuthProvider authProvider;
    private final @NotNull String basePath;
    private final @NotNull URI baseUrl;

    /**
     * <p>
     * Creates a new {@link AbstractRestSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link Session}.
     * </p>
     * <p>
     * <b>Be Aware:</b> Neither {@link SessionContext}, nor {@link AuthProvider} are required to serve multiple
     * {@link Session}s at a time. It is expected to create a new {@link SessionContext} and {@link AuthProvider}
     * per {@link Session} you create.
     * </p>
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used for this {@link Session}.
     * @param serverContext      The {@link SessionContext} initializing the {@link SessionContextSettings} of this
     *                           {@link Session}.
     * @param authProvider       The {@link AuthProvider} for authentication/authorization of this {@link Session}.
     * @throws ResultException Shall be thrown, in case establishing the {@link Session} failed.
     */
    public AbstractSession(@NotNull WebServiceProtocol webServiceProtocol, @NotNull SessionContext serverContext,
            @NotNull AuthProvider authProvider) throws ResultException {
        this.serverContext = new SessionContextSettings(serverContext);
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
     * Returns the {@link SessionContextSettings} used for this session.
     *
     * @return The {@link SessionContextSettings} used for this session.
     */
    @Override
    public @NotNull SessionContextSettings getSessionContext() {
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

}
