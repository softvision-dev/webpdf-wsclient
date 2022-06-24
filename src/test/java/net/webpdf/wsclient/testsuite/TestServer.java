package net.webpdf.wsclient.testsuite;

import net.webpdf.wsclient.testsuite.config.TestConfig;
import org.apache.http.client.utils.URIBuilder;

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

    private static final String SERVER_PATH = "/webPDF";
    private final URIBuilder uriBuilderLocalServer;
    private final URIBuilder uriBuilderPublicServer;

    public TestServer() {
        this.uriBuilderLocalServer = assertDoesNotThrow(() ->
                new URIBuilder(TestConfig.getInstance().getServerConfig().getLocalURL()).setPath(SERVER_PATH));
        if (!uriBuilderLocalServer.isAbsolute()) {
            fail("Invalid URL '" + TestConfig.getInstance().getServerConfig().getLocalURL() + "'");
        }
        this.uriBuilderPublicServer = assertDoesNotThrow(() ->
                new URIBuilder(TestConfig.getInstance().getServerConfig().getPublicURL()).setPath(SERVER_PATH));
        if (!uriBuilderPublicServer.isAbsolute()) {
            fail("Invalid URL '" + TestConfig.getInstance().getServerConfig().getPublicURL() + "'");
        }
    }

    public URL getServer(ServerType serverType) throws URISyntaxException, MalformedURLException {
        return buildServer(serverType, ServerProtocol.HTTP, null, null);
    }

    public URL getServer(ServerType serverType, String user, String password) throws URISyntaxException, MalformedURLException {
        return buildServer(serverType, ServerProtocol.HTTP, user, password);
    }

    public URL getServer(ServerType serverType, ServerProtocol serverProtocol, boolean useCredentials) throws URISyntaxException, MalformedURLException {
        String user = useCredentials ? TestConfig.getInstance().getServerConfig().getLocalUser() : null;
        String password = useCredentials ? TestConfig.getInstance().getServerConfig().getLocalPassword() : null;

        return buildServer(serverType, serverProtocol, user, password);
    }

    private URL buildServer(ServerType serverType, ServerProtocol serverProtocol, String user, String password) throws URISyntaxException, MalformedURLException {

        URI uri;
        URIBuilder uriBuilder;

        switch (serverType) {
            case LOCAL:
                uri = this.uriBuilderLocalServer.build();
                uriBuilder = new URIBuilder(uri);
                if (user != null && password != null) {
                    uriBuilder.setUserInfo(user, password);
                }
                if (serverProtocol.equals(ServerProtocol.HTTP)) {
                    uriBuilder.setPort(8080);
                    uriBuilder.setScheme("http");
                } else {
                    uriBuilder.setPort(8443);
                    uriBuilder.setScheme("https");
                }
                break;

            case PUBLIC:
                uri = this.uriBuilderPublicServer.build();
                uriBuilder = new URIBuilder(uri);
                if (serverProtocol.equals(ServerProtocol.HTTP)) {
                    uriBuilder.setScheme("http");
                    uriBuilder.setPort(80);
                } else {
                    uriBuilder.setScheme("https");
                    uriBuilder.setPort(443);
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

    public String getLocalPassword() {
        return TestConfig.getInstance().getServerConfig().getLocalPassword();
    }

    public File getDemoKeystoreFile(File keystoreFile) throws Exception {
        URL serverUrl = getServer(ServerType.PUBLIC, ServerProtocol.HTTPS, false);
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
