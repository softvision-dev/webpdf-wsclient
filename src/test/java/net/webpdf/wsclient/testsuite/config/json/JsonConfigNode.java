package net.webpdf.wsclient.testsuite.config.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JsonConfigNode implements ConfigNode {

    private final @Nullable JsonNode node;

    public JsonConfigNode(@Nullable JsonNode node) {
        this.node = node;
    }

    @Override
    public @NotNull String getString(@NotNull String key, @NotNull String defaultValue) {
        if (this.node == null) {
            return defaultValue;
        }
        JsonNode valueNode = getNode(key);
        return valueNode != null ? valueNode.asText() : defaultValue;
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        if (this.node == null) {
            return defaultValue;
        }
        JsonNode valueNode = getNode(key);
        return valueNode != null ? valueNode.asBoolean() : defaultValue;
    }

    @Override
    public int getInteger(@NotNull String key, int defaultValue) {
        if (this.node == null) {
            return defaultValue;
        }
        JsonNode valueNode = getNode(key);
        return valueNode == null || valueNode.asText().isEmpty() ? defaultValue : valueNode.asInt();
    }

    private @Nullable JsonNode getNode(@NotNull String key) {
        return this.node != null ?
                this.node.at(key.startsWith("/") ? key : "/" + key) :
                null;
    }

}
