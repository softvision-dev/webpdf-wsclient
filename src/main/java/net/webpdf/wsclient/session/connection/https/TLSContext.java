package net.webpdf.wsclient.session.connection.https;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * <p>
 * An instance of {@link TLSContext} prepares a {@link TLSProtocol} context for encrypted HTTPS connections.
 * </p>
 * <p>
 * <b>Information:</b> TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is
 * no longer supported by the webPDF wsclient, as it is obsolete and insecure.
 * </p>
 * <p>
 * <b>Be aware:</b> An implementation of {@link TLSContext} is not required to serve multiple {@link Session}s
 * at a time. It is expected to create a new {@link TLSContext} for each existing {@link Session}
 * (That shall use HTTPS).
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
     * @param allowSelfSigned    Set to {@code true} to trust every {@link X509Certificate} chain the server presents
     *                           (not only self-signed ones). For REST sessions this also disables the verification
     *                           of the server´s host name. Use this for test environments only.
     * @param tlsProtocol        The {@link TLSProtocol}, that shall be used. (Only applied, if a truststore is set or
     *                           {@code allowSelfSigned} is {@code true}, see {@link #create()}.)
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
     * @param tlsProtocol     The {@link TLSProtocol}, that shall be used. (Only applied, if {@code allowSelfSigned}
     *                        is {@code true}, see {@link #create()}.)
     * @param allowSelfSigned Set to {@code true} to trust every {@link X509Certificate} chain the server presents (not
     *                        only self-signed ones). For REST sessions this also disables the verification of the
     *                        server´s host name. Use this for test environments only.
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
     * Returns {@code true}, if every {@link X509Certificate} chain the server presents shall be trusted (not only
     * self-signed ones). For REST sessions this also disables the verification of the server´s host name.
     *
     * @return {@code true}, if every {@link X509Certificate} chain shall be trusted.
     */
    @SuppressWarnings("unused")
    public boolean isAllowSelfSigned() {
        return allowSelfSigned;
    }

    /**
     * Returns the selected {@link TLSProtocol}. (It is only applied, if a truststore is set or self-signed
     * certificates are allowed, see {@link #create()}.)
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

    /**
     * <p>
     * Returns (and/or initializes) the {@link Session}´s {@link SSLContext}.
     * </p>
     * <p>
     * <b>Information:</b> Actually this is not exactly a "SSL" context, but a "TLS" context.
     * TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is no longer
     * supported by the webPDF wsclient, as it is obsolete and insecure.
     * </p>
     * <p>
     * <b>Be aware:</b> Without a truststore and with self-signed certificates disallowed, this returns a new
     * {@link SSLContext} initialized with the JVM´s default trust material and without client key material (a client
     * certificate configured via {@code javax.net.ssl.keyStore} is not used). The selected {@link TLSProtocol} is not
     * applied then, the protocol versions are negotiated as configured for the JVM. With {@link #isAllowSelfSigned()}
     * every certificate chain is trusted.
     * </p>
     *
     * @return The resulting {@link SSLContext}.
     * @throws ResultException Shall be thrown, should initializing the {@link SSLContext} fail.
     */
    public @NotNull SSLContext create() throws ResultException {
        try {
            if (!isAllowSelfSigned() && getTrustStore() == null) {
                return SSLContexts.createDefault();
            }
            SSLContextBuilder builder = new SSLContextBuilder().setProtocol(getTlsProtocol().getName());
            if (getTrustStore() != null) {
                builder.loadTrustMaterial(getTrustStore(),
                        getTrustStorePassword() != null ? getTrustStorePassword().toCharArray() : null,
                        isAllowSelfSigned() ? TrustAllStrategy.INSTANCE : null);
            } else {
                builder.loadTrustMaterial(TrustAllStrategy.INSTANCE);
            }
            return builder.build();
        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 IOException ex) {
            throw new ClientResultException(Error.TLS_INITIALIZATION_FAILURE, ex);
        }
    }

}
