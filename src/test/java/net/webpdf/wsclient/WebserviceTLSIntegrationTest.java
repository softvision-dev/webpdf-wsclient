package net.webpdf.wsclient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.webpdf.wsclient.documents.RestDocument;
import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import javax.xml.ws.WebServiceException;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

@RunWith(JUnitParamsRunner.class)
public class WebserviceTLSIntegrationTest {
    private final TestResources testResources = new TestResources(WebserviceTLSIntegrationTest.class);
    private final File keystoreFile = testResources.getResource("integration/files/ks.jks");
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private void testSoapSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(selfSigned);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(keystoreFile, "");
        }

        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, url, tlsContext)) {
            ConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = temporaryFolder.newFile();

            try (SoapDocument soapDocument = new SoapDocument(file.toURI(), fileOut)) {
                webService.setDocument(soapDocument);

                try (SoapDocument soapDocument2 = webService.process()) {
                    Assert.assertNotNull(soapDocument2);
                    Assert.assertTrue(fileOut.exists());
                }
            }
        }
    }

    @Test
    public void testSoapSSL() throws Exception {
        testSoapSSL(testServer.getServer(TestServer.ServerType.PUBLIC,
                TestServer.ServerProtocol.HTTPS, false),
                testServer.getDemoKeystoreFile(keystoreFile), false);
    }

    @Test
    public void testSoapSSLSelfSigned() throws Exception {
        testSoapSSL(testServer.getServer(TestServer.ServerType.LOCAL,
                TestServer.ServerProtocol.HTTPS, false), null, true);
    }

    @Test(expected = WebServiceException.class)
    public void testSoapSSLWrongKeystore() throws Exception {
        testSoapSSL(testServer.getServer(TestServer.ServerType.LOCAL,
                TestServer.ServerProtocol.HTTPS, false),
                testServer.getDemoKeystoreFile(keystoreFile), false);
    }

    @Test
    public void testSoapSSLCACertsFallback() throws Exception {
        testSoapSSL(testServer.getServer(
                TestServer.ServerType.PUBLIC, TestServer.ServerProtocol.HTTPS, false),
                null, false);
    }

    @Test(expected = WebServiceException.class)
    public void testSoapSSLProtocolFailure() throws Exception {
        testSoapSSL(testServer.getServer(TestServer.ServerType.PUBLIC, TestServer.ServerProtocol.HTTP, false),
                testServer.getDemoKeystoreFile(keystoreFile), false);
    }

    private void testRestSSL(URL url, File keystoreFile, boolean selfSigned) throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(selfSigned);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(keystoreFile, "");
        }

        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, url, tlsContext)) {

            session.login();

            ConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    @Parameters({
            "PUBLIC|HTTPS|0|true|false",
            "PUBLIC|HTTP|-31|true|false",
            "PUBLIC|HTTPS|0|false|false",
            "PUBLIC|HTTPS|0|false|true",
            "LOCAL|HTTPS|0|false|true",
            "LOCAL|HTTPS|-31|true|false",
            "LOCAL|HTTPS|0|true|true"
    })
    public void testRestSSL(String type, String protocol, int expectedErrorCode, boolean setKeystoreFile, boolean selfSigned) throws Exception {

        TestServer.ServerType serverType = TestServer.ServerType.valueOf(type);
        TestServer.ServerProtocol serverProtocol = TestServer.ServerProtocol.valueOf(protocol);

        try {
            URL url = testServer.getServer(serverType, serverProtocol, false);
            File keystoreFile = setKeystoreFile ? testServer.getDemoKeystoreFile(this.keystoreFile) : null;
            testRestSSL(url, keystoreFile, selfSigned);
            Assert.assertEquals(String.format("Found %d but %d has been expected.", 0, expectedErrorCode), 0, expectedErrorCode);
        } catch (ResultException ex) {
            Assert.assertEquals(String.format("Found %d but %d has been expected.", ex.getResult().getCode(), expectedErrorCode), expectedErrorCode, ex.getResult().getCode());
        }
    }
}
