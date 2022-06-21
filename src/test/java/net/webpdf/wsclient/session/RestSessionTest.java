package net.webpdf.wsclient.session;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class RestSessionTest {
    private static final String SOME_URL = "http://someInvalidURL.de";
    private static final String SOME_CREDENTIALS_URL = "http://username:password@someInvalidURL.de";

    @Test
    public void testCreateRestSession() throws Exception {
        URL url = new URL(SOME_URL);
        try (RestSession<RestDocument> restSession =
                     SessionFactory.createInstance(WebServiceProtocol.REST, url)) {
            assertNotNull(restSession,
                    "RestSession should have been initialized.");
            assertNotNull(restSession.getToken(),
                    "Token should have been initialized.");
            assertNotNull(restSession.getHttpClient(),
                    "HttpClient should have been initialized.");
            assertNotNull(restSession.getDocumentManager(),
                    "DocumentManager should have been initialized.");
            assertEquals(DataFormat.JSON, restSession.getDataFormat(),
                    "DataFormat should have been JSON.");
            assertEquals(WebServiceProtocol.REST, restSession.getWebServiceProtocol(),
                    "WebserviceProtocol should have been REST.");
            assertNull(restSession.getCredentials(),
                    "Credentials should not have been initialized.");
            restSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
            assertNotNull(restSession.getCredentials(),
                    "Credentials should have been set.");
            assertEquals("usr", restSession.getCredentials().getUserPrincipal().getName(),
                    "Credentials should define usr for authentication.");
            assertEquals("pwd", restSession.getCredentials().getPassword(),
                    "Credentials should define pwd as the authentication password.");

            assertEquals(SOME_URL + "/rest/sub", restSession.getURI("sub").toString(),
                    "URI subpath should have been created.");
        }
    }

    @Test
    public void testCreateCredentialsRestSession() throws Exception {
        URL url = new URL(SOME_CREDENTIALS_URL);
        try (RestWebServiceSession restSession = SessionFactory.createInstance(WebServiceProtocol.REST, url)) {
            assertNotNull(restSession,
                    "RestSession should have been initialized.");
            assertNotNull(restSession.getToken(),
                    "Token should have been initialized.");
            assertNotNull(restSession.getHttpClient(),
                    "HttpClient should have been initialized.");
            assertNotNull(restSession.getDocumentManager(),
                    "DocumentManager should have been initialized.");
            assertEquals(DataFormat.JSON, restSession.getDataFormat(),
                    "DataFormat should have been JSON.");
            assertEquals(WebServiceProtocol.REST, restSession.getWebServiceProtocol(),
                    "WebserviceProtocol should have been REST.");
            assertNotNull(restSession.getCredentials(),
                    "Credentials should have been initialized.");
            assertEquals("username", restSession.getCredentials().getUserPrincipal().getName(),
                    "Credentials should define username for authentication.");
            assertEquals("password", restSession.getCredentials().getPassword(),
                    "Credentials should define password as the authentication password.");
            restSession.setCredentials(new UsernamePasswordCredentials("usr", "pwd"));
            assertNotNull(restSession.getCredentials(),
                    "Credentials should have been set.");
            assertEquals("usr", restSession.getCredentials().getUserPrincipal().getName(),
                    "Credentials should define usr for authentication.");
            assertEquals("pwd", restSession.getCredentials().getPassword(),
                    "Credentials should define pwd as the authentication password.");
            assertEquals(SOME_URL + "/rest/sub", restSession.getURI("sub").toString(),
                    "URI subpath should have been created.");
        }
    }

}
