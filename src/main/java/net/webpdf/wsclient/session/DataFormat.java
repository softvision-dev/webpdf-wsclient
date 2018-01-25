package net.webpdf.wsclient.session;

public enum DataFormat {
    XML("application/xml"),
    JSON("application/json");

    private final String mimeType;

    /**
     * Created an enum instance representing the given MIME-type.
     *
     * @param mimeType The MIME-type represented by the enum instance.
     */
    DataFormat(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Return the MIME-type represented by this enum instance.
     *
     * @return The MIME-type represented by this enum instance.
     */
    public String getMimeType() {
        return mimeType;
    }
}

