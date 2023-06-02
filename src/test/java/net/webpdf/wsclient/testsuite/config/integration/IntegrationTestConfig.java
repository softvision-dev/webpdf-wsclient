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
        return getBoolean("enabled", false);
    }

    public boolean isProxyTestsActive() {
        return getBoolean("/proxy/enabled", false);
    }

    public URL getProxyURL(boolean useSSL) throws MalformedURLException {
        String url = useSSL
                ? getString("/proxy/urlSSL", "https://172.17.0.1:8443/webPDF")
                : getString("/proxy/url", "http://172.17.0.1:8080/webPDF");
        return new URL(url);
    }

    public boolean isTlsTestsActive() {
        return getBoolean("/tls/enabled", false);
    }

    public boolean isLdapTestsActive() {
        return getBoolean("/ldap/enabled", false);
    }

    public @NotNull Auth0Config getAuth0Config() {
        return auth0Config;
    }

    public @NotNull AzureConfig getAzureConfig() {
        return azureConfig;
    }

    public boolean useContainer() {
        return getBoolean("useContainer", false);
    }
}
