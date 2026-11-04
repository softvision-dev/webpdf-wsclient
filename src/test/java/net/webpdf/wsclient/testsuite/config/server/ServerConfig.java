package net.webpdf.wsclient.testsuite.config.server;

import com.fasterxml.jackson.databind.JsonNode;
import net.webpdf.wsclient.testsuite.config.json.ConfigNodeContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerConfig extends ConfigNodeContainer {

    public static final @NotNull String SERVER_CONFIG_NODE = "/server";
    private final Boolean useLdap;

    /**
     * The defaults match {@code config/testConfig.sample.json} and the bundled test containers
     * ({@code docker/docker-compose.yml}), so the container based run also works without a configuration file.
     */
    public ServerConfig(@Nullable JsonNode node, @NotNull Boolean useLdap) {
        super(node);

        this.useLdap = useLdap;
    }

    public @NotNull String getLocalURL() {
        return getString("/local/url", "http://localhost");
    }

    public @NotNull String getLocalAdminName() {
        if (this.useLdap) {
            return getString("/local/ldapAdminName", "");
        }

        return getString("/local/adminName", "admin");
    }

    public @NotNull String getLocalAdminPassword() {
        if (this.useLdap) {
            return getString("/local/ldapAdminPassword", "");
        }

        return getString("/local/adminPassword", "admin");
    }

    public @NotNull String getLocalUserName() {
        if (this.useLdap) {
            return getString("/local/ldapUserName", "");
        }

        return getString("/local/userName", "user");
    }

    public @NotNull String getLocalUserPassword() {
        if (this.useLdap) {
            return getString("/local/ldapUserPassword", "");
        }

        return getString("/local/userPassword", "user");
    }

    public int getLocalHttpPort() {
        return getInteger("/local/httpPort", 8080);
    }

    public int getLocalHttpsPort() {
        return getInteger("/local/httpsPort", 8443);
    }

    public @NotNull String getLocalPath() {
        return getString("/local/path", "/webPDF");
    }

    public @NotNull String getPublicURL() {
        return getString("/public/url", "https://portal.webpdf.de");
    }


    public int getPublicHttpPort() {
        return getInteger("/public/httpPort", -1);
    }

    public int getPublicHttpsPort() {
        return getInteger("/public/httpsPort", -1);
    }

    public @NotNull String getPublicPath() {
        return getString("/public/path", "/webPDF");
    }

}
