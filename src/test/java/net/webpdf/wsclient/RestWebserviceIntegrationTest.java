package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.rest.RestWebServiceDocument;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.ImageHelper;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;

import static net.webpdf.wsclient.testsuite.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.*;

public class RestWebserviceIntegrationTest {

    private final TestResources testResources = new TestResources(RestWebserviceIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @Test
    public void testConverter() throws Exception {

        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            ConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            assertNotNull(file);
            File sourceFile = testResources.getTempFolder().newFile();
            FileUtils.copyFile(file, sourceFile);
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(sourceFile));

            assertNotNull(webService.getOperation(),
                    "Operation should have been initialized");
            webService.getOperation().setPages("1-5");
            webService.getOperation().setEmbedFonts(true);

            webService.getOperation().setPdfa(new PdfaType());
            webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
            webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
            webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertNotNull(restDocument,
                    "REST document could not be downloaded.");
            assertNotNull(restDocument.getDocumentFile(),
                    "Downloaded REST document is null");
            assertEquals(FilenameUtils.removeExtension(sourceFile.getName()),
                    restDocument.getDocumentFile().getFileName());
            assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testToolbox() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            ToolboxRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.TOOLBOX);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            MergeType mergeType = new MergeType();
            mergeType.setPage(1);
            mergeType.setSourceIsZip(false);
            mergeType.setMode(MergeModeType.AFTER_PAGE);

            // set merge file data
            mergeType.setData(new MergeFileDataType());
            mergeType.getData().setFormat(FileDataFormatType.PDF);
            mergeType.getData().setValue(Files.readAllBytes(
                    testResources.getResource("integration/files/merge.pdf").toPath()));
            webService.getOperation().add(mergeType);

            // add rotate operation to the toolbox operation list
            RotateType rotateType = new RotateType();
            rotateType.setPages("1-5");
            rotateType.setDegrees(90);
            webService.getOperation().add(rotateType);

            DeleteType deleteType = new DeleteType();
            deleteType.setPages("5-8");
            webService.getOperation().add(deleteType);

