package net.webpdf.wsclient.session.auth;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.token.OAuth2Token;

/**
 * A class implementing {@link OAuth2TokenProvider} shall implement a {@link #provide()} method to determine a
 * {@link OAuth2Token} for the authorization of a {@link Session}.
 */
@SuppressWarnings("unused")
public interface OAuth2TokenProvider extends AuthProvider<OAuth2Token> {

}