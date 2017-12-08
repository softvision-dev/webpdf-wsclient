package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.RestDocument;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.ImageHelper;
import net.webpdf.wsclient.testsuite.TestResources;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class RestWebserviceIntegrationTest {

    private final TestResources testResources = new TestResources(RestWebserviceIntegrationTest.class);

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testConverter() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            ConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

            File file = new File("./files/lorem-ipsum.docx");
            File fileOut = new File("./result/converter_rest.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            webService.getOperation().setPages("1-5");
            webService.getOperation().setEmbedFonts(true);

            webService.getOperation().setPdfa(new PdfaType());
            webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
            webService.getOperation().getPdfa().getConvert().setLevel("3b");
            webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testToolbox() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            ToolboxRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);

            File file = new File("./files/lorem-ipsum.pdf");
            File fileOut = new File("./result/toolbox_rest.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            MergeType mergeType = new MergeType();
            mergeType.setPage(1);
            mergeType.setSourceIsZip(false);
            mergeType.setMode(MergeModeType.AFTER_PAGE);

            // set merge file data
            mergeType.setData(new MergeFileDataType());
            mergeType.getData().setFormat(FileDataFormatType.PDF);

            try {
                mergeType.getData().setValue(Files.readAllBytes(Paths.get("./files/merge.pdf")));
            } catch (IOException ex) {
                System.out.println("Unable to add merge file data to params: " + ex.getMessage());
            }

            webService.getOperation().add(mergeType);

            // add rotate operation to the toolbox operation list
            RotateType rotateType = new RotateType();
            rotateType.setPages("1-5");
            rotateType.setDegrees(90);

            webService.getOperation().add(rotateType);

            DeleteType deleteType = new DeleteType();
            deleteType.setPages("5-8");

            webService.getOperation().add(deleteType);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testSignature() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            SignatureRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.SIGNATURE);

            File file = new File("./files/lorem-ipsum.pdf");
            File fileOut = new File("./result/signature_rest.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            webService.getOperation().setAdd(new SignatureType.Add());
            webService.getOperation().getAdd().setAppearance(new SignatureType.Add.Appearance());
            webService.getOperation().getAdd().getAppearance().setPage(1);
            webService.getOperation().getAdd().getAppearance().setName("John Doe, Company");

            SignatureIdentifierType signatureIdentifierType = new SignatureIdentifierType();
            signatureIdentifierType.setShowCommonName(true);
            signatureIdentifierType.setShowOrganisationName(false);
            signatureIdentifierType.setShowSignedBy(true);
            signatureIdentifierType.setShowCountry(false);
            signatureIdentifierType.setShowMail(false);
            signatureIdentifierType.setShowOrganisationUnit(false);
            webService.getOperation().getAdd().getAppearance().setIdentifierElements(signatureIdentifierType);

            SignaturePositionType signaturePositionType = new SignaturePositionType();
            signaturePositionType.setX(5.0f);
            signaturePositionType.setY(5.0f);
            signaturePositionType.setWidth(80.0f);
            signaturePositionType.setHeight(15.0f);
            webService.getOperation().getAdd().getAppearance().setPosition(signaturePositionType);

            SignatureImageType signatureImageType = new SignatureImageType();
            signatureImageType.setPosition(SignatureImagePositionType.LEFT);

            SignatureFileDataType signatureFileDataType = new SignatureFileDataType();
            signatureFileDataType.setValue(Files.readAllBytes(Paths.get("./files/logo.png")));
            signatureImageType.setData(signatureFileDataType);
            webService.getOperation().getAdd().getAppearance().setImage(signatureImageType);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testPdfa() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            PdfaRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.PDFA);

            File file = new File("./files/lorem-ipsum.pdf");
            File fileOut = new File("./result/pdfa_rest.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(session.getDocumentManager().uploadDocument(file));
            webService.getOperation().setConvert(new PdfaType.Convert());
            webService.getOperation().getConvert().setLevel("3b");
            webService.getOperation().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
            webService.getOperation().getConvert().setImageQuality(90);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testOcr() throws IOException, URISyntaxException, JAXBException {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            OcrRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.OCR);

            File file = new File("./files/ocr.png");
            File fileOut = new File("./result/ocr_rest.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(session.getDocumentManager().uploadDocument(file));
            webService.getOperation().setLanguage(OcrLanguageType.ENG);
            webService.getOperation().setOutputFormat(OcrOutputType.PDF);
            webService.getOperation().setCheckResolution(false);
            webService.getOperation().setImageDpi(200);

            webService.getOperation().setPage(new OcrPageType());
            webService.getOperation().getPage().setHeight(210);
            webService.getOperation().getPage().setWidth(148);
            webService.getOperation().getPage().setMetrics(MetricsType.MM);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testBarcode() throws IOException, URISyntaxException, JAXBException {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            BarcodeRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.BARCODE);

            File file = new File("./files/lorem-ipsum.pdf");
            File fileOut = new File("./result/barcode_rest.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            webService.getOperation().setAdd(new BarcodeType.Add());

            // build a desired barcode type
            QrBarcodeType qrBarcodeType = new QrBarcodeType();

            // set the position and the size for the barcode type
            RectangleType position = new RectangleType();
            position.setX(2.0f);
            position.setY(2.0f);
            position.setHeight(20.0f);
            position.setWidth(20.0f);

            qrBarcodeType.setPosition(position);
            qrBarcodeType.setPages("1-3");

            // set the barcode content value
            qrBarcodeType.setValue("https://www.webpdf.de");

            webService.getOperation().getAdd().getQrcode().add(qrBarcodeType);


            Ean8BarcodeType ean8BarcodeType = new Ean8BarcodeType();
            position = new RectangleType();
            position.setX(190.0f);
            position.setY(2.0f);
            position.setHeight(40.0f);
            position.setWidth(10.0f);
            ean8BarcodeType.setPosition(position);

            ean8BarcodeType.setValue("90311017");
            ean8BarcodeType.setPages("*");
            ean8BarcodeType.setRotation(90);

            webService.getOperation().getAdd().getEan8().add(ean8BarcodeType);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testUrlConverter() throws IOException, JAXBException, URISyntaxException {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            UrlConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.URLCONVERTER);

            webService.setDocument(new RestDocument(/*null,*/ null));

            File fileOut = new File("./result/urlconverter_rest.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.getOperation().setUrl("https://www.webpdf.de");

            webService.getOperation().setPage(new PageType());
            webService.getOperation().getPage().setWidth(150);
            webService.getOperation().getPage().setHeight(200);
            webService.getOperation().getPage().setTop(0);
            webService.getOperation().getPage().setLeft(0);
            webService.getOperation().getPage().setRight(0);
            webService.getOperation().getPage().setBottom(0);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testToolboxStream() throws Exception {
        File configFile = testResources.getResource("toolbox.json");
        File file = new File("./files/lorem-ipsum.pdf");
        String json = FileUtils.readFileToString(configFile, Charset.defaultCharset());
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl());
             StringReader stringReader = new StringReader(json)
        ) {
            StreamSource streamSource = new StreamSource(stringReader);
            session.login();
            ToolboxRestWebService webService = WebServiceFactory.createInstance(session, streamSource);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));
            File fileOut = new File("./result/toolbox_stream_rest.pdf");
            FileUtils.deleteQuietly(fileOut);
            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testToolboxSiwtchToOutputFile() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
            testResources.getArguments().buildServerUrl())) {

            session.login();

            ToolboxRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);

            File file = new File("./files/lorem-ipsum.pdf");
            File fileOut = new File("./result/toolbox_image_rest.jpeg");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            ImageType imageType = new ImageType();
            imageType.setPages("1");

            JpegType jpegType = new JpegType();
            jpegType.setJpegQuality(100);

            imageType.setJpeg(jpegType);

            webService.getOperation().add(imageType);
            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());

            assertEquals("Difference should have been zero.",
                0.0d,
                ImageHelper.compare(
                    ImageIO.read(testResources.getResource("toolbox_image_rest.jpeg")),
                    ImageIO.read(fileOut)
                ),
                0.0d);
        }
    }
}
