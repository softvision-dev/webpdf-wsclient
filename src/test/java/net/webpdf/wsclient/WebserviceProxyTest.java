package net.webpdf.wsclient;

import net.webpdf.wsclient.openapi.OperationPage;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.testsuite.server.ServerProtocol;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
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

import static net.webpdf.wsclient.testsuite.io.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.*;

public class WebserviceProxyTest {

    private final TestResources testResources = new TestResources(WebserviceProxyTest.class);
    private final File keystoreFile = testResources.getResource("integration/files/ks.jks");
    public TestServer testServer = new TestServer();

    @Test
    @ProxyTest
    public void testRESTProxyHTTP() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL,
                            ServerProtocol.HTTP, true)
            )) {
                session.setProxy(new ProxyConfiguration("127.0.0.1", 8888));

                session.login();

                UrlConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.URLCONVERTER);

                webService.setSourceDocument(new RestWebServiceDocument(null));

                File fileOut = testResources.getTempFolder().newFile();

                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                webService.getOperationParameters().setUrl("https://www.webpdf.de");

                OperationPage page = new OperationPage();
                page.setWidth(150);
                page.setHeight(200);
                page.setTop(0);
                page.setLeft(0);
                page.setRight(0);
                page.setBottom(0);
                webService.getOperationParameters().setPage(page);


                RestDocument restDocument = webService.process();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @ProxyTest
    public void testSOAPProxyHTTP() {
        assertDoesNotThrow(() -> {
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

                    webService.setSourceDocument(soapDocument);

                    assertNotNull(webService.getOperationParameters(),
                            "Operation should have been initialized");
                    webService.getOperationParameters().setPages("1-5");
                    webService.getOperationParameters().setEmbedFonts(true);

                    webService.getOperationParameters().setPdfa(new PdfaType());
                    webService.getOperationParameters().getPdfa().setConvert(new PdfaType.Convert());
                    webService.getOperationParameters().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                    webService.getOperationParameters().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                    try (SoapDocument resultDocument = webService.process()) {
                        assertNotNull(resultDocument);
                        assertTrue(fileOut.exists());
                    }
                }
            }
        });
    }


    @Test
    @ProxyTest
    public void testSOAPProxyHTTPS() {
        assertDoesNotThrow(() -> {
            TLSContext tlsContext = new TLSContext()
                    .setAllowSelfSigned(true)
                    .setTrustStore(testServer.getDemoKeystoreFile(keystoreFile), "");
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

                    webService.setSourceDocument(soapDocument);

                    assertNotNull(webService.getOperationParameters(),
                            "Operation should have been initialized");
                    webService.getOperationParameters().setPages("1-5");
                    webService.getOperationParameters().setEmbedFonts(true);

                    webService.getOperationParameters().setPdfa(new PdfaType());
                    webService.getOperationParameters().getPdfa().setConvert(new PdfaType.Convert());
                    webService.getOperationParameters().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                    webService.getOperationParameters().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                    try (SoapDocument resultDocument = webService.process()) {
                        assertNotNull(resultDocument);
                        assertTrue(fileOut.exists());
                    }
                }
            }
        });
    }

    @Test
    @ProxyTest
    public void testRESTProxyHTTPS() {
        assertDoesNotThrow(() -> {
            TLSContext tlsContext = new TLSContext()
                    .setAllowSelfSigned(true)
                    .setTrustStore(testServer.getDemoKeystoreFile(keystoreFile), "");
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

                webService.setSourceDocument(new RestWebServiceDocument(null));

                File fileOut = testResources.getTempFolder().newFile();

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setUrl("https://www.webpdf.de");

                OperationPage page = new OperationPage();
                page.setWidth(150);
                page.setHeight(200);
                page.setTop(0);
                page.setLeft(0);
                page.setRight(0);
                page.setBottom(0);
                webService.getOperationParameters().setPage(page);

                RestDocument restDocument = webService.process();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

}
