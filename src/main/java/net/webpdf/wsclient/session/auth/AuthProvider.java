package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.auth.material.token.SessionToken;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * A class implementing {@link AuthProvider} shall provide {@link AuthMaterial} for a {@link Session} via it´s
 * {@link #provide(Session)} method.
 * </p>
 * <p>
 * <b>Be aware:</b> An implementation of {@link AuthProvider} is not required to serve multiple {@link Session}s
 * at a time. It is expected to create a new {@link AuthProvider} for each existing {@link Session}.
 * </p>
 */
public interface AuthProvider {

    /**
     * Provides authorization {@link SessionToken} for a {@link Session}.
     *
     * @param session The session to provide authorization for.
     * @return The {@link AuthMaterial} provided by this {@link AuthProvider}.
     * @throws AuthResultException Shall be thrown, should the authentication/authorization fail for some reason.
     */
    @NotNull AuthMaterial provide(@NotNull Session session) throws AuthResultException;

}