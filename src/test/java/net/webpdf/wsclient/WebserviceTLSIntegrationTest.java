package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.ServerContext;
import net.webpdf.wsclient.session.connection.https.TLSProtocol;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.server.TransferProtocol;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.TLSTest;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;
import net.webpdf.wsclient.webservice.soap.ConverterWebService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class WebserviceTLSIntegrationTest {
    private final TestResources testResources = new TestResources(WebserviceTLSIntegrationTest.class);
    private final File keystoreFile = testResources.getResource("integration/files/ks.jks");
    public TestServer testServer = new TestServer();

    private void testSoapSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {
        TLSContext tlsContext = new TLSContext(TLSProtocol.TLSV1_3, selfSigned);
        if (keystoreFile != null) {
            tlsContext = new TLSContext(TLSProtocol.TLSV1_3, selfSigned,
                    keystoreFile, "");
        }

        try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                new ServerContext(WebServiceProtocol.SOAP, url)
                        .setTlsContext(tlsContext))) {
            ConverterWebService<SoapDocument> webService =
                    session.createWSInstance(WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            try (SoapDocument soapDocument = webService.process(
                    new SoapWebServiceDocument(file.toURI()))) {
                assertNotNull(soapDocument);
                soapDocument.writeResult(fileOut);
                assertTrue(fileOut.exists());
            }
        }
    }

    @Test
    @TLSTest
    public void testSoapSSL() {
        assertDoesNotThrow(() -> testSoapSSL(testServer.getServer(ServerType.PUBLIC,
                        TransferProtocol.HTTPS),
                testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    @Test
    @TLSTest
    public void testSoapSSLSelfSigned() {
        assertDoesNotThrow(() -> testSoapSSL(testServer.getServer(ServerType.LOCAL,
                TransferProtocol.HTTPS), null, true));
    }

    @Test
    @TLSTest
    public void testSoapSSLWrongKeystore() {
        assertThrows(ClientResultException.class,
                () -> testSoapSSL(testServer.getServer(ServerType.LOCAL,
                                TransferProtocol.HTTPS),
                        testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    @Test
    @TLSTest
    public void testSoapSSLCACertsFallback() {
        assertDoesNotThrow(() -> testSoapSSL(testServer.getServer(
                        ServerType.PUBLIC, TransferProtocol.HTTPS),
                null, false));
    }

    @Test
    @TLSTest
    public void testSoapSSLProtocolFailure() {
        assertThrows(ClientResultException.class, () ->
                testSoapSSL(testServer.getServer(ServerType.PUBLIC, TransferProtocol.HTTP),
                        testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    private void testRestSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {
        TLSContext tlsContext = new TLSContext(TLSProtocol.TLSV1_3, selfSigned);
        if (keystoreFile != null) {
            tlsContext = new TLSContext(TLSProtocol.TLSV1_3, selfSigned,
                    keystoreFile, "");
        }

        try (RestSession<RestDocument> session = SessionFactory.createInstance(
                new ServerContext(WebServiceProtocol.REST, url).
                        setTlsContext(tlsContext))) {
            ConverterRestWebService<RestDocument> webService =
                    session.createWSInstance(WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                webService.process(session.uploadDocument(file))
                        .downloadDocument(fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @ParameterizedTest
    @TLSTest
    @CsvSource(delimiter = '|', value = {
            "LOCAL|HTTPS|0|false|true",
            "LOCAL|HTTPS|-54|true|false",
            "LOCAL|HTTPS|0|true|true"
    })
    public void testRestSSL(String type, String protocol, int expectedErrorCode, boolean setKeystoreFile,
            boolean selfSigned) {
        assertDoesNotThrow(() -> {
            ServerType serverType = ServerType.valueOf(type);
            TransferProtocol serverProtocol = TransferProtocol.valueOf(protocol);

            try {
                URL url = testServer.getServer(serverType, serverProtocol);
                File keystoreFile = setKeystoreFile ? testServer.getDemoKeystoreFile(this.keystoreFile) : null;
                testRestSSL(url, keystoreFile, selfSigned);
                assertEquals(0, expectedErrorCode,
                        String.format("%d had been expected, but the request succeeded.", expectedErrorCode));
            } catch (ResultException ex) {
                assertEquals(expectedErrorCode, ex.getErrorCode(),
                        String.format("Found %d but %d has been expected.", ex.getErrorCode(),
                                expectedErrorCode));
            }
        });
    }

}
