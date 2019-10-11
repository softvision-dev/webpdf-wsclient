package net.webpdf.wsclient.webservicefactory;

import net.webpdf.wsclient.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import static org.junit.Assert.*;

public class SoapWebserviceFactoryTest {

    private final TestResources testResources = new TestResources(SoapWebserviceFactoryTest.class);
    @Rule
    public TestServer testServer = new TestServer();

    private <T extends WebService> T getWebService(WebServiceType webServiceType) throws IOException, URISyntaxException {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            return WebServiceFactory.createInstance(session, webServiceType);
        }
    }

    private <T extends WebService> T getTypedWebservice(Class<T> expectedType, File configFile) throws Exception {
        T webService;
        String xml = FileUtils.readFileToString(configFile, Charset.defaultCharset());

        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            try (StringReader stringReader = new StringReader(xml)) {
                StreamSource streamSource = new StreamSource(stringReader);
                webService = WebServiceFactory.createInstance(session, streamSource);
            }
        }

        assertNotNull("webservice should have been instantiated.",
                webService);
        assertTrue(String.format("webservice should have been an instance of the %s webservice.",
                expectedType.getName()),
                expectedType.isInstance(webService));
        assertNotNull("Operation data should have been initialized",
                webService.getOperation());
        return webService;
    }

    @Test
    public void testFactoryBarcodeFromStream() throws Exception {
        BarcodeWebService webService = getTypedWebservice(
                BarcodeWebService.class,
                testResources.getResource("barcode.xml")
        );

        assertNotNull("Operation should have been initialized", webService.getOperation());
        assertNotNull("Add element should have been created.",
                webService.getOperation().getAdd());
        assertNotNull("QR-code element should have been created.",
                webService.getOperation().getAdd().getQrcode());
        assertEquals("Number of added QR-codes is incorrect.", 1,
                webService.getOperation().getAdd().getQrcode().size());
        assertEquals("Value of value attribute is unexpected.",
                "webPDFTest", webService.getOperation().getAdd().getQrcode().get(0).getValue());
        assertEquals("Value of pages attribute is unexpected.",
                "1", webService.getOperation().getAdd().getQrcode().get(0).getPages());
        assertEquals("Value of rotation attribute is unexpected.",
                90, webService.getOperation().getAdd().getQrcode().get(0).getRotation());
        assertEquals("Value of charset attribute is unexpected.",
                "utf-8", webService.getOperation().getAdd().getQrcode().get(0).getCharset());
        assertEquals("Value of errorCorrection attribute is unexpected.",
                QrCodeErrorCorrectionType.M, webService.getOperation().getAdd().getQrcode().get(0).getErrorCorrection());
        assertEquals("Value of margin attribute is unexpected.",
                1, webService.getOperation().getAdd().getQrcode().get(0).getMargin());
    }

    @Test
    public void testFactoryConverterFromStream() throws Exception {
        ConverterWebService webService = getTypedWebservice(
                ConverterWebService.class,
                testResources.getResource("convert.xml")
        );
        assertNotNull("Operation should have been initialized", webService.getOperation());
        assertTrue("Value of embedFonts attribute is unexpected.",
                webService.getOperation().isEmbedFonts());
        assertEquals("Value of pages attribute is unexpected.",
                "1", webService.getOperation().getPages());
        assertTrue("Value of reduceResolution attribute is unexpected.",
                webService.getOperation().isSetReduceResolution());
        assertEquals("Value of maxRecursion attribute is unexpected.",
                2, webService.getOperation().getMaxRecursion());
        assertEquals("Value of jpegQuality attribute is unexpected.",
                3, webService.getOperation().getJpegQuality());
        assertEquals("Value of fileExtension attribute is unexpected.",
                "zip", webService.getOperation().getFileExtension());
        assertEquals("Value of dpi attribute is unexpected.",
                4, webService.getOperation().getDpi());
        assertFalse("Value of compression attribute is unexpected.",
                webService.getOperation().isCompression());
        assertEquals("Value of maxRecursion attribute is unexpected.",
                "testPwd", webService.getOperation().getAccessPassword());

        assertNotNull("Pdfa element should have been created.",
                webService.getOperation().getPdfa());
        assertNotNull("Convert element should have been created.",
                webService.getOperation().getPdfa().getConvert());
        assertEquals("Value of level attribute is unexpected.",
                "1a", webService.getOperation().getPdfa().getConvert().getLevel().value());
        assertEquals("Value of errorReport attribute is unexpected.",
                PdfaErrorReportType.MESSAGE, webService.getOperation().getPdfa().getConvert().getErrorReport());
        assertEquals("Value of imageQuality attribute is unexpected.",
                1, webService.getOperation().getPdfa().getConvert().getImageQuality());
        assertEquals("Value of successReport attribute is unexpected.",
                PdfaSuccessReportType.ZIP, webService.getOperation().getPdfa().getConvert().getSuccessReport());
    }

    @Test
    public void testFactoryOCRFromStream() throws Exception {
        OcrWebService webService = getTypedWebservice(
                OcrWebService.class,
                testResources.getResource("ocr.xml")
        );
        assertNotNull("Operation should have been initialized", webService.getOperation());
        assertFalse("Value of checkResolution attribute is unexpected.",
                webService.getOperation().isCheckResolution());
        assertTrue("Value of forceEachPage attribute is unexpected.",
                webService.getOperation().isForceEachPage());
        assertEquals("Value of imageDpi attribute is unexpected.",
                1, webService.getOperation().getImageDpi());
        assertEquals("Value of language attribute is unexpected.",
                OcrLanguageType.FRA, webService.getOperation().getLanguage());
        assertEquals("Value of outputFormat attribute is unexpected.",
                OcrOutputType.PDF, webService.getOperation().getOutputFormat());

        assertNotNull("Page element should have been created.",
                webService.getOperation().getPage());
        assertEquals("Value of width attribute is unexpected.",
                1, webService.getOperation().getPage().getWidth());
        assertEquals("Value of height attribute is unexpected.",
                2, webService.getOperation().getPage().getHeight());
        assertEquals("Value of metrics attribute is unexpected.",
                MetricsType.MM, webService.getOperation().getPage().getMetrics());
    }

    @Test
    public void testFactoryPDFAFromStream() throws Exception {
        PdfaWebService webService = getTypedWebservice(
                PdfaWebService.class,
                testResources.getResource("pdfa.xml")
        );
        assertNotNull("Operation should have been initialized", webService.getOperation());
        assertNotNull("Analyze element should have been created.",
                webService.getOperation().getAnalyze());
        assertEquals("Value of level attribute is unexpected.",
                "1a", webService.getOperation().getAnalyze().getLevel().value());
    }

    @Test
    public void testFactorySignatureFromStream() throws Exception {
        SignatureWebService webService = getTypedWebservice(
                SignatureWebService.class,
                testResources.getResource("signature.xml")
        );
        assertNotNull("Operation should have been initialized", webService.getOperation());
        assertNotNull("Add element should have been created.",
                webService.getOperation().getAdd());
        assertEquals("Value of location attribute is unexpected.",
                "testLocation", webService.getOperation().getAdd().getLocation());
        assertTrue("Value of appendSignature attribute is unexpected.",
                webService.getOperation().getAdd().isAppendSignature());
        assertEquals("Value of certificationLevel attribute is unexpected.",
                CertificationLevelType.NONE, webService.getOperation().getAdd().getCertificationLevel());
        assertEquals("Value of contact attribute is unexpected.",
                "testContact", webService.getOperation().getAdd().getContact());
        assertEquals("Value of fieldName attribute is unexpected.",
                "testName", webService.getOperation().getAdd().getFieldName());
        assertEquals("Value of keyName attribute is unexpected.",
                "testKey", webService.getOperation().getAdd().getKeyName());
        assertEquals("Value of keyPassword attribute is unexpected.",
                "testPwd", webService.getOperation().getAdd().getKeyPassword());
        assertEquals("Value of reason attribute is unexpected.",
                "testReason", webService.getOperation().getAdd().getReason());

        assertNotNull("Appearance element should have been created.",
                webService.getOperation().getAdd().getAppearance());
        assertEquals("Value of page attribute is unexpected.",
                1, webService.getOperation().getAdd().getAppearance().getPage());
        assertEquals("Value of name attribute is unexpected.",
                "testName", webService.getOperation().getAdd().getAppearance().getName());
        assertEquals("Value of identifier attribute is unexpected.",
                "testIdentifier", webService.getOperation().getAdd().getAppearance().getIdentifier());
    }

    @Test
    public void testFactoryToolboxFromStream() throws Exception {
        ToolboxWebService webService = getTypedWebservice(
                ToolboxWebService.class,
                testResources.getResource("toolbox.xml")
        );
        BaseToolboxType element1 = webService.getOperation().get(0);
        assertNotNull("First element should have been created.",
                element1);
        assertTrue("First element should have been instance of delete type.",
                element1 instanceof DeleteType);

        assertEquals("Value of pages attribute is unexpected.",
                "1", ((DeleteType) webService.getOperation().get(0)).getPages());

        BaseToolboxType element2 = webService.getOperation().get(1);
        assertNotNull("Second element should have been created.",
                element2);
        assertTrue("Second element should have been instance of rotate type.",
                element2 instanceof RotateType);

        assertEquals("Value of pages attribute is unexpected.",
                "*", ((RotateType) webService.getOperation().get(1)).getPages());
        assertEquals("Value of degrees attribute is unexpected.",
                90, ((RotateType) webService.getOperation().get(1)).getDegrees());
        assertEquals("Value of pageGroup attribute is unexpected.",
                PageGroupType.EVEN, ((RotateType) webService.getOperation().get(1)).getPageGroup());
        assertEquals("Value of pageOrientation attribute is unexpected.",
                PageOrientationType.ANY, ((RotateType) webService.getOperation().get(1)).getPageOrientation());

        BaseToolboxType element3 = webService.getOperation().get(2);
        assertNotNull("Third element should have been created.",
                element3);
        assertTrue("Third element should have been instance of watermark type.",
                element3 instanceof WatermarkType);

        assertEquals("Value of pages attribute is unexpected.",
                "2", ((WatermarkType) webService.getOperation().get(2)).getPages());
        assertEquals("Value of angle attribute is unexpected.",
                180, ((WatermarkType) webService.getOperation().get(2)).getAngle());

        assertNotNull("Text element should have been created.",
                ((WatermarkType) webService.getOperation().get(2)).getText());
        assertEquals("Value of text attribute is unexpected.",
                "testText", ((WatermarkType) webService.getOperation().get(2)).getText().getText());
    }

    @Test
    public void testFactoryUrlConverterFromStream() throws Exception {
        UrlConverterWebService webService = getTypedWebservice(
                UrlConverterWebService.class,
                testResources.getResource("url_convert.xml")
        );
        assertNotNull("Operation should have been initialized", webService.getOperation());
        assertEquals("Value of url attribute is unexpected.",
                "testURL", webService.getOperation().getUrl());

        assertNotNull("Page element should have been created.",
                webService.getOperation().getPage());
        assertEquals("Value of metrics attribute is unexpected.",
                MetricsType.MM, webService.getOperation().getPage().getMetrics());
        assertEquals("Value of height attribute is unexpected.",
                1, webService.getOperation().getPage().getHeight());
        assertEquals("Value of width attribute is unexpected.",
                2, webService.getOperation().getPage().getWidth());
        assertEquals("Value of bottom attribute is unexpected.",
                3, webService.getOperation().getPage().getBottom());
        assertEquals("Value of left attribute is unexpected.",
                4, webService.getOperation().getPage().getLeft());
        assertEquals("Value of right attribute is unexpected.",
                5, webService.getOperation().getPage().getRight());
        assertEquals("Value of top attribute is unexpected.",
                6, webService.getOperation().getPage().getTop());

        assertNotNull("BasicAuth element should have been created.",
                webService.getOperation().getBasicAuth());
        assertEquals("Value of password attribute is unexpected.",
                "testPwd", webService.getOperation().getBasicAuth().getPassword());
        assertEquals("Value of userName attribute is unexpected.",
                "testUser", webService.getOperation().getBasicAuth().getUserName());

        assertNotNull("Proxy element should have been created.",
                webService.getOperation().getProxy());
        assertEquals("Value of userName attribute is unexpected.",
                "testUser", webService.getOperation().getProxy().getUserName());
        assertEquals("Value of password attribute is unexpected.",
                "testPwd", webService.getOperation().getProxy().getPassword());
        assertEquals("Value of address attribute is unexpected.",
                "testAddress", webService.getOperation().getProxy().getAddress());
        assertEquals("Value of port attribute is unexpected.",
                1, webService.getOperation().getProxy().getPort());
    }

    @Test
    public void testFactoryCreateWebserviceInstance() throws Exception {
        ToolboxWebService toolboxWebService = getWebService(WebServiceType.TOOLBOX);
        assertNotNull("The toolbox webservice should have been initialized.", toolboxWebService);
        assertNotNull("The toolbox operation should have been initialized.", toolboxWebService.getOperation());

        ConverterWebService converterWebService = getWebService(WebServiceType.CONVERTER);
        assertNotNull("The converter webservice should have been initialized.", converterWebService);
        assertNotNull("The converter operation should have been initialized.", converterWebService.getOperation());

        SignatureWebService signatureWebService = getWebService(WebServiceType.SIGNATURE);
        assertNotNull("The signature Web service should have been initialized.", signatureWebService);
        assertNotNull("The signature operation should have been initialized.", signatureWebService.getOperation());

        BarcodeWebService barcodeWebService = getWebService(WebServiceType.BARCODE);
        assertNotNull("The barcode webservice should have been initialized.", barcodeWebService);
        assertNotNull("The barcode operation should have been initialized.", barcodeWebService.getOperation());

        PdfaWebService pdfaWebService = getWebService(WebServiceType.PDFA);
        assertNotNull("The pdfa webservice should have been initialized.", pdfaWebService);
        assertNotNull("The pdfa operation should have been initialized.", pdfaWebService.getOperation());

        UrlConverterWebService urlConverterWebService = getWebService(WebServiceType.URLCONVERTER);
        assertNotNull("The url converter webservice should have been initialized.", urlConverterWebService);
        assertNotNull("The url converter operation should have been initialized.", urlConverterWebService.getOperation());

        OcrWebService ocrWebService = getWebService(WebServiceType.OCR);
        assertNotNull("The ocr webservice should have been initialized.", ocrWebService);
        assertNotNull("The ocr operation should have been initialized.", ocrWebService.getOperation());
    }

    @Test
    public void testNoOperationData() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testServer.getServer(TestServer.ServerType.LOCAL))) {
            WebServiceFactory.createInstance(session, (StreamSource) null);
            fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Error code %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                    ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
    }

    @Test
    public void testNoSession() {
        try {
            WebServiceFactory.createInstance(null, (StreamSource) null);
            fail("ResultException expected");
        } catch (ResultException ex) {
            assertEquals(String.format("Error code %s expected.", Error.SESSION_CREATE.getCode()),
                    ex.getResult().getCode(), Error.SESSION_CREATE.getCode());
        }
    }
}
