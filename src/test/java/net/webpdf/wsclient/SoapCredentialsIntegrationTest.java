package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaLevelType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.testsuite.integration.annotations.IntegrationTest;
import net.webpdf.wsclient.testsuite.io.TestResources;
import net.webpdf.wsclient.testsuite.server.ServerType;
import net.webpdf.wsclient.testsuite.server.TestServer;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.ConverterWebService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.*;

public class SoapCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(SoapCredentialsIntegrationTest.class);
    public TestServer testServer = TestServer.getInstance();

    private void executeConverter(SoapSession<SoapDocument> session) {
        File file = testResources.getResource("integration/files/lorem-ipsum.docx");
        File fileOut = testResources.getTempFolder().newFile();

        assertDoesNotThrow(() -> {
            try (FileInputStream fileInputStream = new FileInputStream(file);
                 FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
                ConverterWebService<SoapDocument> webService =
                        session.createWebServiceInstance(WebServiceType.CONVERTER);

                assertNotNull(webService.getOperationParameters(), "Operation should have been initialized");
                webService.getOperationParameters().setPages("1-5");
                webService.getOperationParameters().setEmbedFonts(true);

                webService.getOperationParameters().setPdfa(new PdfaType());
                webService.getOperationParameters().getPdfa().setConvert(new PdfaType.Convert());
                webService.getOperationParameters().getPdfa().getConvert().setLevel(PdfaLevelType.LEVEL_3B);
                webService.getOperationParameters().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

                try (SoapDocument resultDocument = webService.process(
                        session.createDocument(fileInputStream))) {
                    resultDocument.writeResult(fileOutputStream);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

    @Test
    @IntegrationTest
    public void testWithUserCredentials() {
        assertDoesNotThrow(() -> {
            try (SoapSession<SoapDocument> session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)),
                    new UserAuthProvider(
                            testServer.getLocalAdminName(), testServer.getLocalAdminPassword()))) {
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
                    new SessionContext(WebServiceProtocol.SOAP,
                            testServer.getServer(ServerType.LOCAL)));
                 StringReader stringReader = new StringReader(xml)) {
                ConverterWebService<SoapDocument> webService = WebServiceFactory.createInstance(session,
                        new StreamSource(stringReader));

                File file = testResources.getResource("integration/files/lorem-ipsum.docx");
                File fileOut = testResources.getTempFolder().newFile();

                try (SoapDocument soapDocument = webService.process(
                        session.createDocument(file.toURI()));
                     OutputStream outputStream = new FileOutputStream(fileOut)) {
                    soapDocument.writeResult(outputStream);
                    assertTrue(fileOut.exists());
                }
            }
        });
    }

}