package net.webpdf.wsclient.testsuite.config.integration.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import net.webpdf.wsclient.testsuite.config.json.ConfigNodeContainer;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * Example implementation that reads the value of client claims from the given json config file.<br>
 * As said: This is just an example - Feel free to provide the claims in the way most fitting for your application.
 * </p>
 */
public abstract class OAuthConfig extends ConfigNodeContainer {

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
     * @param node A json config node, that contains all claims required to request the access token from
     *             the authorization provider.
     */
    public OAuthConfig(@Nullable JsonNode node) {
        super(node);
    }

    /**
     * Returns {@code true}, if such OAuth test shall be executed.
     *
     * @return {@code true}, if such OAuth test shall be executed.
     */
    public boolean isEnabled() {
        return getBoolean("enabled", false);
    }

}
