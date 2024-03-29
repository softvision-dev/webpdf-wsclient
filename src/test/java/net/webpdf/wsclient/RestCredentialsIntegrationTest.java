package net.webpdf.wsclient;

import net.webpdf.wsclient.openapi.OperationConvertPdfa;
import net.webpdf.wsclient.openapi.OperationPdfa;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class RestCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(RestCredentialsIntegrationTest.class);
    public TestServer testServer = TestServer.getInstance();

    private void executeConverter(RestSession<RestDocument> session) {
        assertDoesNotThrow(() -> {
            ConverterRestWebService<RestDocument> webService =
                    session.createWebServiceInstance(WebServiceType.CONVERTER);
            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();
            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            webService.getOperationParameters().setPages("1-5");
            webService.getOperationParameters().setEmbedFonts(true);
            OperationPdfa pdfa = new OperationPdfa();
            webService.getOperationParameters().setPdfa(pdfa);
            OperationConvertPdfa convertPdfa = new OperationConvertPdfa();
            pdfa.setConvert(convertPdfa);
            convertPdfa.setLevel(OperationConvertPdfa.LevelEnum._3B);
            convertPdfa.setErrorReport(OperationConvertPdfa.ErrorReportEnum.MESSAGE);
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                webService.process(session.uploadDocument(file))
                        .downloadDocument(fileOutputStream);
            }
            assertTrue(fileOut.exists());
        });
    }

    @Test
    @IntegrationTest
    public void testWithUserCredentials() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(testServer.getLocalAdminName(), testServer.getLocalAdminPassword()))) {
                executeConverter(session);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testWithSetOptions() {
        assertDoesNotThrow(() -> {
            File resFile = testResources.getResource("convert.json");
            String json = FileUtils.readFileToString(resFile, Charset.defaultCharset());
            try (RestSession<RestDocument> session = SessionFactory.createInstance(
                    new SessionContext(
                            WebServiceProtocol.REST,
                            testServer.getServer(ServerType.LOCAL)));
                 StringReader stringReader = new StringReader(json)) {
                ConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        new StreamSource(stringReader));
                File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                File fileOut = testResources.getTempFolder().newFile();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    webService.process(session.getDocumentManager().uploadDocument(file))
                            .downloadDocument(fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

}