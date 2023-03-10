package net.webpdf.wsclient.session.connection;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * <p>
 * An instance of {@link SessionContext} collects and provides advanced settings for the initialization of
 * a webPDF {@link Session}. During it´s existence a {@link Session} will obey those settings, when making requests
 * to a webPDF server.
 * </p>
 * <p>
 * <b>Be aware:</b> A {@link Session} will deduce it´s {@link SessionContextSettings} from a {@link SessionContext}.
 * The {@link SessionContextSettings} of a running {@link Session} can not be changed.
 * </p>
 * <p>
 * <b>Be aware:</b> A {@link SessionContext} is not required to serve multiple {@link Session}s at a time. It is
 * expected to create a new {@link SessionContext} for each existing {@link Session}.
 * </p>
 */
@SuppressWarnings("unused")
public class SessionContext {

    private final @NotNull WebServiceProtocol webServiceProtocol;
    private final @NotNull URL url;
    private @Nullable TLSContext tlsContext;
    private @Nullable ProxyConfiguration proxyConfiguration;
    private int skewTime = 0;

    /**
     * <p>
     * Instantiates a new {@link SessionContext} for {@link Session}s.<br>
     * The constructor initializes the non-optional settings, that must be provided. All further settings are optional,
     * and you may or may not require to set them.
     * </p>
     * <p>
     * <b>Be aware:</b> A {@link Session} will deduce it´s {@link SessionContextSettings} from a {@link SessionContext}.
     * The {@link SessionContextSettings} of a running {@link Session} can not be changed.
     * </p>
     * <p>
     * <b>Be aware:</b> A {@link SessionContext} is not required to serve multiple {@link Session}s at a time. It is
     * expected to create a new {@link SessionContext} for each existing {@link Session}.
     * </p>
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     */
    public SessionContext(@NotNull WebServiceProtocol webServiceProtocol, @NotNull URL url) {
        this.webServiceProtocol = webServiceProtocol;
        this.url = url;
    }

    /**
     * Returns the {@link WebServiceProtocol}, that shall be used to communicate with the server.
     *
     * @return The {@link WebServiceProtocol}, that shall be used to communicate with the server.
     */
    public @NotNull WebServiceProtocol getWebServiceProtocol() {
        return webServiceProtocol;
    }

    /**
     * Returns the {@link URL} of the server.
     *
     * @return The {@link URL} of the server.
     */
    public @NotNull URL getUrl() {
        return url;
    }

    /**
     * <p>
     * Sets the {@link TLSContext} for {@link Session}s.<br>
     * A {@link TLSContext} allows to send requests to the server via secured HTTPS connections.
     * </p>
     *
     * @param tlsContext The {@link TLSContext}, that shall be used.
     * @return This {@link SessionFactory} itself.
     */
    public @NotNull SessionContext setTlsContext(@Nullable TLSContext tlsContext) {
        this.tlsContext = tlsContext;
        return this;
    }

    /**
     * <p>
     * Returns the {@link TLSContext} for {@link Session}s.<br>
     * A {@link TLSContext} allows to send requests to the server via secured HTTPS connections.
     * </p>
     *
     * @return The {@link TLSContext}, that shall be used.
     */
    public @Nullable TLSContext getTlsContext() {
        return tlsContext;
    }

    /**
     * <p>
     * Sets the {@link ProxyConfiguration} for {@link Session}s.<br>
     * A {@link ProxyConfiguration} allows to route requests to the server via the defined proxy.
     * </p>
     *
     * @param proxyConfiguration The {@link ProxyConfiguration}, that shall be used.
     * @return This {@link SessionFactory} itself.
     */
    public @NotNull SessionContext setProxy(@Nullable ProxyConfiguration proxyConfiguration) {
        this.proxyConfiguration = proxyConfiguration;
        return this;
    }

    /**
     * <p>
     * Returns the {@link ProxyConfiguration} for {@link Session}s.<br>
     * A {@link ProxyConfiguration} allows to route requests to the server via the defined proxy.
     * </p>
     *
     * @return The {@link ProxyConfiguration}, that shall be used.
     */
    public @Nullable ProxyConfiguration getProxy() {
        return proxyConfiguration;
    }

    /**
     * <p>
     * Sets a skew time for the token refresh of {@link Session}s.<br>
     * The skew time helps to avoid using expired authorization tokens. The returned value (in seconds) is subtracted
     * from the expiry time to avoid issues possibly caused by transfer delays.<br>
     * It can not be guaranteed, but is recommended, that custom implementations of {@link AuthProvider} handle this
     * accordingly.
     * </p>
     *
     * @param skewTime The skew time in seconds, that shall be used.
     * @return This {@link SessionFactory} itself.
     */
    public @NotNull SessionContext setSkewTime(int skewTime) {
        this.skewTime = skewTime;
        return this;
    }

    /**
     * <p>
     * Returns a skew time for the token refresh of {@link Session}s.<br>
     * The skew time helps to avoid using expired authorization tokens. The returned value (in seconds) is subtracted
     * from the expiry time to avoid issues possibly caused by transfer delays.<br>
     * It can not be guaranteed, but is recommended, that custom implementations of {@link AuthProvider} handle this
     * accordingly.
     * </p>
     *
     * @return The skew time in seconds, that shall be used.
     */
    public int getSkewTime() {
        return skewTime;
    }

}
