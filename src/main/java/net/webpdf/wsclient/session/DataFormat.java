package net.webpdf.wsclient.session;

public enum DataFormat {
    XML("application/xml"),
    JSON("application/json");

    private final String mimeType;

    DataFormat(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}

