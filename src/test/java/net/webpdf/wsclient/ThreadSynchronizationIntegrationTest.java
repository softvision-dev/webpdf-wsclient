package net.webpdf.wsclient;

import net.webpdf.wsclient.openapi.OperationConvertPdfa;
import net.webpdf.wsclient.openapi.OperationPdfa;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadSynchronizationIntegrationTest {

    private final TestResources testResources = new TestResources(ThreadSynchronizationIntegrationTest.class);
    public TestServer testServer = new TestServer();

    @Test
    @IntegrationTest
    public void testRESTSessionSynchronization() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<File> result = new CopyOnWriteArrayList<>();
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)))) {
                executor.execute(() -> assertDoesNotThrow(() -> {
                    ConverterRestWebService<RestDocument> webService =
                            session.createWSInstance(WebServiceType.CONVERTER);
                    File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                    assertNotNull(file);
                    File sourceFile = testResources.getTempFolder().newFile();
                    FileUtils.copyFile(file, sourceFile);
                    File fileOut = testResources.getTempFolder().newFile();
                    RestDocument restDocument = session.getDocumentManager().uploadDocument(sourceFile);

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

                    restDocument = webService.process(restDocument);
                    try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                        restDocument.downloadDocument(fileOutputStream);
                    }
                    assertNotNull(restDocument.getDocumentFile(),
                            "Downloaded REST document is null");
                    assertEquals(FilenameUtils.removeExtension(sourceFile.getName()),
                            restDocument.getDocumentFile().getFileName());
                    assertTrue(fileOut.exists());
                    result.add(fileOut);
                }));
                executor.execute(() -> assertDoesNotThrow(() -> {
                    ConverterRestWebService<RestDocument> webService =
                            session.createWSInstance(WebServiceType.CONVERTER);
                    File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                    assertNotNull(file);
                    File sourceFile = testResources.getTempFolder().newFile();
                    FileUtils.copyFile(file, sourceFile);
                    File fileOut = testResources.getTempFolder().newFile();
                    RestDocument restDocument = session.getDocumentManager().uploadDocument(sourceFile);

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

                    restDocument = webService.process(restDocument);
                    try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                        restDocument.downloadDocument(fileOutputStream);
                    }
                    assertNotNull(restDocument,
                            "REST document could not be downloaded.");
                    assertNotNull(restDocument.getDocumentFile(),
                            "Downloaded REST document is null");
                    assertEquals(FilenameUtils.removeExtension(sourceFile.getName()),
                            restDocument.getDocumentFile().getFileName());
                    assertTrue(fileOut.exists());
                    result.add(fileOut);
                }));
                executor.execute(() -> assertDoesNotThrow(() -> {
                    ConverterRestWebService<RestDocument> webService =
                            session.createWSInstance(WebServiceType.CONVERTER);
                    File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                    assertNotNull(file);
                    File sourceFile = testResources.getTempFolder().newFile();
                    FileUtils.copyFile(file, sourceFile);
                    File fileOut = testResources.getTempFolder().newFile();
                    RestDocument restDocument = session.getDocumentManager().uploadDocument(sourceFile);

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

                    restDocument = webService.process(restDocument);
                    try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                        restDocument.downloadDocument(fileOutputStream);
                    }
                    assertNotNull(restDocument,
                            "REST document could not be downloaded.");
                    assertNotNull(restDocument.getDocumentFile(),
                            "Downloaded REST document is null");
                    assertEquals(FilenameUtils.removeExtension(sourceFile.getName()),
                            restDocument.getDocumentFile().getFileName());
                    assertTrue(fileOut.exists());
                    result.add(fileOut);
                }));
                executor.execute(() -> assertDoesNotThrow(() -> {
                    ConverterRestWebService<RestDocument> webService =
                            session.createWSInstance(WebServiceType.CONVERTER);
                    File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                    assertNotNull(file);
                    File sourceFile = testResources.getTempFolder().newFile();
                    FileUtils.copyFile(file, sourceFile);
                    File fileOut = testResources.getTempFolder().newFile();
                    RestDocument restDocument = session.getDocumentManager().uploadDocument(sourceFile);

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

                    restDocument = webService.process(restDocument);
                    try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                        restDocument.downloadDocument(fileOutputStream);
                    }
                    assertNotNull(restDocument,
                            "REST document could not be downloaded.");
                    assertNotNull(restDocument.getDocumentFile(),
                            "Downloaded REST document is null");
                    assertEquals(FilenameUtils.removeExtension(sourceFile.getName()),
                            restDocument.getDocumentFile().getFileName());
                    assertTrue(fileOut.exists());
                    result.add(fileOut);
                }));
                executor.shutdown();
                assertTrue(executor.awaitTermination(3, TimeUnit.MINUTES));
            }
        });
        assertEquals(4, result.size());
    }

}