package net.webpdf.wsclient.http;

import net.webpdf.wsclient.session.token.SessionToken;
import net.webpdf.wsclient.session.token.Token;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFile;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
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
    public void testRestRequestAndSessionRefreshing() throws Exception {
        File file = testResources.getResource("test.pdf");
        File outputFile = testResources.getTempFolder().newFile();
        File outputFile2 = testResources.getTempFolder().newFile();
        try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL));
             OutputStream fos = Files.newOutputStream(outputFile.toPath());
             OutputStream fos2 = Files.newOutputStream(outputFile2.toPath())) {
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull(httpRestRequest,
                    "HttpRestRequest should have been build.");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
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
            String documentID = response.getDocumentId();
            assertNotNull(httpRestRequest,
                    "HttpRestRequest should have been build.");
            httpRestRequest.setAcceptHeader("application/octet-stream");
            httpRestRequest.buildRequest(HttpMethod.GET, "documents/" + documentID,
                    null);
            httpRestRequest.executeRequest(fos);
            assertTrue(FileUtils.contentEquals(file, outputFile),
                    "Content of output file should be identical to test file.");

            Token previousToken = session.getToken();
            assertTrue(previousToken instanceof SessionToken,
                    "The token should have been a SessionToken.");
            session.refresh();
            Token currentToken = session.getToken();
            assertTrue(currentToken instanceof SessionToken,
                    "The token should have been a SessionToken.");
            assertNotEquals(previousToken, currentToken,
                    "The token should have been replaced.");
            assertNotEquals(((SessionToken) previousToken).getExpiration(),
                    ((SessionToken) currentToken).getExpiration(),
                    "The expiration instant should have differed");

            // Finally download the same document again - even though the token has changed, the session should be the
            // same.
            httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull(httpRestRequest,
                    "HttpRestRequest should have been build.");
            httpRestRequest.setAcceptHeader("application/octet-stream");
            httpRestRequest.buildRequest(HttpMethod.GET, "documents/" + documentID,
                    null);
            httpRestRequest.executeRequest(fos2);
            assertTrue(FileUtils.contentEquals(file, outputFile2),
                    "Content of output file 2 should be identical to test file.");
        }
    }

    @Test
    public void testWithCredentials() throws Exception {
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
            builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
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
    }

    @Test
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
    public void testNullEntity() {
        assertThrows(ResultException.class,
                () -> {
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
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
    public void testNullHttpMethod() {
        assertThrows(ResultException.class,
                () -> {
                    File file = testResources.getResource("test.pdf");
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
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
    public void testNullHttpPath() {
        assertThrows(IOException.class,
                () -> {
                    File file = testResources.getResource("test.pdf");
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
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
    public void testNullTypRequest() {
        assertThrows(IOException.class,
                () -> {
                    File file = testResources.getResource("test.pdf");
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
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
