package net.webpdf.wsclient.session.connection.proxy;

import org.apache.hc.core5.http.HttpHost;
import org.jetbrains.annotations.NotNull;

/**
 * An instance of {@link ProxyConfiguration} defines a proxy host for webPDF wsclient webservice calls.
 */
public class ProxyConfiguration {

    private final @NotNull HttpHost host;

    /**
     * Prepares the proxy server with the given hostname and port for access by webPDF SOAP and REST webservice calls.
     *
     * @param proxyName The proxy server's hostname.
     * @param port      The proxy server's port.
     */
    public ProxyConfiguration(@NotNull String proxyName, int port) {
        this.host = new HttpHost(proxyName, port);
    }

    /**
     * Returns a {@link HttpHost} instance, that bundles all collected host information for the proxy server, that shall
     * be used for webPDF webservice calls.
     *
     * @return The proxy server host information.
     */
    public @NotNull HttpHost getHost() {
        return host;
    }

}
