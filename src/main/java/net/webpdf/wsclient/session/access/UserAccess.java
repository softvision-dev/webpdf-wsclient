package net.webpdf.wsclient.session.access;

import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.jetbrains.annotations.NotNull;

/**
 * An instance of {@link UserAccess} shall provide {@link UsernamePasswordCredentials} for the authentication of a
 * webPDF user.
 */
public class UserAccess implements SessionAccess<UsernamePasswordCredentials> {

    private final @NotNull String userName;
    private final char @NotNull [] password;

    /**
     * Creates a new {@link UserAccess} provider for the given userName and password.
     *
     * @param userName The name of the user to authenticate.
     * @param password The password of the user to authenticate.
     */
    public UserAccess(@NotNull String userName, @NotNull String password) {
        this(userName, password.toCharArray());
    }

    /**
     * Creates a new {@link UserAccess} provider for the given userName and password.
     *
     * @param userName The name of the user to authenticate.
     * @param password The password of the user to authenticate.
     */
    public UserAccess(@NotNull String userName, char @NotNull [] password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Provides {@link UsernamePasswordCredentials} for a {@link Session}.
     *
     * @return The {@link UsernamePasswordCredentials} provided by this {@link SessionAccess}.
     */
    @Override
    public UsernamePasswordCredentials getCredentials() {
        return new UsernamePasswordCredentials(this.userName, this.password);
    }

}
