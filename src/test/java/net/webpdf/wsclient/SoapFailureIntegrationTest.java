package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.exception.ServerResultException;
import net.webpdf.wsclient.schema.operation.ExtractionTextType;
import net.webpdf.wsclient.schema.operation.ExtractionType;
import net.webpdf.wsclient.schema.operation.SignatureType;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.*;
import org.junit.jupiter.api.Test;

import java.io.File;

import static net.webpdf.wsclient.testsuite.io.TestResources.fallbackFailAndClose;
import static org.junit.jupiter.api.Assertions.*;

public class SoapFailureIntegrationTest {
    private final TestResources testResources = new TestResources(SoapFailureIntegrationTest.class);
    public TestServer testServer = TestServer.getInstance();

    @Test
    @IntegrationTest
    public void testConverterFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/invalid.gif");
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)))) {
                ConverterWebService<SoapDocument> webService =
                        session.createWebServiceInstance(WebServiceType.CONVERTER);
                fallbackFailAndClose(webService.process(
                        session.createDocument(file.toURI())));
            } catch (ResultException ex) {
                assertInstanceOf(ServerResultException.class, ex);
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
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)))) {
                SignatureWebService<SoapDocument> webService =
                        session.createWebServiceInstance(WebServiceType.SIGNATURE);
                SignatureType.Add add = new SignatureType.Add();
                SignatureType.Add.Appearance appearance = new SignatureType.Add.Appearance();
                appearance.setPage(2000);
                add.setAppearance(appearance);
                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                webService.getOperationParameters().setAdd(add);
                fallbackFailAndClose(webService.process(
                        session.createDocument(file.toURI())));
            } catch (ResultException ex) {
                assertInstanceOf(ServerResultException.class, ex);
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
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)))) {
                PdfaWebService<SoapDocument> webService =
                        session.createWebServiceInstance(WebServiceType.PDFA);
                fallbackFailAndClose(webService.process(
                        session.createDocument(file.toURI())));
            } catch (ResultException ex) {
                assertInstanceOf(ServerResultException.class, ex);
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
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)))) {
                ToolboxWebService<SoapDocument> webService =
                        session.createWebServiceInstance(WebServiceType.TOOLBOX);
                ExtractionType extractionType = new ExtractionType();
                ExtractionTextType textType = new ExtractionTextType();
                textType.setPages("2000");
                extractionType.setText(textType);
                webService.getOperationParameters().add(extractionType);
                fallbackFailAndClose(webService.process(
                        session.createDocument(file.toURI())));
            } catch (ResultException ex) {
                assertInstanceOf(ServerResultException.class, ex);
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
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)))) {
                UrlConverterWebService<SoapDocument> webService =
                        session.createWebServiceInstance(WebServiceType.URLCONVERTER);
                fallbackFailAndClose(webService.process(
                        session.createDocument(file.toURI())));
            } catch (ResultException ex) {
                assertInstanceOf(ServerResultException.class, ex);
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
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)))) {
                OcrWebService<SoapDocument> webService =
                        session.createWebServiceInstance(WebServiceType.OCR);
                fallbackFailAndClose(webService.process(
                        session.createDocument(file.toURI())));
            } catch (ResultException ex) {
                assertInstanceOf(ServerResultException.class, ex);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-5009, exception.getErrorCode());
            }
        });
    }

}
