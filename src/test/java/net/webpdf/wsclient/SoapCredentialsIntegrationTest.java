package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.schema.operation.PdfaErrorReportType;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.SoapSession;
import net.webpdf.wsclient.ssl.SelfSignedCertSSLSocketFactory;
import net.webpdf.wsclient.testsuite.TestResources;
import org.apache.commons.io.FileUtils;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;

public class SoapCredentialsIntegrationTest {

    private final TestResources testResources = new TestResources(SoapCredentialsIntegrationTest.class);

    private void executeConverter(Session session) throws Exception {
        File file = new File("./files/lorem-ipsum.docx");
        File fileOut = new File("./result/converter_soap.pdf");
        FileUtils.deleteQuietly(fileOut);

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
    public void testWithSSL() throws Exception {

        HttpsURLConnection.setDefaultSSLSocketFactory(SelfSignedCertSSLSocketFactory.getDefault());

        try (SoapSession session = SessionFactory.createInstance(WebServiceProtocol.SOAP,
                testResources.getArguments(false, true).setProtocol("https").buildServerUrl())) {
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

            File file = new File("./files/lorem-ipsum.docx");
            File fileOut = new File("./result/converter_soap.pdf");
            FileUtils.deleteQuietly(fileOut);

            webService.setDocument(new SoapDocument(file.toURI(), fileOut));

            try (SoapDocument soapDocument = webService.process()) {
                Assert.assertTrue(fileOut.exists());
            }
        }
    }

}
