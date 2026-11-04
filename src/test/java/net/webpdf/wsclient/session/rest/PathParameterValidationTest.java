package net.webpdf.wsclient.session.rest;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.openapi.KeyStorePassword;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.stub.LocalWebPdfServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins that caller supplied values appended to a REST request path are rejected, when they would change the
 * addressed resource ({@code .}/{@code ..} segments, or a {@code /} in a single segment value) - before any request
 * is sent.
 */
public class PathParameterValidationTest {

    @SuppressWarnings("deprecation")
    @ParameterizedTest
    @ValueSource(strings = {"..", ".", "../admin", "a/../b", "a/./b", "a/..", "a/b"})
    public void testSingleSegmentParametersAreRejected(String value) throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server)) {
            int requestsBefore = server.getRequests().size();

            assertInvalidUrl(() -> session.getUserManager().updateCertificatePasswords(value, new KeyStorePassword()));
            assertInvalidUrl(() -> session.updateCertificates(value, new KeyStorePassword()));
            assertInvalidUrl(() -> session.getAdministrationManager().closeSession(value));

            assertEquals(requestsBefore, server.getRequests().size(), "No request should have been sent.");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"..", ".", "../admin", "dir/../../admin", "dir/./file.txt", "dir/.."})
    public void testArchivePathWithDotSegmentsIsRejected(String archivePath) throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server)) {
            int requestsBefore = server.getRequests().size();

            assertInvalidUrl(() -> session.getDocumentManager()
                    .extractArchiveFile("1", archivePath, new ByteArrayOutputStream()));

            assertEquals(requestsBefore, server.getRequests().size(), "No request should have been sent.");
        }
    }

    @Test
    public void testArchivePathWithoutDotSegmentsIsAccepted() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start();
             RestWebServiceSession session = createSession(server)) {
            // Names merely containing dots are valid, the call fails later on, because the document is unknown.
            ResultException exception = assertThrows(ResultException.class, () -> session.getDocumentManager()
                    .extractArchiveFile("1", "dir/..file/a..b/.hidden", new ByteArrayOutputStream()));
            assertEquals(Error.INVALID_DOCUMENT.getCode(), exception.getErrorCode(),
                    "The archive path should have passed the validation.");
        }
    }

    private static RestWebServiceSession createSession(LocalWebPdfServer server) throws ResultException {
        return SessionFactory.createInstance(new SessionContext(WebServiceProtocol.REST, server.getUrl()),
                new UserAuthProvider("admin", "secret"));
    }

    private static void assertInvalidUrl(Executable executable) {
        ResultException exception = assertThrows(ResultException.class, executable);
        assertEquals(Error.INVALID_URL.getCode(), exception.getErrorCode(),
                "The path parameter should have been rejected as invalid URL.");
    }

}
