package net.webpdf.wsclient.testsuite.config.server;

import com.fasterxml.jackson.databind.JsonNode;
import net.webpdf.wsclient.testsuite.config.json.ConfigNodeContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerConfig extends ConfigNodeContainer {

    public static final @NotNull String SERVER_CONFIG_NODE = "/server";
    private final Boolean useLdap;

    public ServerConfig(@Nullable JsonNode node, @NotNull Boolean useLdap) {
        super(node);

        this.useLdap = useLdap;
    }

    public @NotNull String getLocalURL() {
        return getString("/local/url", "");
    }

    public @NotNull String getLocalAdminName() {
        if (this.useLdap) {
            return getString("/local/ldapAdminName", "");
        }

        return getString("/local/adminName", "");
    }

    public @NotNull String getLocalAdminPassword() {
        if (this.useLdap) {
            return getString("/local/ldapAdminPassword", "");
        }

        return getString("/local/adminPassword", "");
    }

    public @NotNull String getLocalUserName() {
        if (this.useLdap) {
            return getString("/local/ldapUserName", "");
        }

        return getString("/local/userName", "");
    }

    public @NotNull String getLocalUserPassword() {
        if (this.useLdap) {
            return getString("/local/ldapUserPassword", "");
        }

        return getString("/local/userPassword", "");
    }

    public int getLocalHttpPort() {
        return getInteger("/local/httpPort", -1);
    }

    public int getLocalHttpsPort() {
        return getInteger("/local/httpsPort", -1);
    }

    public @NotNull String getLocalPath() {
        return getString("/local/path", "");
    }

    public @NotNull String getPublicURL() {
        return getString("/public/url", "");
    }


    public int getPublicHttpPort() {
        return getInteger("/public/httpPort", -1);
    }

    public int getPublicHttpsPort() {
        return getInteger("/public/httpsPort", -1);
    }

    public @NotNull String getPublicPath() {
        return getString("/public/path", "");
    }

}
