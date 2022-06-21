package net.webpdf.wsclient.https;

import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * {@link TLSProtocol} enumerates all currently supported TLS (Transport Layer Security) protocol versions for encrypted
 * HTTPS connections.
 * </p>
 * <p>
 * <b>Information:</b> TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is
 * no longer supported by the webPDF wsclient, as it is obsolete and insecure.
 * </p>
 *
 * @see #TLSV1
 * @see #TLSV1_1
 * @see #TLSV1_2
 */
public enum TLSProtocol {

    /**
     * Transport Layer Security (protocol) version 1.0
     */
    TLSV1("TLSv1"),
    /**
     * Transport Layer Security (protocol) version 1.1
     */
    TLSV1_1("TLSv1.1"),
    /**
     * Transport Layer Security (protocol) version 1.2
     */
    TLSV1_2("TLSv1.2");

    private final @NotNull String name;

    /**
     * Enumerates all {@link TLSProtocol} versions, that are supported by the wsclient.
     *
     * @param name The name of the {@link TLSProtocol}, that is supported by the wsclient.
     */
    TLSProtocol(@NotNull String name) {
        this.name = name;
    }

    /**
     * Returns the name of the supported {@link TLSProtocol}.
     *
     * @return The name of the supported {@link TLSProtocol}.
     */
    public @NotNull String getName() {
        return name;
    }

}
