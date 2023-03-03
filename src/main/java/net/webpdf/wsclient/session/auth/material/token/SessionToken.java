package net.webpdf.wsclient.session.auth.material.token;

import com.fasterxml.jackson.annotation.*;
import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.Instant;

/**
 * <p>
 * An instance of {@link SessionToken} wraps the access and refresh tokens provided by the webPDF server.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"expiresIn", "token", "refreshToken"})
public class SessionToken implements Serializable, JWTToken {

    @JsonProperty("token")
    private @NotNull String token = "";
    @JsonProperty("refreshToken")
    private @NotNull String refreshToken = "";
    @SuppressWarnings("unused")
    @JsonProperty("expiresIn")
    private long expiresIn = -1;
    private @NotNull Instant expiration = Instant.now();

    /**
     * Creates a new {@link SessionToken} from preexisting token values.
     *
     * @param accessToken  The access token {@code String} value.
     * @param refreshToken The refresh token {@code String} value.
     * @param expiresIn    The token expiry time in seconds.
     */
    @JsonCreator
    public SessionToken(@JsonProperty("token") @NotNull String accessToken,
            @JsonProperty("refreshToken") @NotNull String refreshToken,
            @JsonProperty("expiresIn") long expiresIn) {
        this.expiration = this.expiration.plusSeconds(expiresIn);
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
    }

    /**
     * Creates a new, empty {@link SessionToken}.
     */
    @SuppressWarnings("unused")
    public SessionToken() {
    }

    /**
     * Returns the access token {@code String} value. Shall return an empty String for an uninitialized session.
     *
     * @return The access token {@code String} value.
     */
    @Override
    public @NotNull String getToken() {
        return token;
    }

    /**
     * <p>
     * Replaces the access token with the refresh token.
     * </p>
     * <p>
     * <b>Be aware:</b> Using the refresh token as auth material is only valid and advisable while refreshing the
     * webPDF access token - other calls to the server will fail until a new access token is provided.<br>
     * Synchronization of webservice calls is absolutely necessary during a token refresh and it is recommended to delay
     * other calls until a fresh and valid access token is available.<br>
     * This {@link SessionToken} should be discarded after refreshing the token has finished.
     * </p>
     */
    public void refresh() {
        this.token = refreshToken;
    }

    /**
     * Returns the {@link Instant} the {@link SessionToken} will expire at.
     *
     * @return The {@link Instant} the {@link SessionToken} will expire at.
     */
    public @NotNull Instant getExpiration() {
        return expiration;
    }

    /**
     * Returns {@code true}, if the current access token is expired and {@link #refresh()} should be called to request
     * a new access token for the {@link Session}.
     *
     * @param skewTime An additional skew time, in seconds, that is added during expiry evaluation.
     *                 (Adding a skew time helps in avoiding to use expired access tokens because of transfer delays.)
     * @return {@code true}, if the current access token is expired.
     */
    public boolean isExpired(int skewTime) {
        return getExpiration().isBefore(Instant.now().plusSeconds(skewTime));
    }

}