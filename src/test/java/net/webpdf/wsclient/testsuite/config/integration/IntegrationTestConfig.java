package net.webpdf.wsclient.testsuite.config.integration;

import com.fasterxml.jackson.databind.JsonNode;
import net.webpdf.wsclient.testsuite.config.integration.oauth.Auth0Config;
import net.webpdf.wsclient.testsuite.config.integration.oauth.AzureConfig;
import net.webpdf.wsclient.testsuite.config.json.ConfigNodeContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

import static net.webpdf.wsclient.testsuite.config.integration.oauth.Auth0Config.OAUTH_AUTH_0_CONFIG_NODE;
import static net.webpdf.wsclient.testsuite.config.integration.oauth.AzureConfig.OAUTH_AZURE_CONFIG_NODE;

public class IntegrationTestConfig extends ConfigNodeContainer {

    public static final @NotNull String INTEGRATION_TEST_CONFIG = "/integrationTests";

    private final @NotNull Auth0Config auth0Config;
    private final @NotNull AzureConfig azureConfig;

    public IntegrationTestConfig(@Nullable JsonNode node) {
        super(node);
        this.auth0Config = new Auth0Config(node != null ?
                node.at(OAUTH_AUTH_0_CONFIG_NODE) : null);
        this.azureConfig = new AzureConfig(node != null ?
                node.at(OAUTH_AZURE_CONFIG_NODE) : null);
    }

    public boolean isIntegrationTestsActive() {
        return getBooleanSysProp("webpdf.test.integration.enabled", "enabled", false);
    }

    public boolean isProxyTestsActive() {
        return getBooleanSysProp("webpdf.test.proxy.enabled", "/proxy/enabled", false);
    }

    public URL getProxyURL(boolean useSSL) throws MalformedURLException {
        // The proxy container resolves the server by its compose service name; a Docker bridge address
        // depends on the local Docker address pool and is not reachable everywhere.
        String url = useSSL
                ? getString("/proxy/urlSSL", "https://webpdf:8443/webPDF")
                : getString("/proxy/url", "http://webpdf:8080/webPDF");
        return new URL(url);
    }

    public boolean isTlsTestsActive() {
        return getBooleanSysProp("webpdf.test.tls.enabled", "/tls/enabled", false);
    }

    public boolean isLdapTestsActive() {
        return getBooleanSysProp("webpdf.test.ldap.enabled", "/ldap/enabled", false);
    }

    public @NotNull Auth0Config getAuth0Config() {
        return auth0Config;
    }

    public @NotNull AzureConfig getAzureConfig() {
        return azureConfig;
    }

    public boolean useContainer() {
        return getBooleanSysProp("webpdf.test.useContainer", "useContainer", false);
    }

    private boolean getBooleanSysProp(@NotNull String sysPropKey, @NotNull String jsonKey, boolean defaultValue) {
        String sysProp = System.getProperty(sysPropKey);
        if (sysProp == null) {
            return getBoolean(jsonKey, defaultValue);
        }
        if ("true".equalsIgnoreCase(sysProp) || "false".equalsIgnoreCase(sysProp)) {
            return Boolean.parseBoolean(sysProp);
        }
        throw new IllegalArgumentException(
                "System property '" + sysPropKey + "' must be 'true' or 'false', got: '" + sysProp + "'");
    }
}
