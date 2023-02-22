package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.auth.Credentials;

/**
 * A class implementing {@link AuthProvider} shall provide {@link Credentials} for a {@link Session} via it´s
 * {@link #provide()} method.
 *
 * @param <T> The type of {@link Credentials}, that are provided by a {@link AuthProvider} implementation.
 */
public interface AuthProvider<T extends Credentials> {

    /**
     * Provides authentication/authorization {@link Credentials} for a {@link Session}.
     *
     * @return The authentication/authorization {@link Credentials} provided by this {@link AuthProvider}.
     * @throws AuthResultException Shall be thrown, should the authentication/authorization fail for some reason.
     */
    T provide() throws AuthResultException;

}