package net.webpdf.wsclient.testsuite.server;

import net.webpdf.wsclient.testsuite.config.TestConfig;
import org.apache.hc.core5.net.URIBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(TestServer.class);
    private static final File composeFile = new File("docker/docker-compose.yml");

    private final URIBuilder uriBuilderLocalServer;
    private final URIBuilder uriBuilderPublicServer;

    private DockerComposeContainer<?> environment;

    private static volatile TestServer testServer;

    private static @NotNull TestServer getOrCreateInstance() {
        if (TestServer.testServer == null) {
            synchronized (TestServer.class) {
                if (TestServer.testServer == null) {
                    TestServer.testServer = new TestServer();
                }
            }
        }
        return TestServer.testServer;
    }

    public static @NotNull TestServer getInstance() {
        return getOrCreateInstance();
    }

    private TestServer() {
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

    public String getLocalAdminName() {
        return TestConfig.getInstance().getServerConfig().getLocalAdminName();
    }

    public char[] getLocalAdminPassword() {
        return TestConfig.getInstance().getServerConfig().getLocalAdminPassword().toCharArray();
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

    public synchronized void startContainer() {
        boolean isServerRequired = TestConfig.getInstance().getIntegrationTestConfig().useContainer();
        if (isServerRequired) {
            LOGGER.info("Starting docker container '{}'", composeFile);
            //noinspection resource
            this.environment = new DockerComposeContainer<>(composeFile)
                    .withExposedService("LDAP", 10389, Wait.forListeningPort())
                    .withExposedService("webPDF", 8080,
                            Wait.forHttp("/webPDF/rest/portal/info").forPort(8080));
            this.environment.start();
            LOGGER.info("Docker container started");

        }
    }

    public void stopContainer() {
        if (this.environment != null) {
            LOGGER.info("Stopping docker container '{}'", composeFile);
            this.environment.stop();
            LOGGER.info("Docker container stopped");
        }
    }
}
