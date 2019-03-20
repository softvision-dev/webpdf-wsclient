package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.SoapSession;
import net.webpdf.wsclient.testsuite.TestResources;
import org.apache.commons.io.FileUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

public class SoapCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(SoapCredentialsIntegrationTest.class);
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private void executeConverter(Session session) throws Exception {
        File file = testResources.getResource("integration/files/lorem-ipsum.docx");
        File fileOut = temporaryFolder.newFile();

        try (FileInputStream fileInputStream = new FileInputStream(file);
             FileOutputStream fileOutputStream = new FileOutputStream(fileOut)) {
            SoapDocument soapDocument = new SoapDocument(fileInputStream, fileOutputStream);

            ConverterWebService webService = WebServiceFactory.createInstance(session, WebServiceType.CONVERTER);

            webService.setDocument(soapDocument);

            webService.getOperation().setPages("1-5");
            webService.getOperation().setEmbedFonts(true);

            webService.getOperation().setPdfa(new PdfaType());
            webService.getOperation().getPdfa().setConvert(new PdfaType.Convert());
            webService.getOperation().getPdfa().getConvert().setLevel("3b");
            webService.getOperation().getPdfa().getConvert().setErrorReport(PdfaErrorReportType.MESSAGE);

            try (SoapDocument resultDocument = webService.process()) {
                Assert.assertNotNull(resultDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

    @Test
    public void testWithUserCredentialsInURL() throws Exception {
        try (SoapSession session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testResources.getArguments(true, false).buildServerUrl())) {
            executeConverter(session);
        }
    }

    @Test
    public void testWithUserCredentials() throws Exception {
        try (SoapSession session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testResources.getArguments().buildServerUrl())) {

            UsernamePasswordCredentials userCredentials = new UsernamePasswordCredentials("admin", "admin");
            session.setCredentials(userCredentials);

            executeConverter(session);
        }
    }

    @Test
    public void testWithSetOptions() throws Exception {
        File resFile = testResources.getResource("convert.xml");
        String xml = FileUtils.readFileToString(resFile, Charset.defaultCharset());

        try (SoapSession session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testResources.getArguments().buildServerUrl());
             StringReader stringReader = new StringReader(xml)) {
            ConverterWebService webService = WebServiceFactory.createInstance(session, new StreamSource(stringReader));

            File file = testResources.getResource("integration/files/lorem-ipsum.docx");
            File fileOut = temporaryFolder.newFile();

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertNotNull(soapDocument);
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

}
