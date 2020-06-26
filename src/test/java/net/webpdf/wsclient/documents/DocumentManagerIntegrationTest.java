package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.*;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.schema.operation.RotateType;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import static org.junit.Assert.*;


@SuppressWarnings("deprecation")
public class DocumentManagerIntegrationTest {

    private final TestResources testResources = new TestResources(DocumentManagerIntegrationTest.class);
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testHandleDocument() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL));
             OutputStream outputStream = new FileOutputStream(targetFile)
        ) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            document = session.getDocumentManager().findDocument(document.getSourceDocumentId());
            assertNotNull("Valid document should have been returned.", document);
            session.getDocumentManager().downloadDocument(document, outputStream);
            assertTrue("The content of the uploaded and the downloaded document should have been equal.", FileUtils.contentEquals(sourceFile, targetFile));
            List<RestDocument> fileList = session.getDocumentManager().getDocumentList();
            assertEquals("file list should contain 1 document.", 1, fileList.size());
            session.getDocumentManager().deleteDocument(document.getSourceDocumentId());
            fileList = session.getDocumentManager().getDocumentList();
            assertTrue("file list should be empty.", fileList.isEmpty());
        }
    }

    @Test
    public void testDocumentRename() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            assertNotNull("Valid document file should have been contained.", document.getDocumentFile());
            assertEquals("Filename should be test", "test", document.getDocumentFile().getFileName());
            document = session.getDocumentManager().renameDocument(document.getSourceDocumentId(), "new");
            assertNotNull("Valid document should have been returned.", document);
            assertNotNull("Valid document file should have been contained.", document.getDocumentFile());
            assertEquals("Filename should be new", "new", document.getDocumentFile().getFileName());
        }
    }

    @Test
    public void testDocumentList() throws Exception {
        File sourceFile1 = testResources.getResource("test.pdf");
        File sourceFile2 = testResources.getResource("logo.png");
        File sourceFile3 = testResources.getResource("lorem-ipsum.txt");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile1);
            assertNotNull("Valid document should have been returned.", document);
            document = session.getDocumentManager().uploadDocument(sourceFile2);
            assertNotNull("Valid document should have been returned.", document);
            document = session.getDocumentManager().uploadDocument(sourceFile3);
            assertNotNull("Valid document should have been returned.", document);
            List<RestDocument> fileList = session.getDocumentManager().getDocumentList();
            assertEquals("file list should contain 3 documents.", 3, fileList.size());
        }
    }

    @Test
    public void testDocumentListFromSession() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            RestSession resumedSession = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL));
            List<RestDocument> fileList = resumedSession.getDocumentManager().getDocumentList();
            assertTrue("file list should be empty.", fileList.isEmpty());
            // refresh token
            resumedSession.login(session.getToken());
            fileList = resumedSession.getDocumentManager().updateDocumentList();
            assertEquals("file list should contain 1 document.", 1, fileList.size());
        }
    }

    @Test
    public void testDocumentHistory() throws Exception {
        File sourceFile = testResources.getResource("logo.png");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            assertNotNull("Valid session should have been created.", session);
            session.login();

            session.getDocumentManager().setUseHistory(true);
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            assertNotNull("Valid document id should have been returned.", document.getSourceDocumentId());
            assertNotNull("Valid document file should have been contained.", document.getDocumentFile());

            String documentId = document.getSourceDocumentId();

            document = session.getDocumentManager().findDocument(document.getSourceDocumentId());
            assertNotNull("Uploaded document should have been in the list", document);
            assertEquals("image/png", document.getDocumentFile().getMimeType());

            List<HistoryEntry> historyList = document.getHistory();
            assertEquals("history list should contain 1 element.", 1, historyList.size());

            HistoryEntry historyEntry = historyList.get(0);
            assertEquals("", historyEntry.getOperation());
            assertEquals("logo", historyEntry.getFileName());
            assertTrue(historyEntry.isActive());

            RestDocument restDocument = session.getDocumentManager().updateHistoryOperation(documentId, historyEntry.getId(), "File uploaded");
            assertNotNull("Valid document should have been returned.", document);

            document = session.getDocumentManager().findDocument(documentId);
            assertNotNull("Changed document should have been in the list", document);

            historyList = document.getHistory();
            assertEquals("history list should contain 1 element.", 1, historyList.size());
            historyEntry = document.getHistory().get(0);

            assertEquals("File uploaded", historyEntry.getOperation());
            assertEquals("logo", historyEntry.getFileName());
            assertTrue(historyEntry.isActive());

            // execute two web service operations
            ConverterRestWebService converterRestWebService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
            converterRestWebService.setDocument(document);
            converterRestWebService.process();

            assertEquals("application/pdf", document.getDocumentFile().getMimeType());

            historyList = document.getHistory();
            assertEquals("history list should contain 2 elements.", 2, historyList.size());

            PdfaRestWebService pdfaRestWebService = WebServiceFactory.createInstance(session, WebServiceType.PDFA);
            pdfaRestWebService.setDocument(document);
            pdfaRestWebService.getOperation().setConvert(new PdfaType.Convert());
            pdfaRestWebService.process();

            historyList = document.getHistory();
            assertEquals("history list should contain 3 elements.", 3, historyList.size());

            document = session.getDocumentManager().findDocument(documentId);
            assertNotNull("Valid document should have been returned.", document);

            assertFalse(historyList.get(0).isActive());
            assertFalse(historyList.get(1).isActive());

            historyEntry = historyList.get(2);
            assertEquals("PDFA", historyEntry.getOperation());
            assertEquals("logo", historyEntry.getFileName());
            assertTrue(historyEntry.isActive());

            // change active document
            document = session.getDocumentManager().activateHistory(documentId, 2);
            assertNotNull("Valid document should have been returned.", document);

            historyList = document.getHistory();
            assertEquals("history list should contain 3 elements.", 3, historyList.size());
            assertFalse(historyList.get(0).isActive());
            assertTrue(historyList.get(1).isActive());
            assertFalse(historyList.get(2).isActive());

            historyEntry = historyList.get(1);
            assertEquals("CONVERTER", historyEntry.getOperation());
            assertEquals("logo", historyEntry.getFileName());
            assertTrue(historyEntry.isActive());

            // export to JPEG
            ToolboxRestWebService toolboxRestWebService = WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);
            toolboxRestWebService.setDocument(document);
            RotateType rotateType = new RotateType();
            rotateType.setDegrees(90);
            toolboxRestWebService.getOperation().add(rotateType);
            toolboxRestWebService.process();

            document = session.getDocumentManager().findDocument(documentId);
            assertNotNull("Valid document should have been returned.", document);

            assertEquals("application/pdf", document.getDocumentFile().getMimeType());

            historyList = document.getHistory();
            assertEquals("history list should contain 4 elements.", 4, historyList.size());
            assertFalse(historyList.get(0).isActive());
            assertFalse(historyList.get(1).isActive());
            assertFalse(historyList.get(2).isActive());
            assertTrue(historyList.get(3).isActive());

            historyEntry = historyList.get(3);
            assertEquals("TOOLBOX:ROTATE", historyEntry.getOperation());
            assertEquals("logo", historyEntry.getFileName());
            assertTrue(historyEntry.isActive());
        }
    }

    @Test
    public void testHandleDocumentByID() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL));
             OutputStream outputStream = new FileOutputStream(targetFile)
        ) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            session.getDocumentManager().downloadDocument(document, outputStream);
            assertTrue("The content of the uploaded and the downloaded document should have been equal.", FileUtils.contentEquals(sourceFile, targetFile));
        }
    }

    @Test(expected = ResultException.class)
    public void testDownloadNullDocument() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL));
             OutputStream outputStream = new FileOutputStream(targetFile)
        ) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            session.getDocumentManager().downloadDocument((RestDocument) null, outputStream);
        }
    }

    @Test(expected = ResultException.class)
    public void downloadToNullStream() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            session.getDocumentManager().downloadDocument(document, null);
        }
    }

    @Test(expected = ResultException.class)
    public void uploadNullFile() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            session.getDocumentManager().uploadDocument(null);
        }
    }

    @Test(expected = ResultException.class)
    public void testRequestNullDocument() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            session.getDocumentManager().findDocument("");
        }
    }
}
