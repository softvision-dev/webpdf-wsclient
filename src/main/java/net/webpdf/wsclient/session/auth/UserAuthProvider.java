package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.AuthenticationMaterial;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.jetbrains.annotations.NotNull;

/**
 * An instance of {@link UserAuthProvider} shall provide {@link UsernamePasswordCredentials} for the authentication of a
 * webPDF user.
 */
public class UserAuthProvider extends AbstractAuthenticationProvider {

    /**
     * <p>
     * Creates a new {@link UserAuthProvider} for the given userName and password.<br>
     * <b>Be aware:</b> The given values may not be empty. Use the {@link AnonymousAuthProvider} to create anonymous
     * {@link Session}s.
     * </p>
     *
     * @param userName The name of the user to authenticate.
     * @param password The password of the user to authenticate.
     */
    public UserAuthProvider(@NotNull String userName, @NotNull String password) throws ResultException {
        this(userName, password.toCharArray());
    }

    /**
     * <p>
     * Creates a new {@link UserAuthProvider} provider for the given userName and password.<br>
     * <b>Be aware:</b> The given values may not be empty. Use the {@link AnonymousAuthProvider} to create anonymous
     * {@link Session}s.
     * </p>
     *
     * @param userName The name of the user to authenticate.
     * @param password The password of the user to authenticate.
     */
    public UserAuthProvider(@NotNull String userName, char @NotNull [] password) throws ResultException {
        super(new AuthenticationMaterial(userName, password));
        if (userName.isEmpty() || password.length == 0) {
            throw new ClientResultException(Error.INVALID_AUTH_MATERIAL);
        }
    }

}