package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.session.auth.material.AuthenticationMaterial;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pins that Basic credentials are encoded as UTF-8, the encoding the webPDF server decodes them with, independent of
 * the platform charset.
 */
// Note: this test can only detect a regression on a JVM whose default charset is not UTF-8 (e.g. Java 11-17
// on Windows); with a UTF-8 default (Linux locales, Java 18+) the platform encoding gives the same bytes.
public class AuthenticationMaterialTest {

    @Test
    public void testBasicCredentialsAreUtf8Encoded() {
        // "Ω" and "ł" are not part of windows-1252 or ISO-8859-1, a platform charset encoding would replace them.
        String user = "jürgen.łukasz";
        String password = "päßwort-Ω-€";
        AuthenticationMaterial material = new AuthenticationMaterial(user, password.toCharArray());

        String expectedToken = Base64.getEncoder().encodeToString(
                (user + ":" + password).getBytes(StandardCharsets.UTF_8));
        assertEquals(expectedToken, material.getToken(), "The Basic token should encode the credentials as UTF-8.");
        assertEquals("Basic " + expectedToken, material.getRawAuthHeader(),
                "The authorization header should carry the UTF-8 encoded token.");
    }

}
