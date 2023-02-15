package net.webpdf.wsclient.testsuite.config.server;

import com.fasterxml.jackson.databind.JsonNode;
import net.webpdf.wsclient.testsuite.config.json.ConfigNodeContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerConfig extends ConfigNodeContainer {

    public static final @NotNull String SERVER_CONFIG_NODE = "/server";

    public ServerConfig(@Nullable JsonNode node) {
        super(node);
    }

    public @NotNull String getLocalURL() {
        return getString("/local/url", "");
    }

    public @NotNull String getLocalUser() {
        return getString("/local/user", "");
    }

    public @NotNull String getLocalPassword() {
        return getString("/local/password", "");
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
