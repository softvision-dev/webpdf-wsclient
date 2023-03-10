package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.testsuite.io.TestResources;
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

        assertDoesNotThrow(() -> {
            try (SoapWebServiceDocument soapDocument = new SoapWebServiceDocument(sourceFile.toURI())) {
                assertNotNull(soapDocument.getSourceDataHandler(),
                        "Source data handler should have been provided.");
                DataHandler dataHandler = soapDocument.getSourceDataHandler();
                assertNotNull(dataHandler, "Data handler should have been provided.");
                assertNull(soapDocument.getResult());
                assertEquals(sourceFile.toURI(), soapDocument.getSource(),
                        "Source URI should not have changed.");
            }
        });
    }

    @Test
    public void testSoapDocumentStreaming() {
        File sourceFile = testResources.getResource("test.pdf");
        assertDoesNotThrow(() -> {
            try (InputStream inputStream = Files.newInputStream(sourceFile.toPath());
                 SoapWebServiceDocument soapDocument = new SoapWebServiceDocument(inputStream)) {
                assertNotNull(soapDocument.getSourceDataHandler(),
                        "Source data handler should have been provided.");
                DataHandler dataHandler = soapDocument.getSourceDataHandler();
                assertNotNull(dataHandler,
                        "Data handler should have been provided.");
                assertNull(soapDocument.getSource(),
                        "Source URI should not differ from NULL for stream source.");
            }
        });
    }

    @Test
    public void testDataSource() {
        File sourceFile = testResources.getResource("test.pdf");

        assertDoesNotThrow(() -> {
            try (InputStream inputStream = Files.newInputStream(sourceFile.toPath());
                 SoapWebServiceDocument soapDocument =
                         new SoapWebServiceDocument(sourceFile.toURI())) {
                assertNotNull(soapDocument.getSourceDataHandler(),
                        "Source data handler should have been provided.");
                assertEquals("application/octet-stream",
                        soapDocument.getSourceDataHandler().getDataSource().getContentType(),
                        "Content type should have been octet stream.");
                assertEquals("test.pdf", soapDocument.getSourceDataHandler().getDataSource().getName(),
                        "Data source name should have been 'test.pdf'.");
                assertEquals(IOUtils.toString(inputStream, StandardCharsets.UTF_8),
                        IOUtils.toString(soapDocument.getSourceDataHandler().getDataSource().getInputStream(),
                                StandardCharsets.UTF_8),
                        "Input-stream of data source and original input should be identical.");
            }
        });
    }

}
