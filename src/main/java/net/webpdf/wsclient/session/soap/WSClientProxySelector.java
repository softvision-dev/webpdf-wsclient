package net.webpdf.wsclient.session.soap;

import org.apache.hc.core5.http.HttpHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.util.*;

/**
 * An instance of {@link WSClientProxySelector} initializes a custom proxy selector, that prepares proxy settings for
 * webPDF calls. It shall fallback to the default proxy selector, when a processed {@link URI} is not pointing to a
 * webPDF endpoint. It will restore the previously set proxy selector, when closed. The default proxy selector shall
 * also be used as a fallback, when a resource path shall be routed, that is not based on one of the given
 * routedResources.
 */
@SuppressWarnings("unused")
public class WSClientProxySelector extends ProxySelector implements AutoCloseable {

    private static final @NotNull String[] SUPPORTED_SCHEMES = new String[]{"http", "https"};
    private final @NotNull Map<SocketAddress, Proxy> proxies = new HashMap<>();
    private final @NotNull List<URI> routedResources;
    private final @Nullable ProxySelector defaultProxySelector;

    /**
     * Initializes a custom proxy selector, that prepares proxy settings for webPDF calls. It shall fallback to the
     * default proxy selector, when a processed {@link URI} is not pointing to a webPDF endpoint. It will restore the
     * previously set proxy selector, when closed. The default proxy selector shall also be used as a fallback, when a
     * resource path shall be routed, that is not based on one of the given routedResources.
     *
     * @param proxies         The proxy server hosts, that shall be used.
     * @param routedResources A list of all base {@link URI}s, that shall be routed via this selector. All URIs a proxy
     *                        is requested for, shall start with one of the hereby given {@link URI}s.
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
     * Adds the given {@link URI} to the list of {@link URI}s, that must be resolved using a proxy route from this
     * selector. All {@link URI}s a proxy shall be requested for, will be routed, if they start with one of the hereby
     * configured {@link URI}s.
     * (otherwise a default selector shall be used instead.)
     *
     * @param routedResource The {@link URI}, that shall be routed via this selector.
     */
    public void addRoutedResource(URI routedResource) {
        this.routedResources.add(routedResource);
    }

    /**
     * Selects all the applicable proxies based on the protocol to access the resource with and a destination address to
     * access the resource at. The format of the {@link URI} is defined as follows:
     * <UL>
     * <LI>http {@link URI} for http connections</LI>
     * <LI>https {@link URI} for https connections
     * <LI>{@code socket://host:port}<br>
     * for tcp client sockets connections</LI>
     * </UL>
     * <p>
     * If the {@link URI} does not point to a routed resource, the previously known default proxy selector shall be
     * used, to select the proxies.
     *
     * @param uri The {@link URI} that a connection is required to.
     * @return A List of Proxies. Each element in the List is of type {@link Proxy Proxy}; when no proxy
     * is available, the list will contain one element of type {@link Proxy Proxy} that represents a direct
     * connection.
     * @throws IllegalArgumentException if the argument is {@code null}.
     */
    @Override
    public @NotNull List<Proxy> select(@Nullable URI uri) {
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
     * using the address and the {@link IOException} caught when trying to connect.
     *
     * @param uri           The {@link URI} that a connection could not be established to.
     * @param socketAddress The {@link SocketAddress} of the proxy/SOCKS server
     * @param exception     The {@link IOException}, that shall be thrown when a connection attempt fails.
     * @throws IllegalArgumentException Shall be thrown, when either argument is {@code null}.
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
     * Returns {@code true}, if the given {@link URI} points to a routed resource.
     *
     * @param uri The {@link URI}, that shall be checked.
     * @return {@code true}, if the {@link URI} points to a routed resource.
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
     * Returns {@code true}, if the given connection scheme is supported by this proxy selector.
     *
     * @param scheme The scheme, that shall be checked.
     * @return {@code true}, if the given connection scheme is supported.
     */
    private boolean isSupportedScheme(@NotNull String scheme) {
        return Arrays.asList(SUPPORTED_SCHEMES).contains(scheme);
    }

}