package net.webpdf.wsclient.documents;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins that the temp file, that buffers a {@link SoapWebServiceDocument} created from an {@link InputStream}, is
 * deleted when the document is closed - and not only when the JVM exits.
 */
public class SoapDocumentTempFileTest {

    private static final byte[] CONTENT = "temp file content".getBytes(StandardCharsets.UTF_8);

    @Test
    public void testCloseDeletesTempFileOfStreamSource() throws Exception {
        SoapWebServiceDocument document = new SoapWebServiceDocument(new ByteArrayInputStream(CONTENT));
        DataHandler dataHandler = document.getSourceDataHandler();
        assertNotNull(dataHandler, "A stream source should provide a data handler.");
        DataSource dataSource = dataHandler.getDataSource();
        try (InputStream inputStream = dataSource.getInputStream()) {
            assertArrayEquals(CONTENT, inputStream.readAllBytes(), "The temp file should hold the stream content.");
        }

        document.close();

        assertThrows(IOException.class, dataSource::getInputStream,
                "The temp file should have been deleted when the document was closed.");
    }

    @Test
    public void testRepeatedCloseOfStreamSourceIsIgnored() throws Exception {
        SoapWebServiceDocument document = new SoapWebServiceDocument(new ByteArrayInputStream(CONTENT));
        document.close();
        assertDoesNotThrow(document::close, "Closing an already closed document should have no effect.");
    }

}
