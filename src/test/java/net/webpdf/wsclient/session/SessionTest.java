package net.webpdf.wsclient.session;

import net.webpdf.wsclient.session.auth.UserAuthProvider;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.session.soap.SoapWebServiceSession;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Post 9.0.0 this test can not work for REST, as a RestSession will automatically try to connect to the
 * given URL during initialization, while SOAP still delays the actual request.
 * (An actual connection attempt to the hereby used URLs must obviously fail.)
 */
public class SessionTest {

    private static final String SOME_URL = "http://someInvalidURL.de";

    @Test
    public void testCreateSoapSession() {
        assertDoesNotThrow(() -> {
            URL url = new URL(SOME_URL);
            try (SoapWebServiceSession soapSession =
                         SessionFactory.createInstance(WebServiceProtocol.SOAP, url,
                                 new UserAuthProvider("usr", "pwd"))) {
                assertNotNull(soapSession,
                        "SOAPSession should have been initialized.");
                assertTrue(soapSession.isUseLocalWsdl(),
                        "SOAPSession should use local wsdl by default.");
                soapSession.setUseLocalWsdl(false);
                assertFalse(soapSession.isUseLocalWsdl(),
                        "SOAPSession should not be using local wsdl after modification.");
                assertEquals(WebServiceProtocol.SOAP, soapSession.getWebServiceProtocol(),
                        "WebserviceProtocol should have been SOAP.");
                assertNotNull(soapSession.getAuthMaterial().getCredentials());
                assertEquals("usr", soapSession.getAuthMaterial().getCredentials()
                                .getUserPrincipal().getName(),
                        "Credentials should define usr for authentication.");
                assertEquals("pwd",
                        new String(soapSession.getAuthMaterial().getCredentials().getPassword()),
                        "Credentials should define pwd as the authentication password.");

                assertEquals(SOME_URL + "/soap/sub", soapSession.getURI("sub").toString(),
                        "URI sub-path should have been created.");
            }
        });
    }

}