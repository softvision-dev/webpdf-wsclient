package net.webpdf.wsclient.testsuite.server;

import net.webpdf.wsclient.testsuite.config.TestConfig;
import org.apache.hc.core5.net.URIBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.cert.Certificate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

public final class TestServer {
    private final URIBuilder uriBuilderLocalServer;
    private final URIBuilder uriBuilderPublicServer;

    public TestServer() {
        this.uriBuilderLocalServer = assertDoesNotThrow(() ->
                new URIBuilder(TestConfig.getInstance().getServerConfig().getLocalURL())
                        .setPath(TestConfig.getInstance().getServerConfig().getLocalPath()));
        if (!uriBuilderLocalServer.isAbsolute()) {
            fail("Invalid URL '" + TestConfig.getInstance().getServerConfig().getLocalURL() + "'");
        }
        this.uriBuilderPublicServer = assertDoesNotThrow(() ->
                new URIBuilder(TestConfig.getInstance().getServerConfig().getPublicURL())
                        .setPath(TestConfig.getInstance().getServerConfig().getPublicPath()));
        if (!uriBuilderPublicServer.isAbsolute()) {
            fail("Invalid URL '" + TestConfig.getInstance().getServerConfig().getPublicURL() + "'");
        }
    }

    public URL getServer(ServerType serverType) {
        return assertDoesNotThrow(() -> buildServer(serverType, TransferProtocol.HTTP));
    }

    public URL getServer(ServerType serverType, TransferProtocol transferProtocol) {
        return assertDoesNotThrow(() -> buildServer(serverType, transferProtocol));
    }

    private URL buildServer(ServerType serverType, TransferProtocol transferProtocol)
            throws URISyntaxException, MalformedURLException {

        URI uri;
        URIBuilder uriBuilder;

        switch (serverType) {
            case LOCAL:
                uri = this.uriBuilderLocalServer.build();
                uriBuilder = new URIBuilder(uri);
                if (transferProtocol.equals(TransferProtocol.HTTP)) {
                    uriBuilder.setPort(TestConfig.getInstance().getServerConfig().getLocalHttpPort());
                    uriBuilder.setScheme("http");
                } else {
                    uriBuilder.setPort(TestConfig.getInstance().getServerConfig().getLocalHttpsPort());
                    uriBuilder.setScheme("https");
                }
                break;
            case PUBLIC:
                uri = this.uriBuilderPublicServer.build();
                uriBuilder = new URIBuilder(uri);
                if (transferProtocol.equals(TransferProtocol.HTTP)) {
                    uriBuilder.setScheme("http");
                    uriBuilder.setPort(TestConfig.getInstance().getServerConfig().getPublicHttpPort());
                } else {
                    uriBuilder.setScheme("https");
                    uriBuilder.setPort(TestConfig.getInstance().getServerConfig().getPublicHttpsPort());
                }
                break;
            default:
                throw new MalformedURLException("URL not available");
        }

        return uriBuilder.build().toURL();
    }

    public String getLocalUser() {
        return TestConfig.getInstance().getServerConfig().getLocalUser();
    }

    public char[] getLocalPassword() {
        return TestConfig.getInstance().getServerConfig().getLocalPassword().toCharArray();
    }

    public File getDemoKeystoreFile(File keystoreFile) throws Exception {
        URL serverUrl = getServer(ServerType.PUBLIC, TransferProtocol.HTTPS);
        HttpsURLConnection conn = (HttpsURLConnection) serverUrl.openConnection();
        conn.connect();
        Certificate[] certs = conn.getServerCertificates();
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null);
        for (Certificate cert : certs) {
            ks.setCertificateEntry("webPDF", cert);
        }
        try (OutputStream fos = Files.newOutputStream(keystoreFile.toPath())) {
            ks.store(fos, "".toCharArray());
        }
        return keystoreFile;
    }

}
