package net.webpdf.wsclient.testsuite.config.json;

import org.jetbrains.annotations.NotNull;

public interface ConfigNode {

    @NotNull String getString(@NotNull String key, @NotNull String defaultValue);

    @SuppressWarnings("SameParameterValue")
    boolean getBoolean(@NotNull String key, boolean defaultValue);

    int getInteger(@NotNull String key, int defaultValue);

}
