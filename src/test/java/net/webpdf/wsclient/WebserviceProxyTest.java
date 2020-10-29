package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.RestDocument;
import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.proxy.ProxyConfiguration;
import net.webpdf.wsclient.schema.operation.PageType;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.SoapSession;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static junit.framework.TestCase.assertNotNull;

public class WebserviceProxyTest {

    private final TestResources testResources = new TestResources(WebserviceProxyTest.class);
    private final File keystoreFile = testResources.getResource("integration/files/ks.jks");
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testRESTProxyHTTP() throws Exception {
        try (RestSession session = SessionFactory.createInstance(
            WebServiceProtocol.REST,
            testServer.getServer(TestServer.ServerType.LOCAL, TestServer.ServerProtocol.HTTP, true)
        )) {
            session.setProxy(new ProxyConfiguration("127.0.0.1", 8888));

            session.login();

            UrlConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.URLCONVERTER);

            webService.setDocument(new RestDocument(null));

            File fileOut = temporaryFolder.newFile();

            assertNotNull("Operation should have been initialized", webService.getOperation());
            webService.getOperation().setUrl("https://www.webpdf.de");

            webService.getOperation().setPage(new PageType());
            webService.getOperation().getPage().setWidth(150);
            webService.getOperation().getPage().setHeight(200);
            webService.getOperation().getPage().setTop(0);
            webService.getOperation().getPage().setLeft(0);
            webService.getOperation().getPage().setRight(0);
            webService.getOperation().getPage().setBottom(0);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testSOAPProxyHTTP() throws Exception {
        try (SoapSession session = SessionFactory.createInstance(
            WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL, TestServer.ServerProtocol.HTTP, true)
        )) {
            session.setProxy(new ProxyConfiguration("localhost", 8888));

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = temporaryFolder.newFile();

            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                SoapDocument soapDocument = new SoapDocument(fileInputStream, fileOutputStream);

                ConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

                webService.setDocument(soapDocument);

                Assert.assertNotNull("Operation should have been initialized", webService.getOperation());
                webService.getOperation().setPages("1-5");
                webService.getOperation().setEmbedFonts(true);

                webService.getOperation().setPdfa(new PdfaType());
                webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument resultDocument = webService.process()) {
                    Assert.assertNotNull(resultDocument);
                    Assert.assertTrue(fileOut.exists());
                }
            }
        }
    }


    @Test
    public void testSOAPProxyHTTPS() throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(true);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(testServer.getDemoKeystoreFile(keystoreFile), "");
        }
        try (SoapSession session = SessionFactory.createInstance(
            WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL, TestServer.ServerProtocol.HTTPS, true),
            tlsContext
        )) {
            session.setProxy(new ProxyConfiguration("localhost", 8888));

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = temporaryFolder.newFile();

            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                SoapDocument soapDocument = new SoapDocument(fileInputStream, fileOutputStream);

                ConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

                webService.setDocument(soapDocument);

                Assert.assertNotNull("Operation should have been initialized", webService.getOperation());
                webService.getOperation().setPages("1-5");
                webService.getOperation().setEmbedFonts(true);

                webService.getOperation().setPdfa(new PdfaType());
                webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument resultDocument = webService.process()) {
                    Assert.assertNotNull(resultDocument);
                    Assert.assertTrue(fileOut.exists());
                }
            }
        }
    }

    @Test
    public void testRESTProxyHTTPS() throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(true);
        if (keystoreFile != null) {
            tlsContext.setTrustStore(testServer.getDemoKeystoreFile(keystoreFile), "");
        }
        try (RestSession session = SessionFactory.createInstance(
            WebServiceProtocol.REST,
            testServer.getServer(TestServer.ServerType.LOCAL, TestServer.ServerProtocol.HTTPS, true),
            tlsContext
        )) {
            session.setProxy(new ProxyConfiguration("127.0.0.1", 8888));

            session.login();

            UrlConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.URLCONVERTER);

            webService.setDocument(new RestDocument(null));

            File fileOut = temporaryFolder.newFile();

            assertNotNull("Operation should have been initialized", webService.getOperation());
            webService.getOperation().setUrl("https://www.webpdf.de");

            webService.getOperation().setPage(new PageType());
            webService.getOperation().getPage().setWidth(150);
            webService.getOperation().getPage().setHeight(200);
            webService.getOperation().getPage().setTop(0);
            webService.getOperation().getPage().setLeft(0);
            webService.getOperation().getPage().setRight(0);
            webService.getOperation().getPage().setBottom(0);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }
}
