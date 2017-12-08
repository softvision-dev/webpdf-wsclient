package net.webpdf.wsclient.webservicefactory;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.webpdf.wsclient.*;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

@RunWith(JUnitParamsRunner.class)
public class RestWebserviceFactoryTest {

    private final TestResources testResources = new TestResources(RestWebserviceFactoryTest.class);

    @After
    public void tearDown() throws Exception {
    }

    private <T extends WebService> T getWebService(WebServiceType webServiceType) throws IOException {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {
            return WebServiceFactory.createInstance(session, webServiceType);
        }
    }

    private <T extends WebService> T getTypedWebservice(DataFormat dataFormat, Class<T> expectedType, File configFile) throws Exception {
        WebService webService;
        String json = FileUtils.readFileToString(configFile, Charset.defaultCharset());

        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {
            session.setDataFormat(dataFormat);
            try (StringReader stringReader = new StringReader(json)) {
                StreamSource streamSource = new StreamSource(stringReader);
                webService = WebServiceFactory.createInstance(session, streamSource);
            }
        }

        Assert.assertNotNull("webservice should have been instantiated.",
            webService);
        Assert.assertTrue(String.format("webservice should have been an instance of the %s webservice.",
            expectedType.getName()),
            expectedType.isInstance(webService));
        Assert.assertNotNull("Operation data should have been initialized",
            webService.getOperation());
        return (T) webService;
    }

    @Test
    @Parameters({
                    "XML|barcode.xml",
                    "JSON|barcode.json"
    })
    public void testFactoryBarcodeFromStream(String dataType, String configFileName) throws Exception {
        DataFormat dataFormat = DataFormat.valueOf(dataType);
        BarcodeRestWebService webService = getTypedWebservice(
            dataFormat, BarcodeRestWebService.class, testResources.getResource(configFileName)
        );
        Assert.assertNotNull("Add element should have been created.",
            webService.getOperation().getAdd());
        Assert.assertNotNull("QR-code element should have been created.",
            webService.getOperation().getAdd().getQrcode());
        Assert.assertEquals("Number of added QR-codes is incorrect.", 1,
            webService.getOperation().getAdd().getQrcode().size());
        Assert.assertEquals("Value of value attribute is unexpected.",
            "webPDFTest", webService.getOperation().getAdd().getQrcode().get(0).getValue());
        Assert.assertEquals("Value of pages attribute is unexpected.",
            "1", webService.getOperation().getAdd().getQrcode().get(0).getPages());
        Assert.assertEquals("Value of rotation attribute is unexpected.",
            90, webService.getOperation().getAdd().getQrcode().get(0).getRotation());
        Assert.assertEquals("Value of charset attribute is unexpected.",
            "utf-8", webService.getOperation().getAdd().getQrcode().get(0).getCharset());
        Assert.assertEquals("Value of errorCorrection attribute is unexpected.",
            QrCodeErrorCorrectionType.M, webService.getOperation().getAdd().getQrcode().get(0).getErrorCorrection());
        Assert.assertEquals("Value of margin attribute is unexpected.",
            1, webService.getOperation().getAdd().getQrcode().get(0).getMargin());
    }

    @Test
    @Parameters({
                    "XML|convert.xml",
                    "JSON|convert.json"
    })
    public void testFactoryConverterFromStream(String dataType, String configFileName) throws Exception {
        DataFormat dataFormat = DataFormat.valueOf(dataType);
        ConverterRestWebService webService = getTypedWebservice(
            dataFormat, ConverterRestWebService.class, testResources.getResource(configFileName)
        );
        Assert.assertTrue("Value of embedFonts attribute is unexpected.",
            webService.getOperation().isEmbedFonts());
        Assert.assertEquals("Value of pages attribute is unexpected.",
            "1", webService.getOperation().getPages());
        Assert.assertTrue("Value of reduceResolution attribute is unexpected.",
            webService.getOperation().isSetReduceResolution());
        Assert.assertEquals("Value of maxRecursion attribute is unexpected.",
            2, webService.getOperation().getMaxRecursion());
        Assert.assertEquals("Value of jpegQuality attribute is unexpected.",
            3, webService.getOperation().getJpegQuality());
        Assert.assertEquals("Value of fileExtension attribute is unexpected.",
            "zip", webService.getOperation().getFileExtension());
        Assert.assertEquals("Value of dpi attribute is unexpected.",
            4, webService.getOperation().getDpi());
        Assert.assertFalse("Value of compression attribute is unexpected.",
            webService.getOperation().isCompression());
        Assert.assertEquals("Value of maxRecursion attribute is unexpected.",
            "testPwd", webService.getOperation().getAccessPassword());

        Assert.assertNotNull("Pdfa element should have been created.",
            webService.getOperation().getPdfa());
        Assert.assertNotNull("Convert element should have been created.",
            webService.getOperation().getPdfa().getConvert());
        Assert.assertEquals("Value of level attribute is unexpected.",
            "1a", webService.getOperation().getPdfa().getConvert().getLevel());
        Assert.assertEquals("Value of errorReport attribute is unexpected.",
            PdfaErrorReportType.MESSAGE, webService.getOperation().getPdfa().getConvert().getErrorReport());
        Assert.assertEquals("Value of imageQuality attribute is unexpected.",
            1, webService.getOperation().getPdfa().getConvert().getImageQuality());
        Assert.assertEquals("Value of successReport attribute is unexpected.",
            PdfaSuccessReportType.ZIP, webService.getOperation().getPdfa().getConvert().getSuccessReport());
    }

