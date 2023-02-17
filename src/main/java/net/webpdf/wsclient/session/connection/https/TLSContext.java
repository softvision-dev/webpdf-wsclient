package net.webpdf.wsclient.session.connection.https;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.*;
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
 */
@SuppressWarnings({"unused"})
public class TLSContext {

    private static final TrustManager[] TRUST_ALL = new TrustManager[]{new AlwaysTrustManager()};

    private @Nullable SSLContext sslContext;
    private boolean allowSelfSigned = false;
    private @Nullable File trustStore = null;
    private @Nullable String trustStorePassword = null;
    private @NotNull TLSProtocol tlsProtocol = TLSProtocol.TLSV1_2;

    /**
     * Prepares a fresh the {@link TLSContext} for an HTTPS connection.
     */
    public TLSContext() {
    }

    /**
     * <p>
     * Initializes the {@link SSLContext}, using the parameters previously set for this {@link TLSContext} instance.
     * </p>
     * <p>
     * <b>Information:</b> Actually this is not exactly a "SSL" context, but a "TLS" context.
     * TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is no longer
     * supported by the webPDF wsclient, as it is obsolete and insecure.
     * </p>
     *
     * @throws ResultException Shall be thrown, if the {@link TLSContext} initialization failed.
     * @see #setAllowSelfSigned(boolean)
     * @see #setTLSProtocol(TLSProtocol)
     * @see #setTrustStore(File, String)
     */
    private void initSSLContext() throws ResultException {
        try {
            if (this.trustStore != null || allowSelfSigned) {
                this.sslContext = trustStore != null ?
                        new SSLContextBuilder()
                                .setProtocol(tlsProtocol.getName())
                                .loadTrustMaterial(
                                        trustStore, trustStorePassword != null ?
                                                trustStorePassword.toCharArray() : null, null
                                )
                                .build() :
                        new SSLContextBuilder()
                                .setProtocol(tlsProtocol.getName())
                                .build();
                if (allowSelfSigned) {
                    sslContext.init(new KeyManager[0], TRUST_ALL, new SecureRandom());
                }
            } else {
                this.sslContext = SSLContexts.createDefault();
            }

        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException |
                 IOException ex) {
            throw new ClientResultException(Error.HTTPS_IO_ERROR, ex);
        }
    }

    /**
     * When set to {@code true} self-signed {@link X509Certificate}s will be accepted.
     *
     * @param allowSelfSigned Set to {@code true} to allow the usage of self-signed {@link X509Certificate}s.
     * @return The {@link TLSContext} itself.
     */
    public @NotNull TLSContext setAllowSelfSigned(boolean allowSelfSigned) {
        this.allowSelfSigned = allowSelfSigned;
        return this;
    }

    /**
     * Sets the {@link X509Certificate} truststore file and it´s password. The truststore shall determine which
     * connection targets shall be deemed trustworthy.
     *
     * @param trustStore         Selects the {@link X509Certificate} truststore file
     * @param trustStorePassword Selects a possibly necessary password for the truststore file.
     * @return The {@link TLSContext} itself.
     */
    public @NotNull TLSContext setTrustStore(@Nullable File trustStore, @Nullable String trustStorePassword) {
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    /**
     * Selects the {@link TLSProtocol}, that shall be used.
     *
     * @param tlsProtocol The {@link TLSProtocol}, that shall be used.
     * @return The {@link TLSContext} itself.
     */
    public @NotNull TLSContext setTLSProtocol(@NotNull TLSProtocol tlsProtocol) {
        this.tlsProtocol = tlsProtocol;
        return this;
    }

    /**
     * <p>
     * Returns (and initializes) the resulting {@link SSLContext}.
     * </p>
     * <p>
     * <b>Information:</b> Actually this is not exactly a "SSL" context, but a "TLS" context.
     * TLS is the follow up protocol of the (better known) SSL (Secure Socket Layer) protocol - SSL is no longer
     * supported by the webPDF wsclient, as it is obsolete and insecure.
     * </p>
     *
     * @return The resulting {@link SSLContext}.
     * @see #initSSLContext()
     */
    public @NotNull SSLContext getSslContext() throws ResultException {
        if (sslContext == null) {
            initSSLContext();
        }
        return sslContext;
    }

}
