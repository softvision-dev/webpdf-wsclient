package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SoapWebserviceIntegrationTest {

    private final TestResources testResources = new TestResources(SoapWebserviceIntegrationTest.class);
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @SuppressWarnings("Duplicates")
    @Test
    public void testConverter() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, testServer.getServer(TestServer.ServerType.LOCAL))) {
            ConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));
            webService.getOperation().setPages("1-5");
            webService.getOperation().setEmbedFonts(true);

            webService.getOperation().setPdfa(new PdfaType());
            webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
            webService.getOperation().getPdfa().getConvert().setLevel("3b");
            webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }


    @Test
    public void testToolbox() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, testServer.getServer(TestServer.ServerType.LOCAL))) {
            ToolboxWebService webService = WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));

            MergeType mergeType = new MergeType();
            mergeType.setPage(1);
            mergeType.setSourceIsZip(false);
            mergeType.setMode(MergeModeType.AFTER_PAGE);

            // set merge file data
            mergeType.setData(new MergeFileDataType());
            mergeType.getData().setFormat(FileDataFormatType.PDF);

            try {
                mergeType.getData().setValue(Files.readAllBytes(testResources.getResource("integration/files/merge.pdf").toPath()));
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

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

    @Test
    public void testSignature() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, testServer.getServer(TestServer.ServerType.LOCAL))) {
            SignatureWebService webService = WebServiceFactory.createInstance(session, WebServiceType.SIGNATURE);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));

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
            signatureFileDataType.setValue(Files.readAllBytes(testResources.getResource("integration/files/logo.png").toPath()));
            signatureImageType.setData(signatureFileDataType);
            webService.getOperation().getAdd().getAppearance().setImage(signatureImageType);

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

    @Test
    public void testPdfa() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, testServer.getServer(TestServer.ServerType.LOCAL))) {
            PdfaWebService webService = WebServiceFactory.createInstance(session, WebServiceType.PDFA);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));

            webService.getOperation().setConvert(new PdfaType.Convert());
            webService.getOperation().getConvert().setLevel("3b");
            webService.getOperation().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
            webService.getOperation().getConvert().setImageQuality(90);

            webService.getBilling().setUserName("John Doe");
            webService.getBilling().setApplicationName("webPDF Sample Application");
            webService.getBilling().setCustomerCode("ABC123");

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

    @Test
    public void testOcr() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, testServer.getServer(TestServer.ServerType.LOCAL))) {
            OcrWebService webService = WebServiceFactory.createInstance(session, WebServiceType.OCR);

            File file = testResources.getResource("integration/files/ocr.png");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));

            webService.getOperation().setLanguage(OcrLanguageType.ENG);
            webService.getOperation().setOutputFormat(OcrOutputType.PDF);
            webService.getOperation().setCheckResolution(false);
            webService.getOperation().setImageDpi(200);

            webService.getOperation().setPage(new OcrPageType());
            webService.getOperation().getPage().setHeight(210);
            webService.getOperation().getPage().setWidth(148);
            webService.getOperation().getPage().setMetrics(MetricsType.MM);

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

    @Test
    public void testBarcode() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, testServer.getServer(TestServer.ServerType.LOCAL))) {
            BarcodeWebService webService = WebServiceFactory.createInstance(session, WebServiceType.BARCODE);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));

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

            // create a second barcode (EAN8)
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

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

    @Test
    public void testUrlConverter() throws Exception {
        try (Session session = SessionFactory.createInstance(WebServiceProtocol.SOAP, testServer.getServer(TestServer.ServerType.LOCAL))) {
            UrlConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.URLCONVERTER);

            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(null, fileOut));

            webService.getOperation().setUrl("https://www.webpdf.de");
            webService.getOperation().setPage(new PageType());
            webService.getOperation().getPage().setWidth(150);
            webService.getOperation().getPage().setHeight(200);
            webService.getOperation().getPage().setTop(0);
            webService.getOperation().getPage().setLeft(0);
            webService.getOperation().getPage().setRight(0);
            webService.getOperation().getPage().setBottom(0);

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }
}
