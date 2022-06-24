package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class SoapWebserviceIntegrationTest {

    private final TestResources testResources = new TestResources(SoapWebserviceIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @SuppressWarnings("Duplicates")
    @Test
    @IntegrationTest
    public void testConverter() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.CONVERTER);

                File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                assertNotNull(webService.getOperation(), "Operation should have been initialized");
                webService.getOperation().setPages("1-5");
                webService.getOperation().setEmbedFonts(true);

                webService.getOperation().setPdfa(new PdfaType());
                webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testToolbox() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                ToolboxWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.TOOLBOX);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                MergeType mergeType = new MergeType();
                mergeType.setPage(1);
                mergeType.setSourceIsZip(false);
                mergeType.setMode(MergeModeType.AFTER_PAGE);

                // set merge file data
                mergeType.setData(new MergeFileDataType());
                mergeType.getData().setFormat(FileDataFormatType.PDF);

                try {
                    mergeType.getData().setValue(Files.readAllBytes(
                            testResources.getResource("integration/files/merge.pdf").toPath()));
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

                SecurityType securityType = new SecurityType();
                EncryptType encryptType = new EncryptType();
                EncryptType.Password password = new EncryptType.Password();
                password.setOpen("büro");
                encryptType.setPassword(password);
                securityType.setEncrypt(encryptType);
                webService.getOperation().add(securityType);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    @Disabled("this assumes, that 'test' actually is a certificate acronym in your webPDF server keystore.")
    public void testSignature() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                SignatureWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.SIGNATURE);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
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
                signatureFileDataType.setValue(Files.readAllBytes(
                        testResources.getResource("integration/files/logo.png").toPath()));
                signatureImageType.setData(signatureFileDataType);
                webService.getOperation().getAdd().getAppearance().setImage(signatureImageType);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testPdfa() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                PdfaWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.PDFA);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
                webService.getOperation().setConvert(new PdfaType.Convert());
                webService.getOperation().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
                webService.getOperation().getConvert().setImageQuality(90);

                webService.getBilling().setUserName("John Doe");
                webService.getBilling().setApplicationName("webPDF Sample Application");
                webService.getBilling().setCustomerCode("ABC123");

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testOcr() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                OcrWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.OCR);

                File file = testResources.getResource("integration/files/ocr.png");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
                webService.getOperation().setLanguage(OcrLanguageType.ENG);
                webService.getOperation().setOutputFormat(OcrOutputType.PDF);
                webService.getOperation().setCheckResolution(false);
                webService.getOperation().setImageDpi(200);

                webService.getOperation().setPage(new OcrPageType());
                webService.getOperation().getPage().setHeight(210);
                webService.getOperation().getPage().setWidth(148);
                webService.getOperation().getPage().setMetrics(MetricsType.MM);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testBarcode() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                BarcodeWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.BARCODE);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
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
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testUrlConverter() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                UrlConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.URLCONVERTER);

                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(null, fileOut));

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

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testConverterRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.CONVERTER);

                File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                assertNotNull(webService.getOperation(), "Operation should have been initialized");
                webService.getOperation().setPages("1-5");
                webService.getOperation().setEmbedFonts(true);

                webService.getOperation().setPdfa(new PdfaType());
                webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testToolboxRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                ToolboxWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.TOOLBOX);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                MergeType mergeType = new MergeType();
                mergeType.setPage(1);
                mergeType.setSourceIsZip(false);
                mergeType.setMode(MergeModeType.AFTER_PAGE);

                // set merge file data
                mergeType.setData(new MergeFileDataType());
                mergeType.getData().setFormat(FileDataFormatType.PDF);

                try {
                    mergeType.getData().setValue(Files.readAllBytes(
                            testResources.getResource("integration/files/merge.pdf").toPath()));
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

                SecurityType securityType = new SecurityType();
                EncryptType encryptType = new EncryptType();
                EncryptType.Password password = new EncryptType.Password();
                password.setOpen("büro");
                encryptType.setPassword(password);
                securityType.setEncrypt(encryptType);
                webService.getOperation().add(securityType);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    @Disabled("this assumes, that 'test' actually is a certificate acronym in your webPDF server keystore.")
    public void testSignatureRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                SignatureWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.SIGNATURE);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
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
                signatureFileDataType.setValue(Files.readAllBytes(
                        testResources.getResource("integration/files/logo.png").toPath()));
                signatureImageType.setData(signatureFileDataType);
                webService.getOperation().getAdd().getAppearance().setImage(signatureImageType);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testPdfaRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                PdfaWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.PDFA);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
                webService.getOperation().setConvert(new PdfaType.Convert());
                webService.getOperation().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
                webService.getOperation().getConvert().setImageQuality(90);

                webService.getBilling().setUserName("John Doe");
                webService.getBilling().setApplicationName("webPDF Sample Application");
                webService.getBilling().setCustomerCode("ABC123");

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testOcrRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                OcrWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.OCR);

                File file = testResources.getResource("integration/files/ocr.png");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
                webService.getOperation().setLanguage(OcrLanguageType.ENG);
                webService.getOperation().setOutputFormat(OcrOutputType.PDF);
                webService.getOperation().setCheckResolution(false);
                webService.getOperation().setImageDpi(200);

                webService.getOperation().setPage(new OcrPageType());
                webService.getOperation().getPage().setHeight(210);
                webService.getOperation().getPage().setWidth(148);
                webService.getOperation().getPage().setMetrics(MetricsType.MM);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testBarcodeRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                BarcodeWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.BARCODE);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperation(),
                        "Operation should have been initialized");
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
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testUrlConverterRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                UrlConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.URLCONVERTER);

                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(null, fileOut));

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

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

}
