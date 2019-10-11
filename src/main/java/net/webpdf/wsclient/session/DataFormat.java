package net.webpdf.wsclient.session;

import org.jetbrains.annotations.NotNull;

public enum DataFormat {

    XML("application/xml"),
    JSON("application/json");

    @NotNull
    private final String mimeType;

    /**
     * Created an enum instance representing the given MIME-type.
     *
     * @param mimeType The MIME-type represented by the enum instance.
     */
    DataFormat(@NotNull String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Return the MIME-type represented by this enum instance.
     *
     * @return The MIME-type represented by this enum instance.
     */
    @NotNull
    public String getMimeType() {
        return mimeType;
    }

}

