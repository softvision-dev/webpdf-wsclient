package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;

import static net.webpdf.wsclient.testsuite.TestResources.fallbackFailAndClose;
import static org.junit.jupiter.api.Assertions.*;

public class SoapFailureIntegrationTest {
    private final TestResources testResources = new TestResources(SoapFailureIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @Test
    public void testConverterFailure() throws Exception {
        File file = testResources.getResource("integration/files/invalid.gif");
        File fileOut = testResources.getTempFolder().newFile();
        try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.CONVERTER);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
            fallbackFailAndClose(webService.process());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebServiceException);
            WebServiceException exception = (WebServiceException) cause;
            assertEquals(-106, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testSignatureFailure() throws Exception {
        File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
        File fileOut = testResources.getTempFolder().newFile();
        try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            SignatureWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.SIGNATURE);
            SignatureType.Add add = new SignatureType.Add();
            SignatureType.Add.Appearance appearance = new SignatureType.Add.Appearance();
            appearance.setPage(2000);
            add.setAppearance(appearance);
            assertNotNull(webService.getOperation(), "Operation should have been initialized");
            webService.getOperation().setAdd(add);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
            fallbackFailAndClose(webService.process());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebServiceException);
            WebServiceException exception = (WebServiceException) cause;
            assertEquals(-311, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testPdfaFailure() throws Exception {
        File file = testResources.getResource("integration/files/user-owner-password.pdf");
        File fileOut = testResources.getTempFolder().newFile();
        try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            PdfaWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.PDFA);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
            fallbackFailAndClose(webService.process());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebServiceException);
            WebServiceException exception = (WebServiceException) cause;
            assertEquals(-21, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testToolboxFailure() throws Exception {
        File file = testResources.getResource("integration/files/user-owner-password.pdf");
        File fileOut = testResources.getTempFolder().newFile();
        try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            ToolboxWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.TOOLBOX);
            ExtractionType extractionType = new ExtractionType();
            ExtractionTextType textType = new ExtractionTextType();
            textType.setPages("2000");
            extractionType.setText(textType);
            webService.getOperation().add(extractionType);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
            fallbackFailAndClose(webService.process());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebServiceException);
            WebServiceException exception = (WebServiceException) cause;
            assertEquals(-5009, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testUrlConverterFailure() throws Exception {
        File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
        File fileOut = testResources.getTempFolder().newFile();
        try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            UrlConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.URLCONVERTER);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
            fallbackFailAndClose(webService.process());
            assertTrue(fileOut.exists());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebServiceException);
            WebServiceException exception = (WebServiceException) cause;
            assertEquals(-20, exception.getFaultInfo().getErrorCode());
        }
    }

    @Test
    public void testOCRFailure() throws Exception {
        File file = testResources.getResource("integration/files/user-owner-password.pdf");
        File fileOut = testResources.getTempFolder().newFile();
        try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            OcrWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.OCR);
            FileUtils.deleteQuietly(fileOut);
            webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
            fallbackFailAndClose(webService.process());
            assertTrue(fileOut.exists());
        } catch (ResultException ex) {
            Throwable cause = ex.getCause();
            assertTrue(cause instanceof WebServiceException);
            WebServiceException exception = (WebServiceException) cause;
            assertEquals(-5009, exception.getFaultInfo().getErrorCode());
        }
    }

}
