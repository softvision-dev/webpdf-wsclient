package net.webpdf.wsclient;

import jakarta.xml.ws.WebServiceException;
import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.ServerProtocol;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
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

import static net.webpdf.wsclient.testsuite.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.*;

public class WebserviceTLSIntegrationTest {
    private final TestResources testResources = new TestResources(WebserviceTLSIntegrationTest.class);
    private final File keystoreFile = testResources.getResource("integration/files/ks.jks");
    public TestServer testServer = new TestServer();

    private void testSoapSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(selfSigned);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(keystoreFile, "");
        }

        try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP, url,
                tlsContext)) {
            ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            try (SoapDocument soapDocument = new SoapWebServiceDocument(file.toURI(), fileOut)) {
                webService.setDocument(soapDocument);

                try (SoapDocument soapDocument2 = webService.process()) {
                    assertNotNull(soapDocument2);
                    assertTrue(fileOut.exists());
                }
            }
        }
    }

    @Test
    public void testSoapSSL() throws Exception {
        testSoapSSL(testServer.getServer(ServerType.PUBLIC,
                        ServerProtocol.HTTPS, false),
                testServer.getDemoKeystoreFile(keystoreFile), false);
    }

    @Test
    public void testSoapSSLSelfSigned() throws Exception {
        testSoapSSL(testServer.getServer(ServerType.LOCAL,
                ServerProtocol.HTTPS, false), null, true);
    }

    @Test
    public void testSoapSSLWrongKeystore() {
        assertThrows(WebServiceException.class,
                () -> testSoapSSL(testServer.getServer(ServerType.LOCAL,
                                ServerProtocol.HTTPS, false),
                        testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    @Test
    public void testSoapSSLCACertsFallback() throws Exception {
        testSoapSSL(testServer.getServer(
                        ServerType.PUBLIC, ServerProtocol.HTTPS,
                        false),
                null, false);
    }

    @Test
    public void testSoapSSLProtocolFailure() {
        assertThrows(WebServiceException.class, () ->
                testSoapSSL(testServer.getServer(ServerType.PUBLIC,
                                ServerProtocol.HTTP, false),
                        testServer.getDemoKeystoreFile(keystoreFile), false));
    }

    private void testRestSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(selfSigned);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(keystoreFile, "");
        }

        try (RestSession<RestDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.REST, url, tlsContext)) {

            session.login();

            ConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            /*"PUBLIC|HTTPS|0|true|false",
            "PUBLIC|HTTP|-34|true|false",
            "PUBLIC|HTTPS|0|false|false",
            "PUBLIC|HTTPS|0|false|true",*/
            "LOCAL|HTTPS|0|false|true",
            "LOCAL|HTTPS|-31|true|false",
            "LOCAL|HTTPS|0|true|true"
    })
    // TODO: The public webPDF Server currently provides the wrong Token type - all calls to the public server will fail.
    //  re-enable those tests, as soon, as possible.
    public void testRestSSL(String type, String protocol, int expectedErrorCode, boolean setKeystoreFile,
            boolean selfSigned) throws Exception {

        ServerType serverType = ServerType.valueOf(type);
        ServerProtocol serverProtocol = ServerProtocol.valueOf(protocol);

        try {
            URL url = testServer.getServer(serverType, serverProtocol, false);
            File keystoreFile = setKeystoreFile ? testServer.getDemoKeystoreFile(this.keystoreFile) : null;
            testRestSSL(url, keystoreFile, selfSigned);
            assertEquals(0, expectedErrorCode,
                    String.format("Found %d but %d has been expected.", 0, expectedErrorCode));
        } catch (ResultException ex) {
            assertEquals(expectedErrorCode, ex.getResult().getCode(),
                    String.format("Found %d but %d has been expected.", ex.getResult().getCode(),
                            expectedErrorCode));
        }
    }

}
