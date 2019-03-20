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
        testSoapSSL(new URL("https://portal.webpdf.de/webPDF"), testResources.getDemoKeystoreFile(), false);
    }

    @Test
    public void testSoapSSLSelfSigned() throws Exception {
        testSoapSSL(new URL("https://localhost:8443/webPDF"), null, true);
    }

    @Test(expected = WebServiceException.class)
    public void testSoapSSLWrongKeystore() throws Exception {
        testSoapSSL(new URL("https://localhost:8443/webPDF"), testResources.getDemoKeystoreFile(), false);
    }

    @Test
    public void testSoapSSLCACertsFallback() throws Exception {
        testSoapSSL(new URL("https://portal.webpdf.de/webPDF"), null, false);
    }

    @Test(expected = WebServiceException.class)
    public void testSoapSSLProtocolFailure() throws Exception {
        testSoapSSL(new URL("http://portal.webpdf.de/webPDF"), testResources.getDemoKeystoreFile(), false);
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
            "https://portal.webpdf.de/webPDF|0|true|false",
            "http://portal.webpdf.de/webPDF|-31|true|false",
            "https://portal.webpdf.de/webPDF|0|false|false",
            "https://portal.webpdf.de/webPDF|0|false|true",
            "https://localhost:8443/webPDF|0|false|true",
            "https://localhost:8443/webPDF|-31|true|false",
            "https://localhost:8443/webPDF|0|true|true",
    })
    public void testRestSSL(String url, int expectedErrorCode, boolean setKeystoreFile, boolean selfSigned) throws Exception {
        try {
            File keystoreFile = setKeystoreFile ? testResources.getDemoKeystoreFile() : null;
            testRestSSL(new URL(url), keystoreFile, selfSigned);
            Assert.assertEquals(String.format("Found %d but %d has been expected.", 0, expectedErrorCode), 0, expectedErrorCode);
        } catch (ResultException ex) {
            Assert.assertEquals(String.format("Found %d but %d has been expected.", ex.getResult().getCode(), expectedErrorCode), expectedErrorCode, ex.getResult().getCode());
        }
    }
}