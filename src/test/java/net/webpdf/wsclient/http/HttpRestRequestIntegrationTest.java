package net.webpdf.wsclient.http;

import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRestRequestIntegrationTest {

    private final TestResources testResources = new TestResources(HttpRestRequestIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @Test
    @IntegrationTest
    public void testWithCredentials() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("test.pdf");
            File outputFile = testResources.getTempFolder().newFile();
            try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL));
                 OutputStream fos = Files.newOutputStream(outputFile.toPath())) {
                UsernamePasswordCredentials userCredentials = new UsernamePasswordCredentials(
                        testServer.getLocalUser(), testServer.getLocalPassword());
                session.setCredentials(userCredentials);
                session.login();
                HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                assertNotNull(httpRestRequest,
                        "HttpRestRequest should have been build.");
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
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
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL))) {
                        UsernamePasswordCredentials userCredentials = new UsernamePasswordCredentials(
                                "invalid", "invalid");
                        session.setCredentials(userCredentials);
                        session.login();
                    }
                });
    }

    @Test
    @IntegrationTest
    public void testNullEntity() {
        assertThrows(ResultException.class,
                () -> {
                    try (RestWebServiceSession session =
                                 SessionFactory.createInstance(WebServiceProtocol.REST,
                                         testServer.getServer(ServerType.LOCAL))) {
                        session.login();
                        HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                        assertNotNull(httpRestRequest,
                                "HttpRestRequest should have been build.");
                        httpRestRequest.buildRequest(HttpMethod.POST, "documents/", null);
                        DocumentFile response = httpRestRequest.executeRequest(DocumentFile.class);
                        assertNotNull(response);
                    }
                });
    }

    @Test
    @IntegrationTest
    public void testNullHttpMethod() {
        assertThrows(ResultException.class,
                () -> {
                    File file = testResources.getResource("test.pdf");
                    try (RestWebServiceSession session =
                                 SessionFactory.createInstance(WebServiceProtocol.REST,
                                         testServer.getServer(ServerType.LOCAL))) {
                        session.login();
                        HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                        assertNotNull(httpRestRequest, "HttpRestRequest should have been build.");
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY,
                                file.getName());
                        HttpEntity entity = builder.build();
                        assertNotNull(entity, "HttpEntity should have been build.");
                        httpRestRequest.buildRequest(null, "documents/", entity);
                    }
                });
    }

    @Test
    @IntegrationTest
    public void testNullHttpPath() {
        assertThrows(IOException.class,
                () -> {
                    File file = testResources.getResource("test.pdf");
                    try (RestWebServiceSession session =
                                 SessionFactory.createInstance(WebServiceProtocol.REST,
                                         testServer.getServer(ServerType.LOCAL))) {
                        session.login();
                        HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                        assertNotNull(httpRestRequest,
                                "HttpRestRequest should have been build.");
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY,
                                file.getName());
                        HttpEntity entity = builder.build();
                        assertNotNull(entity,
                                "HttpEntity should have been build.");
                        httpRestRequest.buildRequest(HttpMethod.GET, (String) null, entity);
                        httpRestRequest.executeRequest(DocumentFile.class);
                    }
                });
    }

    @Test
    @IntegrationTest
    public void testNullTypRequest() {
        assertThrows(IOException.class,
                () -> {
                    File file = testResources.getResource("test.pdf");
                    try (RestWebServiceSession session =
                                 SessionFactory.createInstance(WebServiceProtocol.REST,
                                         testServer.getServer(ServerType.LOCAL))) {
                        session.login();
                        HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                        assertNotNull(httpRestRequest,
                                "HttpRestRequest should have been build.");
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY,
                                file.getName());
                        HttpEntity entity = builder.build();
                        assertNotNull(entity,
                                "HttpEntity should have been build.");
                        httpRestRequest.buildRequest(HttpMethod.GET, "/documents", entity);
                        httpRestRequest.executeRequest((Class<?>) null);
                    }
                });
    }

    @Test
    @IntegrationTest
    public void testHttpPathNullOutput() {
        assertThrows(ResultException.class,
                () -> {
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL))) {
                        session.login();
                        HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
                        assertNotNull(httpRestRequest,
                                "HttpRestRequest should have been build.");
                        httpRestRequest.setAcceptHeader("application/octet-stream");
                        httpRestRequest.buildRequest(HttpMethod.GET, (String) null, null);
                        httpRestRequest.executeRequest((OutputStream) null);
                    }
                });
    }

}
