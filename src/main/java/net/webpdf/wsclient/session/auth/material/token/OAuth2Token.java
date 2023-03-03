package net.webpdf.wsclient.session.auth.material.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * An instance of {@link OAuth2Token} wraps the access token of a webPDF {@link Session} in an object.
 * </p>
 * <p>
 * Such a Token can only be obtained and refreshed, by a valid external authorization provider and can not be acquired
 * via the webPDF server.
 * </p>
 * <p>
 * <b>Important:</b> Make sure, that the token belongs to an authorization provider known to the webPDF server.
 * </p>
 */
public class OAuth2Token implements JWTToken {

    private final @NotNull String token;

    /**
     * Creates a new {@link OAuth2Token} for the access token determined by an OAuth provider.
     *
     * @param accessToken The access token {@code String} value.
     */
    @JsonCreator
    public OAuth2Token(@NotNull String accessToken) {
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

}