    @Test
    @Parameters({
                    "XML|ocr.xml",
                    "JSON|ocr.json"
    })
    public void testFactoryOCRFromStream(String dataType, String configFileName) throws Exception {
        DataFormat dataFormat = DataFormat.valueOf(dataType);
        OcrRestWebService webService = getTypedWebservice(
            dataFormat, OcrRestWebService.class, testResources.getResource(configFileName)
        );
        Assert.assertFalse("Value of checkResolution attribute is unexpected.",
            webService.getOperation().isCheckResolution());
        Assert.assertTrue("Value of forceEachPage attribute is unexpected.",
            webService.getOperation().isForceEachPage());
        Assert.assertEquals("Value of imageDpi attribute is unexpected.",
            1, webService.getOperation().getImageDpi());
        Assert.assertEquals("Value of language attribute is unexpected.",
            OcrLanguageType.FRA, webService.getOperation().getLanguage());
        Assert.assertEquals("Value of outputFormat attribute is unexpected.",
            OcrOutputType.PDF, webService.getOperation().getOutputFormat());

        Assert.assertNotNull("Page element should have been created.",
            webService.getOperation().getPage());
        Assert.assertEquals("Value of width attribute is unexpected.",
            1, webService.getOperation().getPage().getWidth());
        Assert.assertEquals("Value of height attribute is unexpected.",
            2, webService.getOperation().getPage().getHeight());
        Assert.assertEquals("Value of metrics attribute is unexpected.",
            MetricsType.MM, webService.getOperation().getPage().getMetrics());
    }

    @Test
    @Parameters({
                    "XML|pdfa.xml",
                    "JSON|pdfa.json"
    })
    public void testFactoryPDFAFromStream(String dataType, String configFileName) throws Exception {
        DataFormat dataFormat = DataFormat.valueOf(dataType);
        PdfaRestWebService webService = getTypedWebservice(
            dataFormat, PdfaRestWebService.class, testResources.getResource(configFileName)
        );
        Assert.assertNotNull("Analyze element should have been created.",
            webService.getOperation().getAnalyze());
        Assert.assertEquals("Value of level attribute is unexpected.",
            "1a", webService.getOperation().getAnalyze().getLevel());
    }

    @Test
    @Parameters({
                    "XML|signature.xml",
                    "JSON|signature.json"
    })
    public void testFactorySignatureFromStream(String dataType, String configFileName) throws Exception {
        DataFormat dataFormat = DataFormat.valueOf(dataType);
        SignatureRestWebService webService = getTypedWebservice(
            dataFormat, SignatureRestWebService.class, testResources.getResource(configFileName)
        );
        Assert.assertNotNull("Add element should have been created.",
            webService.getOperation().getAdd());
        Assert.assertEquals("Value of location attribute is unexpected.",
            "testLocation", webService.getOperation().getAdd().getLocation());
        Assert.assertTrue("Value of appendSignature attribute is unexpected.",
            webService.getOperation().getAdd().isAppendSignature());
        Assert.assertEquals("Value of certificationLevel attribute is unexpected.",
            CertificationLevelType.NONE, webService.getOperation().getAdd().getCertificationLevel());
        Assert.assertEquals("Value of contact attribute is unexpected.",
            "testContact", webService.getOperation().getAdd().getContact());
        Assert.assertEquals("Value of fieldName attribute is unexpected.",
            "testName", webService.getOperation().getAdd().getFieldName());
        Assert.assertEquals("Value of keyName attribute is unexpected.",
            "testKey", webService.getOperation().getAdd().getKeyName());
        Assert.assertEquals("Value of keyPassword attribute is unexpected.",
            "testPwd", webService.getOperation().getAdd().getKeyPassword());
        Assert.assertEquals("Value of reason attribute is unexpected.",
            "testReason", webService.getOperation().getAdd().getReason());

        Assert.assertNotNull("Appearance element should have been created.",
            webService.getOperation().getAdd().getAppearance());
        Assert.assertEquals("Value of page attribute is unexpected.",
            1, webService.getOperation().getAdd().getAppearance().getPage());
        Assert.assertEquals("Value of name attribute is unexpected.",
            "testName", webService.getOperation().getAdd().getAppearance().getName());
        Assert.assertEquals("Value of identifier attribute is unexpected.",
            "testIdentifier", webService.getOperation().getAdd().getAppearance().getIdentifier());
    }

