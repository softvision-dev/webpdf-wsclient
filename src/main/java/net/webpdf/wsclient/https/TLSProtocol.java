package net.webpdf.wsclient.https;

import org.jetbrains.annotations.NotNull;

public enum TLSProtocol {

    TLSV1("TLSv1"),
    TLSV1_1("TLSv1.1"),
    TLSV1_2("TLSv1.2");

    @NotNull
    private final String name;

    /**
     * Enumerates all TLS protocol versions, that are supported by the client.
     *
     * @param name The name of the TLS protocol, that is supported by the client.
     */
    TLSProtocol(@NotNull String name) {
        this.name = name;
    }

    /**
     * Returns the name of the supported TLS protocol.
     *
     * @return The name of the supported TLS protocol.
     */
    @NotNull
    public String getName() {
        return name;
    }

}
