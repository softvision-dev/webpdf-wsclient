package net.webpdf.wsclient.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class AlwaysTrustManager implements X509TrustManager {

    /**
     * Does never throw exceptions for certificates - ie.: Does not validate certificates and trusts all certificates.
     *
     * @param certificates The peer certificate chain.
     * @param authType     The used authentication type based on the client certificate.
     */
    @Override
    public void checkClientTrusted(@Nullable X509Certificate[] certificates, @Nullable String authType) {
    }

    /**
     * Does never throw exceptions for certificates - ie.: Does not validate certificates and trusts all certificates.
     *
     * @param certificates The peer certificate chain.
     * @param authType     The used authentication type based on the client certificate.
     */
    @Override
    public void checkServerTrusted(@Nullable X509Certificate[] certificates, @Nullable String authType) {
    }

    /**
     * This implementation does not provide accepted certificate authority information at all.
     *
     * @return A non-null (possibly empty) array of acceptable CA issuer certificates.
     */
    @Override
    @NotNull
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
