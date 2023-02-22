package net.webpdf.wsclient.testsuite.config.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ConfigNodeContainer implements ConfigNode {

    private final @NotNull JsonConfigNode node;

    public ConfigNodeContainer(@Nullable JsonNode node) {
        this.node = new JsonConfigNode(node);
    }

    @Override
    public @NotNull String getString(@NotNull String key, @NotNull String defaultValue) {
        return node.getString(key, defaultValue);
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return node.getBoolean(key, defaultValue);
    }

    @Override
    public int getInteger(@NotNull String key, int defaultValue) {
        return node.getInteger(key, defaultValue);
    }

}
