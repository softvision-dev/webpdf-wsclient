package net.webpdf.wsclient;

import net.webpdf.wsclient.openapi.OperationConvertPdfa;
import net.webpdf.wsclient.openapi.OperationPdfa;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.server.ServerProtocol;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
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

import static net.webpdf.wsclient.testsuite.io.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.*;

public class RestCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(RestCredentialsIntegrationTest.class);
    public TestServer testServer = new TestServer();

    private void executeConverter(RestSession<RestDocument> session) {
        assertDoesNotThrow(() -> {
            ConverterRestWebService<RestDocument> webService =
                    WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

            assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
            webService.getOperationParameters().setPages("1-5");
            webService.getOperationParameters().setEmbedFonts(true);

            OperationPdfa pdfa = new OperationPdfa();
            webService.getOperationParameters().setPdfa(pdfa);
            OperationConvertPdfa convertPdfa = new OperationConvertPdfa();
            pdfa.setConvert(convertPdfa);
            convertPdfa.setLevel(OperationConvertPdfa.LevelEnum._3B);
            convertPdfa.setErrorReport(OperationConvertPdfa.ErrorReportEnum.MESSAGE);

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        });
    }

    @Test
    @IntegrationTest
    public void testWithUserCredentialsInURL() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL,
                            ServerProtocol.HTTP, true))) {
                executeConverter(session);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testWithUserCredentials() {
        assertDoesNotThrow(() -> {
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL),
                    new UserAuthProvider(testServer.getLocalUser(), testServer.getLocalPassword()))) {
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
            try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                    testServer.getServer(ServerType.LOCAL));
                 StringReader stringReader = new StringReader(json)) {
                ConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                        new StreamSource(stringReader));

                File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setSourceDocument(session.getDocumentManager().uploadDocument(file));

                RestDocument restDocument = webService.process();
                try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                    session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
                }
                assertTrue(fileOut.exists());
            }
        });
    }

}