    @Test
    @Parameters({
                    "XML|toolbox.xml",
                    "JSON|toolbox.json"
    })
    public void testFactoryToolboxFromStream(String dataType, String configFileName) throws Exception {
        DataFormat dataFormat = DataFormat.valueOf(dataType);
        ToolboxRestWebService webService = getTypedWebservice(
            dataFormat, ToolboxRestWebService.class, testResources.getResource(configFileName)
        );
        BaseToolboxType element1 = webService.getOperation().get(0);
        Assert.assertNotNull("First element should have been created.",
            element1);
        Assert.assertTrue("First element should have been instance of delete type.",
            DeleteType.class.isInstance(element1));

        Assert.assertEquals("Value of pages attribute is unexpected.",
            "1", ((DeleteType) webService.getOperation().get(0)).getPages());

        BaseToolboxType element2 = webService.getOperation().get(1);
        Assert.assertNotNull("Second element should have been created.",
            element2);
        Assert.assertTrue("Second element should have been instance of rotate type.",
            RotateType.class.isInstance(element2));

        Assert.assertEquals("Value of pages attribute is unexpected.",
            "*", ((RotateType) webService.getOperation().get(1)).getPages());
        Assert.assertEquals("Value of degrees attribute is unexpected.",
            90, ((RotateType) webService.getOperation().get(1)).getDegrees());
        Assert.assertEquals("Value of pageGroup attribute is unexpected.",
            PageGroupType.EVEN, ((RotateType) webService.getOperation().get(1)).getPageGroup());
        Assert.assertEquals("Value of pageOrientation attribute is unexpected.",
            PageOrientationType.ANY, ((RotateType) webService.getOperation().get(1)).getPageOrientation());

        BaseToolboxType element3 = webService.getOperation().get(2);
        Assert.assertNotNull("Third element should have been created.",
            element3);
        Assert.assertTrue("Third element should have been instance of watermark type.",
            WatermarkType.class.isInstance(element3));
        Assert.assertEquals("Value of pages attribute is unexpected.",
            "2", ((WatermarkType) webService.getOperation().get(2)).getPages());
        Assert.assertEquals("Value of angle attribute is unexpected.",
            180, ((WatermarkType) webService.getOperation().get(2)).getAngle());

        Assert.assertNotNull("Text element should have been created.",
            ((WatermarkType) webService.getOperation().get(2)).getText());
        Assert.assertEquals("Value of text attribute is unexpected.",
            "testText", ((WatermarkType) webService.getOperation().get(2)).getText().getText());
    }

