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
        JsonNode valueNode = getNode(key);
        return valueNode != null ? valueNode.asText() : defaultValue;
    }

    @Override
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        JsonNode valueNode = getNode(key);
        return valueNode != null ? valueNode.asBoolean() : defaultValue;
    }

    @Override
    public int getInteger(@NotNull String key, int defaultValue) {
        JsonNode valueNode = getNode(key);
        return valueNode == null || valueNode.asText().isEmpty() ? defaultValue : valueNode.asInt();
    }

    /**
     * Returns the value node for the given key, or {@code null} when the key is not configured.
     * {@link JsonNode#at(String)} never returns {@code null} but a "missing" node, which must count as absent -
     * otherwise the caller's default value would never apply.
     */
    private @Nullable JsonNode getNode(@NotNull String key) {
        if (this.node == null) {
            return null;
        }
        JsonNode valueNode = this.node.at(key.startsWith("/") ? key : "/" + key);
        return valueNode.isMissingNode() || valueNode.isNull() ? null : valueNode;
    }

}
