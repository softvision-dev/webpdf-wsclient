package net.webpdf.wsclient.http;

import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.DocumentFile;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRestRequestIntegrationTest {
    private final TestResources testResources = new TestResources(HttpRestRequestIntegrationTest.class);
    public TestServer testServer = TestServer.getInstance();

    @Test
    @IntegrationTest
    public void testWithCredentials() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("test.pdf");
            File outputFile = testResources.getTempFolder().newFile();
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword()));
                 OutputStream fos = Files.newOutputStream(outputFile.toPath())) {
                HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                assertNotNull(httpRestRequest,
                        "HttpRestRequest should have been build.");
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.LEGACY);
                builder.addBinaryBody("filedata", file,
                        ContentType.DEFAULT_BINARY, file.getName());
                HttpEntity entity = builder.build();
                assertNotNull(entity,
                        "HttpEntity should have been build.");
                httpRestRequest.buildRequest(HttpMethod.POST, "documents/", entity);
                DocumentFile response = httpRestRequest.executeRequest(DocumentFile.class);
                assertNotNull(response,
                        "Uploaded file should not be null");
                assertEquals("test", response.getFileName(),
                        "Uploaded filename is incorrect.");
                assertEquals("application/pdf", response.getMimeType(),
                        "Uploaded MimeType is incorrect.");

                httpRestRequest = HttpRestRequest.createRequest(session);
                assertNotNull(httpRestRequest,
                        "HttpRestRequest should have been build.");
                httpRestRequest.setAcceptHeader("application/octet-stream");
                httpRestRequest.buildRequest(HttpMethod.GET, "documents/" + response.getDocumentId(),
                        null);
                httpRestRequest.executeRequest(fos);
                assertTrue(FileUtils.contentEquals(file, outputFile),
                        "Content of output file should be identical to test file.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testWithInvalidCredentials() {
        assertThrows(ResultException.class,
                () -> {
                    //noinspection unused
                    try (RestWebServiceSession session = SessionFactory.createInstance(
                            new SessionContext(WebServiceProtocol.REST,
                                    testServer.getServer(ServerType.LOCAL)),
                            new UserAuthProvider("invalid", "invalid"))) {
                        fail("The login itself shall fail and this line should never be reached.");
                    }
                });
    }

    @Test
    @IntegrationTest
    public void testNullEntity() {
        assertThrows(ResultException.class,
                () -> {
                    try (RestWebServiceSession session = SessionFactory.createInstance(
                            new SessionContext(WebServiceProtocol.REST,
                                    testServer.getServer(ServerType.LOCAL)))) {
                        HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                        assertNotNull(httpRestRequest,
                                "HttpRestRequest should have been build.");
                        httpRestRequest.buildRequest(HttpMethod.POST, "documents/");
                        DocumentFile response = httpRestRequest.executeRequest(DocumentFile.class);
                        assertNotNull(response);
                    }
                });
    }
}