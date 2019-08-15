package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class SoapFailureIntegrationTest {
    private final TestResources testResources = new TestResources(SoapFailureIntegrationTest.class);
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testConverterFailure() throws Exception {
        File file = testResources.getResource("integration/files/invalid.gif");
        File fileOut = temporaryFolder.newFile();
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL))) {
            ConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapDocument(file.toURI(), fileOut));
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
        File fileOut = temporaryFolder.newFile();
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL))) {
            SignatureWebService webService = WebServiceFactory.createInstance(session, WebServiceType.SIGNATURE);
            SignatureType.Add add = new SignatureType.Add();
            SignatureType.Add.Appearance appearance = new SignatureType.Add.Appearance();
            appearance.setPage(2000);
            add.setAppearance(appearance);
            webService.getOperation().setAdd(add);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapDocument(file.toURI(), fileOut));
            webService.process();
            assertTrue(fileOut.exists());
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
        File fileOut = temporaryFolder.newFile();
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL))) {
            PdfaWebService webService = WebServiceFactory.createInstance(session, WebServiceType.PDFA);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapDocument(file.toURI(), fileOut));
            webService.process();
            assertTrue(fileOut.exists());
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
        File fileOut = temporaryFolder.newFile();
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL))) {
            ToolboxWebService webService = WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);
            ExtractionType extractionType = new ExtractionType();
            ExtractionTextType textType = new ExtractionTextType();
            textType.setPages("2000");
            extractionType.setText(textType);
            webService.getOperation().add(extractionType);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapDocument(file.toURI(), fileOut));
            webService.process();
            assertTrue(fileOut.exists());
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
        File fileOut = temporaryFolder.newFile();
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL))) {
            UrlConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.URLCONVERTER);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapDocument(file.toURI(), fileOut));
            webService.process();
            assertTrue(fileOut.exists());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-20, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testOCRFailure() throws Exception {
        File file = testResources.getResource("integration/files/user-owner-password.pdf");
        File fileOut = temporaryFolder.newFile();
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
            testServer.getServer(TestServer.ServerType.LOCAL))) {
            OcrWebService webService = WebServiceFactory.createInstance(session, WebServiceType.OCR);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapDocument(file.toURI(), fileOut));
            webService.process();
            assertTrue(fileOut.exists());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebserviceException);
            WebserviceException exception = (WebserviceException) cause;
            assertEquals(-5009, exception.getFaultInfo().getErrorCode());
        }
    }
}
