package net.webpdf.wsclient.webservicefactory;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class RestWebserviceFactoryTest {

    private final TestResources testResources = new TestResources(RestWebserviceFactoryTest.class);
    public TestServer testServer = new TestServer();

    private <T extends RestWebService<?, ?, RestDocument>> T getWebService(WebServiceType webServiceType)
            throws ResultException {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            return WebServiceFactory.createInstance(session, webServiceType);
        }
    }

    private <T_WEBSERVICE extends RestWebService<?, ?, RestDocument>> T_WEBSERVICE
    getTypedWebservice(File configFile)
            throws Exception {
        T_WEBSERVICE webService;
        String json = FileUtils.readFileToString(configFile, Charset.defaultCharset());

        try (RestSession<RestDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL))) {
            try (StringReader stringReader = new StringReader(json)) {
                StreamSource streamSource = new StreamSource(stringReader);
                webService = WebServiceFactory.createInstance(session, streamSource);
            }
        }

        assertNotNull(webService, "webservice should have been instantiated.");
        assertNotNull(webService.getOperationParameters(),
                "Operation parameters should have been initialized");
        return webService;
    }

    @Test
    public void testFactoryBarcodeFromStream() {
        assertDoesNotThrow(() -> {
            BarcodeRestWebService<RestDocument> webService =
                    getTypedWebservice(testResources.getResource("barcode.json"));
            assertNotNull(webService.getOperationParameters(),
                    "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getAdd(),
                    "Add element should have been created.");
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
            assertEquals(OperationQrBarcode.ErrorCorrectionEnum.M,
                    webService.getOperationParameters().getAdd().getQrcode().get(0).getErrorCorrection(),
                    "Value of errorCorrection attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getAdd().getQrcode().get(0).getMargin(),
                    "Value of margin attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryConverterFromStream() {
        assertDoesNotThrow(() -> {
            ConverterRestWebService<RestDocument> webService =
                    getTypedWebservice(testResources.getResource("convert.json"));
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getEmbedFonts(),
                    "Value of embedFonts should have been initialized.");
            assertTrue(webService.getOperationParameters().getEmbedFonts(),
                    "Value of embedFonts attribute is unexpected.");
            assertEquals("1", webService.getOperationParameters().getPages(),
                    "Value of pages attribute is unexpected.");
            assertNotNull(webService.getOperationParameters().getReduceResolution(),
                    "Value of reduceResolution should have been initialized");
            assertTrue(webService.getOperationParameters().getReduceResolution(),
                    "Value of reduceResolution attribute is unexpected.");
            assertEquals(2, webService.getOperationParameters().getMaxRecursion(),
                    "Value of maxRecursion attribute is unexpected.");
            assertEquals(3, webService.getOperationParameters().getJpegQuality(),
                    "Value of jpegQuality attribute is unexpected.");
            assertEquals("zip", webService.getOperationParameters().getFileExtension(),
                    "Value of fileExtension attribute is unexpected.");
            assertEquals(4, webService.getOperationParameters().getDpi(),
                    "Value of dpi attribute is unexpected.");
            assertNotNull(webService.getOperationParameters().getCompression(),
                    "Value of compression should have been initialized");
            assertFalse(webService.getOperationParameters().getCompression(),
                    "Value of compression attribute is unexpected.");
            assertEquals("testPwd", webService.getOperationParameters().getAccessPassword(),
                    "Value of maxRecursion attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getPdfa(),
                    "Pdfa element should have been created.");
            assertNotNull(webService.getOperationParameters().getPdfa().getConvert(),
                    "Convert element should have been created.");
            assertEquals(OperationConvertPdfa.LevelEnum._1A, webService.getOperationParameters().getPdfa()
                            .getConvert().getLevel(),
                    "Value of level attribute is unexpected.");
            assertEquals(OperationConvertPdfa.ErrorReportEnum.MESSAGE,
                    webService.getOperationParameters().getPdfa().getConvert().getErrorReport(),
                    "Value of errorReport attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getPdfa().getConvert().getImageQuality(),
                    "Value of imageQuality attribute is unexpected.");
            assertEquals(OperationConvertPdfa.SuccessReportEnum.ZIP,
                    webService.getOperationParameters().getPdfa().getConvert().getSuccessReport(),
                    "Value of successReport attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryOCRFromStream() {
        assertDoesNotThrow(() -> {
            OcrRestWebService<RestDocument> webService =
                    getTypedWebservice(testResources.getResource("ocr.json"));
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getCheckResolution(),
                    "Value of checkResolution should have been initialized");
            assertFalse(webService.getOperationParameters().getCheckResolution(),
                    "Value of checkResolution attribute is unexpected.");
            assertNotNull(webService.getOperationParameters().getForceEachPage(),
                    "Value of forceEachPage should have been initialized");
            assertTrue(webService.getOperationParameters().getForceEachPage(),
                    "Value of forceEachPage attribute is unexpected.");
            assertEquals(1, webService.getOperationParameters().getImageDpi(),
                    "Value of imageDpi attribute is unexpected.");
            assertEquals(OperationOcr.LanguageEnum.FRA,
                    webService.getOperationParameters().getLanguage(),
                    "Value of language attribute is unexpected.");
            assertEquals(OperationOcr.OutputFormatEnum.PDF,
                    webService.getOperationParameters().getOutputFormat(),
                    "Value of outputFormat attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getPage(),
                    "Page element should have been created.");
            assertEquals(1, webService.getOperationParameters().getPage().getWidth(),
                    "Value of width attribute is unexpected.");
            assertEquals(2, webService.getOperationParameters().getPage().getHeight(),
                    "Value of height attribute is unexpected.");
            assertEquals(OperationOcrPage.MetricsEnum.MM,
                    webService.getOperationParameters().getPage().getMetrics(),
                    "Value of metrics attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryPDFAFromStream() {
        assertDoesNotThrow(() -> {
            PdfaRestWebService<RestDocument> webService =
                    getTypedWebservice(testResources.getResource("pdfa.json"));
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getAnalyze(),
                    "Analyze element should have been created.");
            assertEquals(OperationAnalyzePdfa.LevelEnum._1A,
                    webService.getOperationParameters().getAnalyze().getLevel(),
                    "Value of level attribute is unexpected.");
        });
    }

    @Test
    public void testFactorySignatureFromStream() {
        assertDoesNotThrow(() -> {
            SignatureRestWebService<RestDocument> webService =
                    getTypedWebservice(testResources.getResource("signature.json"));
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            assertNotNull(webService.getOperationParameters().getAdd(),
                    "Add element should have been created.");
            assertEquals("testLocation", webService.getOperationParameters().getAdd().getLocation(),
                    "Value of location attribute is unexpected.");
            assertNotNull(webService.getOperationParameters().getAdd().getAppendSignature(),
                    "Value of appendSignature should have been initialized");
            assertTrue(webService.getOperationParameters().getAdd().getAppendSignature(),
                    "Value of appendSignature attribute is unexpected.");
            assertEquals(OperationAddSignature.CertificationLevelEnum.NONE,
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
            assertEquals("testName",
                    webService.getOperationParameters().getAdd().getAppearance().getName(),
                    "Value of name attribute is unexpected.");
            assertEquals("testIdentifier",
                    webService.getOperationParameters().getAdd().getAppearance().getIdentifier(),
                    "Value of identifier attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryToolboxFromStream() {
        assertDoesNotThrow(() -> {
            ToolboxRestWebService<RestDocument> webService =
                    getTypedWebservice(testResources.getResource("toolbox.json"));
            OperationBaseToolbox element1 = webService.getOperationParameters().get(0);
            assertNotNull(element1, "First element should have been created.");
            assertNotNull(element1.getDelete(),
                    "First element should have been instance of delete type.");

            assertNotNull(element1.getDelete().getPages(),
                    "Value of pages should have been initialized");
            assertEquals("1", (element1.getDelete()).getPages(),
                    "Value of pages attribute is unexpected.");

            OperationBaseToolbox element2 = webService.getOperationParameters().get(1);
            assertNotNull(element2, "Second element should have been created.");
            assertNotNull(element2.getRotate(),
                    "Second element should have been instance of rotate type.");

            assertNotNull(element2.getRotate().getPages(),
                    "Value of pages should have been initialized");
            assertEquals("*", element2.getRotate().getPages(),
                    "Value of pages attribute is unexpected.");
            assertEquals(90, element2.getRotate().getDegrees(),
                    "Value of degrees attribute is unexpected.");
            assertEquals(OperationToolboxRotateRotate.PageGroupEnum.EVEN,
                    element2.getRotate().getPageGroup(),
                    "Value of pageGroup attribute is unexpected.");
            assertEquals(OperationToolboxRotateRotate.PageOrientationEnum.ANY,
                    element2.getRotate().getPageOrientation(),
                    "Value of pageOrientation attribute is unexpected.");

            OperationBaseToolbox element3 = webService.getOperationParameters().get(2);
            assertNotNull(element3,
                    "Third element should have been created.");
            assertNotNull(element3.getWatermark(),
                    "Third element should have been instance of watermark type.");
            assertEquals("2", element3.getWatermark().getPages(),
                    "Value of pages attribute is unexpected.");
            assertEquals(180, element3.getWatermark().getAngle(),
                    "Value of angle attribute is unexpected.");

            assertNotNull(element3.getWatermark().getText(),
                    "Text element should have been created.");
            assertEquals("testText", element3.getWatermark().getText().getText(),
                    "Value of text attribute is unexpected.");
        });
    }

    @Test
    public void testFactoryUrlConverterFromStream() {
        assertDoesNotThrow(() -> {
            UrlConverterRestWebService<RestDocument> webService =
                    getTypedWebservice(testResources.getResource("url_convert.json"));
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            assertEquals("testURL", webService.getOperationParameters().getUrl(),
                    "Value of url attribute is unexpected.");

            assertNotNull(webService.getOperationParameters().getPage(),
                    "Page element should have been created.");
            assertEquals(OperationUrlConverterPage.MetricsEnum.MM,
                    webService.getOperationParameters().getPage().getMetrics(),
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
            ToolboxRestWebService<RestDocument> toolboxWebService = getWebService(WebServiceType.TOOLBOX);
            assertNotNull(toolboxWebService,
                    "The toolbox webservice should have been initialized.");
            assertNotNull(toolboxWebService.getOperationParameters(),
                    "The toolbox operation should have been initialized.");

            ConverterRestWebService<RestDocument> converterWebService = getWebService(WebServiceType.CONVERTER);
            assertNotNull(converterWebService, "The converter webservice should have been initialized.");
            assertNotNull(converterWebService.getOperationParameters(),
                    "The converter operation should have been initialized.");

            SignatureRestWebService<RestDocument> signatureWebService = getWebService(WebServiceType.SIGNATURE);
            assertNotNull(signatureWebService, "The signature webservice should have been initialized.");
            assertNotNull(signatureWebService.getOperationParameters(),
                    "The signature operation should have been initialized.");

            BarcodeRestWebService<RestDocument> barcodeWebService = getWebService(WebServiceType.BARCODE);
            assertNotNull(barcodeWebService, "The barcode webservice should have been initialized.");
            assertNotNull(barcodeWebService.getOperationParameters(),
                    "The barcode operation should have been initialized.");

            PdfaRestWebService<RestDocument> pdfaWebService = getWebService(WebServiceType.PDFA);
            assertNotNull(pdfaWebService, "The pdfa webservice should have been initialized.");
            assertNotNull(pdfaWebService.getOperationParameters(),
                    "The pdfa operation should have been initialized.");

            UrlConverterRestWebService<RestDocument> urlConverterWebService =
                    getWebService(WebServiceType.URLCONVERTER);
            assertNotNull(urlConverterWebService, "The url converter webservice should have been initialized.");
            assertNotNull(urlConverterWebService.getOperationParameters(),
                    "The url converter operation should have been initialized.");

            OcrRestWebService<RestDocument> ocrWebService = getWebService(WebServiceType.OCR);
            assertNotNull(ocrWebService, "The ocr webservice should have been initialized.");
            assertNotNull(ocrWebService.getOperationParameters(),
                    "The ocr operation should have been initialized.");
        });
    }

    @Test
    public void testNoOperationData() {
        assertDoesNotThrow(() -> {
            try (Session<RestDocument> session = SessionFactory.createInstance(
                    WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL))) {
                WebServiceFactory.createInstance(session, (StreamSource) null);
                fail("ResultException expected");
            } catch (ClientResultException ex) {
                assertEquals(ex.getCode(), Error.INVALID_OPERATION_DATA.getCode(),
                        String.format("Error code %s expected.", Error.INVALID_OPERATION_DATA.getCode()));
            }
        });
    }

}

