package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.session.SessionFactory;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.stub.LocalWebPdfServer;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pins the token handling of {@link AbstractAuthenticationProvider}: a refresh must not alter the token handed out
 * before, and a provider created with a session token resumes that session without a new login. A
 * {@link LocalWebPdfServer} issues the tokens ("access-n"/"refresh-n" per login).
 */
public class AuthenticationProviderTest {

    @Test
    public void testRefreshDoesNotAlterHandedOutToken() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            UserAuthProvider provider = new UserAuthProvider("admin", "secret");
            try (RestWebServiceSession session = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, server.getUrl()), provider)) {
                AuthMaterial handedOut = provider.provide(session);
                assertEquals("Bearer access-1", handedOut.getRawAuthHeader(), "Unexpected initial token.");

                AuthMaterial refreshed = provider.refresh(session);

                assertEquals("Bearer access-1", handedOut.getRawAuthHeader(),
                        "A token handed out before the refresh must keep its access token.");
                assertEquals("Bearer refreshed-access-1", refreshed.getRawAuthHeader(),
                        "The refresh should have provided the new token.");
                assertEquals("Bearer refreshed-access-1", provider.provide(session).getRawAuthHeader(),
                        "The provider should use the refreshed token.");
                List<LocalWebPdfServer.RecordedRequest> refreshRequests =
                        server.getRequests("POST", LocalWebPdfServer.REFRESH_PATH);
                assertEquals(1, refreshRequests.size(), "One refresh request should have been sent.");
                assertEquals("Bearer refresh-1", refreshRequests.get(0).getAuthorization(),
                        "The refresh request should be authorized with the refresh token.");
            }
        }
    }

    @Test
    public void testResumedTokenIsUsedByFirstSession() throws Exception {
        try (LocalWebPdfServer server = LocalWebPdfServer.start()) {
            UserAuthProvider original = new UserAuthProvider("admin", "secret");
            try (RestWebServiceSession first = SessionFactory.createInstance(
                    new SessionContext(WebServiceProtocol.REST, server.getUrl()), original)) {
                AuthMaterial token = original.provide(first);

                UserAuthProvider resumed = new UserAuthProvider("admin", "secret", token);
                try (RestWebServiceSession resumedSession = SessionFactory.createInstance(
                        new SessionContext(WebServiceProtocol.REST, server.getUrl()), resumed)) {
                    assertEquals(1, server.getLoginCount(), "Resuming a session should not log in again.");
                    assertEquals("Bearer access-1", resumed.provide(resumedSession).getRawAuthHeader(),
                            "The resumed session should use the given token.");
                }
            }
        }
    }

}
