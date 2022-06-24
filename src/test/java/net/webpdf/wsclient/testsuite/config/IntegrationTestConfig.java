package net.webpdf.wsclient.testsuite.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.webpdf.wsclient.testsuite.config.oauth.Auth0Config;
import net.webpdf.wsclient.testsuite.config.oauth.AzureConfig;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class IntegrationTestConfig {

    private static final @NotNull String INTEGRATION_CONFIG_LOCATION = "config/integrationTestConfig.json";
    private static final @NotNull String AUTH_0_CONFIG_NODE = "/oAuth/auth0Client";
    private static final @NotNull String AZURE_CONFIG_NODE = "/oAuth/azureClient";

    private static @Nullable IntegrationTestConfig instance;

    private final @NotNull Auth0Config auth0Config;
    private final @NotNull AzureConfig azureConfig;

    private IntegrationTestConfig() {
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
                configNode.at(AUTH_0_CONFIG_NODE) : null);
        this.azureConfig = new AzureConfig(configNode != null ?
                configNode.at(AZURE_CONFIG_NODE) : null);
    }


    public @NotNull Auth0Config getAuth0Config() {
        return auth0Config;
    }

    public @NotNull AzureConfig getAzureConfig() {
        return azureConfig;
    }

    public static @NotNull IntegrationTestConfig getInstance() {
        return instance != null ? instance : (instance = new IntegrationTestConfig());
    }

}
