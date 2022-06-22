package net.webpdf.wsclient.session.token;

import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * An instance of {@link RefreshToken} wraps a refresh token provided by the webPDF server.
 * </p>
 * <p>
 * Such a Token can be obtained, via {@link SessionToken#provideRefreshToken()}.
 * </p>
 */
public class RefreshToken implements Token {

    private final @NotNull String refreshToken;

    /**
     * Creates a new {@link RefreshToken} from preexisting token values.
     *
     * @param refreshToken The refresh token {@code String} value.
     */
    public RefreshToken(@NotNull String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /**
     * Returns the {@link RefreshToken} {@code String} value, to refresh a {@link Session} and {@link SessionToken}
     * with.
     *
     * @return The {@link RefreshToken} {@code String} value, to refresh a {@link Session} and {@link SessionToken}
     * with.
     */
    @Override
    public @NotNull String getToken() {
        return refreshToken;
    }

}
