package net.webpdf.wsclient.session.auth.material.token;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.auth.material.AuthMethod;
import org.apache.hc.client5.http.auth.Credentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link JWTToken} wraps an access token that can be used to authorize a webPDF server {@link Session}.
 */
public interface JWTToken extends AuthMaterial {

    /**
     * Returns the access token {@code String} value.
     *
     * @return The access token {@code String} value.
     */
    @NotNull String getToken();

    /**
     * <p>
     * Provides {@link Credentials} for the authentication of a {@link Session}.<br>
     * This may validly return {@code null} for anonymous {@link Session}s, or in case an authentication is superfluous,
     * because some other means are used to authorize the {@link Session}.
     * </p>
     *
     * @return {@link Credentials} for the authentication of a {@link Session}.
     */
    @Override
    default @Nullable Credentials getCredentials() {
        return null;
    }

    /**
     * Returns the {@link String} value of an authorization header, that shall be used by a {@link Session}.
     *
     * @return The {@link String} value of an authorization header, that shall be used by a {@link Session}.
     */
    @Override
    default @Nullable String getRawAuthHeader() {
        return AuthMethod.BEARER_AUTHORIZATION.getKey() + " " + getToken();
    }

}