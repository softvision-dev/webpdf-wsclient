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
                //noinspection HttpUrlsUsage
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
                assertEquals(-47, result.getError().getCode(), "Unable to find 'OfficeBridge.exe' error expected.");
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
                assertFalse(logContents.isEmpty(), "Content size should be > 0.");
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

    @Test
    @IntegrationTest
    public void testClusterConfig() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ClusterSettings clusterSettings = session.getAdministrationManager().getClusterConfiguration();
                assertNotNull(clusterSettings, "cluster configuration should exist.");
                assertNotNull(clusterSettings.getMode(), "mode should exist.");

                clusterSettings.setMode(ClusterMode.CLUSTER);
                assertEquals(ClusterMode.CLUSTER, clusterSettings.getMode(), "mode should be cluster.");

                session.getAdministrationManager().updateClusterConfiguration(clusterSettings);
                clusterSettings = session.getAdministrationManager().fetchClusterConfiguration();
                assertNotNull(clusterSettings, "cluster configuration should exist.");
                assertEquals(ClusterMode.CLUSTER, clusterSettings.getMode(), "mode should be cluster.");

                // reset cluster mode
                clusterSettings.setMode(ClusterMode.SINGLE);
                session.getAdministrationManager().updateClusterConfiguration(clusterSettings);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testClusterStatus() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            )) {
                ClusterStatus clusterStatus = session.getAdministrationManager().fetchClusterStatus();
                assertNotNull(clusterStatus.getSettings(), "settings should exist.");
                assertEquals(ClusterMode.SINGLE, clusterStatus.getSettings().getMode(), "mode should be single.");
            }
        });
    }
}