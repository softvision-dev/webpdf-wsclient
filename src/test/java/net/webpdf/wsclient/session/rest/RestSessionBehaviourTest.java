package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.exception.ServerResultException;
import net.webpdf.wsclient.openapi.DocumentFile;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.http.HttpMethod;
import net.webpdf.wsclient.session.connection.http.HttpRestRequest;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import net.webpdf.wsclient.stub.LocalWebPdfServer;
import net.webpdf.wsclient.tools.SerializeHelper;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins client side behaviour of a REST session, that does not depend on a webPDF server: lifecycle (failed creation,
 * repeated close), error mapping of server responses and the handling of local document state. A
 * {@link LocalWebPdfServer} stands in for the webPDF server.
 */
public class RestSessionBehaviourTest {

    private static final String LOG_PATH = LocalWebPdfServer.REST + "admin/server/log";

    @Test
    public void testFailedCreationReleasesWithoutLogout() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            server.respond("GET", LocalWebPdfServer.CERTIFICATES_PATH, 500, "text/plain", "failure");

            assertThrows(ResultException.class, () -> createSession(server.getUrl()),
                    "The session creation should have failed.");
            // The constructor can not tell a fresh server session from a resumed one, so it must not log out.
            assertTrue(server.getRequests("GET", LocalWebPdfServer.LOGOUT_PATH).isEmpty(),
                    "A failed session creation must not log out the server session.");
        }
    }

    @Test
    public void testFailedCreationKeepsResumedSession() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            UserAuthProvider original = new UserAuthProvider("admin", "secret");
            try (RestWebServiceSession first = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, server.getUrl()), original)) {
                AuthMaterial token = original.provide(first);
                UserAuthProvider resumed = new UserAuthProvider("admin", "secret", token);

                server.respond("GET", LocalWebPdfServer.CERTIFICATES_PATH, 503, "text/html", "<html>busy</html>");
                assertThrows(ResultException.class, () -> SessionFactory.createInstance(
                                new SessionContext(WebServiceProtocol.REST, server.getUrl()), resumed),
                        "Resuming the session should have failed.");
                assertTrue(server.getRequests("GET", LocalWebPdfServer.LOGOUT_PATH).isEmpty(),
                        "The failed attempt must not log out the resumed server session.");

                server.respond("GET", LocalWebPdfServer.CERTIFICATES_PATH, 200, "application/json", "{}");
                try (RestWebServiceSession retry = SessionFactory.createInstance(
                        new SessionContext(WebServiceProtocol.REST, server.getUrl()), resumed)) {
                    assertEquals("Bearer access-1", resumed.provide(retry).getRawAuthHeader(),
                            "The retry should resume the same server session.");
                    assertEquals(1, server.getLoginCount(), "The retry must not open a new server session.");
                }
            }
        }
    }

    @Test
    public void testFailedLoginDoesNotRepeatLogin() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            server.respond("POST", LocalWebPdfServer.LOGIN_PATH, 401, "text/plain", "unauthorized");

            assertThrows(AuthResultException.class, () -> createSession(server.getUrl()),
                    "The session creation should have failed.");

            assertEquals(1, server.getRequests("POST", LocalWebPdfServer.LOGIN_PATH).size(),
                    "The failed login should not have been repeated.");
            assertTrue(server.getRequests("GET", LocalWebPdfServer.LOGOUT_PATH).isEmpty(),
                    "No logout should have been sent without a server session.");
        }
    }

    @Test
    public void testRepeatedCloseIsIgnored() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            RestWebServiceSession session = createSession(server.getUrl());
            session.close();

            assertDoesNotThrow(session::close, "Closing an already closed session should have no effect.");
            assertEquals(1, server.getRequests("GET", LocalWebPdfServer.LOGOUT_PATH).size(),
                    "The session should have been logged out once.");
        }
    }

    @Test
    public void testCloseOfSessionEndedOnServerSucceeds() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            RestWebServiceSession session = createSession(server.getUrl());
            // webPDF 10.0.7 ends a session for good when an administrator closes it or it expires; a later logout is
            // answered with "Session for provided token not found" (-60).
            server.respond("GET", LocalWebPdfServer.LOGOUT_PATH, 401, "application/json",
                    "{\"errorCode\":-60,\"errorMessage\":\"Session for provided token not found\",\"stackTrace\":\"\"}");

            assertDoesNotThrow(session::close, "Closing a session the server has already ended should succeed.");
        }
    }

    @Test
    public void testCloseReportsOtherLogoutFailures() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            RestWebServiceSession session = createSession(server.getUrl());
            server.respond("GET", LocalWebPdfServer.LOGOUT_PATH, 500, "application/json",
                    "{\"errorCode\":-1,\"errorMessage\":\"Unknown error\",\"stackTrace\":\"\"}");

            ServerResultException exception = assertThrows(ServerResultException.class, session::close,
                    "Other logout failures should still be reported.");
            assertEquals(-1, exception.getErrorCode(), "The server's error code should have been kept.");
        }
    }

    @Test
    public void testJsonErrorWithCharsetParameterIsServerResult() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server.getUrl())) {
            server.respond("GET", LocalWebPdfServer.REST + "failing", 500, "application/json; charset=UTF-8",
                    "{\"errorCode\":-5,\"errorMessage\":\"Ärger\",\"stackTrace\":\"\"}");

            ServerResultException exception = assertThrows(ServerResultException.class,
                    () -> HttpRestRequest.createRequest(session)
                            .buildRequest(HttpMethod.GET, "failing")
                            .executeRequest(Object.class),
                    "A JSON error description should be reported as server result.");
            assertEquals(-5, exception.getErrorCode(), "The server's error code should have been kept.");
            assertEquals("Ärger", exception.getMessage(), "The server's error message should have been kept.");
        }
    }

    @Test
    public void testFetchMetricsWithUserInfoAndQueryInServerUrl() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            server.respond("GET", LocalWebPdfServer.CONTEXT + "metrics", 200, "text/plain", "metric 1");
            String hostAndPort = server.getHost() + ":" + server.getPort();

            try (RestWebServiceSession session = createSession(LocalWebPdfServer.createUrl(
                    "http://user:password@" + hostAndPort + LocalWebPdfServer.CONTEXT))) {
                assertEquals("metric 1", session.getAdministrationManager().fetchMetrics(),
                        "User info in the server URL must not break the metrics request.");
            }
            try (RestWebServiceSession session = createSession(LocalWebPdfServer.createUrl(
                    "http://" + hostAndPort + LocalWebPdfServer.CONTEXT + "?tenant=a"))) {
                assertEquals("metric 1", session.getAdministrationManager().fetchMetrics(),
                        "A query in the server URL must not break the metrics path.");
            }
        }
    }

    @Test
    public void testFetchLogLengthBeyondIntegerRange() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server.getUrl())) {
            server.respond("HEAD", LOG_PATH, exchange -> {
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.getResponseHeaders().set("Content-Length", "1234");
                exchange.sendResponseHeaders(200, -1);
            });
            assertEquals(1234, session.getAdministrationManager().fetchLogLength(null),
                    "The log length should have been read.");

            server.respond("HEAD", LOG_PATH, exchange -> {
                exchange.getResponseHeaders().set("Content-Type", "text/plain");
                exchange.getResponseHeaders().set("Content-Length", "3000000000");
                exchange.sendResponseHeaders(200, -1);
            });
            ClientResultException exception = assertThrows(ClientResultException.class,
                    () -> session.getAdministrationManager().fetchLogLength(null),
                    "A log length beyond the Integer range should be reported as ResultException.");
            assertInstanceOf(NumberFormatException.class, exception.getCause(), "Unexpected cause.");
        }
    }

    @Test
    public void testFailedRenameKeepsLocalName() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server.getUrl())) {
            RestWebServiceDocument document = createDocument(server, session);
            String updatePath = LocalWebPdfServer.REST + "documents/doc-1/update";
            server.respond("POST", updatePath, 500, "text/plain", "failure");

            assertThrows(ResultException.class,
                    () -> session.getDocumentManager().renameDocument("doc-1", "renamed.pdf"),
                    "The rename should have failed.");

            assertEquals("original.pdf", document.getDocumentFile().getFileName(),
                    "A failed rename must not change the local document state.");
            List<LocalWebPdfServer.RecordedRequest> updateRequests = server.getRequests("POST", updatePath);
            assertEquals(1, updateRequests.size(), "One update request should have been sent.");
            assertTrue(updateRequests.get(0).getBody().contains("\"renamed.pdf\""),
                    "The update request should carry the new name.");

            server.respond("POST", updatePath, 200, "application/json",
                    "{\"documentId\":\"doc-1\",\"fileName\":\"renamed.pdf\"}");
            session.getDocumentManager().renameDocument("doc-1", "renamed.pdf");
            assertEquals("renamed.pdf", document.getDocumentFile().getFileName(),
                    "A confirmed rename should update the local document state.");
        }
    }

    @Test
    public void testDownloadToUnwritableFileKeepsCause(@TempDir File tempDir) throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server.getUrl())) {
            RestWebServiceDocument document = createDocument(server, session);

            ClientResultException exception = assertThrows(ClientResultException.class,
                    () -> document.downloadDocument(new File(tempDir, "missing" + File.separator + "out.pdf")),
                    "Writing to a file in a missing folder should fail.");
            assertEquals(Error.REST_EXECUTION, exception.getClientError(), "Unexpected error code.");
            assertInstanceOf(IOException.class, exception.getCause(), "The IOException should have been kept.");
        }
    }

    private static RestWebServiceSession createSession(URL url) throws ResultException {
        return SessionFactory.createInstance(new SessionContext(WebServiceProtocol.REST, url),
                new UserAuthProvider("admin", "secret"));
    }

    private static RestWebServiceDocument createDocument(LocalWebPdfServer server, RestWebServiceSession session)
            throws ResultException {
        // The document ID is read-only in the model, documents are only ever created from server responses.
        String json = "{\"documentId\":\"doc-1\",\"fileName\":\"original.pdf\"}";
        server.respond("GET", LocalWebPdfServer.REST + "documents/doc-1/info", 200, "application/json", json);
        return session.getDocumentManager().synchronizeDocument(
                SerializeHelper.fromJSON(new StreamSource(new StringReader(json)), DocumentFile.class));
    }

}
