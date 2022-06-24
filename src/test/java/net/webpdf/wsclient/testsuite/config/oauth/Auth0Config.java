package net.webpdf.wsclient.testsuite.config.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Example implementation that reads the value of Auth0 client claims from the given json config file.<br>
 * As said: This is just an example - Feel free to provide the claims in the way most fitting for your application.
 * </p>
 * <p>
 * You can find a documentation of Auth0 access tokens here:
 * <a href="https://auth0.com/docs/secure/tokens/access-tokens/get-access-tokens">Request Auth0 access tokens</a>
 * </p>
 */
public class Auth0Config extends OAuthConfig {

    /**
     * <p>
     * Example implementation that reads the value of Auth0 client claims from the given json config file.<br>
     * As said: This is just an example - Feel free to provide the claims in the way most fitting for your
     * application.<br>
     * <b>Be aware:</b> The values and names of the claims, that need to be defined are depending on the used
     * provider.<br>
     * <b>Be aware:</b> Said authorization provider must also be configured for your webPDF server. (server.xml)
     * </p>
     *
     * @param clientID A json config node, that contains all claims required to request the access token from
     *                 the authorization provider.
     */
    public Auth0Config(@Nullable JsonNode clientID) {
        super(clientID);
    }

    /**
     * Returns the value of the claim "authority".
     *
     * @return The value of the "authority" claim.
     */
    public @NotNull String getAuthority() {
        String claim = getClaim("authority");
        return claim != null ? claim : "";
    }

    /**
     * Returns the value of the claim "clientId".
     *
     * @return The value of the "clientId" claim.
     */
    public @NotNull String getClientID() {
        String claim = getClaim("clientId");
        return claim != null ? claim : "";
    }

    /**
     * Returns the value of the claim "clientSecret".
     *
     * @return The value of the "clientSecret" claim.
     */
    public @NotNull String getClientSecret() {
        String claim = getClaim("clientSecret");
        return claim != null ? claim : "";
    }

    /**
     * Returns the value of the claim "audience".
     *
     * @return The value of the "audience" claim.
     */
    public @NotNull String getAudience() {
        String claim = getClaim("audience");
        return claim != null ? claim : "";
    }

}
