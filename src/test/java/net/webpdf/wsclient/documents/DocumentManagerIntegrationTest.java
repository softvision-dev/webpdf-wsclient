package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.RestWebServiceDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;

import static net.webpdf.wsclient.testsuite.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerIntegrationTest {

    private final TestResources testResources = new TestResources(DocumentManagerIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @Test
    @IntegrationTest
    public void testHandleDocument() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = testResources.getTempFolder().newFile();
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL));
             OutputStream outputStream = Files.newOutputStream(targetFile.toPath())
        ) {
            assertNotNull(session,
                    "Valid session should have been created.");
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull(document,
                    "Valid document should have been returned.");
            assertNotNull(document.getDocumentId());
            document = session.getDocumentManager().getDocument(document.getDocumentId());
            assertNotNull(document,
                    "Valid document should have been returned.");
            assertNotNull(document.getDocumentId());
            session.getDocumentManager().downloadDocument(document.getDocumentId(), outputStream);
            assertTrue(FileUtils.contentEquals(sourceFile, targetFile),
                    "The content of the uploaded and the downloaded document should have been equal.");
            List<RestDocument> fileList = session.getDocumentManager().getDocuments();
            assertEquals(1, fileList.size(),
                    "file list should contain 1 document.");
            assertNotNull(document.getDocumentId());
            session.getDocumentManager().deleteDocument(document.getDocumentId());
            fileList = session.getDocumentManager().getDocuments();
            assertTrue(fileList.isEmpty(),
                    "file list should be empty.");
        }
    }

    @Test
    @IntegrationTest
    public void testDocumentRename() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            assertNotNull(session,
                    "Valid session should have been created.");
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull(document,
                    "Valid document should have been returned.");
            assertNotNull(document.getDocumentFile(),
                    "Valid document file should have been contained.");
            assertEquals("test",
                    document.getDocumentFile().getFileName(),
                    "Filename should be test");
            assertNotNull(document.getDocumentId());
            document = session.getDocumentManager().renameDocument(document.getDocumentId(), "new");
            assertNotNull(document,
                    "Valid document should have been returned.");
            assertNotNull(document.getDocumentFile(),
                    "Valid document file should have been contained.");
            assertEquals("new", document.getDocumentFile().getFileName(),
                    "Filename should be new");
        }
    }

    @Test
    @IntegrationTest
    public void testDocumentList() throws Exception {
        File sourceFile1 = testResources.getResource("test.pdf");
        File sourceFile2 = testResources.getResource("logo.png");
        File sourceFile3 = testResources.getResource("lorem-ipsum.txt");
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            assertNotNull(session,
                    "Valid session should have been created.");
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile1);
            assertNotNull(document,
                    "Valid document should have been returned.");
            document = session.getDocumentManager().uploadDocument(sourceFile2);
            assertNotNull(document,
                    "Valid document should have been returned.");
            document = session.getDocumentManager().uploadDocument(sourceFile3);
            assertNotNull(document,
                    "Valid document should have been returned.");
            List<RestDocument> fileList = session.getDocumentManager().getDocuments();
            assertEquals(3, fileList.size(),
                    "file list should contain 3 documents.");
        }
    }

    @Test
    @IntegrationTest
    public void testDocumentHistory() throws Exception {
        File sourceFile = testResources.getResource("logo.png");
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            assertNotNull(session,
                    "Valid session should have been created.");
            session.login();

            session.getDocumentManager().setDocumentHistoryActive(true);
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull(document,
                    "Valid document should have been returned.");
            assertNotNull(document.getDocumentFile(),
                    "Valid document file should have been contained.");
            List<HistoryEntry> historyList = session.getDocumentManager().getDocumentHistory(document.getDocumentId());
            assertEquals(1, historyList.size(),
                    "history list should contain 1 element.");
            HistoryEntry historyEntry = historyList.get(historyList.size() - 1);
            assertEquals("", historyEntry.getOperation(),
                    "history operation should be \"\".");
            historyEntry.setOperation("File uploaded");
            assertNotNull(document,
                    "Valid document should have been returned.");
            historyEntry = session.getDocumentManager().updateDocumentHistory(document.getDocumentId(), historyEntry);
            assertNotNull(historyEntry,
                    "History entry should have been updated.");
            assertEquals("File uploaded", historyEntry.getOperation(),
                    "history operation should be changed to \"File uploaded\".");

            ConverterRestWebService<RestDocument> webService =
                    WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
            webService.setDocument(document);
            webService.process();
            historyList = session.getDocumentManager().getDocumentHistory(document.getDocumentId());
            assertEquals(2, historyList.size(), "history list should contain 2 elements.");

            historyEntry = historyList.get(historyList.size() - 1);
            historyEntry.setOperation("File converted");
            historyEntry = session.getDocumentManager().updateDocumentHistory(document.getDocumentId(), historyEntry);
            assertNotNull(historyEntry,
                    "History entry should have been updated.");
            assertEquals("File converted", historyEntry.getOperation(),
                    "history operation should be changed to \"File converted\".");
            assertEquals("application/pdf", document.getDocumentFile().getMimeType(),
                    "Filetype should be application/pdf");

            historyEntry = historyList.get(0);
            historyEntry.setActive(true);
            session.getDocumentManager().updateDocumentHistory(document.getDocumentId(), historyEntry);
            assertEquals("image/png", document.getDocumentFile().getMimeType(),
                    "Filetype should be image/png");
        }
    }

    @Test
    @IntegrationTest
    public void testHandleDocumentByID() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = testResources.getTempFolder().newFile();
        try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL));
             OutputStream outputStream = Files.newOutputStream(targetFile.toPath())
        ) {
            assertNotNull(session, "Valid session should have been created.");
            session.login();
            RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull(document, "Valid document should have been returned.");
            session.getDocumentManager().downloadDocument(getDocumentID(document), outputStream);
            assertTrue(FileUtils.contentEquals(sourceFile, targetFile),
                    "The content of the uploaded and the downloaded document should have been equal.");
        }
    }

    @Test
    @IntegrationTest
    public void downloadToNullStream() {
        assertThrows(ResultException.class,
                () -> {
                    File sourceFile = testResources.getResource("test.pdf");
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL))) {
                        assertNotNull(session,
                                "Valid session should have been created.");
                        session.login();
                        RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                        assertNotNull(document,
                                "Valid document should have been returned.");
                        session.getDocumentManager().downloadDocument(getDocumentID(document), null);
                    }
                });

    }

    @Test
    @IntegrationTest
    public void uploadNullFile() {
        assertThrows(ResultException.class,
                () -> {
                    try (RestWebServiceSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL))) {
                        assertNotNull(session, "Valid session should have been created.");
                        session.login();
                        session.getDocumentManager().uploadDocument(null);
                    }
                });
    }

    @Test
    @IntegrationTest
    public void testRequestNullDocument() {
        assertThrows(ResultException.class,
                () -> {
                    try (RestSession<RestDocument> session = SessionFactory.createInstance(
                            WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL))) {
                        assertNotNull(session, "Valid session should have been created.");
                        session.login();
                        session.getDocumentManager().getDocument(null);
                    }
                });
    }

}
