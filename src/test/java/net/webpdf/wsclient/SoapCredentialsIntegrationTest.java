package net.webpdf.wsclient;

import net.webpdf.wsclient.session.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.documents.soap.SoapWebServiceDocument;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.testsuite.server.ServerProtocol;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.ConverterWebService;
import org.apache.commons.io.FileUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class SoapCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(SoapCredentialsIntegrationTest.class);
    public TestServer testServer = new TestServer();

    private void executeConverter(Session<SoapDocument> session) {
        File file = testResources.getResource("integration/files/lorem-ipsum.docx");
        File fileOut = testResources.getTempFolder().newFile();

        assertDoesNotThrow(() -> {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                SoapDocument soapDocument = new SoapWebServiceDocument(fileInputStream, fileOutputStream);

                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        WebServiceType.CONVERTER);

                webService.setDocument(soapDocument);

                assertNotNull(webService.getOperation(), "Operation should have been initialized");
                webService.getOperation().setPages("1-5");
                webService.getOperation().setEmbedFonts(true);

                webService.getOperation().setPdfa(new PdfaType());
                webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperation().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument resultDocument = webService.process()) {
                    assertNotNull(resultDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testWithUserCredentialsInURL() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    WebServiceProtocol.SOAP, testServer.getServer(
                            ServerType.LOCAL, ServerProtocol.HTTP,
                            true))) {
                executeConverter(session);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testWithUserCredentials() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL))) {

                UsernamePasswordCredentials userCredentials = new UsernamePasswordCredentials(
                        testServer.getLocalUser(), testServer.getLocalPassword());
                session.setCredentials(userCredentials);

                executeConverter(session);
            }
        });
    }

    @Test
    @IntegrationTest
    public void testWithSetOptions() {
        assertDoesNotThrow(() -> {
            File resFile = testResources.getResource("convert.xml");
            String xml = FileUtils.readFileToString(resFile, Charset.defaultCharset());

            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    WebServiceProtocol.SOAP,
                    testServer.getServer(ServerType.LOCAL));
                 StringReader stringReader = new StringReader(xml)) {
                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        new StreamSource(stringReader));

                File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                File fileOut = testResources.getTempFolder().newFile();

                webService.setDocument(new SoapWebServiceDocument(file.toURI(), fileOut));

                try (SoapDocument soapDocument = webService.process()) {
                    assertNotNull(soapDocument);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

}
