package net.webpdf.wsclient;

import jakarta.xml.bind.DatatypeConverter;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Execution(value = ExecutionMode.SAME_THREAD,
        reason = "Tests that change the configuration cannot be executed in parallel, as otherwise the test results cannot be predicted.")
public class RestAdministrationConfigTest {

    public TestServer testServer = TestServer.getInstance();
    private final TestResources testResources = new TestResources(RestAdministrationIntegrationTest.class);

    @Test
    @IntegrationTest
    public void testServerConfig() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ServerConfigServer serverConfiguration = session.getAdministrationManager().getServerConfiguration();
                assertNotNull(serverConfiguration, "Server configuration should exist.");
                assertEquals("localhost", serverConfiguration.getHost().getName(), "Host name should be localhost.");

                serverConfiguration.getHost().setName("custom");
                AdminConfigurationResult adminConfigurationResult = session.getAdministrationManager().updateServerConfiguration(serverConfiguration);
                assertNotNull(adminConfigurationResult);
                WebserviceResult webserviceResult = adminConfigurationResult.getError();
                assertNotNull(webserviceResult);
                assertEquals(0, webserviceResult.getCode());

                serverConfiguration = session.getAdministrationManager().fetchServerConfiguration();
                assertNotNull(serverConfiguration, "Server configuration should exist.");
                assertEquals("custom", serverConfiguration.getHost().getName(), "Host name should be custom.");

