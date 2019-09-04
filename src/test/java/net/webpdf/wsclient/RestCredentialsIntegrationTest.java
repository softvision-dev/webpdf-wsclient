package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.RestDocument;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.RestSession;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import net.webpdf.wsclient.testsuite.TestServer;
import org.apache.commons.io.FileUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

import static org.junit.Assert.assertNotNull;

public class RestCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(RestCredentialsIntegrationTest.class);
    @Rule
    public TestServer testServer = new TestServer();
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private void executeConverter(RestSession session) throws Exception {
        ConverterRestWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

        File file = testResources.getResource("integration/files/lorem-ipsum.docx");
        File fileOut = temporaryFolder.newFile();

        webService.setDocument(session.getDocumentManager().uploadDocument(file));

        assertNotNull("Operation should have been initialized", webService.getOperation());
        webService.getOperation().setPages("1-5");
        webService.getOperation().setEmbedFonts(true);

        webService.getOperation().setPdfa(new PdfaType());
        webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
        webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
        webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

        RestDocument restDocument = webService.process();
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
            session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
        }
        Assert.assertTrue(fileOut.exists());
    }

    @Test
    public void testWithUserCredentialsInURL() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(TestServer.ServerType.LOCAL, TestServer.ServerProtocol.HTTP, true))) {
            session.login();
            executeConverter(session);
        }
    }

    @Test
    public void testWithUserCredentials() throws Exception {
        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(TestServer.ServerType.LOCAL))) {

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

        try (RestSession session = SessionFactory.createInstance(WebServiceProtocol.REST,
                testServer.getServer(TestServer.ServerType.LOCAL));
             StringReader stringReader = new StringReader(json)) {
            session.login();

            ConverterRestWebService webService = WebServiceFactory.createInstance(session, new StreamSource(stringReader));

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(session.getDocumentManager().uploadDocument(file));

            RestDocument restDocument = webService.process();
            try (FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                session.getDocumentManager().downloadDocument(restDocument, fileOutputStream);
            }
            Assert.assertTrue(fileOut.exists());

        }
    }
}
