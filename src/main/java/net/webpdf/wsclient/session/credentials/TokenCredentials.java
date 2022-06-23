package net.webpdf.wsclient.session.credentials;

import net.webpdf.wsclient.session.token.Token;
import org.apache.http.auth.BasicUserPrincipal;
import org.apache.http.auth.Credentials;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.security.Principal;

/**
 * An instance of {@link TokenCredentials} shall construct and provide {@link Credentials} based on a given
 * {@link Token}.
 */
public class TokenCredentials<T extends Token> implements Credentials, Serializable {

    private final @NotNull T token;

    /**
     * Constructs a new {@link TokenCredentials} instance, that shall construct and provide {@link Credentials} based
     * on a given {@link Token}.
     *
     * @param token The {@link Token} to create {@link TokenCredentials} for.
     */
    public TokenCredentials(@NotNull T token) {
        this.token = token;
    }

    /**
     * Returns the determined {@link Principal}, which shall reuse {@link Token#getToken()}.
     *
     * @return The determined {@link Principal}.
     */
    @Override
    public Principal getUserPrincipal() {
        return new BasicUserPrincipal(token.getToken());
    }

    /**
     * Returns the password matching the {@link Principal}.<br>
     * A {@link TokenCredentials} instance shall assume, that it´s {@link Principal} does not require a password and
     * shall return {@code null}.
     *
     * @return The password matching the {@link Principal}.
     */
    @Override
    public String getPassword() {
        return null;
    }

}
