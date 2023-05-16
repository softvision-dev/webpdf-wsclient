package net.webpdf.wsclient.testsuite;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.OAuth2Provider;
import net.webpdf.wsclient.session.auth.material.token.OAuth2Token;
import net.webpdf.wsclient.session.auth.material.token.SessionToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AzureProvider implements OAuth2Provider {
    private @Nullable OAuth2Token token;
    private final @NotNull String authority;
    private final @NotNull String clientId;
    private final @NotNull String clientSecret;
    private final @NotNull String scope;

    public AzureProvider(
            @NotNull String authority, @NotNull String clientId, @NotNull String clientSecret, @NotNull String scope
    ) {
        this.authority = authority;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.scope = scope;
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

        // Request an access token from the Azure authorization provider:
        ConfidentialClientApplication app;
        try {
            app = ConfidentialClientApplication.builder(clientId, ClientCredentialFactory.createFromSecret(clientSecret))
                    .authority(authority)
                    .build();
        } catch (MalformedURLException ex) {
            throw new AuthResultException(ex);
        }
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                        Collections.singleton(scope))
                .build();
        CompletableFuture<IAuthenticationResult> future =
                app.acquireToken(clientCredentialParam);
        IAuthenticationResult authenticationResult =
                assertDoesNotThrow(() -> future.get());

        // Create and return the webPDF wsclient access Token.
        token = new OAuth2Token(authenticationResult.accessToken());

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
