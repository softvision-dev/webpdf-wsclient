package net.webpdf.wsclient.testsuite.config.oauth;


import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * <p>
 * Example implementation that reads the value of client claims from the given json config file.<br>
 * As said: This is just an example - Feel free to provide the claims in the way most fitting for your application.
 * </p>
 */
public abstract class OAuthConfig {

    private final @Nullable JsonNode clientID;

    /**
     * <p>
     * Example implementation that reads the value of client claims from the given json config file.<br>
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
    public OAuthConfig(@Nullable JsonNode clientID) {
        this.clientID = clientID;
    }

    /**
     * Reads the claim with the given name from the client config.
     *
     * @param claimName The name of the claim, that shall be read.
     * @return The value of the claim, that has been read.
     */
    protected @Nullable String getClaim(@NotNull String claimName) {
        if (clientID == null) {
            return null;
        }
        JsonNode claim = clientID.get(claimName);
        return claim != null ? claim.asText() : null;
    }

    /**
     * Reads the claim with the given name from the client config.
     *
     * @param claimName The name of the claim, that shall be read.
     * @return The value of the claim, that has been read.
     */
    @SuppressWarnings("SameParameterValue")
    protected @Nullable Boolean getBoolClaim(@NotNull String claimName) {
        if (clientID == null) {
            return null;
        }
        JsonNode claim = clientID.get(claimName);
        return claim != null ? claim.asBoolean() : null;
    }

    /**
     * Returns {@code true}, if such OAuth test shall be executed.
     *
     * @return {@code true}, if such OAuth test shall be executed.
     */
    public boolean isEnabled() {
        Boolean claim = getBoolClaim("enabled");
        return claim != null ? claim : false;
    }

}