    @Test
    @Parameters({
                    "XML|url_convert.xml",
                    "JSON|url_convert.json"
    })
    public void testFactoryUrlConverterFromStream(String dataType, String configFileName) throws Exception {
        DataFormat dataFormat = DataFormat.valueOf(dataType);
        UrlConverterRestWebService webService = getTypedWebservice(
            dataFormat, UrlConverterRestWebService.class,
            testResources.getResource(configFileName)
        );
        Assert.assertEquals("Value of url attribute is unexpected.",
            "testURL", webService.getOperation().getUrl());

        Assert.assertNotNull("Page element should have been created.",
            webService.getOperation().getPage());
        Assert.assertEquals("Value of metrics attribute is unexpected.",
            MetricsType.MM, webService.getOperation().getPage().getMetrics());
        Assert.assertEquals("Value of height attribute is unexpected.",
            1, webService.getOperation().getPage().getHeight());
        Assert.assertEquals("Value of width attribute is unexpected.",
            2, webService.getOperation().getPage().getWidth());
        Assert.assertEquals("Value of bottom attribute is unexpected.",
            3, webService.getOperation().getPage().getBottom());
        Assert.assertEquals("Value of left attribute is unexpected.",
            4, webService.getOperation().getPage().getLeft());
        Assert.assertEquals("Value of right attribute is unexpected.",
            5, webService.getOperation().getPage().getRight());
        Assert.assertEquals("Value of top attribute is unexpected.",
            6, webService.getOperation().getPage().getTop());

        Assert.assertNotNull("BasicAuth element should have been created.",
            webService.getOperation().getBasicAuth());
        Assert.assertEquals("Value of password attribute is unexpected.",
            "testPwd", webService.getOperation().getBasicAuth().getPassword());
        Assert.assertEquals("Value of userName attribute is unexpected.",
            "testUser", webService.getOperation().getBasicAuth().getUserName());

        Assert.assertNotNull("Proxy element should have been created.",
            webService.getOperation().getProxy());
        Assert.assertEquals("Value of userName attribute is unexpected.",
            "testUser", webService.getOperation().getProxy().getUserName());
        Assert.assertEquals("Value of password attribute is unexpected.",
            "testPwd", webService.getOperation().getProxy().getPassword());
        Assert.assertEquals("Value of address attribute is unexpected.",
            "testAddress", webService.getOperation().getProxy().getAddress());
        Assert.assertEquals("Value of port attribute is unexpected.",
            1, webService.getOperation().getProxy().getPort());
    }

    @Test
    public void testFactoryCreateWebserviceInstance() throws Exception {
        ToolboxRestWebService toolboxWebService = getWebService(WebServiceType.TOOLBOX);
        Assert.assertNotNull("The toolbox webservice should have been initialized.", toolboxWebService);
        Assert.assertNotNull("The toolbox operation should have been initialized.", toolboxWebService.getOperation());

        ConverterRestWebService converterWebService = getWebService(WebServiceType.CONVERTER);
        Assert.assertNotNull("The converter webservice should have been initialized.", converterWebService);
        Assert.assertNotNull("The converter operation should have been initialized.", converterWebService.getOperation());

        SignatureRestWebService signatureWebService = getWebService(WebServiceType.SIGNATURE);
        Assert.assertNotNull("The signature webservice should have been initialized.", signatureWebService);
        Assert.assertNotNull("The signature operation should have been initialized.", signatureWebService.getOperation());

        BarcodeRestWebService barcodeWebService = getWebService(WebServiceType.BARCODE);
        Assert.assertNotNull("The barcode webservice should have been initialized.", barcodeWebService);
        Assert.assertNotNull("The barcode operation should have been initialized.", barcodeWebService.getOperation());

        PdfaRestWebService pdfaWebService = getWebService(WebServiceType.PDFA);
        Assert.assertNotNull("The pdfa webservice should have been initialized.", pdfaWebService);
        Assert.assertNotNull("The pdfa operation should have been initialized.", pdfaWebService.getOperation());

        UrlConverterRestWebService urlConverterWebService = getWebService(WebServiceType.URLCONVERTER);
        Assert.assertNotNull("The url converter webservice should have been initialized.", urlConverterWebService);
        Assert.assertNotNull("The url converter operation should have been initialized.", urlConverterWebService.getOperation());

        OcrRestWebService ocrWebService = getWebService(WebServiceType.OCR);
        Assert.assertNotNull("The ocr webservice should have been initialized.", ocrWebService);
        Assert.assertNotNull("The ocr operation should have been initialized.", ocrWebService.getOperation());
    }

    @Test
    public void testNoOperationData() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {
            StreamSource source = null;
            WebServiceFactory.createInstance(session, source);
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            Assert.assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
    }

    @Test
    public void testNoValidDataFormat() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl());
             StringReader reader = new StringReader("")) {
            session.setDataFormat(null);
            WebServiceFactory.createInstance(session, new StreamSource(reader));
            Assert.fail("ResultException expected");
        } catch (ResultException ex) {
            Assert.assertEquals(String.format("Errorcode %s expected.", Error.INVALID_OPERATION_DATA.getCode()),
                ex.getResult().getCode(), Error.INVALID_OPERATION_DATA.getCode());
        }
    }
}

