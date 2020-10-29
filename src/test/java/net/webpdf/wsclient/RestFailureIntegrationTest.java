package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.ExtractionTextType;
import net.webpdf.wsclient.schema.operation.ExtractionType;
import net.webpdf.wsclient.schema.operation.SignatureType;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class RestFailureIntegrationTest {
    private final TestResources testResources = new TestResources(RestFailureIntegrationTest.class);
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testConverterFailure() throws Exception {
        File file = testResources.getResource("integration/files/invalid.gif");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            ConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            webService.process();
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-106, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testSignatureFailure() throws Exception {
        File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            SignatureRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.SIGNATURE);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            SignatureType.Add add = new SignatureType.Add();
            SignatureType.Add.Appearance appearance = new SignatureType.Add.Appearance();
            appearance.setPage(2000);
            add.setAppearance(appearance);
            assertNotNull("Operation should have been initialized", webService.getOperation());
            webService.getOperation().setAdd(add);

            webService.process();
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-328, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testPdfaFailure() throws Exception {
        File file = testResources.getResource("integration/files/user-owner-password.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            PdfaRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.PDFA);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            webService.process();
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-21, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testToolboxFailure() throws Exception {
        File file = testResources.getResource("integration/files/user-owner-password.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            ToolboxRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            ExtractionType extractionType = new ExtractionType();
            ExtractionTextType textType = new ExtractionTextType();
            textType.setPages("2000");
            extractionType.setText(textType);
            webService.getOperation().add(extractionType);

            webService.process();
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-5009, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testUrlConverterFailure() throws Exception {
        File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            UrlConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.URLCONVERTER);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            webService.process();
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-58, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testOCRFailure() throws Exception {
        File file = testResources.getResource("integration/files/user-owner-password.pdf");
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST, testServer.getServer(TestServer.ServerType.LOCAL))) {
            session.login();
            OcrRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.OCR);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            webService.process();
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-5009, exception.getFaultInfo().getErrorCode());
        }
    }
}
