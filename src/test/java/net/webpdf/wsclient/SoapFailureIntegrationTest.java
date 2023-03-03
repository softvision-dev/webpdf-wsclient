package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ServerResultException;
import net.webpdf.wsclient.session.auth.AnonymousAuthProvider;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static net.webpdf.wsclient.testsuite.io.TestResources.fallbackFailAndClose;
import static org.junit.jupiter.api.Assertions.*;

public class SoapFailureIntegrationTest {
    private final TestResources testResources = new TestResources(SoapFailureIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @Test
    @IntegrationTest
    public void testConverterFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/invalid.gif");
            File fileOut = testResources.getTempFolder().newFile();
            try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL),
                    new AnonymousAuthProvider())) {
                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.CONVERTER);
                FileUtils.deleteQuietly(fileOut);
                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                fallbackFailAndClose(webService.process());
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-106, exception.getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testSignatureFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();
            try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL),
                    new AnonymousAuthProvider())) {
                SignatureWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.SIGNATURE);
                SignatureType.Add add = new SignatureType.Add();
                SignatureType.Add.Appearance appearance = new SignatureType.Add.Appearance();
                appearance.setPage(2000);
                add.setAppearance(appearance);
                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                webService.getOperationParameters().setAdd(add);
                FileUtils.deleteQuietly(fileOut);
                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                fallbackFailAndClose(webService.process());
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-311, exception.getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testPdfaFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/user-owner-password.pdf");
            File fileOut = testResources.getTempFolder().newFile();
            try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL),
                    new AnonymousAuthProvider())) {
                PdfaWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.PDFA);
                FileUtils.deleteQuietly(fileOut);
                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                fallbackFailAndClose(webService.process());
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-21, exception.getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testToolboxFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/user-owner-password.pdf");
            File fileOut = testResources.getTempFolder().newFile();
            try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL),
                    new AnonymousAuthProvider())) {
                ToolboxWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.TOOLBOX);
                ExtractionType extractionType = new ExtractionType();
                ExtractionTextType textType = new ExtractionTextType();
                textType.setPages("2000");
                extractionType.setText(textType);
                webService.getOperationParameters().add(extractionType);
                FileUtils.deleteQuietly(fileOut);
                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                fallbackFailAndClose(webService.process());
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-5009, exception.getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testUrlConverterFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();
            try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL),
                    new AnonymousAuthProvider())) {
                UrlConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.URLCONVERTER);
                FileUtils.deleteQuietly(fileOut);
                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                fallbackFailAndClose(webService.process());
                assertTrue(fileOut.exists());
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-20, exception.getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testOCRFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/user-owner-password.pdf");
            File fileOut = testResources.getTempFolder().newFile();
            try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL),
                    new AnonymousAuthProvider())) {
                OcrWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.OCR);
                FileUtils.deleteQuietly(fileOut);
                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                fallbackFailAndClose(webService.process());
                assertTrue(fileOut.exists());
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-5009, exception.getErrorCode());
            }
        });
    }

}
