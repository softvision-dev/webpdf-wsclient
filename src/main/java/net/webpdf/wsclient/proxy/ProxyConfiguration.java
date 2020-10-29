package net.webpdf.wsclient.proxy;

import org.apache.http.HttpHost;
import org.jetbrains.annotations.NotNull;

public class ProxyConfiguration {
    @NotNull
    private final HttpHost host;

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
    @NotNull
    public HttpHost getHost() {
        return host;
    }
}
