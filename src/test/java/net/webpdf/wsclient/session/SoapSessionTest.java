package net.webpdf.wsclient.session;

import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.session.soap.SoapWebServiceSession;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class SoapSessionTest {

    private static final String SOME_URL = "http://someInvalidURL.de";
    private static final String SOME_CREDENTIALS_URL = "http://username:password@someInvalidURL.de";

    @Test
    public void testCreateSoapSession() {
        assertDoesNotThrow(() -> {
            URL url = new URL(SOME_URL);
            try (SoapWebServiceSession soapSession =
                         SessionFactory.createInstance(WebServiceProtocol.SOAP, url)) {
                assertNotNull(soapSession,
                        "SOAPSession should have been initialized.");
                assertTrue(soapSession.isUseLocalWsdl(),
                        "SOAPSession should use local wsdl by default.");
                soapSession.setUseLocalWsdl(false);
                assertFalse(soapSession.isUseLocalWsdl(),
                        "SOAPSession should not be using local wsdl after modification.");
                assertNotNull(soapSession.getDataFormat(),
                        "DataFormat should have been initialized.");
                assertEquals(DataFormat.XML, soapSession.getDataFormat(),
                        "DataFormat should have been XML.");
                assertEquals(DataFormat.XML.getMimeType(), soapSession.getDataFormat().getMimeType(),
                        "MimeType should have been xml.");
                assertEquals(WebServiceProtocol.SOAP, soapSession.getWebServiceProtocol(),
                        "WebserviceProtocol should have been SOAP.");
                assertNull(soapSession.getCredentials(),
                        "Credentials should not have been initialized.");
                soapSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
                assertEquals("usr", soapSession.getCredentials().getUserPrincipal().getName(),
                        "Credentials should define usr for authentication.");
                assertEquals("pwd", soapSession.getCredentials().getPassword(),
                        "Credentials should define pwd as the authentication password.");

                assertEquals(SOME_URL + "/soap/sub", soapSession.getURI("sub").toString(),
                        "URI subpath should have been created.");
            }
        });
    }

    @Test
    public void testCreateCredentialsSoapSession() {
        assertDoesNotThrow(() -> {
            URL url = new URL(SOME_CREDENTIALS_URL);
            try (SoapWebServiceSession soapSession =
                         SessionFactory.createInstance(WebServiceProtocol.SOAP, url)) {
                assertNotNull(soapSession,
                        "SOAPSession should have been initialized.");
                assertTrue(soapSession.isUseLocalWsdl(),
                        "SOAPSession should use local wsdl by default.");
                soapSession.setUseLocalWsdl(false);
                assertFalse(soapSession.isUseLocalWsdl(),
                        "SOAPSession should not be using local wsdl after modification.");
                assertNotNull(soapSession.getDataFormat(),
                        "DataFormat should have been initialized");
                assertEquals(DataFormat.XML, soapSession.getDataFormat(),
                        "DataFormat should have been XML.");
                assertEquals(DataFormat.XML.getMimeType(), soapSession.getDataFormat().getMimeType(),
                        "MimeType should have been xml.");
                assertEquals(WebServiceProtocol.SOAP, soapSession.getWebServiceProtocol(),
                        "WebserviceProtocol should have been SOAP.");
                assertNotNull(soapSession.getCredentials());
                assertEquals("username", soapSession.getCredentials().getUserPrincipal().getName(),
                        "Credentials should define username for authentication.");
                assertEquals("password", soapSession.getCredentials().getPassword(),
                        "Credentials should define password as the authentication password.");
                soapSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
                assertEquals("usr", soapSession.getCredentials().getUserPrincipal().getName(),
                        "Credentials should define usr for authentication.");
                assertEquals("pwd", soapSession.getCredentials().getPassword(),
                        "Credentials should define pwd as the authentication password.");

                assertEquals(SOME_URL + "/soap/sub", soapSession.getURI("sub").toString(),
                        "URI subpath should have been created.");
            }
        });
    }

}
