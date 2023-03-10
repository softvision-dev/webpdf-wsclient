package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.exception.AuthResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.material.token.OAuth2Token;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * A class implementing {@link OAuth2Provider} shall implement a {@link #provide(Session)} method
 * to determine a {@link OAuth2Token} for the authorization of a {@link Session}.<br>
 * This interface is directly intended to enable you, to implement your own custom authorization provider - refer to the
 * <a href="https://github.com/softvision-dev/webpdf-wsclient/wiki/OAuth2">wiki</a> for examples.
 * </p>
 * <p>
 * <b>Be aware:</b> An implementation of {@link AuthProvider} is not required to serve multiple {@link Session}s
 * at a time. It is expected to create a new {@link AuthProvider} for each existing {@link Session}.
 * </p>
 */
@SuppressWarnings("unused")
public interface OAuth2Provider extends AuthProvider {

    /**
     * Provides an {@link OAuth2Token} for the authorization of a {@link Session}.
     *
     * @param session The {@link Session} to provide authorization for.
     * @return The {@link OAuth2Token} to authorize the {@link Session} with.
     * @throws AuthResultException Shall be thrown, should the authorization fail for some reason.
     *                             (Use {@link AuthResultException} to wrap exceptions, that might occur during your
     *                             authorization request.)
     */
    @Override
    @NotNull OAuth2Token provide(@NotNull Session session) throws AuthResultException;

}
