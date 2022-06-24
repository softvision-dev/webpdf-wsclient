package net.webpdf.wsclient.webservicefactory;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class RestWebserviceFactoryTest {

    private final TestResources testResources = new TestResources(RestWebserviceFactoryTest.class);
    public TestServer testServer = new TestServer();

    private <T extends RestWebService<RestDocument, ?>> T getWebService(WebServiceType webServiceType)
            throws IOException, URISyntaxException {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            return WebServiceFactory.createInstance(session, webServiceType);
        }
    }

    private <T_WEBSERVICE extends RestWebService<RestDocument, ?>> T_WEBSERVICE
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
        assertNotNull(webService.getOperation(), "Operation data should have been initialized");
        return webService;
    }

    @Test
    public void testFactoryBarcodeFromStream() throws Exception {
        BarcodeRestWebService<RestDocument> webService =
                getTypedWebservice(testResources.getResource("barcode.json"));
        assertNotNull(webService.getOperation(),
                "Operation should have been initialized");
        assertNotNull(webService.getOperation().getAdd(),
                "Add element should have been created.");
        assertNotNull(webService.getOperation().getAdd().getQrcode(),
                "QR-code element should have been created.");
        assertEquals(1, webService.getOperation().getAdd().getQrcode().size(),
                "Number of added QR-codes is incorrect.");
        assertEquals("webPDFTest", webService.getOperation().getAdd().getQrcode().get(0).getValue(),
                "Value of value attribute is unexpected.");
        assertEquals("1", webService.getOperation().getAdd().getQrcode().get(0).getPages(),
                "Value of pages attribute is unexpected.");
        assertEquals(90, webService.getOperation().getAdd().getQrcode().get(0).getRotation(),
                "Value of rotation attribute is unexpected.");
        assertEquals("utf-8", webService.getOperation().getAdd().getQrcode().get(0).getCharset(),
                "Value of charset attribute is unexpected.");
        assertEquals(QrCodeErrorCorrectionType.M,
                webService.getOperation().getAdd().getQrcode().get(0).getErrorCorrection(),
                "Value of errorCorrection attribute is unexpected.");
        assertEquals(1, webService.getOperation().getAdd().getQrcode().get(0).getMargin(),
                "Value of margin attribute is unexpected.");
    }

    @Test
    public void testFactoryConverterFromStream() throws Exception {
        ConverterRestWebService<RestDocument> webService =
                getTypedWebservice(testResources.getResource("convert.json"));
        assertNotNull(webService.getOperation(), "Operation should have been initialized");
        assertTrue(webService.getOperation().isEmbedFonts(),
                "Value of embedFonts attribute is unexpected.");
        assertEquals("1", webService.getOperation().getPages(),
                "Value of pages attribute is unexpected.");
        assertTrue(webService.getOperation().isSetReduceResolution(),
                "Value of reduceResolution attribute is unexpected.");
        assertEquals(2, webService.getOperation().getMaxRecursion(),
                "Value of maxRecursion attribute is unexpected.");
        assertEquals(3, webService.getOperation().getJpegQuality(),
                "Value of jpegQuality attribute is unexpected.");
        assertEquals("zip", webService.getOperation().getFileExtension(),
                "Value of fileExtension attribute is unexpected.");
        assertEquals(4, webService.getOperation().getDpi(),
                "Value of dpi attribute is unexpected.");
        assertFalse(webService.getOperation().isCompression(),
                "Value of compression attribute is unexpected.");
        assertEquals("testPwd", webService.getOperation().getAccessPassword(),
                "Value of maxRecursion attribute is unexpected.");

        assertNotNull(webService.getOperation().getPdfa(),
                "Pdfa element should have been created.");
        assertNotNull(webService.getOperation().getPdfa().getConvert(),
                "Convert element should have been created.");
        assertEquals(PdfaLevelType.LEVEL_1A, webService.getOperation().getPdfa().getConvert().getLevel(),
                "Value of level attribute is unexpected.");
        assertEquals(PdfaErrorReportType.MESSAGE,
                webService.getOperation().getPdfa().getConvert().getErrorReport(),
                "Value of errorReport attribute is unexpected.");
        assertEquals(1, webService.getOperation().getPdfa().getConvert().getImageQuality(),
                "Value of imageQuality attribute is unexpected.");
        assertEquals(PdfaSuccessReportType.ZIP,
                webService.getOperation().getPdfa().getConvert().getSuccessReport(),
                "Value of successReport attribute is unexpected.");
    }

    @Test
    public void testFactoryOCRFromStream() throws Exception {
        OcrRestWebService<RestDocument> webService =
                getTypedWebservice(testResources.getResource("ocr.json"));
        assertNotNull(webService.getOperation(), "Operation should have been initialized");
        assertFalse(webService.getOperation().isCheckResolution(),
                "Value of checkResolution attribute is unexpected.");
        assertTrue(webService.getOperation().isForceEachPage(),
                "Value of forceEachPage attribute is unexpected.");
        assertEquals(1, webService.getOperation().getImageDpi(),
                "Value of imageDpi attribute is unexpected.");
        assertEquals(OcrLanguageType.FRA, webService.getOperation().getLanguage(),
                "Value of language attribute is unexpected.");
        assertEquals(OcrOutputType.PDF, webService.getOperation().getOutputFormat(),
                "Value of outputFormat attribute is unexpected.");

        assertNotNull(webService.getOperation().getPage(), "Page element should have been created.");
        assertEquals(1, webService.getOperation().getPage().getWidth(),
                "Value of width attribute is unexpected.");
        assertEquals(2, webService.getOperation().getPage().getHeight(),
                "Value of height attribute is unexpected.");
        assertEquals(MetricsType.MM, webService.getOperation().getPage().getMetrics(),
                "Value of metrics attribute is unexpected.");
    }

    @Test
    public void testFactoryPDFAFromStream() throws Exception {
        PdfaRestWebService<RestDocument> webService =
                getTypedWebservice(testResources.getResource("pdfa.json"));
        assertNotNull(webService.getOperation(), "Operation should have been initialized");
        assertNotNull(webService.getOperation().getAnalyze(),
                "Analyze element should have been created.");
        assertEquals(PdfaLevelType.LEVEL_1A, webService.getOperation().getAnalyze().getLevel(),
                "Value of level attribute is unexpected.");
    }

    @Test
    public void testFactorySignatureFromStream() throws Exception {
        SignatureRestWebService<RestDocument> webService =
                getTypedWebservice(testResources.getResource("signature.json"));
        assertNotNull(webService.getOperation(), "Operation should have been initialized");
        assertNotNull(webService.getOperation().getAdd(), "Add element should have been created.");
        assertEquals("testLocation", webService.getOperation().getAdd().getLocation(),
                "Value of location attribute is unexpected.");
        assertTrue(webService.getOperation().getAdd().isAppendSignature(),
                "Value of appendSignature attribute is unexpected.");
        assertEquals(CertificationLevelType.NONE,
                webService.getOperation().getAdd().getCertificationLevel(),
                "Value of certificationLevel attribute is unexpected.");
        assertEquals("testContact", webService.getOperation().getAdd().getContact(),
                "Value of contact attribute is unexpected.");
        assertEquals("testName", webService.getOperation().getAdd().getFieldName(),
                "Value of fieldName attribute is unexpected.");
        assertEquals("testKey", webService.getOperation().getAdd().getKeyName(),
                "Value of keyName attribute is unexpected.");
        assertEquals("testPwd", webService.getOperation().getAdd().getKeyPassword(),
                "Value of keyPassword attribute is unexpected.");
        assertEquals("testReason", webService.getOperation().getAdd().getReason(),
                "Value of reason attribute is unexpected.");

        assertNotNull(webService.getOperation().getAdd().getAppearance(),
                "Appearance element should have been created.");
        assertEquals(1, webService.getOperation().getAdd().getAppearance().getPage(),
                "Value of page attribute is unexpected.");
        assertEquals("testName", webService.getOperation().getAdd().getAppearance().getName(),
                "Value of name attribute is unexpected.");
        assertEquals("testIdentifier",
                webService.getOperation().getAdd().getAppearance().getIdentifier(),
                "Value of identifier attribute is unexpected.");
    }

    @Test
    public void testFactoryToolboxFromStream() throws Exception {
        ToolboxRestWebService<RestDocument> webService =
                getTypedWebservice(testResources.getResource("toolbox.json"));
        BaseToolboxType element1 = webService.getOperation().get(0);
        assertNotNull(element1, "First element should have been created.");
        assertTrue(element1 instanceof DeleteType,
                "First element should have been instance of delete type.");

        assertEquals("1", ((DeleteType) webService.getOperation().get(0)).getPages(),
                "Value of pages attribute is unexpected.");

        BaseToolboxType element2 = webService.getOperation().get(1);
        assertNotNull(element2, "Second element should have been created.");
        assertTrue(element2 instanceof RotateType,
                "Second element should have been instance of rotate type.");

        assertEquals("*", ((RotateType) webService.getOperation().get(1)).getPages(),
                "Value of pages attribute is unexpected.");
        assertEquals(90, ((RotateType) webService.getOperation().get(1)).getDegrees(),
                "Value of degrees attribute is unexpected.");
        assertEquals(PageGroupType.EVEN, ((RotateType) webService.getOperation().get(1)).getPageGroup(),
                "Value of pageGroup attribute is unexpected.");
        assertEquals(PageOrientationType.ANY,
                ((RotateType) webService.getOperation().get(1)).getPageOrientation(),
                "Value of pageOrientation attribute is unexpected.");

        BaseToolboxType element3 = webService.getOperation().get(2);
        assertNotNull(element3,
                "Third element should have been created.");
        assertTrue(element3 instanceof WatermarkType,
                "Third element should have been instance of watermark type.");
        assertEquals("2", ((WatermarkType) webService.getOperation().get(2)).getPages(),
                "Value of pages attribute is unexpected.");
        assertEquals(180, ((WatermarkType) webService.getOperation().get(2)).getAngle(),
                "Value of angle attribute is unexpected.");

        assertNotNull(((WatermarkType) webService.getOperation().get(2)).getText(),
                "Text element should have been created.");
        assertEquals("testText", ((WatermarkType) webService.getOperation().get(2)).getText().getText(),
                "Value of text attribute is unexpected.");
    }

    @Test
    public void testFactoryUrlConverterFromStream() throws Exception {
        UrlConverterRestWebService<RestDocument> webService =
                getTypedWebservice(testResources.getResource("url_convert.json"));
        assertNotNull(webService.getOperation(), "Operation should have been initialized");
        assertEquals("testURL", webService.getOperation().getUrl(),
                "Value of url attribute is unexpected.");

        assertNotNull(webService.getOperation().getPage(), "Page element should have been created.");
        assertEquals(MetricsType.MM, webService.getOperation().getPage().getMetrics(),
                "Value of metrics attribute is unexpected.");
        assertEquals(1, webService.getOperation().getPage().getHeight(),
                "Value of height attribute is unexpected.");
        assertEquals(2, webService.getOperation().getPage().getWidth(),
                "Value of width attribute is unexpected.");
        assertEquals(3, webService.getOperation().getPage().getBottom(),
                "Value of bottom attribute is unexpected.");
        assertEquals(4, webService.getOperation().getPage().getLeft(),
                "Value of left attribute is unexpected.");
        assertEquals(5, webService.getOperation().getPage().getRight(),
                "Value of right attribute is unexpected.");
        assertEquals(6, webService.getOperation().getPage().getTop(),
                "Value of top attribute is unexpected.");

        assertNotNull(webService.getOperation().getBasicAuth(),
                "BasicAuth element should have been created.");
        assertEquals("testPwd", webService.getOperation().getBasicAuth().getPassword(),
                "Value of password attribute is unexpected.");
        assertEquals("testUser", webService.getOperation().getBasicAuth().getUserName(),
                "Value of userName attribute is unexpected.");

        assertNotNull(webService.getOperation().getProxy(),
                "Proxy element should have been created.");
        assertEquals("testUser", webService.getOperation().getProxy().getUserName(),
                "Value of userName attribute is unexpected.");
        assertEquals("testPwd", webService.getOperation().getProxy().getPassword(),
                "Value of password attribute is unexpected.");
        assertEquals("testAddress", webService.getOperation().getProxy().getAddress(),
                "Value of address attribute is unexpected.");
        assertEquals(1, webService.getOperation().getProxy().getPort(),
                "Value of port attribute is unexpected.");
    }

    @Test
    public void testFactoryCreateWebserviceInstance() throws Exception {
        ToolboxRestWebService<RestDocument> toolboxWebService = getWebService(WebServiceType.TOOLBOX);
        assertNotNull(toolboxWebService,
                "The toolbox webservice should have been initialized.");
        assertNotNull(toolboxWebService.getOperation(),
                "The toolbox operation should have been initialized.");

        ConverterRestWebService<RestDocument> converterWebService = getWebService(WebServiceType.CONVERTER);
        assertNotNull(converterWebService, "The converter webservice should have been initialized.");
        assertNotNull(converterWebService.getOperation(),
                "The converter operation should have been initialized.");

        SignatureRestWebService<RestDocument> signatureWebService = getWebService(WebServiceType.SIGNATURE);
        assertNotNull(signatureWebService, "The signature webservice should have been initialized.");
        assertNotNull(signatureWebService.getOperation(),
                "The signature operation should have been initialized.");

        BarcodeRestWebService<RestDocument> barcodeWebService = getWebService(WebServiceType.BARCODE);
        assertNotNull(barcodeWebService, "The barcode webservice should have been initialized.");
        assertNotNull(barcodeWebService.getOperation(),
                "The barcode operation should have been initialized.");

        PdfaRestWebService<RestDocument> pdfaWebService = getWebService(WebServiceType.PDFA);
        assertNotNull(pdfaWebService, "The pdfa webservice should have been initialized.");
        assertNotNull(pdfaWebService.getOperation(),
                "The pdfa operation should have been initialized.");

        UrlConverterRestWebService<RestDocument> urlConverterWebService =
                getWebService(WebServiceType.URLCONVERTER);
        assertNotNull(urlConverterWebService, "The url converter webservice should have been initialized.");
        assertNotNull(urlConverterWebService.getOperation(),
                "The url converter operation should have been initialized.");

        OcrRestWebService<RestDocument> ocrWebService = getWebService(WebServiceType.OCR);
        assertNotNull(ocrWebService, "The ocr webservice should have been initialized.");
        assertNotNull(ocrWebService.getOperation(),
                "The ocr operation should have been initialized.");
    }

    @Test
    public void testNoOperationData() throws Exception {
        try (Session<RestDocument> session = SessionFactory.createInstance(
                WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL))) {
            WebServiceFactory.createInstance(session, (StreamSource) null);
            fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode(),
                    String.format("Error code %s expected.", Error.INVALID_OPERATION_DATA.getCode()));
        }
    }

}

