package net.webpdf.wsclient.testsuite;

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

    private static final String KEY_SERVER_LOCAL_URL = "server.local.url";
    private static final String KEY_SERVER_LOCAL_USER = "server.local.user";
    private static final String KEY_SERVER_LOCAL_PASSWORD = "server.local.password";
    private static final String KEY_SERVER_PUBLIC_URL = "server.public.url";
    private static final String SERVER_LOCAL_URL = System.getProperty(KEY_SERVER_LOCAL_URL);
    private static final String SERVER_LOCAL_USER = System.getProperty(KEY_SERVER_LOCAL_USER);
    private static final String SERVER_LOCAL_PASSWORD = System.getProperty(KEY_SERVER_LOCAL_PASSWORD);
    private static final String SERVER_PUBLIC_URL = System.getProperty(KEY_SERVER_PUBLIC_URL);
    private final URIBuilder uriBuilderLocalServer;
    private final URIBuilder uriBuilderPublicServer;

    public TestServer() {
        this.uriBuilderLocalServer = assertDoesNotThrow(() ->
                new URIBuilder(SERVER_LOCAL_URL).setPath(SERVER_PATH));
        if (!uriBuilderLocalServer.isAbsolute()) {
            fail("Invalid URL '" + SERVER_LOCAL_URL + "'");
        }
        this.uriBuilderPublicServer = assertDoesNotThrow(() ->
                new URIBuilder(SERVER_PUBLIC_URL).setPath(SERVER_PATH));
        if (!uriBuilderPublicServer.isAbsolute()) {
            fail("Invalid URL '" + SERVER_PUBLIC_URL + "'");
        }
    }

    public URL getServer(ServerType serverType) throws URISyntaxException, MalformedURLException {
        return buildServer(serverType, ServerProtocol.HTTP, null, null);
    }

    public URL getServer(ServerType serverType, String user, String password) throws URISyntaxException, MalformedURLException {
        return buildServer(serverType, ServerProtocol.HTTP, user, password);
    }

    public URL getServer(ServerType serverType, ServerProtocol serverProtocol, boolean useCredentials) throws URISyntaxException, MalformedURLException {
        String user = useCredentials ? SERVER_LOCAL_USER : null;
        String password = useCredentials ? SERVER_LOCAL_PASSWORD : null;

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
        return SERVER_LOCAL_USER;
    }

    public String getLocalPassword() {
        return SERVER_LOCAL_PASSWORD;
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
