package net.webpdf.wsclient.testsuite.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.webpdf.wsclient.testsuite.config.json.JsonConfigNode;
import net.webpdf.wsclient.testsuite.config.oauth.Auth0Config;
import net.webpdf.wsclient.testsuite.config.oauth.AzureConfig;
import net.webpdf.wsclient.testsuite.config.server.ServerConfig;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static net.webpdf.wsclient.testsuite.config.oauth.Auth0Config.OAUTH_AUTH_0_CONFIG_NODE;
import static net.webpdf.wsclient.testsuite.config.oauth.AzureConfig.OAUTH_AZURE_CONFIG_NODE;
import static net.webpdf.wsclient.testsuite.config.server.ServerConfig.SERVER_CONFIG_NODE;

public class TestConfig {

    private static final @NotNull String INTEGRATION_CONFIG_LOCATION = "config/testConfig.json";
    private static final @NotNull String INTEGRATION_TEST_CONFIG = "/integrationTests";
    private static final @NotNull String PROXY_TESTS_CONFIG = "/integrationTests/proxy";
    private static final @NotNull String TLS_TESTS_CONFIG = "/integrationTests/tls";

    private static @Nullable TestConfig instance;

    private final @NotNull Auth0Config auth0Config;
    private final @NotNull AzureConfig azureConfig;
    private final @NotNull ServerConfig serverConfig;

    private final boolean integrationTestsActive;
    private final boolean proxyTestsActive;
    private final boolean tlsTestsActive;

    private TestConfig() {
        File confFile = new File(Paths.get("").toAbsolutePath().toFile(), INTEGRATION_CONFIG_LOCATION);
        JsonNode configNode = null;
        if (confFile.exists() && confFile.isFile()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                configNode = mapper.readTree(
                        FileUtils.readFileToString(confFile, StandardCharsets.UTF_8));
            } catch (IOException e) {
                // IGNORE
            }
        }
        this.auth0Config = new Auth0Config(configNode != null ?
                configNode.at(OAUTH_AUTH_0_CONFIG_NODE) : null);
        this.azureConfig = new AzureConfig(configNode != null ?
                configNode.at(OAUTH_AZURE_CONFIG_NODE) : null);
        this.serverConfig = new ServerConfig(configNode != null ?
                configNode.at(SERVER_CONFIG_NODE) : null);
        this.integrationTestsActive = new JsonConfigNode(configNode != null ?
                configNode.at(INTEGRATION_TEST_CONFIG) : null).getBoolean("enabled", false);
        this.proxyTestsActive = new JsonConfigNode(configNode != null ?
                configNode.at(PROXY_TESTS_CONFIG) : null).getBoolean("enabled", false);
        this.tlsTestsActive = new JsonConfigNode(configNode != null ?
                configNode.at(TLS_TESTS_CONFIG) : null).getBoolean("enabled", false);
    }


    public @NotNull Auth0Config getAuth0Config() {
        return auth0Config;
    }

    public @NotNull AzureConfig getAzureConfig() {
        return azureConfig;
    }

    public @NotNull ServerConfig getServerConfig() {
        return serverConfig;
    }

    public boolean isIntegrationTestsActive() {
        return integrationTestsActive;
    }

    public boolean isProxyTestsActive() {
        return proxyTestsActive;
    }

    public boolean isTlsTestsActive() {
        return tlsTestsActive;
    }

    public static @NotNull TestConfig getInstance() {
        return instance != null ? instance : (instance = new TestConfig());
    }

}