            SecurityType securityType = new SecurityType();
            EncryptType encryptType = new EncryptType();
            EncryptType.Password password = new EncryptType.Password();
            password.setOpen("büro");
            encryptType.setPassword(password);
            securityType.setEncrypt(encryptType);
            webService.getOperation().add(securityType);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    @Disabled("this assumes, that 'test' actually is a certificate acronym in your webPDF server keystore.")
    public void testSignature() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            SignatureRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.SIGNATURE);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            assertNotNull(webService.getOperation(), "Operation should have been initialized");
            webService.getOperation().setAdd(new SignatureType.Add());
            webService.getOperation().getAdd().setKeyName("test");
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

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testPdfa() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            PdfaRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.PDFA);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));
            assertNotNull(webService.getOperation(), "Operation should have been initialized");
            webService.getOperation().setConvert(new PdfaType.Convert());
            webService.getOperation().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
            webService.getOperation().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
            webService.getOperation().getConvert().setImageQuality(90);

            File zugferdFile = testResources.getResource("integration/files/zugferd21-xrechnung-cii.xml");
            ZugferdType zugferdType = new ZugferdType();
            ZugferdFileDataType zugferdFileDataType = new ZugferdFileDataType();
            zugferdFileDataType.setVersion(ZugferdVersionType.V_21_X_RECHNUNG);
            zugferdFileDataType.setValue(FileUtils.readFileToByteArray(zugferdFile));
            zugferdFileDataType.setSource(FileDataSourceType.VALUE);
            zugferdType.setXmlFile(zugferdFileDataType);
            webService.getOperation().getConvert().setZugferd(zugferdType);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testOcr() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            OcrRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.OCR);

            File file = testResources.getResource("integration/files/ocr.png");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));
            assertNotNull(webService.getOperation(), "Operation should have been initialized");
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
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testBarcode() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            BarcodeRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.BARCODE);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            assertNotNull(webService.getOperation(), "Operation should have been initialized");
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
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testUrlConverter() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            UrlConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.URLCONVERTER);

            webService.setDocument(new RestWebServiceDocument(null));

            File fileOut = testResources.getTempFolder().newFile();

            assertNotNull(webService.getOperation(),
                    "Operation should have been initialized");
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
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testToolboxStream() throws Exception {
        File configFile = testResources.getResource("toolbox.json");
        File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
        String json = FileUtils.readFileToString(configFile, Charset.defaultCharset());
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL));
             StringReader stringReader = new StringReader(json)) {
            StreamSource streamSource = new StreamSource(stringReader);
            session.login();
            ToolboxRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session, streamSource);
            webService.setDocument(session.getDocumentManager().uploadDocument(file));
            File fileOut = testResources.getTempFolder().newFile();

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

    @Test
    public void testToolboxSwitchToOutputFile() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            session.login();

            ToolboxRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    WebServiceType.TOOLBOX);

            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            ImageType imageType = new ImageType();
            imageType.setPages("1");

            JpegType jpegType = new JpegType();
            jpegType.setJpegQuality(100);

            imageType.setJpeg(jpegType);

            webService.getOperation().add(imageType);
            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());

            assertEquals(
                    0.0d,
                    ImageHelper.compare(
                            ImageIO.read(testResources.getResource("toolbox_image_rest.jpeg")),
                            ImageIO.read(fileOut)
                    ),
                    0.0d,
                    "Difference should have been zero.");
        }
    }

    @Test
    public void testHandleRestSession() throws Exception {
        // Anonymous
        try (RestSession<RestDocument> restSession = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {
            assertNotNull(restSession,
                    "Valid session should have been created.");
            restSession.login();
            assertNotNull(restSession.getToken(),
                    "Token should have been initialized.");
            assertNotEquals("", restSession.getToken().getToken(),
                    "Token should have been not empty.");
            assertNotNull(restSession.getUser(),
                    "UserCredentials should have been initialized.");
            assertTrue(restSession.getUser().isUser(),
                    "User should be user");
            assertFalse(restSession.getUser().isAuthenticated(),
                    "User should not be authenticated");
            assertFalse(restSession.getUser().isAdmin(),
                    "User should not be admin");
            assertEquals("", restSession.getUser().getUserName(),
                    "Username should be empty.");
        }

        // User
        try (RestWebServiceSession restSession = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL, "user", "user"))) {
            assertNotNull(restSession,
                    "Valid session should have been created.");
            restSession.login();
            assertNotNull(restSession.getToken(),
                    "Token should have been initialized.");
            assertNotEquals("", restSession.getToken().getToken(),
                    "Token should have been not empty.");
            assertNotNull(restSession.getUser(),
                    "UserInfo should have been initialized.");
            assertTrue(restSession.getUser().isUser(),
                    "User should be user");
            assertTrue(restSession.getUser().isAuthenticated(),
                    "User should be authenticated");
            assertFalse(restSession.getUser().isAdmin(),
                    "User should not be admin");
            assertEquals("user", restSession.getUser().getUserName(),
                    "Username should be user.");
        }

        // Admin
        try (RestWebServiceSession restSession = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL, "admin", "admin"))) {
            assertNotNull(restSession,
                    "Valid session should have been created.");
            restSession.login();
            assertNotNull(restSession.getToken(),
                    "Token should have been initialized.");
            assertNotEquals("", restSession.getToken().getToken(),
                    "Token should have been not empty.");
            assertNotNull(restSession.getUser(),
                    "UserInfo should have been initialized.");
            assertTrue(restSession.getUser().isUser(),
                    "User should be user");
            assertTrue(restSession.getUser().isAuthenticated(),
                    "User should not be authenticated");
            assertTrue(restSession.getUser().isAdmin(),
                    "User should not be admin");
            assertEquals("admin", restSession.getUser().getUserName(),
                    "Username should be admin.");
        }
    }

}
