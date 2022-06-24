package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.RestWebServiceDocument;
import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.session.proxy.ProxyConfiguration;
import net.webpdf.wsclient.schema.operation.PageType;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.testsuite.ServerProtocol;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.ProxyTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.UrlConverterRestWebService;
import net.webpdf.wsclient.webservice.soap.ConverterWebService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static net.webpdf.wsclient.testsuite.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebserviceProxyTest {

    private final TestResources testResources = new TestResources(WebserviceProxyTest.class);
    private final File keystoreFile = testResources.getResource("integration/files/ks.jks");
    public TestServer testServer = new TestServer();

    @Test
    @ProxyTest
    public void testRESTProxyHTTP() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL,
                        ServerProtocol.HTTP, true)
        )) {
            session.setProxy(new ProxyConfiguration("127.0.0.1", 8888));

            session.login();

            UrlConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.URLCONVERTER);

            webService.setDocument(new RestWebServiceDocument(null));

            File fileOut = testResources.getTempFolder().newFile();

            assertNotNull(webService.getOperation(), "Operation should have been initialized");
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
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    @ProxyTest
    public void testSOAPProxyHTTP() throws Exception {
        try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.SOAP,
                testServer.getServer(ServerType.LOCAL,
                        ServerProtocol.HTTP, true)
        )) {
            session.setProxy(new ProxyConfiguration("localhost", 8888));

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                SoapWebServiceDocument soapDocument = new SoapWebServiceDocument(fileInputStream, fileOutputStream);

                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.CONVERTER);

                webService.setDocument(soapDocument);

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
                webService.getOperation().setPages("1-5");
                webService.getOperation().setEmbedFonts(true);

                webService.getOperation().setPdfa(new PdfaType());
                webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument resultDocument = webService.process()) {
                    assertNotNull(resultDocument);
                    assertTrue(fileOut.exists());
                }
            }
        }
    }


    @Test
    @ProxyTest
    public void testSOAPProxyHTTPS() throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(true);
        tlsContext.setTrustStore(testServer.getDemoKeystoreFile(keystoreFile), "");
        try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.SOAP,
                testServer.getServer(ServerType.LOCAL,
                        ServerProtocol.HTTPS, true),
                tlsContext
        )) {
            session.setProxy(new ProxyConfiguration("localhost", 8888));

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                SoapWebServiceDocument soapDocument = new SoapWebServiceDocument(fileInputStream, fileOutputStream);

                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.CONVERTER);

                webService.setDocument(soapDocument);

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
                webService.getOperation().setPages("1-5");
                webService.getOperation().setEmbedFonts(true);

                webService.getOperation().setPdfa(new PdfaType());
                webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument resultDocument = webService.process()) {
                    assertNotNull(resultDocument);
                    assertTrue(fileOut.exists());
                }
            }
        }
    }

    @Test
    @ProxyTest
    public void testRESTProxyHTTPS() throws Exception {
        TLSContext tlsContext = TLSContext.createDefault();
        tlsContext.setAllowSelfSigned(true);
        tlsContext.setTrustStore(testServer.getDemoKeystoreFile(keystoreFile), "");
        try (RestSession<RestDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL,
                        ServerProtocol.HTTPS, true),
                tlsContext
        )) {
            session.setProxy(new ProxyConfiguration("127.0.0.1", 8888));

            session.login();

            UrlConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.URLCONVERTER);

            webService.setDocument(new RestWebServiceDocument(null));

            File fileOut = testResources.getTempFolder().newFile();

            assertNotNull(webService.getOperation(),
                    "Operation should have been initialized");
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
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

}
