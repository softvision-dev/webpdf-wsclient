package net.webpdf.wsclient.ssl;

import javax.net.ssl.X509TrustManager;

/**
 * A X509TrustManager to accept all 'self-signed certificates'
 */
class SelfSignedCertTrustManager implements X509TrustManager {

    /**
     * Given the partial or complete certificate chain provided by the peer, build a certificate path to a trusted root
     * and return if it can be validated and is trusted for client SSL authentication based on the authentication type.
     * The authentication type is determined by the actual certificate used. For instance, if RSAPublicKey is used, the
     * authType should be "RSA". Checking is case-sensitive.
     *
     * @param xcs    the peer certificate chain
     * @param string the authentication type based on the client certificate
     */
    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string) {

    }

    /**
     * Given the partial or complete certificate chain provided by the peer, build a certificate path to a trusted root
     * and return if it can be validated and is trusted for server SSL authentication based on the authentication type.
     * The authentication type is the key exchange algorithm portion of the cipher suites represented as a String, such as
     * "RSA", "DHE_DSS". Note: for some exportable cipher suites, the key exchange algorithm is determined at run time
     * during the handshake. For instance, for TLS_RSA_EXPORT_WITH_RC4_40_MD5, the authType should be RSA_EXPORT when an
     * ephemeral RSA key is used for the key exchange, and RSA when the key from the server certificate is used.
     * Checking is case-sensitive.
     *
     * @param xcs    the peer certificate chain
     * @param string the key exchange algorithm used
     */
    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string) {

    }

    /**
     * Return an array of certificate authority certificates which are trusted for authenticating peers.
     *
     * @return a non-null (possibly empty) array of acceptable CA issuer certificates.
     */
    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
    }

}
