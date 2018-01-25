package net.webpdf.wsclient.ssl;

import org.apache.http.conn.ssl.TrustStrategy;

import java.security.cert.X509Certificate;

public class SelfSignedTrustStrategy implements TrustStrategy {
    /**
     * Determines whether the certificate chain can be trusted without consulting the trust manager configured in the
     * actual SSL context. This method can be used to override the standard JSSE certificate verification process.
     * Please note that, if this method returns false, the trust manager configured in the actual SSL context can still
     * clear the certificate as trusted.
     *
     * @param x509Certificates the peer certificate chain
     * @param s                the authentication type based on the client certificat
     * @return true if the certificate can be trusted without verification by the trust manager, false otherwise.
     */
    @Override
    public boolean isTrusted(X509Certificate[] x509Certificates, String s) {
        return true;
    }
}
