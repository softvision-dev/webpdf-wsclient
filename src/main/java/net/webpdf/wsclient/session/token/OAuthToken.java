package net.webpdf.wsclient.session.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * An instance of {@link OAuthToken} wraps the access token of a webPDF {@link Session} in an object.
 * </p>
 * <p>
 * Such a Token can only be obtained and refreshed, by a valid external authorization provider and can not be acquired
 * via the webPDF server.
 * </p>
 * <p>
 * <b>Important:</b> Make sure, that the token belongs to an authorization provider known to the webPDF server.
 * </p>
 */
@SuppressWarnings("unused")
public class OAuthToken implements Token {

    private @NotNull String token;

    /**
     * Creates a new {@link OAuthToken} for the access token determined by an OAuth provider.
     *
     * @param accessToken The access token {@code String} value.
     */
    @JsonCreator
    public OAuthToken(@NotNull String accessToken) {
        this.token = accessToken;
    }

    /**
     * Returns the access token {@code String} value.
     *
     * @return The access token {@code String} value.
     */
    @Override
    public @NotNull String getToken() {
        return this.token;
    }

    /**
     * <p>
     * Refreshes the {@link OAuthToken} by replacing the stored accessToken with a refreshed, new access token.
     * </p>
     * <p>
     * <b>Important:</b> It is out of scope for The webPDF server (and wsclient) to actually implement a refreshing
     * logic for all possible authorization providers.
     * The actual refreshing and handling of a refresh token must be solved elsewhere.
     * </p>
     *
     * @param accessToken The refreshed access token {@code String} value.
     */
    public void refresh(@NotNull String accessToken) {
        this.token = accessToken;
    }

}
