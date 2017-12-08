package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.testsuite.TestResources;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.activation.DataHandler;
import java.io.*;

import static org.junit.Assert.*;

public class SoapDocumentTest {

    private final TestResources testResources = new TestResources(SoapDocumentTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testSoapDocument() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();

        try (SoapDocument soapDocument = new SoapDocument(sourceFile.toURI(), targetFile)) {
            assertNotNull("Source data handler should have been provided.", soapDocument.getSourceDataHandler());
            DataHandler dataHandler = soapDocument.getSourceDataHandler();
            assertNotNull("Data handler should have been provided.", dataHandler);
            soapDocument.save(dataHandler);
            assertTrue(FileUtils.contentEquals(sourceFile, targetFile));
            assertEquals("Source URI should not have changed.", sourceFile.toURI(), soapDocument.getSource());
        }
    }

    @Test(expected = IOException.class)
    public void testSoapDocumentNullSource() throws Exception {
        File targetFile = temporaryFolder.newFile();

        try (SoapDocument soapDocument = new SoapDocument(null, targetFile)) {
            assertNull("Source data handler should not be providable.", soapDocument.getSourceDataHandler());
            soapDocument.save(soapDocument.getSourceDataHandler());
        }
    }

    @Test(expected = IOException.class)
    public void testSoapDocumentNullTarget() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = null;
        try (SoapDocument soapDocument = new SoapDocument(sourceFile.toURI(), targetFile)) {
            assertNotNull("Source data handler should have been provided.", soapDocument.getSourceDataHandler());
            soapDocument.save(soapDocument.getSourceDataHandler());
        }
    }

    @Test
    public void testSoapDocumentStreaming() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();

        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(targetFile);
             SoapDocument soapDocument = new SoapDocument(inputStream, outputStream)) {
            assertNotNull("Source data handler should have been provided.", soapDocument.getSourceDataHandler());
            DataHandler dataHandler = soapDocument.getSourceDataHandler();
            assertNotNull("Data handler should have been provided.", dataHandler);
            soapDocument.save(dataHandler);
            assertTrue(FileUtils.contentEquals(sourceFile, targetFile));
            assertNull("Source URI should not differ from NULL for stream source.", soapDocument.getSource());
        }
    }

    @Test(expected = IOException.class)
    public void testSoapDocumentNullSourceStreaming() throws Exception {
        File targetFile = temporaryFolder.newFile();

        try (OutputStream outputStream = new FileOutputStream(targetFile);
             SoapDocument soapDocument = new SoapDocument(null, outputStream)) {
            assertNull("Source data handler should not be providable.", soapDocument.getSourceDataHandler());
            soapDocument.save(soapDocument.getSourceDataHandler());
        }
    }

    @Test(expected = IOException.class)
    public void testSoapDocumentNullTargetStreaming() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        try (InputStream inputStream = new FileInputStream(sourceFile);
             SoapDocument soapDocument = new SoapDocument(inputStream, null)) {
            assertNotNull("Source data handler should have been provided.", soapDocument.getSourceDataHandler());
            soapDocument.save(soapDocument.getSourceDataHandler());
        }
    }

    @Test(expected = IOException.class)
    public void testHandleTargetDataHandler() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();

        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(targetFile);
             SoapDocument soapDocument = new SoapDocument(inputStream, outputStream)) {
            assertNotNull("Source data handler should have been provided.", soapDocument.getSourceDataHandler());
            soapDocument.save(null);
        }
    }

    @Test
    public void testDataSource() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();

        try (InputStream inputStream = new FileInputStream(sourceFile);
             SoapDocument soapDocument = new SoapDocument(sourceFile.toURI(), targetFile)) {
            assertNotNull("Source data handler should have been provided.", soapDocument.getSourceDataHandler());
            assertEquals("Content type should have been octet stream.", "application/octet-stream", soapDocument.getSourceDataHandler().getDataSource().getContentType());
            assertEquals("Data source name should have been empty.", "", soapDocument.getSourceDataHandler().getDataSource().getName());
            assertEquals("Inputstream of data source and original input should be identical.", IOUtils.toString(inputStream, "utf-8"), IOUtils.toString(soapDocument.getSourceDataHandler().getDataSource().getInputStream(), "utf-8"));
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testDataSourceGetOutputstream() throws Exception {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = temporaryFolder.newFile();

        try (InputStream inputStream = new FileInputStream(sourceFile);
             OutputStream outputStream = new FileOutputStream(targetFile);
             SoapDocument soapDocument = new SoapDocument(inputStream, outputStream)) {
            assertNotNull("Source data handler should have been provided.", soapDocument.getSourceDataHandler());
            soapDocument.getSourceDataHandler().getDataSource().getOutputStream();
        }
    }
}
