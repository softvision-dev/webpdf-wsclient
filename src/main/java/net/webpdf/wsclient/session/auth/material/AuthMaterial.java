package net.webpdf.wsclient.session.auth.material;

import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link AuthMaterial} provides information for the authentication {@link #getCredentials()} and
 * authorization {@link #getAuthHeader()}/{@link #getRawAuthHeader()} of a {@link Session}.
 */
public interface AuthMaterial {

    /**
     * <p>
     * Provides {@link Credentials} for the authentication of a {@link Session}.<br>
     * This may validly return {@code null} for anonymous {@link Session}s, or in case an authentication is superfluous,
     * because some other means is used to authorize the {@link Session}.
     * </p>
     *
     * @return {@link Credentials} for the authentication of a {@link Session}.
     */
    @Nullable Credentials getCredentials();

    /**
     * <p>
     * Builds and returns an authorization {@link Header} for a session.<br>
     * This may validly return {@code null} for anonymous {@link Session}s.<br>
     * Unless overridden, this method uses the result of {@link #getRawAuthHeader()}.
     * </p>
     *
     * @return An authorization {@link Header} for a session.
     * @see #getRawAuthHeader()
     */
    default @Nullable Header getAuthHeader() {
        String rawAuthHeader = getRawAuthHeader();
        return rawAuthHeader == null ? null : new BasicHeader(HttpHeaders.AUTHORIZATION, getRawAuthHeader());
    }

    /**
     * Returns the {@link String} value of an authorization header, that shall be used by a {@link Session}.
     *
     * @return The {@link String} value of an authorization header, that shall be used by a {@link Session}.
     */
    @Nullable String getRawAuthHeader();

    /**
     * Returns the raw String token, that shall be passed to the authorization {@link Header}.
     *
     * @return The raw String token, that shall be passed to the authorization {@link Header}.
     */
    @NotNull String getToken();

}
