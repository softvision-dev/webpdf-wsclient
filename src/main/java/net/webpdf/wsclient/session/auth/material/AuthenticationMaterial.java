package net.webpdf.wsclient.session.auth.material;

import jakarta.xml.bind.DatatypeConverter;
import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.core5.http.Header;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link AuthenticationMaterial} serves to establish a user based {@link Session} with the webPDF
 * server.<br>
 * It shall both provide {@link UsernamePasswordCredentials} for a user, and a proper authorization {@link Header} to
 * communicate the authentication attempt to the server.
 * </p>
 */
public class AuthenticationMaterial implements AuthMaterial {

    private final @NotNull String userName;
    private final char @NotNull [] password;

    /**
     * <p>
     * Instantiates fresh {@link AuthenticationMaterial} for the given user, using the given password.
     * </p>
     *
     * @param user     The username to authenticate.
     * @param password The password of the authenticated user.
     */
    public AuthenticationMaterial(@NotNull String user, char @NotNull [] password) {
        this.userName = user;
        this.password = password;
    }

    /**
     * Returns the user´s {@link UsernamePasswordCredentials}.
     *
     * @return The user´s {@link UsernamePasswordCredentials}.
     */
    @Override
    public @Nullable Credentials getCredentials() {
        return new UsernamePasswordCredentials(userName, password);
    }

    /**
     * Returns a new {@link AuthMethod#BASIC_AUTHORIZATION} header using the given {@link UsernamePasswordCredentials}.
     *
     * @return A new {@link AuthMethod#BASIC_AUTHORIZATION} header using the given {@link UsernamePasswordCredentials}.
     */
    @Override
    public @Nullable String getRawAuthHeader() {
        return AuthMethod.BASIC_AUTHORIZATION.getKey() + " " + DatatypeConverter.printBase64Binary(
                (userName + ":" + new String(password)).getBytes()
        );
    }

}
