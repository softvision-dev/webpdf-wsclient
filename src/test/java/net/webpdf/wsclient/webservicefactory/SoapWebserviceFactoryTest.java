package net.webpdf.wsclient.webservicefactory;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class SoapWebserviceFactoryTest {

    private final TestResources testResources = new TestResources(SoapWebserviceFactoryTest.class);
    public TestServer testServer = new TestServer();

    private <T extends SoapWebService<?, ?, SoapDocument>> T getWebService(WebServiceType webServiceType)
            throws ResultException {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(ServerType.LOCAL))) {
            return WebServiceFactory.createInstance(session, webServiceType);
        }
    }

    private <T extends SoapWebService<?, ?, SoapDocument>> T getTypedWebservice(File configFile)
            throws Exception {
        T webService;
        String xml = FileUtils.readFileToString(configFile, Charset.defaultCharset());

        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(ServerType.LOCAL))) {
            try (StringReader stringReader = new StringReader(xml)) {
                StreamSource streamSource = new StreamSource(stringReader);
                webService = WebServiceFactory.createInstance(session, streamSource);
            }
        }

        assertNotNull(webService, "webservice should have been instantiated.");
        assertNotNull(webService.getOperationParameters(), "Operation parameters should have been initialized");
        return webService;
    }

    @Test
    public void testFactoryBarcodeFromStream() {
        assertDoesNotThrow(() -> {
            BarcodeWebService<SoapDocument> webService =
                    getTypedWebservice(testResources.getResource("barcode.xml"));

            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getAdd(), "Add element should have been created.");
            assertNotNull(webService.getOperationParameters().getAdd().getQrcode(),
                    "QR-code element should have been created.");
            assertEquals(1, webService.getOperationParameters().getAdd().getQrcode().size(),
                    "Number of added QR-codes is incorrect.");
            assertEquals("webPDFTest", webService.getOperationParameters().getAdd().getQrcode().get(0).getValue(),
                    "Value of value attribute is unexpected.");
            assertEquals("1", webService.getOperationParameters().getAdd().getQrcode().get(0).getPages(),
                    "Value of pages attribute is unexpected.");
            assertEquals(90, webService.getOperationParameters().getAdd().getQrcode().get(0).getRotation(),
                    "Value of rotation attribute is unexpected.");
            assertEquals("utf-8", webService.getOperationParameters().getAdd().getQrcode().get(0).getCharset(),
                    "Value of charset attribute is unexpected.");
            assertEquals(QrCodeErrorCorrectionType.M,
                    webService.getOperationParameters().getAdd().getQrcode().get(0).getErrorCorrection(),
                    "Value of errorCorrection attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getAdd().getQrcode().get(0).getMargin(),
                    "Value of margin attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryConverterFromStream() {
        assertDoesNotThrow(() -> {
            ConverterWebService<SoapDocument> webService =
                    getTypedWebservice(testResources.getResource("convert.xml"));
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            assertTrue(webService.getOperationParameters().isEmbedFonts(),
                    "Value of embedFonts attribute is unexpected.");
            assertEquals("1", webService.getOperationParameters().getPages(),
                    "Value of pages attribute is unexpected.");
            assertTrue(webService.getOperationParameters().isSetReduceResolution(),
                    "Value of reduceResolution attribute is unexpected.");
            assertEquals(2, webService.getOperationParameters().getMaxRecursion(),
                    "Value of maxRecursion attribute is unexpected.");
            assertEquals(3, webService.getOperationParameters().getJpegQuality(),
                    "Value of jpegQuality attribute is unexpected.");
            assertEquals("zip", webService.getOperationParameters().getFileExtension(),
                    "Value of fileExtension attribute is unexpected.");
            assertEquals(4, webService.getOperationParameters().getDpi(),
                    "Value of dpi attribute is unexpected.");
            assertFalse(webService.getOperationParameters().isCompression(),
                    "Value of compression attribute is unexpected.");
            assertEquals("testPwd", webService.getOperationParameters().getAccessPassword(),
                    "Value of maxRecursion attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getPdfa(),
                    "Pdfa element should have been created.");
            assertNotNull(webService.getOperationParameters().getPdfa().getConvert(),
                    "Convert element should have been created.");
            assertEquals("1a", webService.getOperationParameters().getPdfa().getConvert().getLevel().value(),
                    "Value of level attribute is unexpected.");
            assertEquals(PdfaErrorReportType.MESSAGE,
                    webService.getOperationParameters().getPdfa().getConvert().getErrorReport(),
                    "Value of errorReport attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getPdfa().getConvert().getImageQuality(),
                    "Value of imageQuality attribute is unexpected.");
            assertEquals(PdfaSuccessReportType.ZIP,
                    webService.getOperationParameters().getPdfa().getConvert().getSuccessReport(),
                    "Value of successReport attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryOCRFromStream() {
        assertDoesNotThrow(() -> {
            OcrWebService<SoapDocument> webService =
                    getTypedWebservice(testResources.getResource("ocr.xml"));
            assertNotNull(webService.getOperationParameters(),
                    "Operation should have been initialized");
            assertFalse(webService.getOperationParameters().isCheckResolution(),
                    "Value of checkResolution attribute is unexpected.");
            assertTrue(webService.getOperationParameters().isForceEachPage(),
                    "Value of forceEachPage attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getImageDpi(),
                    "Value of imageDpi attribute is unexpected.");
            assertEquals(OcrLanguageType.FRA, webService.getOperationParameters().getLanguage(),
                    "Value of language attribute is unexpected.");
            assertEquals(OcrOutputType.PDF, webService.getOperationParameters().getOutputFormat(),
                    "Value of outputFormat attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getPage(),
                    "Page element should have been created.");
            assertEquals(1, webService.getOperationParameters().getPage().getWidth(),
                    "Value of width attribute is unexpected.");
            assertEquals(2, webService.getOperationParameters().getPage().getHeight(),
                    "Value of height attribute is unexpected.");
            assertEquals(MetricsType.MM, webService.getOperationParameters().getPage().getMetrics(),
                    "Value of metrics attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryPDFAFromStream() {
        assertDoesNotThrow(() -> {
            PdfaWebService<SoapDocument> webService =
                    getTypedWebservice(testResources.getResource("pdfa.xml"));
            assertNotNull(webService.getOperationParameters(),
                    "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getAnalyze(),
                    "Analyze element should have been created.");
            assertEquals("1a", webService.getOperationParameters().getAnalyze().getLevel().value(),
                    "Value of level attribute is unexpected.");
        });
    }

    @Test
    public void testFactorySignatureFromStream() {
        assertDoesNotThrow(() -> {
            SignatureWebService<SoapDocument> webService =
                    getTypedWebservice(testResources.getResource("signature.xml"));
            assertNotNull(webService.getOperationParameters(),
                    "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getAdd(),
                    "Add element should have been created.");
            assertEquals("testLocation", webService.getOperationParameters().getAdd().getLocation(),
                    "Value of location attribute is unexpected.");
            assertTrue(webService.getOperationParameters().getAdd().isAppendSignature(),
                    "Value of appendSignature attribute is unexpected.");
            assertEquals(CertificationLevelType.NONE,
                    webService.getOperationParameters().getAdd().getCertificationLevel(),
                    "Value of certificationLevel attribute is unexpected.");
            assertEquals("testContact", webService.getOperationParameters().getAdd().getContact(),
                    "Value of contact attribute is unexpected.");
            assertEquals("testName", webService.getOperationParameters().getAdd().getFieldName(),
                    "Value of fieldName attribute is unexpected.");
            assertEquals("testKey", webService.getOperationParameters().getAdd().getKeyName(),
                    "Value of keyName attribute is unexpected.");
            assertEquals("testPwd", webService.getOperationParameters().getAdd().getKeyPassword(),
                    "Value of keyPassword attribute is unexpected.");
            assertEquals("testReason", webService.getOperationParameters().getAdd().getReason(),
                    "Value of reason attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getAdd().getAppearance(),
                    "Appearance element should have been created.");
            assertEquals(1, webService.getOperationParameters().getAdd().getAppearance().getPage(),
                    "Value of page attribute is unexpected.");
            assertEquals("testName", webService.getOperationParameters().getAdd().getAppearance().getName(),
                    "Value of name attribute is unexpected.");
            assertEquals("testIdentifier", webService.getOperationParameters().getAdd().getAppearance().getIdentifier(),
                    "Value of identifier attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryToolboxFromStream() {
        assertDoesNotThrow(() -> {
            ToolboxWebService<SoapDocument> webService =
                    getTypedWebservice(testResources.getResource("toolbox.xml"));
            BaseToolboxType element1 = webService.getOperationParameters().get(0);
            assertNotNull(element1,
                    "First element should have been created.");
            assertTrue(element1 instanceof DeleteType,
                    "First element should have been instance of delete type.");

            assertEquals("1", ((DeleteType) webService.getOperationParameters().get(0)).getPages(),
                    "Value of pages attribute is unexpected.");

            BaseToolboxType element2 = webService.getOperationParameters().get(1);
            assertNotNull(element2,
                    "Second element should have been created.");
            assertTrue(element2 instanceof RotateType,
                    "Second element should have been instance of rotate type.");

            assertEquals("*", ((RotateType) webService.getOperationParameters().get(1)).getPages(),
                    "Value of pages attribute is unexpected.");
            assertEquals(90, ((RotateType) webService.getOperationParameters().get(1)).getDegrees(),
                    "Value of degrees attribute is unexpected.");
            assertEquals(PageGroupType.EVEN, ((RotateType) webService.getOperationParameters().get(1)).getPageGroup(),
                    "Value of pageGroup attribute is unexpected.");
            assertEquals(PageOrientationType.ANY,
                    ((RotateType) webService.getOperationParameters().get(1)).getPageOrientation(),
                    "Value of pageOrientation attribute is unexpected.");

            BaseToolboxType element3 = webService.getOperationParameters().get(2);
            assertNotNull(element3,
                    "Third element should have been created.");
            assertTrue(element3 instanceof WatermarkType,
                    "Third element should have been instance of watermark type.");

            assertEquals("2", ((WatermarkType) webService.getOperationParameters().get(2)).getPages(),
                    "Value of pages attribute is unexpected.");
            assertEquals(180, ((WatermarkType) webService.getOperationParameters().get(2)).getAngle(),
                    "Value of angle attribute is unexpected.");

            assertNotNull(((WatermarkType) webService.getOperationParameters().get(2)).getText(),
                    "Text element should have been created.");
            assertEquals("testText", ((WatermarkType) webService.getOperationParameters().get(2)).getText().getText(),
                    "Value of text attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryUrlConverterFromStream() {
        assertDoesNotThrow(() -> {
            UrlConverterWebService<SoapDocument> webService =
                    getTypedWebservice(testResources.getResource("url_convert.xml"));
            assertNotNull(webService.getOperationParameters(),
                    "Operation should have been initialized");
            assertEquals("testURL", webService.getOperationParameters().getUrl(),
                    "Value of url attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getPage(),
                    "Page element should have been created.");
            assertEquals(MetricsType.MM, webService.getOperationParameters().getPage().getMetrics(),
                    "Value of metrics attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getPage().getHeight(),
                    "Value of height attribute is unexpected.");
            assertEquals(2, webService.getOperationParameters().getPage().getWidth(),
                    "Value of width attribute is unexpected.");
            assertEquals(3, webService.getOperationParameters().getPage().getBottom(),
                    "Value of bottom attribute is unexpected.");
            assertEquals(4, webService.getOperationParameters().getPage().getLeft(),
                    "Value of left attribute is unexpected.");
            assertEquals(5, webService.getOperationParameters().getPage().getRight(),
                    "Value of right attribute is unexpected.");
            assertEquals(6, webService.getOperationParameters().getPage().getTop(),
                    "Value of top attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getBasicAuth(),
                    "BasicAuth element should have been created.");
            assertEquals("testPwd", webService.getOperationParameters().getBasicAuth().getPassword(),
                    "Value of password attribute is unexpected.");
            assertEquals("testUser", webService.getOperationParameters().getBasicAuth().getUserName(),
                    "Value of userName attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getProxy(),
                    "Proxy element should have been created.");
            assertEquals("testUser", webService.getOperationParameters().getProxy().getUserName(),
                    "Value of userName attribute is unexpected.");
            assertEquals("testPwd", webService.getOperationParameters().getProxy().getPassword(),
                    "Value of password attribute is unexpected.");
            assertEquals("testAddress", webService.getOperationParameters().getProxy().getAddress(),
                    "Value of address attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getProxy().getPort(),
                    "Value of port attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryCreateWebserviceInstance() {
        assertDoesNotThrow(() -> {
            ToolboxWebService<SoapDocument> toolboxWebService = getWebService(WebServiceType.TOOLBOX);
            assertNotNull(toolboxWebService,
                    "The toolbox webservice should have been initialized.");
            assertNotNull(toolboxWebService.getOperationParameters(),
                    "The toolbox operation should have been initialized.");

            ConverterWebService<SoapDocument> converterWebService = getWebService(WebServiceType.CONVERTER);
            assertNotNull(converterWebService,
                    "The converter webservice should have been initialized.");
            assertNotNull(converterWebService.getOperationParameters(),
                    "The converter operation should have been initialized.");

            SignatureWebService<SoapDocument> signatureWebService = getWebService(WebServiceType.SIGNATURE);
            assertNotNull(signatureWebService,
                    "The signature Web service should have been initialized.");
            assertNotNull(signatureWebService.getOperationParameters(),
                    "The signature operation should have been initialized.");

            BarcodeWebService<SoapDocument> barcodeWebService = getWebService(WebServiceType.BARCODE);
            assertNotNull(barcodeWebService,
                    "The barcode webservice should have been initialized.");
            assertNotNull(barcodeWebService.getOperationParameters(),
                    "The barcode operation should have been initialized.");

            PdfaWebService<SoapDocument> pdfaWebService = getWebService(WebServiceType.PDFA);
            assertNotNull(pdfaWebService,
                    "The pdfa webservice should have been initialized.");
            assertNotNull(pdfaWebService.getOperationParameters(),
                    "The pdfa operation should have been initialized.");

            UrlConverterWebService<SoapDocument> urlConverterWebService =
                    getWebService(WebServiceType.URLCONVERTER);
            assertNotNull(urlConverterWebService,
                    "The url converter webservice should have been initialized.");
            assertNotNull(urlConverterWebService.getOperationParameters(),
                    "The url converter operation should have been initialized.");

            OcrWebService<SoapDocument> ocrWebService = getWebService(WebServiceType.OCR);
            assertNotNull(ocrWebService,
                    "The ocr webservice should have been initialized.");
            assertNotNull(ocrWebService.getOperationParameters(),
                    "The ocr operation should have been initialized.");
        });
    }

    @Test
    public void testNoOperationData() {
        assertDoesNotThrow(() -> {
            try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                WebServiceFactory.createInstance(session, (StreamSource) null);
                fail("ResultException expected");
            } catch (ClientResultException ex) {
                assertEquals(ex.getWsclientError(), Error.INVALID_OPERATION_DATA,
                        String.format("Error code %s expected.", Error.INVALID_OPERATION_DATA.getCode()));
            }
        });
    }

    @Test
    public void testNoSession() {
        try {
            WebServiceFactory.createInstance(null, (StreamSource) null);
            fail("ResultException expected");
        } catch (ClientResultException ex) {
            assertEquals(ex.getWsclientError(), Error.SESSION_CREATE,
                    String.format("Error code %s expected.", Error.SESSION_CREATE.getCode()));
        } catch (ResultException ex) {
            fail("A ClientResultException had been expected.");
        }
    }

}
