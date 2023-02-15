package net.webpdf.wsclient;

import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.testsuite.integration.certificate.GenericCertificate;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.*;
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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                webService.getOperationParameters().setPages("1-5");
                webService.getOperationParameters().setEmbedFonts(true);

                webService.getOperationParameters().setPdfa(new PdfaType());
                webService.getOperationParameters().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperationParameters().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperationParameters().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

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
                webService.getOperationParameters().add(mergeType);

                // add rotate operation to the toolbox operation list
                RotateType rotateType = new RotateType();
                rotateType.setPages("1-5");
                rotateType.setDegrees(90);
                webService.getOperationParameters().add(rotateType);

                DeleteType deleteType = new DeleteType();
                deleteType.setPages("5-8");
                webService.getOperationParameters().add(deleteType);

                SecurityType securityType = new SecurityType();
                EncryptType encryptType = new EncryptType();
                EncryptType.Password password = new EncryptType.Password();
                password.setOpen("büro");
                encryptType.setPassword(password);
                securityType.setEncrypt(encryptType);
                webService.getOperationParameters().add(securityType);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testSignature() {
        assertDoesNotThrow(() -> {
            try (Session<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                SignatureWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.SIGNATURE);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                GenericCertificate genericCertificate = new GenericCertificate("John Doe");

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                SignatureType.Add add = new SignatureType.Add();
                webService.getOperationParameters().setAdd(add);
                add.setSigner(new SignatureType.Add.Signer());

                KeyPairType keyPairType = new KeyPairType();
                CertificateFileDataType certificateFileDataType = new CertificateFileDataType();
                certificateFileDataType.setValue(genericCertificate.getCertificatesAsPEM());
                certificateFileDataType.setSource(FileDataSourceType.VALUE);
                keyPairType.setCertificate(certificateFileDataType);

                PrivateKeyFileDataType privateKeyFileDataType = new PrivateKeyFileDataType();
                privateKeyFileDataType.setValue(genericCertificate.getPrivateKeyAsPEM());
                privateKeyFileDataType.setSource(FileDataSourceType.VALUE);
                keyPairType.setPrivateKey(privateKeyFileDataType);

                add.getSigner().setKeyPair(keyPairType);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setConvert(new PdfaType.Convert());
                webService.getOperationParameters().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperationParameters().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
                webService.getOperationParameters().getConvert().setImageQuality(90);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setLanguage(OcrLanguageType.ENG);
                webService.getOperationParameters().setOutputFormat(OcrOutputType.PDF);
                webService.getOperationParameters().setCheckResolution(false);
                webService.getOperationParameters().setImageDpi(200);

                webService.getOperationParameters().setPage(new OcrPageType());
                webService.getOperationParameters().getPage().setHeight(210);
                webService.getOperationParameters().getPage().setWidth(148);
                webService.getOperationParameters().getPage().setMetrics(MetricsType.MM);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setAdd(new BarcodeType.Add());

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

                webService.getOperationParameters().getAdd().getQrcode().add(qrBarcodeType);

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

                webService.getOperationParameters().getAdd().getEan8().add(ean8BarcodeType);

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

                webService.setSourceDocument(new SoapWebServiceDocument(null, fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setUrl("https://www.webpdf.de");
                webService.getOperationParameters().setPage(new UrlConverterPageType());
                webService.getOperationParameters().getPage().setWidth(150);
                webService.getOperationParameters().getPage().setHeight(200);
                webService.getOperationParameters().getPage().setTop(0);
                webService.getOperationParameters().getPage().setLeft(0);
                webService.getOperationParameters().getPage().setRight(0);
                webService.getOperationParameters().getPage().setBottom(0);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));
                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                webService.getOperationParameters().setPages("1-5");
                webService.getOperationParameters().setEmbedFonts(true);

                webService.getOperationParameters().setPdfa(new PdfaType());
                webService.getOperationParameters().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperationParameters().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperationParameters().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

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
                webService.getOperationParameters().add(mergeType);

                // add rotate operation to the toolbox operation list
                RotateType rotateType = new RotateType();
                rotateType.setPages("1-5");
                rotateType.setDegrees(90);
                webService.getOperationParameters().add(rotateType);

                DeleteType deleteType = new DeleteType();
                deleteType.setPages("5-8");
                webService.getOperationParameters().add(deleteType);

                SecurityType securityType = new SecurityType();
                EncryptType encryptType = new EncryptType();
                EncryptType.Password password = new EncryptType.Password();
                password.setOpen("büro");
                encryptType.setPassword(password);
                securityType.setEncrypt(encryptType);
                webService.getOperationParameters().add(securityType);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testSignatureRemoteWSDL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {
                session.setUseLocalWsdl(false);
                SignatureWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.SIGNATURE);

                File file = testResources.getResource("integration/files/lorem-ipsum.pdf");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                GenericCertificate genericCertificate = new GenericCertificate("John Doe");

                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                SignatureType.Add add = new SignatureType.Add();
                webService.getOperationParameters().setAdd(add);
                add.setSigner(new SignatureType.Add.Signer());

                KeyPairType keyPairType = new KeyPairType();
                CertificateFileDataType certificateFileDataType = new CertificateFileDataType();
                certificateFileDataType.setValue(genericCertificate.getCertificatesAsPEM());
                certificateFileDataType.setSource(FileDataSourceType.VALUE);
                keyPairType.setCertificate(certificateFileDataType);

                PrivateKeyFileDataType privateKeyFileDataType = new PrivateKeyFileDataType();
                privateKeyFileDataType.setValue(genericCertificate.getPrivateKeyAsPEM());
                privateKeyFileDataType.setSource(FileDataSourceType.VALUE);
                keyPairType.setPrivateKey(privateKeyFileDataType);

                add.getSigner().setKeyPair(keyPairType);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setConvert(new PdfaType.Convert());
                webService.getOperationParameters().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperationParameters().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);
                webService.getOperationParameters().getConvert().setImageQuality(90);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setLanguage(OcrLanguageType.ENG);
                webService.getOperationParameters().setOutputFormat(OcrOutputType.PDF);
                webService.getOperationParameters().setCheckResolution(false);
                webService.getOperationParameters().setImageDpi(200);

                webService.getOperationParameters().setPage(new OcrPageType());
                webService.getOperationParameters().getPage().setHeight(210);
                webService.getOperationParameters().getPage().setWidth(148);
                webService.getOperationParameters().getPage().setMetrics(MetricsType.MM);

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

                webService.setSourceDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setAdd(new BarcodeType.Add());

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

                webService.getOperationParameters().getAdd().getQrcode().add(qrBarcodeType);

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

                webService.getOperationParameters().getAdd().getEan8().add(ean8BarcodeType);

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

                webService.setSourceDocument(new SoapWebServiceDocument(null, fileOut));

                assertNotNull(webService.getOperationParameters(),
                        "Operation should have been initialized");
                webService.getOperationParameters().setUrl("https://www.webpdf.de");
                webService.getOperationParameters().setPage(new UrlConverterPageType());
                webService.getOperationParameters().getPage().setWidth(150);
                webService.getOperationParameters().getPage().setHeight(200);
                webService.getOperationParameters().getPage().setTop(0);
                webService.getOperationParameters().getPage().setLeft(0);
                webService.getOperationParameters().getPage().setRight(0);
                webService.getOperationParameters().getPage().setBottom(0);

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

}
