package net.webpdf.wsclient;

import jakarta.xml.ws.WebServiceException;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.server.ServerProtocol;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.TLSTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
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

import static net.webpdf.wsclient.testsuite.io.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.*;

public class WebserviceTLSIntegrationTest {
    private final TestResources testResources = new TestResources(WebserviceTLSIntegrationTest.class);
    private final File keystoreFile = testResources.getResource("integration/files/ks.jks");
    public TestServer testServer = new TestServer();

    private void testSoapSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {
        TLSContext tlsContext = new TLSContext()
                .setAllowSelfSigned(selfSigned);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(keystoreFile, "");
        }

        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, url,
                tlsContext)) {
            ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            try (SoapDocument soapDocument = new SoapWebServiceDocument(file.toURI(), fileOut)) {
                webService.setSourceDocument(soapDocument);

                try (SoapDocument soapDocument2 = webService.process()) {
                    assertNotNull(soapDocument2);
                    assertTrue(fileOut.exists());
                }
            }
        }
    }

    @Test
    @TLSTest
    public void testSoapSSL() {
        assertDoesNotThrow(() -> testSoapSSL(testServer.getServer(ServerType.PUBLIC,
                        ServerProtocol.HTTPS, false),
                testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    @Test
    @TLSTest
    public void testSoapSSLSelfSigned() {
        assertDoesNotThrow(() -> testSoapSSL(testServer.getServer(ServerType.LOCAL,
                ServerProtocol.HTTPS, false), null, true));
    }

    @Test
    @TLSTest
    public void testSoapSSLWrongKeystore() {
        assertThrows(WebServiceException.class,
                () -> testSoapSSL(testServer.getServer(ServerType.LOCAL,
                                ServerProtocol.HTTPS, false),
                        testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    @Test
    @TLSTest
    public void testSoapSSLCACertsFallback() {
        assertDoesNotThrow(() -> testSoapSSL(testServer.getServer(
                        ServerType.PUBLIC, ServerProtocol.HTTPS,
                        false),
                null, false));
    }

    @Test
    @TLSTest
    public void testSoapSSLProtocolFailure() {
        assertThrows(WebServiceException.class, () ->
                testSoapSSL(testServer.getServer(ServerType.PUBLIC,
                                ServerProtocol.HTTP, false),
                        testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    private void testRestSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {

        TLSContext tlsContext = new TLSContext()
                .setAllowSelfSigned(selfSigned);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(keystoreFile, "");
        }

        try (RestSession<RestDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.REST, url, tlsContext)) {
            ConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @ParameterizedTest
    @TLSTest
    @CsvSource(delimiter = '|', value = {
            "LOCAL|HTTPS|0|false|true",
            "LOCAL|HTTPS|-31|true|false",
            "LOCAL|HTTPS|0|true|true"
    })
    public void testRestSSL(String type, String protocol, int expectedErrorCode, boolean setKeystoreFile,
            boolean selfSigned) {
        assertDoesNotThrow(() -> {
            ServerType serverType = ServerType.valueOf(type);
            ServerProtocol serverProtocol = ServerProtocol.valueOf(protocol);

            try {
                URL url = testServer.getServer(serverType, serverProtocol, false);
                File keystoreFile = setKeystoreFile ? testServer.getDemoKeystoreFile(this.keystoreFile) : null;
                testRestSSL(url, keystoreFile, selfSigned);
                assertEquals(0, expectedErrorCode,
                        String.format("%d had been expected, but the request succeeded.", expectedErrorCode));
            } catch (ClientResultException ex) {
                assertEquals(expectedErrorCode, ex.getErrorCode(),
                        String.format("Found %d but %d has been expected.", ex.getErrorCode(),
                                expectedErrorCode));
            }
        });
    }

}
