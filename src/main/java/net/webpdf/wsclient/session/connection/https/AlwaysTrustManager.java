package net.webpdf.wsclient.session.connection.https;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * An instance of {@link AlwaysTrustManager} implements a {@link X509TrustManager} that shall accept all
 * {@link X509Certificate} as trustworthy.
 */
public class AlwaysTrustManager implements X509TrustManager {

    /**
     * Does never throw exceptions for {@link X509Certificate}s - i.e.: Does not validate certificates and trusts all
     * certificates.
     *
     * @param certificates The peer {@link X509Certificate}s chain.
     * @param authType     The used authentication type based on the client {@link X509Certificate}.
     */
    @Override
    public void checkClientTrusted(@Nullable X509Certificate[] certificates, @Nullable String authType) {
    }

    /**
     * Does never throw exceptions for {@link X509Certificate}s - i.e.: Does not validate certificates and trusts all
     * certificates.
     *
     * @param certificates The peer {@link X509Certificate} chain.
     * @param authType     The used authentication type based on the client {@link X509Certificate}.
     */
    @Override
    public void checkServerTrusted(@Nullable X509Certificate[] certificates, @Nullable String authType) {
    }

    /**
     * This implementation does not provide accepted {@link X509Certificate}s authority information at all.
     *
     * @return A non-null (possibly empty) array of acceptable CA issuer {@link X509Certificate}s.
     */
    @Override
    public @NotNull X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

}
