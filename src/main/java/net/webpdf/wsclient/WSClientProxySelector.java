package net.webpdf.wsclient;

import org.apache.http.HttpHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.util.*;

@SuppressWarnings("unused")
public class WSClientProxySelector extends ProxySelector implements AutoCloseable {

    private static final String[] SUPPORTED_SCHEMES = new String[]{"http", "https"};
    @NotNull
    private final Map<SocketAddress, Proxy> proxies = new HashMap<>();
    @NotNull
    private final List<URI> routedResources;
    @Nullable
    private final ProxySelector defaultProxySelector;

    /**
     * Initializes a custom proxy selector, that prepares proxy settings for webPDF calls. It shall fallback to the
     * default proxy selector, when a processed URI is not pointing to a webPDF endpoint. It will restore the previously
     * set proxy selector, when closed. The default proxy selector shall also be used as a fallback, when a resource
     * path shall be routed, that is not based on one of the given routedResources.
     *
     * @param proxies         The proxy server hosts, that shall be used.
     * @param routedResources A list of all base URIs, that shall be routed via this selector. All URIs a proxy is requested
     *                        for, shall start with one of the hereby given URIs.
     */
    public WSClientProxySelector(@NotNull URI[] routedResources, @NotNull HttpHost... proxies) {
        this.routedResources = Arrays.asList(routedResources);
        this.defaultProxySelector = ProxySelector.getDefault();
        ProxySelector.setDefault(this);

        for (HttpHost proxy : proxies) {
            addProxy(proxy);
        }
    }

    /**
     * Adds the given proxy to the list of usable proxy servers.
     *
     * @param proxy A proxy server, that shall be used.
     */
    public void addProxy(@NotNull HttpHost proxy) {
        if (isSupportedScheme(proxy.getSchemeName())) {
            SocketAddress socketAddress = new InetSocketAddress(proxy.getHostName(), proxy.getPort());
            Proxy resolvedProxy = new Proxy(Proxy.Type.HTTP, socketAddress);
            proxies.put(socketAddress, resolvedProxy);
        }
    }

    /**
     * Adds the given URI to the list of URIs, that must be resolved using a proxy route from this selector. All URIs
     * a proxy shall be requested for, will be routed, if they start with one of the hereby configured URIs.
     * (otherwise a default selector shall be used instead.)
     *
     * @param routedResource The URI, that shall be routed via this selector.
     */
    public void addRoutedResource(URI routedResource) {
        this.routedResources.add(routedResource);
    }

    /**
     * Selects all the applicable proxies based on the protocol to access the resource with and a destination address to
     * access the resource at. The format of the URI is defined as follow:
     * <UL>
     * <LI>http URI for http connections</LI>
     * <LI>https URI for https connections
     * <LI>{@code socket://host:port}<br>
     * for tcp client sockets connections</LI>
     * </UL>
     * <p>
     * If the URI does not point to a routed resource, the previously known default proxy selector shall be used, to
     * select the proxies.
     *
     * @param uri The URI that a connection is required to
     * @return a List of Proxies. Each element in the the List is of type {@link java.net.Proxy Proxy}; when no proxy
     * is available, the list will contain one element of type {@link java.net.Proxy Proxy} that represents a direct
     * connection.
     * @throws IllegalArgumentException if the argument is null
     */
    @Override
    @NotNull
    public List<Proxy> select(@Nullable URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI can't be null.");
        }

        if (isRoutedURI(uri)) {
            return new ArrayList<>(proxies.values());
        } else if (defaultProxySelector != null) {
            return defaultProxySelector.select(uri);
        } else {
            return Collections.singletonList(Proxy.NO_PROXY);
        }
    }

    /**
     * Called to indicate that a connection could not be established to a proxy/socks server. An implementation of this
     * method can temporarily remove the proxies or reorder the sequence of proxies returned by {@link #select(URI)},
     * using the address and the IOException caught when trying to connect.
     *
     * @param uri           The URI that the proxy at sa failed to serve.
     * @param socketAddress The socket address of the proxy/SOCKS server
     * @param exception     The I/O exception thrown when the connect failed.
     * @throws IllegalArgumentException if either argument is null
     */
    @Override
    public void connectFailed(@Nullable URI uri, @Nullable SocketAddress socketAddress, @Nullable IOException exception) {
        if (uri == null || socketAddress == null || exception == null) {
            throw new IllegalArgumentException("Arguments can't be null.");
        }

        if (proxies.get(socketAddress) == null && defaultProxySelector != null) {
            defaultProxySelector.connectFailed(uri, socketAddress, exception);
        }
    }

    /**
     * Restores the previously found proxy selector as the VM's default.
     */
    @Override
    public void close() {
        ProxySelector.setDefault(this.defaultProxySelector);
    }

    /**
     * Returns true, if the given URI points to a routed resource.
     *
     * @param uri The URI, that shall be checked.
     * @return True, if the URI points to a routed resource.
     */
    private boolean isRoutedURI(@NotNull URI uri) {
        if (!isSupportedScheme(uri.getScheme())) {
            return false;
        }
        for (URI routedURI : routedResources) {
            if (uri.toString().startsWith(routedURI.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true, if the given connection scheme is supported by this proxy selector.
     *
     * @param scheme The scheme, that shall be checked.
     * @return True, if the given connection scheme is supported.
     */
    private boolean isSupportedScheme(@NotNull String scheme) {
        return Arrays.asList(SUPPORTED_SCHEMES).contains(scheme);
    }
}