package net.webpdf.wsclient.session;

import org.jetbrains.annotations.NotNull;

/**
 * {@link DataFormat} enumerates all known formats for a {@link Session}´s data transfer objects.
 *
 * @see #XML
 * @see #JSON
 */
public enum DataFormat {

    /**
     * Extensible Markup Language
     */
    XML("application/xml"),
    /**
     * JavaScript Object Notation
     */
    JSON("application/json");

    private final @NotNull String mimeType;

    /**
     * Created a {@link DataFormat} representing the given MIME-type.
     *
     * @param mimeType The MIME-type represented by the {@link DataFormat}.
     */
    DataFormat(@NotNull String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Return the MIME-type represented by this {@link DataFormat}.
     *
     * @return The MIME-type represented by this {@link DataFormat}.
     */
    public @NotNull String getMimeType() {
        return mimeType;
    }

}

