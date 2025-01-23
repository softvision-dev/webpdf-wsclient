package net.webpdf.wsclient.documents;

import net.webpdf.wsclient.exception.ServerResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.schema.beans.HistoryEntry;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.documents.RestWebServiceDocument;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;
import net.webpdf.wsclient.webservice.rest.ToolboxRestWebService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentManagerIntegrationTest {

    private final TestResources testResources = new TestResources(DocumentManagerIntegrationTest.class);
    public TestServer testServer = TestServer.getInstance();

    @Test
    @IntegrationTest
    public void testHandleDocument() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("test.pdf");
            File targetFile = testResources.getTempFolder().newFile();
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)));
                 OutputStream outputStream = Files.newOutputStream(targetFile.toPath())
            ) {
                assertNotNull(session,
                        "Valid session should have been created.");
                RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document,
                        "Valid document should have been returned.");
                assertNotNull(document.getDocumentId());
                document = session.getDocumentManager().getDocument(document.getDocumentId());
                assertNotNull(document,
                        "Valid document should have been returned.");
                assertNotNull(document.getDocumentId());
                document.downloadDocument(outputStream);
                assertTrue(FileUtils.contentEquals(sourceFile, targetFile),
                        "The content of the uploaded and the downloaded document should have been equal.");
                List<RestDocument> fileList = session.getDocumentManager().getDocuments();
                assertEquals(1, fileList.size(),
                        "file list should contain 1 document.");
                assertNotNull(document.getDocumentId());
                document.deleteDocument();
                fileList = session.getDocumentManager().getDocuments();
                assertTrue(fileList.isEmpty(),
                        "file list should be empty.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentRename() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("test.pdf");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session,
                        "Valid session should have been created.");
                RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document,
                        "Valid document should have been returned.");
                assertNotNull(document.getDocumentFile(),
                        "Valid document file should have been contained.");
                assertEquals("test",
                        document.getDocumentFile().getFileName(),
                        "Filename should be test");
                assertNotNull(document.getDocumentId());
                document.renameDocument("new");
                assertNotNull(document,
                        "Valid document should have been returned.");
                assertNotNull(document.getDocumentFile(),
                        "Valid document file should have been contained.");
                assertEquals("new", document.getDocumentFile().getFileName(),
                        "Filename should be new");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentList() {
        assertDoesNotThrow(() -> {
            File sourceFile1 = testResources.getResource("test.pdf");
            File sourceFile2 = testResources.getResource("logo.png");
            File sourceFile3 = testResources.getResource("lorem-ipsum.txt");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session,
                        "Valid session should have been created.");
                RestDocument document = session.getDocumentManager().uploadDocument(sourceFile1);
                assertNotNull(document,
                        "Valid document should have been returned.");
                document = session.getDocumentManager().uploadDocument(sourceFile2);
                assertNotNull(document,
                        "Valid document should have been returned.");
                document = session.getDocumentManager().uploadDocument(sourceFile3);
                assertNotNull(document,
                        "Valid document should have been returned.");
                List<RestDocument> fileList = session.getDocumentManager().getDocuments();
                assertEquals(3, fileList.size(),
                        "file list should contain 3 documents.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentHistory() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("logo.png");
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session,
                        "Valid session should have been created.");
                session.getDocumentManager().setDocumentHistoryActive(true);
                RestDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document,
                        "Valid document should have been returned.");
                assertNotNull(document.getDocumentFile(),
                        "Valid document file should have been contained.");
                List<HistoryEntry> historyList = session.getDocumentManager()
                        .getDocumentHistory(document.getDocumentId());
                assertEquals(1, historyList.size(),
                        "history list should contain 1 element.");
                HistoryEntry historyEntry = historyList.get(historyList.size() - 1);
                assertEquals("", historyEntry.getOperation(),
                        "history operation should be \"\".");
                historyEntry.setOperation("File uploaded");
                assertNotNull(document,
                        "Valid document should have been returned.");
                historyEntry = session.getDocumentManager()
                        .updateDocumentHistory(document.getDocumentId(), historyEntry);
                assertNotNull(historyEntry,
                        "History entry should have been updated.");
                assertEquals("File uploaded", historyEntry.getOperation(),
                        "history operation should be changed to \"File uploaded\".");

                ConverterRestWebService<RestDocument> webService =
                        WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);
                webService.process(document);
                historyList = session.getDocumentManager().getDocumentHistory(document.getDocumentId());
                assertEquals(2, historyList.size(), "history list should contain 2 elements.");

                historyEntry = historyList.get(historyList.size() - 1);
                historyEntry.setOperation("File converted");
                historyEntry = session.getDocumentManager()
                        .updateDocumentHistory(document.getDocumentId(), historyEntry);
                assertNotNull(historyEntry,
                        "History entry should have been updated.");
                assertEquals("File converted", historyEntry.getOperation(),
                        "history operation should be changed to \"File converted\".");
                assertEquals("application/pdf", document.getDocumentFile().getMimeType(),
                        "Filetype should be application/pdf");

                historyEntry = historyList.get(0);
                historyEntry.setActive(true);
                session.getDocumentManager().updateDocumentHistory(document.getDocumentId(), historyEntry);
                assertEquals("image/png", document.getDocumentFile().getMimeType(),
                        "Filetype should be image/png");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testHandleDocumentByID() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("test.pdf");
            File targetFile = testResources.getTempFolder().newFile();
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)));
                 OutputStream outputStream = Files.newOutputStream(targetFile.toPath())
            ) {
                assertNotNull(session, "Valid session should have been created.");
                RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document, "Valid document should have been returned.");
                document.downloadDocument(outputStream);
                assertTrue(FileUtils.contentEquals(sourceFile, targetFile),
                        "The content of the uploaded and the downloaded document should have been equal.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentSecurityTextPassword() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("password.pdf");
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session, "Valid session should have been created.");
                RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document, "Valid document should have been returned.");
                assertNotNull(document.getDocumentFile().getError(), "document error should be set.");
                assertEquals(-5009, document.getDocumentFile().getError().getErrorCode(), "error code should be -5009");

                PdfPasswordType passwordType = new PdfPasswordType();
                passwordType.setOpen("a");

                document = (RestWebServiceDocument) document.updateDocumentSecurity(passwordType);
                assertNotNull(document, "Valid document should have been returned.");
                assertNotNull(document.getDocumentFile().getError(), "document error should be set.");
                assertEquals(0, document.getDocumentFile().getError().getErrorCode(), "error code should be 0");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentSecurityCertificate() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("protected_certificate.pdf");
            File certificateFile = testResources.getResource("heinz_mustermann_certificate.pem");
            File privateKeyFile = testResources.getResource("heinz_mustermann_private.pem");
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session, "Valid session should have been created.");
                RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document, "Valid document should have been returned.");
                assertNotNull(document.getDocumentFile().getError(), "document error should be set.");
                assertEquals(-5055, document.getDocumentFile().getError().getErrorCode(), "error code should be -5055");

                PdfPasswordType passwordType = new PdfPasswordType();
                KeyPairType keyPairType = new KeyPairType();
                CertificateFileDataType certificateFileDataType = new CertificateFileDataType();
                certificateFileDataType.setSource(FileDataSourceType.VALUE);
                certificateFileDataType.setValue(FileUtils.readFileToString(certificateFile, StandardCharsets.UTF_8));
                keyPairType.setCertificate(certificateFileDataType);
                PrivateKeyFileDataType privateKeyFileDataType = new PrivateKeyFileDataType();
                privateKeyFileDataType.setSource(FileDataSourceType.VALUE);
                privateKeyFileDataType.setValue(FileUtils.readFileToString(privateKeyFile, StandardCharsets.UTF_8));
                privateKeyFileDataType.setPassword("geheim");
                keyPairType.setPrivateKey(privateKeyFileDataType);
                passwordType.setKeyPair(keyPairType);

                document = (RestWebServiceDocument) document.updateDocumentSecurity(passwordType);
                assertNotNull(document, "Valid document should have been returned.");
                assertNotNull(document.getDocumentFile().getError(), "document error should be set.");
                assertEquals(0, document.getDocumentFile().getError().getErrorCode(), "error code should be 0");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentListFromSession() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("test.pdf");
            RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword())
            );
            assertNotNull(session, "Valid session should have been created.");
            RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
            assertNotNull(document, "Valid document should have been returned.");
            List<RestWebServiceDocument> fileList = session.getDocumentManager().getDocuments();
            assertEquals(1, fileList.size(), "file list should contain 1 document.");

            RestWebServiceSession resumedSession = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(
                            testServer.getLocalAdminName(), testServer.getLocalAdminPassword(), session.getAuthProvider().provide(session)
                    )
            );
            resumedSession.getAuthProvider().refresh(resumedSession);
            fileList = resumedSession.getDocumentManager().getDocuments();
            assertEquals(0, fileList.size(), "file list should contain 0 documents.");
            fileList = resumedSession.getDocumentManager().synchronize();
            assertEquals(1, fileList.size(), "file list should contain 1 document.");
            resumedSession.close();
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentPasswordHandling() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("test.pdf");
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session, "Valid session should have been created.");
                RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document, "Valid document should have been returned.");

                String openPassword = "open";
                String permissionPassword = "permission";

                // encrypt document
                OperationPasswordEncrypt password = new OperationPasswordEncrypt();
                password.setOpen(openPassword);
                password.setPermission(permissionPassword);
                OperationEncrypt encryptType = new OperationEncrypt();
                encryptType.setPassword(password);
                OperationToolboxSecuritySecurity securityType = new OperationToolboxSecuritySecurity();
                securityType.setEncrypt(encryptType);
                OperationBaseToolbox baseToolbox = new OperationBaseToolbox();
                baseToolbox.setSecurity(securityType);
                List<OperationBaseToolbox> parameters = new ArrayList<>();
                parameters.add(baseToolbox);
                ToolboxRestWebService<RestDocument> encryptWebService =
                        WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);
                encryptWebService.setOperationParameters(parameters);

                RestDocument encryptedDocument = encryptWebService.process(document);
                assertNotNull(encryptedDocument.getDocumentFile().getError(), "The document error should be set.");
                assertEquals(0, encryptedDocument.getDocumentFile().getError().getErrorCode(),
                        "The document password should be set.");
                assertNotNull(encryptedDocument.getDocumentFile().getMetadata(), "The metadata should be set.");
                assertInstanceOf(DocumentMetadataPdf.class, encryptedDocument.getDocumentFile().getMetadata());
                DocumentMetadataPdf documentMetadataPdf = (DocumentMetadataPdf) encryptedDocument.getDocumentFile().getMetadata();
                assertNotNull(documentMetadataPdf.getInformation(), "The metadata information should be readable.");

                // rotate pages with initially set password
                OperationToolboxRotateRotate operationToolboxRotate = new OperationToolboxRotateRotate();
                operationToolboxRotate.setDegrees(90);
                baseToolbox = new OperationBaseToolbox();
                baseToolbox.setRotate(operationToolboxRotate);
                parameters = new ArrayList<>();
                parameters.add(baseToolbox);
                ToolboxRestWebService<RestDocument> rotateWebService = WebServiceFactory.createInstance(session, WebServiceType.TOOLBOX);
                rotateWebService.setOperationParameters(parameters);
                rotateWebService.process(encryptedDocument);

                // set the wrong password
                PdfPasswordType wrongPasswordType = new PdfPasswordType();
                wrongPasswordType.setOpen("wrong");
                final RestDocument updatedLockedDocument = encryptedDocument.updateDocumentSecurity(wrongPasswordType);
                assertNotNull(updatedLockedDocument.getDocumentFile().getError(), "The document error should be set.");
                assertEquals(-5008, updatedLockedDocument.getDocumentFile().getError().getErrorCode(),
                        "The document password should be wrong.");
                assertNull(updatedLockedDocument.getDocumentFile().getMetadata(), "The metadata should not be set.");

                // rotate pages with the wrong password
                assertThrows(ServerResultException.class, () -> rotateWebService.process(updatedLockedDocument));

                // rotate pages with the correct temporary password
                OperationPdfPassword correctOperationPdfPassword = new OperationPdfPassword();
                correctOperationPdfPassword.setOpen(openPassword);
                correctOperationPdfPassword.setPermission(permissionPassword);
                rotateWebService.setPassword(correctOperationPdfPassword);

                // set the correct password
                PdfPasswordType correctPasswordType = new PdfPasswordType();
                correctPasswordType.setOpen(openPassword);
                correctPasswordType.setPermission(permissionPassword);
                RestDocument updatedOpenedDocument = encryptedDocument.updateDocumentSecurity(correctPasswordType);

                // rotate pages with the correct password
                rotateWebService.setPassword(null);
                rotateWebService.process(updatedOpenedDocument);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentInfo() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("form.pdf");
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session, "Valid session should have been created.");
                RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document, "Valid document should have been returned.");

                DocumentInfoForm documentInfo = (DocumentInfoForm) document.getDocumentInfo(DocumentInfoType.FORM);
                assertNotNull(documentInfo, "Form info should have been fetched.");
                assertEquals(DocumentInfoType.FORM, documentInfo.getInfoType(), "Info type should be form.");
                assertFalse(new String(documentInfo.getValue(), Charset.defaultCharset()).isEmpty(), "There should be a value.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentExtractAll() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("files.zip");
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session, "Valid session should have been created.");
                RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document.getDocumentFile());
                DocumentFile documentFile = document.getDocumentFile();
                assertInstanceOf(DocumentMetadataArchive.class, documentFile.getMetadata(), "The metadata should be set.");
                DocumentMetadataArchive documentMetadataArchive = (DocumentMetadataArchive) documentFile.getMetadata();
                assertNotNull(documentMetadataArchive.getFiles());
                assertEquals(3, documentMetadataArchive.getFiles().size());
                assertNotNull(document, "Valid document should have been returned.");
                List<RestWebServiceDocument> unzippedFiles = document.extractDocument(new DocumentFileExtract());
                assertNotNull(unzippedFiles, "Valid documents should have been returned.");
                assertEquals(3, unzippedFiles.size(), "There should be 3 result documents.");
            }
        });
    }

    @Test
    @IntegrationTest
    public void testDocumentExtractWithFilter() {
        assertDoesNotThrow(() -> {
            File sourceFile = testResources.getResource("files.zip");
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session, "Valid session should have been created.");
                RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                assertNotNull(document, "Valid document should have been returned.");

                assertNotNull(document.getDocumentFile());
                DocumentFile documentFile = document.getDocumentFile();
                assertInstanceOf(DocumentMetadataArchive.class, documentFile.getMetadata(), "The metadata should be set.");
                DocumentMetadataArchive documentMetadataArchive = (DocumentMetadataArchive) documentFile.getMetadata();
                assertNotNull(documentMetadataArchive.getFiles());
                assertEquals(3, documentMetadataArchive.getFiles().size());

                DocumentFileFilterRule fileFilterRule = new DocumentFileFilterRule();
                fileFilterRule.setRuleType(DocumentFileFilterType.GLOB);
                fileFilterRule.setRulePattern("logo.png");

                DocumentFileFilter fileFilter = new DocumentFileFilter();
                fileFilter.addIncludeRulesItem(fileFilterRule);

                DocumentFileExtract fileExtract = new DocumentFileExtract();
                fileExtract.setFileFilter(fileFilter);

                List<RestWebServiceDocument> unzippedFiles = document.extractDocument(fileExtract);
                assertNotNull(unzippedFiles, "Valid documents should have been returned.");
                assertEquals(1, unzippedFiles.size(), "There should be 1 result document.");

                RestWebServiceDocument restWebServiceDocument = unzippedFiles.get(0);
                assertNotNull(restWebServiceDocument.getDocumentFile());
                documentFile = restWebServiceDocument.getDocumentFile();
                assertInstanceOf(DocumentMetadataImage.class, documentFile.getMetadata(), "The metadata should be set.");
                DocumentMetadataImage documentMetadataImage = (DocumentMetadataImage) documentFile.getMetadata();
                assertNotNull(documentMetadataImage.getImages());
                assertEquals(1, documentMetadataImage.getImages().size());
                for (DocumentMetadataImageEntry documentMetadataImageEntry : documentMetadataImage.getImages()) {
                    assertNotNull(documentMetadataImageEntry);
                    assertEquals(68, documentMetadataImageEntry.getHeight());
                }
            }
        });
    }

    @ParameterizedTest
    @IntegrationTest
    @CsvSource(delimiter = '|', value = {
            "true",
            "false"
    })
    public void testDocumentCompress(boolean storeDocument) {
        assertDoesNotThrow(() -> {
            File[] sourceFiles = {
                    testResources.getResource("test.pdf"),
                    testResources.getResource("logo.png"),
                    testResources.getResource("lorem-ipsum.txt")
            };
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, testServer.getServer(ServerType.LOCAL)))) {
                assertNotNull(session, "Valid session should have been created.");

                List<String> documentIdList = new ArrayList<>();
                for (File sourceFile : sourceFiles) {
                    RestWebServiceDocument document = session.getDocumentManager().uploadDocument(sourceFile);
                    assertNotNull(document, "Valid document should have been returned.");
                    documentIdList.add(document.getDocumentId());
                }

                DocumentFileCompress fileCompress = new DocumentFileCompress();
                fileCompress.setStoreArchive(storeDocument);
                fileCompress.setDocumentIdList(documentIdList);
                fileCompress.setArchiveFileName("archive");

                if (!storeDocument) {
                    ServerResultException serverResultException = assertThrows(ServerResultException.class, () -> session.getDocumentManager().compressDocuments(fileCompress));
                    assertNotNull(serverResultException, "The server result should have been returned.");
                    assertEquals(-50, serverResultException.getErrorCode());
                    return;
                }
                RestDocument archive = session.getDocumentManager().compressDocuments(fileCompress);
                assertNotNull(archive, "Valid document should have been returned.");
                assertEquals(
                        "application/zip", archive.getDocumentFile().getMimeType(),
                        "The result document should be a zip file"
                );

                DocumentFile documentFile = archive.getDocumentFile();
                assertInstanceOf(DocumentMetadataArchive.class, documentFile.getMetadata(), "The metadata should be set.");
                DocumentMetadataArchive documentMetadataArchive = (DocumentMetadataArchive) documentFile.getMetadata();
                assertNotNull(documentMetadataArchive.getFiles());
                assertEquals(3, documentMetadataArchive.getFiles().size());
            }
        });
    }
}
