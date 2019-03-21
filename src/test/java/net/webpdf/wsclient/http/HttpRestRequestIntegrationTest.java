package net.webpdf.wsclient.http;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.DocumentFileBean;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.*;

public class HttpRestRequestIntegrationTest {

    private final TestResources testResources = new TestResources(HttpRestRequestIntegrationTest.class);
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testRestRequest() throws Exception {
        File file = testResources.getResource("test.pdf");
        File outputFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL));
             OutputStream fos = new FileOutputStream(outputFile)) {
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
            HttpEntity entity = builder.build();
            assertNotNull("HttpEntity should have been build.", entity);
            httpRestRequest.buildRequest(HttpMethod.POST, "documents/", entity);
            DocumentFileBean response = httpRestRequest.executeRequest(DocumentFileBean.class);
            assertEquals("Uploaded filename is incorrect.", "test", response.getFileName());
            assertEquals("Uploaded MimeType is incorrect.", "application/pdf", response.getMimeType());

            httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            httpRestRequest.setAcceptHeader("application/octet-stream");
            httpRestRequest.buildRequest(HttpMethod.GET, "documents/" + response.getDocumentId(), null);
            httpRestRequest.executeRequest(fos);
            assertTrue("Content of output file should be identical to test file.", FileUtils.contentEquals(file, outputFile));
        }
    }

    @Test
    public void testWithCredentials() throws Exception {
        File file = testResources.getResource("test.pdf");
        File outputFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL));
             OutputStream fos = new FileOutputStream(outputFile)) {
            UsernamePasswordCredentials userCredentials = new UsernamePasswordCredentials(testServer.getLocalUser(), testServer.getLocalPassword());
            session.setCredentials(userCredentials);
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
            HttpEntity entity = builder.build();
            assertNotNull("HttpEntity should have been build.", entity);
            httpRestRequest.buildRequest(HttpMethod.POST, "documents/", entity);
            DocumentFileBean response = httpRestRequest.executeRequest(DocumentFileBean.class);
            assertEquals("Uploaded filename is incorrect.", "test", response.getFileName());
            assertEquals("Uploaded MimeType is incorrect.", "application/pdf", response.getMimeType());

            httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            httpRestRequest.setAcceptHeader("application/octet-stream");
            httpRestRequest.buildRequest(HttpMethod.GET, "documents/" + response.getDocumentId(), null);
            httpRestRequest.executeRequest(fos);
            assertTrue("Content of output file should be identical to test file.", FileUtils.contentEquals(file, outputFile));
        }
    }

    @Test(expected = ResultException.class)
    public void testWithInvalidCredentials() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            UsernamePasswordCredentials userCredentials = new UsernamePasswordCredentials("invalid", "invalid");
            session.setCredentials(userCredentials);
            session.login();
        }
    }

    @Test(expected = ResultException.class)
    public void testNullEntity() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            httpRestRequest.buildRequest(HttpMethod.POST, "documents/", null);
            DocumentFileBean response = httpRestRequest.executeRequest(DocumentFileBean.class);
            Assert.assertNotNull(response);
        }
    }

    @Test(expected = ResultException.class)
    public void testNullHttpMethod() throws Exception {
        File file = testResources.getResource("test.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
            HttpEntity entity = builder.build();
            assertNotNull("HttpEntity should have been build.", entity);
            httpRestRequest.buildRequest(null, "documents/", entity);
        }
    }

    @Test(expected = IOException.class)
    public void testNullHttpPath() throws Exception {
        File file = testResources.getResource("test.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
            HttpEntity entity = builder.build();
            assertNotNull("HttpEntity should have been build.", entity);
            httpRestRequest.buildRequest(HttpMethod.GET, null, entity);
            httpRestRequest.executeRequest(DocumentFileBean.class);
        }
    }

    @Test(expected = IOException.class)
    public void testNullTypRequest() throws Exception {
        File file = testResources.getResource("test.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("filedata", file, ContentType.DEFAULT_BINARY, file.getName());
            HttpEntity entity = builder.build();
            assertNotNull("HttpEntity should have been build.", entity);
            httpRestRequest.buildRequest(HttpMethod.GET, "/documents", entity);
            httpRestRequest.executeRequest((Class) null);
        }
    }

    @Test(expected = ResultException.class)
    public void testHttpPathNullOutput() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            HttpRestRequest httpRestRequest = HttpRestRequest.createRequest(session);
            assertNotNull("HttpRestRequest should have been build.", httpRestRequest);
            httpRestRequest.setAcceptHeader("application/octet-stream");
            httpRestRequest.buildRequest(HttpMethod.GET, null, null);
            httpRestRequest.executeRequest((OutputStream) null);
        }
    }
}
