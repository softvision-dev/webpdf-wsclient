package net.webpdf.wsclient.session.token;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * <p>
 * A class implementing {@link TokenProvider} provides a {@link Token}, that a webPDF server shall grant a session
 * and access for.
 * </p>
 * <p>
 * For example such a {@link TokenProvider} can be used to prepare an OAuth authorization access token, that belongs
 * to an authorization provider known to the webPDF server.
 * </p>
 */
public interface TokenProvider {

    /**
     * Returns the access {@link Token} that the webPDF server shall provide a valid session for.
     *
     * @return The access {@link Token} that the webPDF server shall provide a valid session for.
     */
    @Nullable Token provideToken() throws IOException;

}
