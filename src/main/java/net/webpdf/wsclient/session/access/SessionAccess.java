package net.webpdf.wsclient.session.access;

import net.webpdf.wsclient.exception.SessionAccessResultException;
import net.webpdf.wsclient.session.Session;
import org.apache.hc.client5.http.auth.Credentials;

/**
 * A class implementing {@link SessionAccess} shall provide {@link Credentials} for a {@link Session} via it´s
 * {@link #getCredentials()} method.
 *
 * @param <T> The type of {@link Credentials}, that are provided by a {@link SessionAccess} implementation.
 */
public interface SessionAccess<T extends Credentials> {

    /**
     * Provides {@link Credentials} for a {@link Session}.
     *
     * @return The {@link Credentials} provided by this {@link SessionAccess}.
     * @throws Exception May throw any sort of implementation dependent {@link Exception}s. A using class should make
     *                   sure to wrap such {@link Exception}s in a {@link SessionAccessResultException}.
     */
    T getCredentials() throws Exception;

}
