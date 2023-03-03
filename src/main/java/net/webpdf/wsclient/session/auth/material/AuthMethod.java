package net.webpdf.wsclient.session.auth.material;

import org.apache.hc.core5.http.Header;
import org.jetbrains.annotations.NotNull;

/**
 * {@link AuthMethod} enumerates the supported authorization methods, that may be used to build authorization
 * {@link Header}s.
 *
 * @see #BASIC_AUTHORIZATION
 * @see #BEARER_AUTHORIZATION
 */
public enum AuthMethod {

    BASIC_AUTHORIZATION("Basic"),
    BEARER_AUTHORIZATION("Bearer");

    private final @NotNull String key;

    /**
     * Defines a new authorization method.
     *
     * @param key The {@link String} value of the authorization method.
     */
    AuthMethod(@NotNull String key) {
        this.key = key;
    }

    /**
     * Returns the {@link String} value of the authorization method.
     *
     * @return The {@link String} value of the authorization method.
     */
    public @NotNull String getKey() {
        return key;
    }

}
