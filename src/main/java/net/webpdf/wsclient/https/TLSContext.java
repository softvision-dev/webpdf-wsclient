package net.webpdf.wsclient.https;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.http.AlwaysTrustManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@SuppressWarnings({"unused"})
public class TLSContext {

    private static final TrustManager[] TRUST_ALL = new TrustManager[]{new AlwaysTrustManager()};

    @Nullable
    private SSLContext sslContext;
    private boolean allowSelfSigned = false;
    @Nullable
    private File trustStore = null;
    @Nullable
    private String trustStorePassword = null;
    @NotNull
    private TLSProtocol tlsProtocol = TLSProtocol.TLSV1_2;

    /**
     * Prepares the TLS Context for a HTTPS connection.
     */
    private TLSContext() {
    }

    /**
     * Static factory method, to create a fresh instance of this class with default values.
     *
     * @return A fresh instance of this class.
     */
    @NotNull
    public static TLSContext createDefault() {
        return new TLSContext();
    }

    /**
     * Initializes the TSL context using the given parameters.
     *
     * @throws ResultException a {@link ResultException}
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

        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
            throw new ResultException(Result.build(Error.HTTPS_IO_ERROR, ex));
        }
    }

    /**
     * When set to true self signed certificates will be accepted.
     *
     * @param allowSelfSigned Self signed certificates will be accepted, when set to true.
     */
    public void setAllowSelfSigned(boolean allowSelfSigned) {
        this.allowSelfSigned = allowSelfSigned;
    }

    /**
     * Sets the trust store file and the trust store password.
     *
     * @param trustStore         The trust store file.
     * @param trustStorePassword The trust store password.
     */
    public void setTrustStore(@Nullable File trustStore, @Nullable String trustStorePassword) {
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
    }

    /**
     * Returns the resulting SSL context.
     *
     * @return The resulting SSL context.
     */
    @NotNull
    public SSLContext getSslContext() throws ResultException {
        if (sslContext == null) {
            initSSLContext();
        }
        return sslContext;
    }

    /**
     * Set the TLS protocol, that shall be used.
     *
     * @param tlsProtocol The TLS protocol version, that shall be used.
     */
    public void setTLSProtocol(@NotNull TLSProtocol tlsProtocol) {
        this.tlsProtocol = tlsProtocol;
    }

}
