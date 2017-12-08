package net.webpdf.wsclient.ssl;

import javax.net.ssl.X509TrustManager;

/**
 * A X509TrustManager to accept all 'self-signed certificates'
 */
class SelfSignedCertTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string) throws java.security.cert.CertificateException {

    }

    @Override
    public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string) throws java.security.cert.CertificateException {

    }

    @Override
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return new java.security.cert.X509Certificate[0];
    }

}
