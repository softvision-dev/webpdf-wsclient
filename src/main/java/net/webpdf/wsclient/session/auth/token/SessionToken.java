package net.webpdf.wsclient.session.auth.token;

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
public class SessionToken implements Serializable, Token {

    @JsonProperty("token")
    private @NotNull String token = "";
    @JsonProperty("refreshToken")
    private @NotNull String refreshToken = "";
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
     * Returns the token expiry time in seconds. Shall return -1 for an uninitialized  session.
     *
     * @return The token expiry time in seconds.
     */
    public long getExpiresIn() {
        return expiresIn;
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
     * Provides a refresh {@link SessionToken} to refresh a {@link Session} with.
     *
     * @return a refresh {@link SessionToken} to refresh a {@link Session} with.
     */
    public @NotNull RefreshToken provideRefreshToken() {
        return new RefreshToken(this.refreshToken);
    }

}