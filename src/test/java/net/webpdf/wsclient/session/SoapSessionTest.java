package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class SoapSessionTest {

    private static final String SOME_URL = "http://someInvalidURL.de";
    private static final String SOME_CREDENTIALS_URL = "http://username:password@someInvalidURL.de";

    @Test
    public void testCreateSoapSession() throws Exception {
        URL url = new URL(SOME_URL);
        try (SoapSession soapSession = SessionFactory.createInstance(WebServiceProtocol.SOAP, url)) {
            assertNotNull("SOAPSession should have been initialized.", soapSession);
            assertTrue("SOAPSession should use local wsdl by default.", soapSession.isUseLocalWsdl());
            soapSession.setUseLocalWsdl(false);
            assertFalse("SOAPSession should not be using local wsdl after modification.", soapSession.isUseLocalWsdl());
            assertEquals("DataFormat should have been XML.", DataFormat.XML, soapSession.getDataFormat());
            assertEquals("MimeType should have been xml.", DataFormat.XML.getMimeType(), soapSession.getDataFormat().getMimeType());
            assertEquals("WebserviceProtocol should have been SOAP.", WebServiceProtocol.SOAP, soapSession.getWebServiceProtocol());
            assertNull("Credentials should not have been initialized.", soapSession.getCredentials());
            soapSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
            assertEquals("Credentials should define usr for authentication.", "usr", soapSession.getCredentials().getUserPrincipal().getName());
            assertEquals("Credentials should define pwd as the authentication password.", "pwd", soapSession.getCredentials().getPassword());

            assertEquals("URI subpath should have been created.", SOME_URL + "/soap/sub", soapSession.getURI("sub").toString());
        }
    }

    @Test
    public void testCreateCredentialsSoapSession() throws Exception {
        URL url = new URL(SOME_CREDENTIALS_URL);
        try (SoapSession soapSession = SessionFactory.createInstance(WebServiceProtocol.SOAP, url)) {
            assertNotNull("SOAPSession should have been initialized.", soapSession);
            assertTrue("SOAPSession should use local wsdl by default.", soapSession.isUseLocalWsdl());
            soapSession.setUseLocalWsdl(false);
            assertFalse("SOAPSession should not be using local wsdl after modification.", soapSession.isUseLocalWsdl());
            assertEquals("DataFormat should have been XML.", DataFormat.XML, soapSession.getDataFormat());
            assertEquals("MimeType should have been xml.", DataFormat.XML.getMimeType(), soapSession.getDataFormat().getMimeType());
            assertEquals("WebserviceProtocol should have been SOAP.", WebServiceProtocol.SOAP, soapSession.getWebServiceProtocol());
            assertNotNull(soapSession.getCredentials());
            assertEquals("Credentials should define username for authentication.", "username", soapSession.getCredentials().getUserPrincipal().getName());
            assertEquals("Credentials should define password as the authentication password.", "password", soapSession.getCredentials().getPassword());
            soapSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
            assertEquals("Credentials should define usr for authentication.", "usr", soapSession.getCredentials().getUserPrincipal().getName());
            assertEquals("Credentials should define pwd as the authentication password.", "pwd", soapSession.getCredentials().getPassword());

            assertEquals("URI subpath should have been created.", SOME_URL + "/soap/sub", soapSession.getURI("sub").toString());
        }
    }
}