                // reset host name
                serverConfiguration.getHost().setName("localhost");
                session.getAdministrationManager().updateServerConfiguration(serverConfiguration);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testAdministrationTruststore() {
        String truststoreFilename = "myTrustStore.jks";
        File truststoreFile = testResources.getResource(truststoreFilename);
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ServerConfigServer serverConfiguration = session.getAdministrationManager().getServerConfiguration();
                assertNotNull(serverConfiguration, "Server configuration should exist.");
                assertNull(serverConfiguration.getTruststore(), "Truststore should not exist.");

                AdminTrustStoreKeyStore truststore = session.getAdministrationManager().getTrustStoreKeyStore();
                assertEquals("", truststore.getKeyStoreContent(), "Truststore content should be empty.");

                ServerConfigTruststoreServer truststoreServer = new ServerConfigTruststoreServer();
                truststoreServer.setFile(truststoreFilename);
                truststoreServer.password("webpdf");
                serverConfiguration.setTruststore(truststoreServer);
                truststore = new AdminTrustStoreKeyStore();
                truststore.setKeyStoreContent(DatatypeConverter.printBase64Binary(
                        FileUtils.readFileToByteArray(truststoreFile)
                ));

                session.getAdministrationManager().setTrustStoreKeyStore(truststore);
                session.getAdministrationManager().updateServerConfiguration(serverConfiguration);

                serverConfiguration = session.getAdministrationManager().fetchServerConfiguration();
                assertNotNull(serverConfiguration, "Server configuration should exist.");
                assertNotNull(serverConfiguration.getTruststore(), "Truststore should exist.");

                truststore = session.getAdministrationManager().fetchTrustStoreKeyStore();
                assertNotNull(truststore, "Truststore should exist.");

                // reset keystore
                serverConfiguration.setTruststore(null);
                truststore.setKeyStoreContent(null);
                session.getAdministrationManager().updateServerConfiguration(serverConfiguration);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testSslConnectionKeystore() {
        String sslKeystoreFilename = "sslKeyStore.jks";
        File sslKeystoreFile = testResources.getResource(sslKeystoreFilename);
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ServerConfigServer serverConfiguration = session.getAdministrationManager().getServerConfiguration();
                assertNotNull(serverConfiguration, "Server configuration should exist.");
                assertNotNull(serverConfiguration.getConnectors().getConnector(), "connector should exist.");
                ServerConfigSSL sslConfiguration = serverConfiguration.getConnectors().getConnector().get(1).getSsl();
                assertNotNull(sslConfiguration, "SSL config should exist.");
                assertNotNull(sslConfiguration.getKeystore(), "Keystore should exist.");
                assertEquals("ssl.jks", sslConfiguration.getKeystore().getFile(), "Keystore should be default.");

                Map<String, AdminConnectorKeyStore> connectorKeyStores = session.getAdministrationManager().getConnectorKeyStore();
                assertNotNull(connectorKeyStores.get("ssl.jks"), "Default keystore should exist.");
                List<CertificateEntry> certificates = connectorKeyStores.get("ssl.jks").getCertificates();
                assertNotNull(certificates, "certificates should exist.");
                assertFalse(certificates.isEmpty());

                ServerConfigKeystoreSSL keystoreSSL = new ServerConfigKeystoreSSL();
                keystoreSSL.setType(ServerConfigKeystoreSSL.TypeEnum.JKS);
                keystoreSSL.setFile(sslKeystoreFilename);
                keystoreSSL.setPassword("webpdf");
                sslConfiguration.setKeystore(keystoreSSL);

                AdminConnectorKeyStore connectorKeyStore = new AdminConnectorKeyStore();
                connectorKeyStore.setKeyStoreContent(DatatypeConverter.printBase64Binary(
                        FileUtils.readFileToByteArray(sslKeystoreFile)
                ));
                connectorKeyStores.put(sslKeystoreFilename, connectorKeyStore);

                session.getAdministrationManager().setConnectorKeyStore(connectorKeyStores);
                session.getAdministrationManager().updateServerConfiguration(serverConfiguration);
                assertNotNull(serverConfiguration, "Server configuration should exist.");
                sslConfiguration = serverConfiguration.getConnectors().getConnector().get(1).getSsl();
                assertNotNull(sslConfiguration, "SSL config should exist.");
                assertNotNull(sslConfiguration.getKeystore(), "Keystore should exist.");

                connectorKeyStores = session.getAdministrationManager().fetchConnectorKeyStore();
                assertNotNull(connectorKeyStores.get(sslKeystoreFilename), "New keystore should exist.");
                certificates = connectorKeyStores.get(sslKeystoreFilename).getCertificates();
                assertNotNull(certificates, "certificates should exist.");
                assertFalse(certificates.isEmpty(), "Keystore certificate should exist.");

                // reset keystore
                sslConfiguration = serverConfiguration.getConnectors().getConnector().get(1).getSsl();
                assertNotNull(sslConfiguration, "SSL config should exist.");
                assertNotNull(sslConfiguration.getKeystore(), "Keystore should exist.");
                sslConfiguration.getKeystore().setType(
                        ServerConfigKeystoreSSL.TypeEnum.JKS
                );
                sslConfiguration.getKeystore().setFile("ssl.jks");
                connectorKeyStores.remove(sslKeystoreFilename);
                session.getAdministrationManager().setConnectorKeyStore(connectorKeyStores);
                session.getAdministrationManager().updateServerConfiguration(serverConfiguration);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testApplicationConfig() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ApplicationConfigApplication applicationConfiguration = session.getAdministrationManager().getApplicationConfiguration();
                assertNotNull(applicationConfiguration, "Application configuration should exist.");
                assertNotNull(applicationConfiguration.getPortal().getUserInterface(), "UserInterface should exist.");

                ApplicationConfigPortalUserInterfaceLimits limits = new ApplicationConfigPortalUserInterfaceLimits();
                limits.setDisplayDiskSpace(true);
                applicationConfiguration.getPortal().getUserInterface().setLimits(limits);

                ApplicationConfigPortalUserInterface userInterface = applicationConfiguration.getPortal().getUserInterface();
                assertNotNull(userInterface, "UserInterface should exist.");
                assertNotNull(userInterface.getLimits(), "Limits should exist.");
                assertEquals(true, userInterface.getLimits().getDisplayDiskSpace(), "DisplayDiskSpace should be true.");

                session.getAdministrationManager().updateApplicationConfiguration(applicationConfiguration);

                applicationConfiguration = session.getAdministrationManager().fetchApplicationConfiguration();
                assertNotNull(applicationConfiguration, "Application configuration should exist.");
                userInterface = applicationConfiguration.getPortal().getUserInterface();
                assertNotNull(userInterface, "UserInterface should exist.");
                assertNotNull(userInterface.getLimits(), "Limits should exist.");
                assertEquals(true, userInterface.getLimits().getDisplayDiskSpace(), "DisplayDiskSpace should be true.");

                // reset displayDiskSpace
                applicationConfiguration.getPortal().getUserInterface().setLimits(null);
                session.getAdministrationManager().updateApplicationConfiguration(applicationConfiguration);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testAdministrationKeystore() {
        String signatureFilename = "signatureKeyStore.jks";
        File signatureFile = testResources.getResource(signatureFilename);
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ApplicationConfigApplication applicationConfiguration = session.getAdministrationManager().getApplicationConfiguration();
                assertNotNull(applicationConfiguration, "Application configuration should exist.");
                assertNotNull(applicationConfiguration.getKeystore(), "Keystore should exist.");
                assertEquals(ApplicationConfigKeystore.TypeEnum.NONE, applicationConfiguration.getKeystore().getType(),
                        "Keystore type should be NONE.");

                AdminGlobalKeyStore globalKeyStore = session.getAdministrationManager().getGlobalKeyStore();
                assertEquals("", globalKeyStore.getKeyStoreContent(), "Keystore content should be empty.");

                ApplicationConfigKeystore configKeystore = new ApplicationConfigKeystore();
                configKeystore.setType(ApplicationConfigKeystore.TypeEnum.JKS);
                configKeystore.setPassword("webpdf");
                applicationConfiguration.setKeystore(configKeystore);
                globalKeyStore = new AdminGlobalKeyStore();
                globalKeyStore.setKeyStoreContent(DatatypeConverter.printBase64Binary(
                        FileUtils.readFileToByteArray(signatureFile)
                ));

                session.getAdministrationManager().setGlobalKeyStore(globalKeyStore);
                session.getAdministrationManager().updateApplicationConfiguration(applicationConfiguration);
                assertNotNull(applicationConfiguration, "Application configuration should exist.");
                assertNotNull(applicationConfiguration.getKeystore(), "Keystore should exist.");

                globalKeyStore = session.getAdministrationManager().fetchGlobalKeyStore();
                assertNotNull(globalKeyStore, "Keystore should exist.");

                // reset keystore
                applicationConfiguration.getKeystore().setType(ApplicationConfigKeystore.TypeEnum.NONE);
                applicationConfiguration.getKeystore().setPassword(null);
                globalKeyStore.setKeyStoreContent("");
                session.getAdministrationManager().setGlobalKeyStore(globalKeyStore);
                session.getAdministrationManager().updateApplicationConfiguration(applicationConfiguration);

                globalKeyStore = session.getAdministrationManager().fetchGlobalKeyStore();
                assertEquals("", globalKeyStore.getKeyStoreContent(), "Keystore content should be empty.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testUserConfig() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                UserConfigUsers userConfiguration = session.getAdministrationManager().getUserConfiguration();

                UserConfigUser testUser = new UserConfigUser();
                testUser.setUsername("tester");
                testUser.setPassword("password");

                assertNotNull(userConfiguration, "User configuration should exist.");
                List<UserConfigUser> userList = userConfiguration.getUser();
                assertNotNull(userList, "The user list should exist.");
                assertEquals(2, userList.size(), "There should be 2 users.");
                userList.add(testUser);

                userConfiguration.setUser(userList);
                session.getAdministrationManager().updateUserConfiguration(userConfiguration);

                userConfiguration = session.getAdministrationManager().fetchUserConfiguration();
                assertNotNull(userConfiguration, "User configuration should exist.");
                userList = userConfiguration.getUser();
                assertNotNull(userList, "The user list should exist.");
                assertEquals(3, userList.size(), "There should be 3 users.");

                // reset users
                userList.removeIf(user -> {
                    assertNotNull(user.getUsername(), "Username should exist.");
                    return user.getUsername().equals("tester");
                });
                userConfiguration.setUser(userList);
                session.getAdministrationManager().updateUserConfiguration(userConfiguration);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testLogsConfig() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                AdminLogFileConfiguration logConfiguration = session.getAdministrationManager().getLogConfiguration();
                assertNotNull(logConfiguration, "Log configuration should exist.");

                logConfiguration.setDebugMode(AdminLogConfigurationMode.DEBUG);
                session.getAdministrationManager().updateLogConfiguration(logConfiguration);

                logConfiguration = session.getAdministrationManager().fetchLogConfiguration();
                assertNotNull(logConfiguration, "Log configuration should exist.");
                assertEquals(AdminLogConfigurationMode.DEBUG, logConfiguration.getDebugMode(), "Debug mode should be active.");

                // reset debug mode
                logConfiguration.setDebugMode(AdminLogConfigurationMode.NONE);
                session.getAdministrationManager().updateLogConfiguration(logConfiguration);
            }
        });
    }
}
