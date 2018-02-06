package net.webpdf.wsclient.https;

public enum TLSProtocol {
    TLSV1("TLSv1"),
    TLSV1_1("TLSv1.1"),
    TLSV1_2("TLSv1.2");

    private final String name;

    /**
     * Enumerates all TLS protocol versions, that are supported by the client.
     *
     * @param name The name of the TLS protocol, that is supported by the client.
     */
    TLSProtocol(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the supported TLS protocol.
     * @return The name of the supported TLS protocol.
     */
    public String getName() {
        return name;
    }
}
