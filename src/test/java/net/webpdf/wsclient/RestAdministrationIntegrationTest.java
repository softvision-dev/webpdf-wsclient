package net.webpdf.wsclient;

import jakarta.xml.bind.DatatypeConverter;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.schema.beans.JWTToken;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.testsuite.config.TestConfig;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.tools.SerializeHelper;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RestAdministrationIntegrationTest {
    public TestServer testServer = TestServer.getInstance();
    private final TestResources testResources = new TestResources(RestAdministrationIntegrationTest.class);

    @Test
    @IntegrationTest
    public void testUser() {
        assertDoesNotThrow(() -> {
            // Anonymous
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL))
            )) {
                assertThrows(ClientResultException.class, () -> session.getAdministrationManager().getApplicationConfiguration());
            }

            // User
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(TestConfig.getInstance().getServerConfig().getLocalUserName(), TestConfig.getInstance().getServerConfig().getLocalUserPassword())
            )) {
                assertThrows(ClientResultException.class, () -> session.getAdministrationManager().getApplicationConfiguration());
            }

            // Admin
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                assertDoesNotThrow(() -> session.getAdministrationManager().getApplicationConfiguration());
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
                session.getAdministrationManager().updateServerConfiguration(serverConfiguration);

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
    public void validateApplication() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ApplicationConfigApplication applicationConfiguration = session.getAdministrationManager().getApplicationConfiguration();
                assertNotNull(applicationConfiguration, "Application configuration should exist.");

                List<AdminApplicationCheck> applicationChecks = new ArrayList<>();

                AdminTsaApplicationCheck tsaApplicationCheck = new AdminTsaApplicationCheck();
                ApplicationConfigTsa configTsa = new ApplicationConfigTsa();
                configTsa.setUrl("http://timestamp.comodoca.com");
                configTsa.setHashAlgorithm(ApplicationConfigTsa.HashAlgorithmEnum.SHA_256);
                applicationConfiguration.setTsa(configTsa);

                applicationChecks.add(tsaApplicationCheck);

                AdminConfigurationResult result = session.getAdministrationManager().validateApplicationConfiguration(
                        applicationConfiguration, applicationChecks
                );
                assertNotNull(result.getError(), "error should exist.");
                assertEquals(0, result.getError().getCode(), "No error expected.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void validateApplicationException() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ApplicationConfigApplication applicationConfiguration = session.getAdministrationManager().getApplicationConfiguration();
                assertNotNull(applicationConfiguration, "Application configuration should exist.");

                AdminTsaApplicationCheck tsaCheck = new AdminTsaApplicationCheck();
                tsaCheck.setCheckType(AdminApplicationCheckMode.TSA);
                List<AdminApplicationCheck> applicationChecks = new ArrayList<>();
                applicationChecks.add(tsaCheck);

                AdminConfigurationResult result = session.getAdministrationManager().validateApplicationConfiguration(
                        applicationConfiguration, applicationChecks
                );
                assertNotNull(result.getError(), "error should exist.");
                assertEquals(-34, result.getError().getCode(), "Tsa error expected.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void validateApplicationExecutables() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ApplicationConfigApplication applicationConfiguration = session.getAdministrationManager().getApplicationConfiguration();
                assertNotNull(applicationConfiguration, "Application configuration should exist.");

                List<AdminExecutableName> adminExecutableNames = new ArrayList<>();
                adminExecutableNames.add(AdminExecutableName.OUTSIDEIN);
                adminExecutableNames.add(AdminExecutableName.PDFTOOLS);
                adminExecutableNames.add(AdminExecutableName.OFFICEBRIDGE);

                AdminConfigurationResult result = session.getAdministrationManager().testExecutables(
                        applicationConfiguration, adminExecutableNames
                );
                assertNotNull(result.getError(), "error should exist.");
                assertEquals(-47, result.getError().getCode(), "Unable to find 'officebridge.exe' error expected.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testServerStatus() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                AdminServerStatus serverStatus = session.getAdministrationManager().fetchServerStatus();
                assertNotNull(serverStatus.getWebservices(), "Webservices should exist.");
                assertNotNull(serverStatus.getWebservices().get("toolbox"), "Webservice toolbox should exist.");
                assertEquals(
                        WebserviceStatus.OK,
                        serverStatus.getWebservices().get("toolbox").getStatus(),
                        "Webservice status should be ok."
                );
            }
        });
    }

    @Test
    @IntegrationTest
    public void testStreamLog() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                int logLength = session.getAdministrationManager().fetchLogLength();
                String logContents = session.getAdministrationManager().fetchLog("0-" + logLength);
                assertNotNull(logContents, "Log contents should exist.");
                assertTrue(logContents.length() > 0, "Content size should be > 0.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testAdministrationSupportInformation() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    session.getAdministrationManager().buildSupportPackage(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }


    @Test
    @IntegrationTest
    public void testAdministrationDatastore() {
        assertDoesNotThrow(() -> {
            File logoFile = testResources.getResource("logo.png");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                AdminLogoFileDataStore fileDataStore = new AdminLogoFileDataStore();
                fileDataStore.setFileContent(DatatypeConverter.printBase64Binary(
                        FileUtils.readFileToByteArray(logoFile)
                ));
                fileDataStore.setFileGroup(AdminFileGroupDataStore.LOGO);

                AdminLogoFileDataStore currentLogo = (AdminLogoFileDataStore) session.getAdministrationManager().fetchDatastore(
                        AdminFileGroupDataStore.LOGO
                );
                assertNotEquals(
                        fileDataStore.getFileContent(), currentLogo.getFileContent(),
                        "Content of uploaded logo file should be different to local file."
                );

                session.getAdministrationManager().updateDatastore(fileDataStore);
                currentLogo = (AdminLogoFileDataStore) session.getAdministrationManager().fetchDatastore(
                        AdminFileGroupDataStore.LOGO
                );
                assertEquals(
                        fileDataStore.getFileContent(), currentLogo.getFileContent(),
                        "Content of uploaded logo file should be identical to local file."
                );

                session.getAdministrationManager().deleteDatastore(AdminFileGroupDataStore.LOGO);
                currentLogo = (AdminLogoFileDataStore) session.getAdministrationManager().fetchDatastore(
                        AdminFileGroupDataStore.LOGO
                );
                assertNotEquals(
                        fileDataStore.getFileContent(), currentLogo.getFileContent(),
                        "Content of uploaded logo file should be different to local file."
                );
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
                assertTrue(certificates.size() > 0);

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
                assertTrue(
                        certificates.size() > 0,
                        "Keystore certificate should exist."
                );

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
    public void testAdministrationStatistics() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                Date currentDate = new Date();
                Date yesterday = new Date(currentDate.getTime() - 24 * 60 * 60 * 1000);
                List<Webservice> webservices = new ArrayList<>();
                webservices.add(Webservice.CONVERTER);

                AdminStatistic statistic = session.getAdministrationManager().fetchServerStatistic(
                        AdminDataSourceServerState.REALTIME, AdminAggregationServerState.MONTH,
                        webservices, yesterday, currentDate
                );
                assertNotNull(statistic, "There should be statistics.");
                Map<String, Map<String, AdminTimeFrameServerState>> data = statistic.getData();
                assertNotNull(data, "There should be data.");
                assertNotNull(data.get(Webservice.CONVERTER.getValue()), "There should be converter data.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testAdministrationSessions() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                SessionTable sessions = session.getAdministrationManager().fetchSessionTable();
                assertNotNull(sessions, "There should be a session table.");
                assertNotNull(sessions.getActiveSessions(), "There should be active sessions.");
                assertTrue(sessions.getActiveSessions() > 0, "There should be at least 1 active session.");
                assertNotNull(sessions.getSessionList(), "There should be a session list.");

                AuthMaterial authMaterial = session.getAuthProvider().provide(session);
                String[] tokenParts = authMaterial.getToken().split("\\.");
                String jwtJson = new String(Base64.getDecoder().decode(tokenParts[1]), StandardCharsets.UTF_8);

                try (StringReader reader = new StringReader(jwtJson)) {
                    StreamSource streamSource = new StreamSource(reader);
                    JWTToken jwtToken = SerializeHelper.fromJSON(streamSource, JWTToken.class);
                    assertNotNull(jwtToken.getSub(), "There should be a token sub.");
                    session.getAdministrationManager().closeSession(jwtToken.getSub());
                }
            }
        });
    }
}