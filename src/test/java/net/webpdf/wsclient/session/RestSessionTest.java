package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

public class RestSessionTest {
    private static final String SOME_URL = "http://someInvalidURL.de";
    private static final String SOME_CREDENTIALS_URL = "http://username:password@someInvalidURL.de";

    @Test
    public void testCreateRestSession() throws Exception {
        URL url = new URL(SOME_URL);
        try (RestSession restSession = SessionFactory.createInstance(WebServiceProtocol.REST, url)) {
            assertNotNull("RestSession should have been initialized.", restSession);
            assertNull("Token should have not been initialized.", restSession.getToken());
            assertNotNull("HttpClient should have been initialized.", restSession.getHttpClient());
            assertNotNull("DocumentManager should have been initialized.", restSession.getDocumentManager());
            assertEquals("DataFormat should have been JSON.", DataFormat.JSON, restSession.getDataFormat());
            restSession.setDataFormat(DataFormat.XML);
            assertEquals("DataFormat should haven been changed to XML.", DataFormat.XML, restSession.getDataFormat());
            assertEquals("WebserviceProtocol should have been REST.", WebServiceProtocol.REST, restSession.getWebServiceProtocol());
            assertNull("Credentials should not have been initialized.", restSession.getCredentials());
            restSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
            assertNotNull("Credentials should have been set.", restSession.getCredentials());
            assertEquals("Credentials should define usr for authentication.", "usr", restSession.getCredentials().getUserPrincipal().getName());
            assertEquals("Credentials should define pwd as the authentication password.", "pwd", restSession.getCredentials().getPassword());

            assertEquals("URI subpath should have been created.", SOME_URL + "/rest/sub", restSession.getURI("sub").toString());
        }
    }

    @Test
    public void testCreateCredentialsRestSession() throws Exception {
        URL url = new URL(SOME_CREDENTIALS_URL);
        try (RestSession restSession = SessionFactory.createInstance(WebServiceProtocol.REST, url)) {
            assertNotNull("RestSession should have been initialized.", restSession);
            assertNull("Token should have not been initialized.", restSession.getToken());
            assertNotNull("HttpClient should have been initialized.", restSession.getHttpClient());
            assertNotNull("DocumentManager should have been initialized.", restSession.getDocumentManager());
            assertEquals("DataFormat should have been JSON.", DataFormat.JSON, restSession.getDataFormat());
            restSession.setDataFormat(DataFormat.XML);
            assertEquals("DataFormat should haven been changed to XML.", DataFormat.XML, restSession.getDataFormat());
            assertEquals("WebserviceProtocol should have been REST.", WebServiceProtocol.REST, restSession.getWebServiceProtocol());
            assertNotNull("Credentials should have been initialized.", restSession.getCredentials());
            assertEquals("Credentials should define username for authentication.", "username", restSession.getCredentials().getUserPrincipal().getName());
            assertEquals("Credentials should define password as the authentication password.", "password", restSession.getCredentials().getPassword());
            restSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
            assertNotNull("Credentials should have been set.", restSession.getCredentials());
            assertEquals("Credentials should define usr for authentication.", "usr", restSession.getCredentials().getUserPrincipal().getName());
            assertEquals("Credentials should define pwd as the authentication password.", "pwd", restSession.getCredentials().getPassword());

            assertEquals("URI subpath should have been created.", SOME_URL + "/rest/sub", restSession.getURI("sub").toString());
        }
    }
}
