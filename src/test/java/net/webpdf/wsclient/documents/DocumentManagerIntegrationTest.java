package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.ConverterRestWebService;
import net.webpdf.wsclient.WebServiceFactory;
import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.beans.HistoryEntryBean;
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

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

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
            document = session.getDocumentManager().getDocument(document.getSourceDocumentId());
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

            session.getDocumentManager().setActiveDocumentHistory(true);
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            assertNotNull("Valid document file should have been contained.", document.getDocumentFile());
            List<HistoryEntryBean> historyList = session.getDocumentManager().getDocumentHistory(document.getSourceDocumentId());
            assertEquals("history list should contain 1 element.", 1, historyList.size());
            HistoryEntryBean historybean = historyList.get(historyList.size() - 1);
            assertEquals("history operation should be \"\".", "", historybean.getOperation());
            historybean.setOperation("File uploaded");
            assertNotNull("Valid document should have been returned.", document);
            historybean = session.getDocumentManager().setDocumentHistoryElement(document.getSourceDocumentId(), historybean);
            assertEquals("history operation should be changed to \"File uploaded\".", "File uploaded", historybean.getOperation());

            ConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
            webService.setDocument(document);
            webService.process();
            historyList = session.getDocumentManager().getDocumentHistory(document.getSourceDocumentId());
            assertEquals("history list should contain 2 elements.", 2, historyList.size());

            historybean = historyList.get(historyList.size() - 1);
            historybean.setOperation("File converted");
            historybean = session.getDocumentManager().setDocumentHistoryElement(document.getSourceDocumentId(), historybean);
            assertEquals("history operation should be changed to \"File converted\".", "File converted", historybean.getOperation());
            assertEquals("Filetype should be application/pdf", "application/pdf", document.getDocumentFile().getMimeType());

            historybean = historyList.get(0);
            historybean.setActive(true);
            session.getDocumentManager().setDocumentHistoryElement(document.getSourceDocumentId(), historybean);
            assertEquals("Filetype should be image/png", "image/png", document.getDocumentFile().getMimeType());
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
            session.getDocumentManager().downloadDocument(null, outputStream);
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
            session.getDocumentManager().getDocument((String) null);
        }
    }
}
