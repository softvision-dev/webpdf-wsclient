package net.webpdf.wsclient.session.soap;

import org.apache.hc.core5.http.HttpHost;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the lifecycle of {@link ClientProxySelector} as the JVM wide default {@link ProxySelector}: routed resources
 * can be added, closing restores the previous default only if this selector still is the default, and closing is
 * idempotent. Isolated, because the tests replace the JVM wide default {@link ProxySelector}.
 */
@Isolated
public class ClientProxySelectorTest {

    private static final URI SESSION_A = URI.create("http://webpdf-a.example:8080/webPDF/soap/");
    private static final URI SESSION_B = URI.create("http://webpdf-b.example:8080/webPDF/soap/");
    private static final HttpHost PROXY_A = new HttpHost("http", "127.0.0.1", 3128);
    private static final HttpHost PROXY_B = new HttpHost("http", "127.0.0.1", 3129);

    private ProxySelector originalDefault;
    private final ProxySelector baseSelector = new ProxySelector() {
        @Override
        public List<Proxy> select(URI uri) {
            return Collections.singletonList(Proxy.NO_PROXY);
        }

        @Override
        public void connectFailed(URI uri, SocketAddress socketAddress, IOException exception) {
        }
    };

    @BeforeEach
    public void installBaseSelector() {
        this.originalDefault = ProxySelector.getDefault();
        ProxySelector.setDefault(this.baseSelector);
    }

    @AfterEach
    public void restoreOriginalSelector() {
        ProxySelector.setDefault(this.originalDefault);
    }

    @Test
    public void testAddRoutedResource() {
        try (ClientProxySelector selector = new ClientProxySelector(new URI[]{SESSION_A}, PROXY_A)) {
            assertDoesNotThrow(() -> selector.addRoutedResource(SESSION_B),
                    "Adding a routed resource should be supported.");
            assertEquals(proxyFor(PROXY_A), selector.select(SESSION_B.resolve("Converter")),
                    "The added resource should be routed via the proxy.");
        }
    }

    @Test
    public void testOutOfOrderCloseKeepsLaterSelector() {
        ClientProxySelector first = new ClientProxySelector(new URI[]{SESSION_A}, PROXY_A);
        ClientProxySelector second = new ClientProxySelector(new URI[]{SESSION_B}, PROXY_B);
        assertSame(second, ProxySelector.getDefault(), "The second selector should be the default.");

        first.close();

        assertSame(second, ProxySelector.getDefault(),
                "Closing the first selector must not remove the second selector.");
        assertEquals(proxyFor(PROXY_B), ProxySelector.getDefault().select(SESSION_B.resolve("Converter")),
                "The second session should still be routed via its proxy.");
        assertEquals(Collections.singletonList(Proxy.NO_PROXY),
                ProxySelector.getDefault().select(SESSION_A.resolve("Converter")),
                "The closed first selector should no longer route its session.");

        second.close();

        assertSame(this.baseSelector, ProxySelector.getDefault(),
                "Closing the last selector should restore the original default.");
    }

    @Test
    public void testRepeatedCloseHasNoEffect() {
        ClientProxySelector first = new ClientProxySelector(new URI[]{SESSION_A}, PROXY_A);
        ClientProxySelector second = new ClientProxySelector(new URI[]{SESSION_B}, PROXY_B);
        second.close();
        first.close();
        assertSame(this.baseSelector, ProxySelector.getDefault(), "The original default should be restored.");

        second.close();

        assertSame(this.baseSelector, ProxySelector.getDefault(),
                "Closing a selector again must not reinstall a closed selector.");
    }

    private static List<Proxy> proxyFor(HttpHost proxy) {
        return Collections.singletonList(new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress(proxy.getHostName(), proxy.getPort())));
    }

}
