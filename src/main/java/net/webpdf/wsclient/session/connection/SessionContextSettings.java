package net.webpdf.wsclient.session.connection;


import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * <p>
 * An instance of {@link SessionContextSettings} collects and provides advanced settings for a webPDF {@link Session}.
 * During it´s existence a {@link Session} will obey those settings, when making requests to a webPDF server.<br>
 * <b>Be aware:</b> {@link SessionContextSettings} can not be changed post initialization. If you want to make such
 * changes, you have to create a new {@link Session} using the {@link SessionContext} of your choice.
 * </p>
 * <p>
 * <b>Be aware:</b> A {@link SessionContextSettings} is not required to serve multiple {@link Session}s at a time.
 * It is expected to create a new {@link SessionContextSettings} for each existing {@link Session}.
 * </p>
 */
@SuppressWarnings("unused")
public class SessionContextSettings {

    private final @NotNull WebServiceProtocol webServiceProtocol;
    private final @NotNull URL url;
    private final @Nullable TLSContext tlsContext;
    private final @Nullable ProxyConfiguration proxyConfiguration;
    private final int skewTime;

    /**
     * Creates a new {@link SessionContextSettings} from the provided {@link SessionContext}.
     * During it´s existence a {@link Session} will obey those settings, when making requests to a webPDF server.<br>
     * <b>Be aware:</b> A {@link SessionContextSettings} can not be changed post initialization. You must use a
     * {@link SessionContext} instance prior to the {@link Session} initialization, to manipulate those
     * settings.
     *
     * @param contextConfiguration The {@link SessionContext} initializing this {@link SessionContextSettings}.
     */
    public SessionContextSettings(@NotNull SessionContext contextConfiguration) {
        this.webServiceProtocol = contextConfiguration.getWebServiceProtocol();
        this.url = contextConfiguration.getUrl();
        this.tlsContext = contextConfiguration.getTlsContext();
        this.proxyConfiguration = contextConfiguration.getProxy();
        this.skewTime = contextConfiguration.getSkewTime();
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
