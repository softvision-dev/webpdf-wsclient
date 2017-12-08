package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class DocumentManagerIntegrationTest {

    private final TestResources testResources = new TestResources(DocumentManagerIntegrationTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testHandleDocument() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl());
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

    @Test
    public void testHandleDocumentByID() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl());
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
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl());
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
        File targetFile = temporaryFolder.newFile();
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull("Valid document should have been returned.", document);
            session.getDocumentManager().downloadDocument(document, null);
        }
    }

    @Test(expected = ResultException.class)
    public void uploadNullFile() throws Exception {
        File sourceFile = null;
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            session.getDocumentManager().uploadDocument(sourceFile);
        }
    }

    @Test(expected = ResultException.class)
    public void testRequestNullDocument() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {
            assertNotNull("Valid session should have been created.", session);
            session.login();
            session.getDocumentManager().getDocument(null);
        }
    }
}
