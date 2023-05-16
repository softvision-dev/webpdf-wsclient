package net.webpdf.wsclient.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link DataFormat} enumerates all known formats for a {@link Session}´s data transfer objects.
 *
 * @see #XML
 * @see #JSON
 * @see #OCTET_STREAM
 * @see #PLAIN
 * @see #ANY
 */
public enum DataFormat {

    /**
     * Extensible Markup Language
     */
    XML("application/xml"),
    /**
     * JavaScript Object Notation
     */
    JSON("application/json"),
    /**
     * An unspecified binary stream.
     */
    OCTET_STREAM("application/octet-stream"),
    /**
     * A plain text.
     */
    PLAIN("text/plain"),
    /**
     * unspecified file format.
     */
    ANY("*/*");

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

    /**
     * Returns {@code true} should the given MIME-type match the selected {@link DataFormat}.
     *
     * @param mimeType The MIME-type to check.
     * @return {@code true} should the given MIME-type match the selected {@link DataFormat}.
     */
    public boolean matches(@Nullable String mimeType) {
        return this.mimeType.equals(mimeType);
    }

    /**
     * Returns a {@link String} representation of the selected {@link DataFormat}.
     *
     * @return A {@link String} representation of the selected {@link DataFormat}.
     */
    @Override
    public String toString() {
        return getMimeType();
    }

}

