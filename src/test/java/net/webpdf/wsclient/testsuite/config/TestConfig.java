package net.webpdf.wsclient.testsuite.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.webpdf.wsclient.testsuite.config.integration.IntegrationTestConfig;
import net.webpdf.wsclient.testsuite.config.server.ServerConfig;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import static net.webpdf.wsclient.testsuite.config.integration.IntegrationTestConfig.INTEGRATION_TEST_CONFIG;
import static net.webpdf.wsclient.testsuite.config.server.ServerConfig.SERVER_CONFIG_NODE;

public class TestConfig {

    private static final @NotNull String TEST_CONFIG_LOCATION = "config/testConfig.json";

    private static @Nullable TestConfig instance;

    private final @NotNull ServerConfig serverConfig;
    private final @NotNull IntegrationTestConfig integrationTestConfig;

    private TestConfig() {
        File confFile = new File(Paths.get("").toAbsolutePath().toFile(), TEST_CONFIG_LOCATION);
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

        this.integrationTestConfig = new IntegrationTestConfig(
                configNode != null ? configNode.at(INTEGRATION_TEST_CONFIG) : null
        );
        this.serverConfig = new ServerConfig(
                configNode != null ? configNode.at(SERVER_CONFIG_NODE) : null,
                this.integrationTestConfig.isLdapTestsActive()
        );
    }

    public @NotNull ServerConfig getServerConfig() {
        return serverConfig;
    }

    public @NotNull IntegrationTestConfig getIntegrationTestConfig() {
        return integrationTestConfig;
    }

    public static @NotNull TestConfig getInstance() {
        return instance != null ? instance : (instance = new TestConfig());
    }

}
