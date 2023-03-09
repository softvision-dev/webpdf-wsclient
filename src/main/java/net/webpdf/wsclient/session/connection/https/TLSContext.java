package net.webpdf.wsclient.session.connection.https;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.security.cert.X509Certificate;

/**
 * <p>
 * An instance of {@link TLSContext} prepares a {@link TLSProtocol} context for encrypted HTTPS connections.
 * </p>
 * <p>
 * <b>Information:</b> TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is
 * no longer supported by the webPDF wsclient, as it is obsolete and insecure.
 * </p>
 */
public class TLSContext {

    private final @Nullable File trustStore;
    private final @Nullable String trustStorePassword;
    private final boolean allowSelfSigned;
    private final @NotNull TLSProtocol tlsProtocol;

    /**
     * <p>
     * Prepares a fresh the {@link TLSContext} for an HTTPS connection.
     * </p>
     * <p>
     * Sets the {@link X509Certificate} truststore file and it´s password. The truststore shall determine which
     * connection targets shall be deemed trustworthy.
     * </p>
     *
     * @param trustStore         Selects the {@link X509Certificate} truststore file
     * @param trustStorePassword Selects a possibly necessary password for the truststore file.
     * @param allowSelfSigned    Set to {@code true} to allow the usage of self-signed {@link X509Certificate}s.
     * @param tlsProtocol        The {@link TLSProtocol}, that shall be used.
     */
    public TLSContext(@NotNull TLSProtocol tlsProtocol, boolean allowSelfSigned,
            @NotNull File trustStore, @Nullable String trustStorePassword) {
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        this.allowSelfSigned = allowSelfSigned;
        this.tlsProtocol = tlsProtocol;
    }

    /**
     * Prepares a fresh the {@link TLSContext} for an HTTPS connection.
     *
     * @param tlsProtocol     The {@link TLSProtocol}, that shall be used.
     * @param allowSelfSigned Set to {@code true} to allow the usage of self-signed {@link X509Certificate}s.
     */
    public TLSContext(@NotNull TLSProtocol tlsProtocol, boolean allowSelfSigned) {
        this.trustStore = null;
        this.trustStorePassword = null;
        this.allowSelfSigned = allowSelfSigned;
        this.tlsProtocol = tlsProtocol;
    }

    /**
     * Prepares a fresh the {@link TLSContext} for an HTTPS connection.<br>
     * This empty default constructor defaults to {@link TLSProtocol#TLSV1_3}, does not allow self-signed
     * {@link X509Certificate}s and will not configure a truststore.
     */
    @SuppressWarnings("unused")
    public TLSContext() {
        this(TLSProtocol.TLSV1_3, false);
    }

    /**
     * Returns {@code true}, if self-signed {@link X509Certificate}s shall be accepted.
     *
     * @return {@code true}, if self-signed {@link X509Certificate}s shall be accepted.
     */
    @SuppressWarnings("unused")
    public boolean isAllowSelfSigned() {
        return allowSelfSigned;
    }

    /**
     * Returns the selected {@link TLSProtocol}.
     *
     * @return The selected {@link TLSProtocol}.
     */
    @SuppressWarnings("unused")
    public @NotNull TLSProtocol getTlsProtocol() {
        return tlsProtocol;
    }

    /**
     * Returns the selected {@link X509Certificate} truststore file
     *
     * @return the selected {@link X509Certificate} truststore file
     */
    public @Nullable File getTrustStore() {
        return this.trustStore;
    }

    /**
     * Returns the password for the selected {@link X509Certificate} truststore file
     *
     * @return The password for the selected {@link X509Certificate} truststore file
     */
    public @Nullable String getTrustStorePassword() {
        return this.trustStorePassword;
    }

}
