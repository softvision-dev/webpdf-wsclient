package net.webpdf.wsclient.https;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContextBuilder;
import sun.security.ssl.SSLContextImpl;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class TLSContext {

    private SSLContext sslContext;
    private boolean allowSelfSigned = false;
    private File trustStore = null;
    private String trustStorePassword = null;
    private TLSProtocol protocol = TLSProtocol.TLSV1_2;

    /**
     * Prepared the TLS Context for a HTTPS connection.
     *
     * @throws ResultException a {@link ResultException}
     */
    private TLSContext() {
    }

    public static TLSContext createDefault(){
        return new TLSContext();
    }

    /**
     * Initializes the TSL context using the given parameters.
     *
     * @throws ResultException a {@link ResultException}
     */
    private void initSSLContext() throws ResultException {
        try {
            if (this.trustStore != null) {
                this.sslContext = new SSLContextBuilder()
                                      .loadTrustMaterial(
                                          trustStore,
                                          trustStorePassword != null ?
                                              trustStorePassword.toCharArray() : null,
                                          allowSelfSigned ? new TrustSelfSignedStrategy() : null)
                                      .setProtocol(protocol != null ? protocol.getName() : null)
                                      .build();
            } else if (allowSelfSigned) {
                this.sslContext = new SSLContextBuilder()
                                      .loadTrustMaterial(new TrustSelfSignedStrategy())
                                      .setProtocol(protocol != null ? protocol.getName() : null)
                                      .build();
            }

        } catch (KeyManagementException | KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException ex) {
            throw new ResultException(Result.build(Error.HTTPS_IO_ERROR, ex));
        }
    }

    /**
     * When set to true self signed certificates will be accepted.
     *
     * @param allowSelfSigned Self signed certificates will be accepted, when set to true.
     * @return returns self, to enable chained calls.
     */
    public TLSContext setAllowSelfSigned(boolean allowSelfSigned) {
        this.allowSelfSigned = allowSelfSigned;
        return this;
    }

    /**
     * Shall valid self signed certificates accepted?
     *
     * @return True, if self signed certificates may be accepted.
     */
    public boolean isAllowSelfSigned() {
        return allowSelfSigned;
    }


    /**
     * Sets the trust store file and the trust store password.
     *
     * @param trustStore         The trust store file.
     * @param trustStorePassword The trust store password.
     * @return returns self, to enable chained calls.
     */
    public TLSContext setTrustStore(File trustStore, String trustStorePassword) {
        this.trustStore = trustStore;
        this.trustStorePassword = trustStorePassword;
        return this;
    }

    /**
     * Sets the trust store file.
     *
     * @param trustStore The trust store file.
     * @return returns self, to enable chained calls.
     */
    public TLSContext setTrustStore(File trustStore) {
        this.trustStore = trustStore;
        return this;
    }

    /**
     * Returns the trust store file.
     *
     * @return The trust store file.
     */
    public File getTrustStore() {
        return trustStore;
    }

    /**
     * Returns the password for the trust store.
     *
     * @return The trust store password.
     */
    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    /**
     * Sets the TLS version, that shall be used.
     *
     * @param protocol The TLS version, that shall be used.
     * @return returns self, to enable chained calls.
     */
    public TLSContext setProtocol(TLSProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    /**
     * Returns the TLS version, that shall be used.
     *
     * @return The TLS version, that shall be used.
     */
    public TLSProtocol getProtocol() {
        return protocol;
    }

    /**
     * Returns the resulting SSL context.
     *
     * @return The resulting SSL context.
     */
    public SSLContext getSslContext() throws ResultException {
        if (sslContext == null) {
            initSSLContext();
        }
        return sslContext;
    }
}
