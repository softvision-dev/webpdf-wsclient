package net.webpdf.wsclient;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.testsuite.config.TestConfig;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.integration.certificate.GenericCertificate;
import net.webpdf.wsclient.testsuite.io.ImageHelper;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.imageio.ImageIO;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RestWebserviceIntegrationTest {

    private final TestResources testResources = new TestResources(RestWebserviceIntegrationTest.class);
    public TestServer testServer = TestServer.getInstance();

    @ParameterizedTest
    @IntegrationTest
    @CsvSource(delimiter = '|', value = {
            "lorem-ipsum.docx|5",
            "logo.png|1"
    })
    public void testConverter(String fileName, int expectedPages) {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                ConverterRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.CONVERTER);
                File file = testResources.getResource("integration/files/" + fileName);
                assertNotNull(file);
                File sourceFile = testResources.getTempFolder().newFile();
                FileUtils.copyFile(file, sourceFile);
                File fileOut = testResources.getTempFolder().newFile();
                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setPages("1-5");
                webService.getOperationParameters().setEmbedFonts(true);

                OperationPdfa pdfa = new OperationPdfa();
                webService.getOperationParameters().setPdfa(pdfa);
                OperationConvertPdfa convertPdfa = new OperationConvertPdfa();
                pdfa.setConvert(convertPdfa);
                convertPdfa.setLevel(OperationConvertPdfa.LevelEnum._3B);
                convertPdfa.setErrorReport(OperationConvertPdfa.ErrorReportEnum.MESSAGE);

                RestDocument restDocument = webService.process(session.uploadDocument(sourceFile));

                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    restDocument.downloadDocument(fileOutputStream);
                }
                assertNotNull(restDocument, "REST document could not be downloaded.");
                assertNotNull(restDocument.getDocumentFile(), "Downloaded REST document is null");

                DocumentFile documentFile = restDocument.getDocumentFile();
                assertNotNull(documentFile.getMetadata(), "Document metadata could not be retrieved.");
                assertNotNull(documentFile.getMetadata().getMetadataType());
                assertEquals(DocumentMetadataType.PDF, documentFile.getMetadata().getMetadataType());
                DocumentMetadataPdf documentMetadataPdf = (DocumentMetadataPdf) documentFile.getMetadata();
                assertNotNull(documentMetadataPdf.getPages());
                MetadataPagesDocument metadataPagesDocument = documentMetadataPdf.getPages();
                assertNotNull(metadataPagesDocument);
                List<MetadataPagePages> metadataPagePages = metadataPagesDocument.getPage();
                assertNotNull(metadataPagePages);
                assertEquals(expectedPages, metadataPagePages.size());
                assertEquals(FilenameUtils.removeExtension(sourceFile.getName()),
                        restDocument.getDocumentFile().getFileName());
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testConverterTemplate() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                File resourceFile = testResources.getResource("integration/files/bigFive.html");
                assertNotNull(resourceFile);
                File templateFile = testResources.getResource("integration/files/bigFive.json");
                assertNotNull(templateFile);
                File headerFile = testResources.getResource("integration/files/bigFive-header.html");
                assertNotNull(headerFile);
                File footerFile = testResources.getResource("integration/files/bigFive-footer.html");
                assertNotNull(footerFile);
                File fileOut = testResources.getTempFolder().newFile();

                File sourceFile = testResources.getTempFolder().newFile();
                FileUtils.copyFile(resourceFile, sourceFile);

                String json = FileUtils.readFileToString(templateFile, StandardCharsets.UTF_8);

                ConverterRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.CONVERTER);
                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");

                OperationConverterHtml converterHtmlType = new OperationConverterHtml();
                converterHtmlType.setImageMode(OperationConverterHtml.ImageModeEnum.FILE);
                converterHtmlType.setPreferCSSPageSize(true);
                converterHtmlType.setUseAsTemplate(true);
                converterHtmlType.setDownloadImages(true);
                converterHtmlType.setBaseURL("https://upload.wikimedia.org/");
                converterHtmlType.setTemplateData(new OperationTemplateData());
                assertNotNull(converterHtmlType.getTemplateData());
                converterHtmlType.getTemplateData().setSource(OperationTemplateData.SourceEnum.VALUE);
                converterHtmlType.getTemplateData().setValue(json.getBytes());
                webService.getOperationParameters().setHtml(converterHtmlType);

                OperationConverterPage converterPageType = new OperationConverterPage();
                converterPageType.setHeader(new OperationConverterHeader());
                assertNotNull(converterPageType.getHeader());
                converterPageType.getHeader().setValue(FileUtils.readFileToByteArray(headerFile));
                converterPageType.setFooter(new OperationConverterFooter());
                assertNotNull(converterPageType.getFooter());
                converterPageType.getFooter().setValue(FileUtils.readFileToByteArray(footerFile));
                webService.getOperationParameters().setPage(converterPageType);

                RestDocument restDocument = webService.process(session.uploadDocument(sourceFile));
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    restDocument.downloadDocument(fileOutputStream);
                }
                assertNotNull(restDocument, "REST document could not be downloaded.");
                assertNotNull(restDocument.getDocumentFile(), "Downloaded REST document is null");
                assertNotNull(restDocument.getDocumentFile().getMetadata(), "REST Document should have metadata");
                assertInstanceOf(DocumentMetadataPdf.class, restDocument.getDocumentFile().getMetadata());
                DocumentMetadataPdf documentMetadataPdf = (DocumentMetadataPdf) restDocument.getDocumentFile().getMetadata();
                assertNotNull(documentMetadataPdf.getPages(), "REST Document should have pages");
                MetadataPagesDocument metadataPagesDocument = documentMetadataPdf.getPages();
                assertNotNull(metadataPagesDocument.getPage(), "REST Document should have pages");
                List<MetadataPagePages> metadataPagePages = metadataPagesDocument.getPage();
                int pageCount = 0;
                for (MetadataPagePages page : metadataPagePages) {
                    pageCount++;

                    assertNotNull(page.getBoxes());
                    assertNotNull(page.getBoxes().getMediaBox());
                    assertNotNull(page.getBoxes().getMediaBox().getHeight());
                    assertNotNull(page.getBoxes().getMediaBox().getWidth());

                    switch (pageCount) {
                        case 1:
                            assertEquals(595, Math.round(page.getBoxes().getMediaBox().getHeight()));
                            assertEquals(842, Math.round(page.getBoxes().getMediaBox().getWidth()));
                            break;
                        case 2:
                            assertEquals(842, Math.round(page.getBoxes().getMediaBox().getHeight()));
                            assertEquals(595, Math.round(page.getBoxes().getMediaBox().getWidth()));
                            break;
                    }
                }

                assertEquals(2, pageCount);
                assertEquals(FilenameUtils.removeExtension(sourceFile.getName()),
                        restDocument.getDocumentFile().getFileName());
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testToolbox() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                ToolboxRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.TOOLBOX);
                OperationBaseToolbox baseToolbox = new OperationBaseToolbox();
                OperationToolboxMergeMerge mergeType = new OperationToolboxMergeMerge();
                baseToolbox.setMerge(mergeType);
                mergeType.setPage(1);
                mergeType.setSourceIsZip(false);
                mergeType.setMode(OperationToolboxMergeMerge.ModeEnum.AFTER_PAGE);

                // set merge file data
                OperationMergeFileData mergeFileData = new OperationMergeFileData();
                mergeType.setData(mergeFileData);
                mergeType.getData().setFormat(OperationMergeFileData.FormatEnum.PDF);
                mergeType.getData().setValue(Files.readAllBytes(
                        testResources.getResource("integration/files/merge.pdf").toPath()));
                webService.getOperationParameters().add(baseToolbox);

                // add rotate operation to the toolbox operation list
                baseToolbox = new OperationBaseToolbox();
                OperationToolboxRotateRotate rotateType = new OperationToolboxRotateRotate();
                baseToolbox.setRotate(rotateType);
                rotateType.setPages("1-5");
                rotateType.setDegrees(90);
                webService.getOperationParameters().add(baseToolbox);

                baseToolbox = new OperationBaseToolbox();
                OperationToolboxDeleteDelete deleteType = new OperationToolboxDeleteDelete();
                baseToolbox.setDelete(deleteType);
                deleteType.setPages("5-8");
                webService.getOperationParameters().add(baseToolbox);

                baseToolbox = new OperationBaseToolbox();
                OperationToolboxSecuritySecurity securityType = new OperationToolboxSecuritySecurity();
                baseToolbox.setSecurity(securityType);
                OperationEncrypt encryptType = new OperationEncrypt();
                OperationPasswordEncrypt password = new OperationPasswordEncrypt();
                password.setOpen("büro");
                encryptType.setPassword(password);
                securityType.setEncrypt(encryptType);
                webService.getOperationParameters().add(baseToolbox);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.uploadDocument(file))
                            .downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testSignature() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                SignatureRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.SIGNATURE);
                GenericCertificate genericCertificate = new GenericCertificate("John Doe");
                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                OperationAddSignature add = new OperationAddSignature();
                webService.getOperationParameters().setAdd(add);
                OperationSignerAdd signer = new OperationSignerAdd();
                add.setSigner(signer);

                OperationKeyPair keyPairType = new OperationKeyPair();
                OperationCertificateFileData certificateFileDataType = new OperationCertificateFileData();
                certificateFileDataType.setValue(genericCertificate.getCertificatesAsPEM());
                certificateFileDataType.setSource(OperationCertificateFileData.SourceEnum.VALUE);
                keyPairType.setCertificate(certificateFileDataType);

                OperationPrivateKeyFileData privateKeyFileDataType = new OperationPrivateKeyFileData();
                privateKeyFileDataType.setValue(genericCertificate.getPrivateKeyAsPEM());
                privateKeyFileDataType.setSource(OperationPrivateKeyFileData.SourceEnum.VALUE);
                keyPairType.setPrivateKey(privateKeyFileDataType);
                signer.setKeyPair(keyPairType);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.uploadDocument(file))
                            .downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testPdfa() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                PdfaRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.PDFA);

                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                OperationConvertPdfa convertPdfa = new OperationConvertPdfa();
                webService.getOperationParameters().setConvert(convertPdfa);
                convertPdfa.setLevel(OperationConvertPdfa.LevelEnum._3B);
                convertPdfa.setErrorReport(OperationConvertPdfa.ErrorReportEnum.MESSAGE);
                convertPdfa.setImageQuality(90);

                File zugferdFile = testResources.getResource("integration/files/zugferd21-xrechnung-cii.xml");
                OperationZugferd zugferdType = new OperationZugferd();
                OperationZugferdFileData zugferdFileDataType = new OperationZugferdFileData();
                zugferdFileDataType.setVersion(OperationZugferdFileData.VersionEnum.V21_X_RECHNUNG);
                zugferdFileDataType.setValue(FileUtils.readFileToByteArray(zugferdFile));
                zugferdFileDataType.setSource(OperationZugferdFileData.SourceEnum.VALUE);
                zugferdType.setXmlFile(zugferdFileDataType);
                convertPdfa.setZugferd(zugferdType);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.uploadDocument(file))
                            .downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testOcr() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                OcrRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.OCR);

                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                webService.getOperationParameters().setLanguage(OperationOcr.LanguageEnum.ENG);
                webService.getOperationParameters().setOutputFormat(OperationOcr.OutputFormatEnum.PDF);
                webService.getOperationParameters().setCheckResolution(false);
                webService.getOperationParameters().setImageDpi(200);

                OperationOcrPage page = new OperationOcrPage();
                webService.getOperationParameters().setPage(page);
                page.setHeight(210);
                page.setWidth(148);
                page.setMetrics(OperationOcrPage.MetricsEnum.MM);

                File file = testResources.getResource("integration/files/ocr.png");
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.uploadDocument(file))
                            .downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testBarcode() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                BarcodeRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.BARCODE);

                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                OperationAddBarcode addBarcode = new OperationAddBarcode();
                webService.getOperationParameters().setAdd(addBarcode);

                // build a desired barcode type
                OperationQrBarcode qrBarcodeType = new OperationQrBarcode();

                // set the position and the size for the barcode type
                OperationRectangle position = new OperationRectangle();
                position.setX(2.0f);
                position.setY(2.0f);
                position.setHeight(20.0f);
                position.setWidth(20.0f);

                qrBarcodeType.setPosition(position);
                qrBarcodeType.setPages("1-3");

                // set the barcode content value
                qrBarcodeType.setValue("https://www.webpdf.de");

                List<OperationQrBarcode> qrBarcodeList = new ArrayList<>();
                qrBarcodeList.add(qrBarcodeType);
                addBarcode.setQrcode(qrBarcodeList);

                OperationEan8Barcode ean8BarcodeType = new OperationEan8Barcode();
                position = new OperationRectangle();
                position.setX(190.0f);
                position.setY(2.0f);
                position.setHeight(40.0f);
                position.setWidth(10.0f);
                ean8BarcodeType.setPosition(position);

                ean8BarcodeType.setValue("90311017");
                ean8BarcodeType.setPages("*");
                ean8BarcodeType.setRotation(90);

                List<OperationEan8Barcode> ean8BarcodeList = new ArrayList<>();
                ean8BarcodeList.add(ean8BarcodeType);
                addBarcode.setEan8(ean8BarcodeList);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.uploadDocument(file))
                            .downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testUrlConverter() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                UrlConverterRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.URLCONVERTER);

                File fileOut = testResources.getTempFolder().newFile();

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setUrl("https://docs.webpdf.de");

                OperationUrlConverterPage page = new OperationUrlConverterPage();
                webService.getOperationParameters().setPage(page);
                page.setWidth(150);
                page.setHeight(200);
                page.setTop(0);
                page.setLeft(0);
                page.setRight(0);
                page.setBottom(0);

                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process().downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testToolboxStream() {
        assertDoesNotThrow(() -> {
            File configFile = testResources.getResource("toolbox.json");
            File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
            String json = FileUtils.readFileToString(configFile, Charset.defaultCharset());
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)));
                 StringReader stringReader = new StringReader(json)) {
                StreamSource streamSource = new StreamSource(stringReader);
                ToolboxRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session, streamSource);
                File fileOut = testResources.getTempFolder().newFile();

                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.uploadDocument(file))
                            .downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

    @Test
    @IntegrationTest
    public void testToolboxSwitchToOutputFile() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                ToolboxRestWebService<RestDocument> webService =
                        session.createWebServiceInstance(WebServiceType.TOOLBOX);

                OperationBaseToolbox baseToolbox = new OperationBaseToolbox();
                webService.getOperationParameters().add(baseToolbox);

                OperationToolboxImageImage imageType = new OperationToolboxImageImage();
                baseToolbox.setImage(imageType);
                imageType.setPages("1");

                OperationJpeg jpegType = new OperationJpeg();
                jpegType.setJpegQuality(100);

                imageType.setJpeg(jpegType);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.uploadDocument(file))
                            .downloadDocument(fileOutputStream);
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
        });
    }

    @Test
    @IntegrationTest
    public void testHandleRestSession() {
        assertDoesNotThrow(() -> {
            // Anonymous
            try (RestSession<RestDocument> restSession = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(restSession,
                        "Valid session should have been created.");
                assertNotNull(restSession.getUser(),
                        "UserCredentials should have been initialized.");
                assertTrue(restSession.getUser().getIsUser(),
                        "User should be user");
                assertFalse(restSession.getUser().getIsAdmin(),
                        "User should not be admin");
                assertEquals("", restSession.getUser().getUserName(),
                        "Username should be empty.");
            }

            // User
            try (RestWebServiceSession restSession = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(TestConfig.getInstance().getServerConfig().getLocalUserName(), TestConfig.getInstance().getServerConfig().getLocalUserPassword()))) {
                assertNotNull(restSession,
                        "Valid session should have been created.");
                assertNotNull(restSession.getUser(),
                        "UserInfo should have been initialized.");
                assertTrue(restSession.getUser().getIsUser(),
                        "User should be user");
                assertFalse(restSession.getUser().getIsAdmin(),
                        "User should not be admin");
                assertEquals(TestConfig.getInstance().getServerConfig().getLocalUserName(), restSession.getUser().getUserName(),
                        "Username should be user.");
            }

            // Admin
            try (RestWebServiceSession restSession = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(TestConfig.getInstance().getServerConfig().getLocalAdminName(), TestConfig.getInstance().getServerConfig().getLocalAdminPassword()))) {
                assertNotNull(restSession,
                        "Valid session should have been created.");
                assertNotNull(restSession.getUser(),
                        "UserInfo should have been initialized.");
                assertTrue(restSession.getUser().getIsUser(),
                        "User should be user");
                assertTrue(restSession.getUser().getIsAdmin(),
                        "User should not be admin");
                assertEquals(TestConfig.getInstance().getServerConfig().getLocalAdminName(), restSession.getUser().getUserName(),
                        "Username should be admin.");
            }
        });
    }

}
