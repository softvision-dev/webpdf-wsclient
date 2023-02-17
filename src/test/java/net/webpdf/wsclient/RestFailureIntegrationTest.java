package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ServerResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.exception.ResultException;
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
                webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
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
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                SignatureRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.SIGNATURE);
                webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

                OperationAddSignature add = new OperationAddSignature();
                OperationAppearanceAdd appearance = new OperationAppearanceAdd();
                appearance.setPage(2000);
                add.setAppearance(appearance);
                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setAdd(add);

                webService.process();
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
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                PdfaRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.PDFA);
                webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
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
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                ToolboxRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.TOOLBOX);
                webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

                OperationBaseToolbox baseToolbox = new OperationBaseToolbox();
                webService.getOperationParameters().add(baseToolbox);
                OperationToolboxExtractionExtraction extractionType = new OperationToolboxExtractionExtraction();
                baseToolbox.setExtraction(extractionType);
                OperationExtractionText textType = new OperationExtractionText();
                textType.setPages("2000");
                extractionType.setText(textType);


                webService.process();
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
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL))) {
                session.login();
                UrlConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.URLCONVERTER);
                webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-58, exception.getErrorCode());
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
                webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

                webService.process();
            } catch (ResultException ex) {
                assertTrue(ex instanceof ServerResultException);
                ServerResultException exception = (ServerResultException) ex;
                assertEquals(-5009, exception.getErrorCode());
            }
        });
    }

}
