package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.ServerProtocol;
import net.webpdf.wsclient.testsuite.ServerType;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.rest.ConverterRestWebService;
import org.apache.commons.io.FileUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

import static net.webpdf.wsclient.testsuite.TestResources.getDocumentID;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(RestCredentialsIntegrationTest.class);
    public TestServer testServer = new TestServer();

    private void executeConverter(RestSession<RestDocument> session) throws Exception {
        ConverterRestWebService<RestDocument> webService =
                WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

        File file = testResources.getResource("integration/files/lorem-ipsum.docx");
        File fileOut = testResources.getTempFolder().newFile();

        webService.setDocument(session.getDocumentManager().uploadDocument(file));

        assertNotNull(webService.getOperation(), "Operation should have been initialized");
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
        assertTrue(fileOut.exists());
    }

    @Test
    public void testWithUserCredentialsInURL() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL,
                        ServerProtocol.HTTP, true))) {
            session.login();
            executeConverter(session);
        }
    }

    @Test
    public void testWithUserCredentials() throws Exception {
        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL))) {

            UsernamePasswordCredentials userCredentials = new UsernamePasswordCredentials(
                    testServer.getLocalUser(), testServer.getLocalPassword());
            session.setCredentials(userCredentials);

            session.login();
            executeConverter(session);
        }
    }

    @Test
    public void testWithSetOptions() throws Exception {
        File resFile = testResources.getResource("convert.json");
        String json = FileUtils.readFileToString(resFile, Charset.defaultCharset());

        try (RestSession<RestDocument> session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(ServerType.LOCAL));
             StringReader stringReader = new StringReader(json)) {
            session.login();

            ConverterRestWebService<RestDocument> webService = WebServiceFactory.createInstance(session,
                    new StreamSource(stringReader));

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = testResources.getTempFolder().newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(getDocumentID(restDocument), fileOutputStream);
            }
            assertTrue(fileOut.exists());
        }
    }

}
