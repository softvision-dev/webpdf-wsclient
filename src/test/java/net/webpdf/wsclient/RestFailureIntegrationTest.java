package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.ExtractionTextType;
import net.webpdf.wsclient.schema.operation.ExtractionType;
import net.webpdf.wsclient.schema.operation.SignatureType;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.*;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class RestFailureIntegrationTest {

    private final TestResources testResources = new TestResources(RestFailureIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @Test
    @IntegrationTest
    public void testConverterFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/invalid.gif");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                ConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.CONVERTER);
                webService.setDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
            } catch (ResultException ex) {
                Throwable cause = ex.getCause();
                assertTrue(cause instanceof WebServiceException);
                WebServiceException exception = (WebServiceException) cause;
                assertEquals(-106, exception.getFaultInfo().getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testSignatureFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                SignatureRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.SIGNATURE);
                webService.setDocument(session.getDocumentManager().uploadDocument(file));

                SignatureType.Add add = new SignatureType.Add();
                SignatureType.Add.Appearance appearance = new SignatureType.Add.Appearance();
                appearance.setPage(2000);
                add.setAppearance(appearance);
                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
                webService.getOperation().setAdd(add);

                webService.process();
            } catch (ResultException ex) {
                Throwable cause = ex.getCause();
                assertTrue(cause instanceof WebServiceException);
                WebServiceException exception = (WebServiceException) cause;
                assertEquals(-311, exception.getFaultInfo().getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testPdfaFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/user-owner-password.pdf");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                PdfaRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.PDFA);
                webService.setDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
            } catch (ResultException ex) {
                Throwable cause = ex.getCause();
                assertTrue(cause instanceof WebServiceException);
                WebServiceException exception = (WebServiceException) cause;
                assertEquals(-21, exception.getFaultInfo().getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testToolboxFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/user-owner-password.pdf");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                ToolboxRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.TOOLBOX);
                webService.setDocument(session.getDocumentManager().uploadDocument(file));

                ExtractionType extractionType = new ExtractionType();
                ExtractionTextType textType = new ExtractionTextType();
                textType.setPages("2000");
                extractionType.setText(textType);
                webService.getOperation().add(extractionType);

                webService.process();
            } catch (ResultException ex) {
                Throwable cause = ex.getCause();
                assertTrue(cause instanceof WebServiceException);
                WebServiceException exception = (WebServiceException) cause;
                assertEquals(-5009, exception.getFaultInfo().getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testUrlConverterFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                UrlConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.URLCONVERTER);
                webService.setDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
            } catch (ResultException ex) {
                Throwable cause = ex.getCause();
                assertTrue(cause instanceof WebServiceException);
                WebServiceException exception = (WebServiceException) cause;
                assertEquals(-58, exception.getFaultInfo().getErrorCode());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testOCRFailure() {
        assertDoesNotThrow(() -> {
            File file = testResources.getResource("integration/files/user-owner-password.pdf");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                OcrRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.OCR);
                webService.setDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
            } catch (ResultException ex) {
                Throwable cause = ex.getCause();
                assertTrue(cause instanceof WebServiceException);
                WebServiceException exception = (WebServiceException) cause;
                assertEquals(-5009, exception.getFaultInfo().getErrorCode());
            }
        });
    }

}
