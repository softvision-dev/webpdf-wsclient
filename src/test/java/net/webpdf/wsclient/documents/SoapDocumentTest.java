package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.session.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.testsuite.io.TestResources;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import jakarta.activation.DataHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class SoapDocumentTest {

    private final TestResources testResources = new TestResources(SoapDocumentTest.class);

    @Test
    public void testSoapDocument() {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = testResources.getTempFolder().newFile();

        try (SoapWebServiceDocument soapDocument = new SoapWebServiceDocument(sourceFile.toURI(), targetFile)) {
            assertNotNull(soapDocument.getSourceDataHandler(),
                    "Source data handler should have been provided.");
            DataHandler dataHandler = soapDocument.getSourceDataHandler();
            assertNotNull(dataHandler, "Data handler should have been provided.");
            assertDoesNotThrow(() -> soapDocument.save(dataHandler));
            assertDoesNotThrow(() ->
                    assertTrue(FileUtils.contentEquals(sourceFile, targetFile)));
            assertEquals(sourceFile.toURI(), soapDocument.getSource(),
                    "Source URI should not have changed.");
        }
    }

    @Test
    public void testSoapDocumentNullSource() {
        assertThrows(IOException.class,
                () -> {
                    File targetFile = testResources.getTempFolder().newFile();

                    try (SoapWebServiceDocument soapDocument =
                                 new SoapWebServiceDocument(null, targetFile)) {
                        assertNull(soapDocument.getSourceDataHandler(),
                                "Source data handler should not be providable.");
                        soapDocument.save(soapDocument.getSourceDataHandler());
                    }
                });
    }

    @Test
    public void testSoapDocumentNullTarget() {
        assertThrows(IOException.class,
                () -> {
                    File sourceFile = testResources.getResource("test.pdf");
                    try (SoapWebServiceDocument soapDocument =
                                 new SoapWebServiceDocument(sourceFile.toURI(), null)) {
                        assertNotNull(soapDocument.getSourceDataHandler(),
                                "Source data handler should have been provided.");
                        soapDocument.save(soapDocument.getSourceDataHandler());
                    }
                });
    }

    @Test
    public void testSoapDocumentStreaming() {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = testResources.getTempFolder().newFile();

        assertDoesNotThrow(() -> {
            try (InputStream inputStream = Files.newInputStream(sourceFile.toPath());
                 OutputStream outputStream = Files.newOutputStream(targetFile.toPath());
                 SoapWebServiceDocument soapDocument = new SoapWebServiceDocument(inputStream, outputStream)) {
                assertNotNull(soapDocument.getSourceDataHandler(),
                        "Source data handler should have been provided.");
                DataHandler dataHandler = soapDocument.getSourceDataHandler();
                assertNotNull(dataHandler,
                        "Data handler should have been provided.");
                soapDocument.save(dataHandler);
                assertTrue(FileUtils.contentEquals(sourceFile, targetFile));
                assertNull(soapDocument.getSource(),
                        "Source URI should not differ from NULL for stream source.");
            }
        });
    }

    @Test
    public void testSoapDocumentNullSourceStreaming() {
        assertThrows(IOException.class,
                () -> {
                    File targetFile = testResources.getTempFolder().newFile();

                    try (OutputStream outputStream = Files.newOutputStream(targetFile.toPath());
                         SoapWebServiceDocument soapDocument =
                                 new SoapWebServiceDocument(null, outputStream)) {
                        assertNull(soapDocument.getSourceDataHandler(),
                                "Source data handler should not be providable.");
                        soapDocument.save(soapDocument.getSourceDataHandler());
                    }
                });
    }

    @Test
    public void testSoapDocumentNullTargetStreaming() {
        assertThrows(IOException.class,
                () -> {
                    File sourceFile = testResources.getResource("test.pdf");
                    try (InputStream inputStream = Files.newInputStream(sourceFile.toPath());
                         SoapWebServiceDocument soapDocument =
                                 new SoapWebServiceDocument(inputStream, null)) {
                        assertNotNull(soapDocument.getSourceDataHandler(),
                                "Source data handler should have been provided.");
                        soapDocument.save(soapDocument.getSourceDataHandler());
                    }
                });
    }

    @Test
    public void testHandleTargetDataHandler() {
        assertThrows(IOException.class,
                () -> {
                    File sourceFile = testResources.getResource("test.pdf");
                    File targetFile = testResources.getTempFolder().newFile();

                    try (InputStream inputStream = Files.newInputStream(sourceFile.toPath());
                         OutputStream outputStream = Files.newOutputStream(targetFile.toPath());
                         SoapWebServiceDocument soapDocument =
                                 new SoapWebServiceDocument(inputStream, outputStream)) {
                        assertNotNull(soapDocument.getSourceDataHandler(),
                                "Source data handler should have been provided.");
                        soapDocument.save(null);
                    }
                });
    }

    @Test
    public void testDataSource() {
        File sourceFile = testResources.getResource("test.pdf");
        File targetFile = testResources.getTempFolder().newFile();

        assertDoesNotThrow(() -> {
            try (InputStream inputStream = Files.newInputStream(sourceFile.toPath());
                 SoapWebServiceDocument soapDocument =
                         new SoapWebServiceDocument(sourceFile.toURI(), targetFile)) {
                assertNotNull(soapDocument.getSourceDataHandler(),
                        "Source data handler should have been provided.");
                assertEquals("application/octet-stream",
                        soapDocument.getSourceDataHandler().getDataSource().getContentType(),
                        "Content type should have been octet stream.");
                assertEquals("", soapDocument.getSourceDataHandler().getDataSource().getName(),
                        "Data source name should have been empty.");
                assertEquals(IOUtils.toString(inputStream, StandardCharsets.UTF_8),
                        IOUtils.toString(soapDocument.getSourceDataHandler().getDataSource().getInputStream(),
                                StandardCharsets.UTF_8),
                        "Inputstream of data source and original input should be identical.");
            }
        });
    }

    @Test
    public void testDataSourceGetOutputstream() {
        assertThrows(UnsupportedOperationException.class,
                () -> {
                    File sourceFile = testResources.getResource("test.pdf");
                    File targetFile = testResources.getTempFolder().newFile();

                    try (InputStream inputStream = Files.newInputStream(sourceFile.toPath());
                         OutputStream outputStream = Files.newOutputStream(targetFile.toPath());
                         SoapWebServiceDocument soapDocument = new SoapWebServiceDocument(inputStream, outputStream)) {
                        assertNotNull(soapDocument.getSourceDataHandler(),
                                "Source data handler should have been provided.");
                        soapDocument.getSourceDataHandler().getDataSource().getOutputStream();
                    }
                });
    }

}
