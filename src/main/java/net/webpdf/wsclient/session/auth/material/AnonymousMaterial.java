package net.webpdf.wsclient.session.auth.material;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.AnonymousAuthProvider;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.core5.http.Header;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link AnonymousMaterial} serves to establish an anonymous {@link Session} via the
 * {@link AnonymousAuthProvider}.<br>
 * None of the hereby implemented methods shall provide usable authentication or authorization material.
 * </p>
 */
public class AnonymousMaterial implements AuthMaterial {

    /**
     * {@link AnonymousMaterial} does not provide authentication {@link Credentials}.
     *
     * @return Always {@code null}.
     */
    @Override
    public @Nullable Credentials getCredentials() {
        return null;
    }


    /**
     * {@link AnonymousMaterial} does not provide authorization {@link Header}s.
     *
     * @return Always {@code null}.
     */
    @Override
    public @Nullable Header getAuthHeader() {
        return null;
    }


    /**
     * {@link AnonymousMaterial} does not provide authorization material.
     *
     * @return Always {@code null}.
     */
    @Override
    public @Nullable String getRawAuthHeader() {
        return null;
    }

}
