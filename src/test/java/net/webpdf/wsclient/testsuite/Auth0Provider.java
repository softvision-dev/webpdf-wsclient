package net.webpdf.wsclient.testsuite;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.net.TokenRequest;
import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.OAuth2Provider;
import net.webpdf.wsclient.session.auth.material.token.OAuth2Token;
import net.webpdf.wsclient.session.auth.material.token.SessionToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Auth0Provider implements OAuth2Provider {
    private @Nullable OAuth2Token token;
    private final @NotNull String authority;
    private final @NotNull String clientId;
    private final @NotNull String clientSecret;
    private final @NotNull String audience;

    public Auth0Provider(
            @NotNull String authority, @NotNull String clientId, @NotNull String clientSecret, @NotNull String audience
    ) {
        this.authority = authority;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.audience = audience;
    }

    /**
     * Provides an {@link OAuth2Token} for the authorization of a {@link Session}.
     *
     * @param session The {@link Session} to provide authorization for.
     * @return The {@link OAuth2Token} to authorize the {@link Session} with.
     * @throws AuthResultException Shall be thrown, should the authorization fail for some reason.
     *                             (Use {@link AuthResultException} to wrap exceptions, that might occur during your
     *                             authorization request.)
     */
    @Override
    public @NotNull OAuth2Token provide(@NotNull Session session) throws AuthResultException {
        if (token != null) {
            return token;
        }

        // Request an access token from the Auth0 authorization provider:
        AuthAPI auth = new AuthAPI(authority, clientId, clientSecret);
        TokenRequest tokenRequest = auth.requestToken(audience);

        // Create and return the webPDF wsclient access Token.
        try {
            token = new OAuth2Token(tokenRequest.execute().getAccessToken());
        } catch (Auth0Exception ex) {
            throw new AuthResultException(ex);
        }

        return token;
    }

    /**
     * Refresh authorization {@link SessionToken} for an active {@link Session}.
     *
     * @param session The session to refresh the authorization for.
     * @return The {@link OAuth2Token} refreshed by this {@link OAuth2Provider}.
     */
    @Override
    public @NotNull OAuth2Token refresh(@NotNull Session session) {
        throw new Error("not implemented");
    }
}
