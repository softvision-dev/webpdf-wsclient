package net.webpdf.wsclient.session.credentials.token;

import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.Nullable;

/**
 * A class implementing {@link TokenProvider} produces access {@link Token}s, that a webPDF server shall grant a
 * {@link Session} and access for.
 */
public interface TokenProvider<T extends Token> {

    /**
     * Returns the access {@link Token} that the webPDF server shall provide a valid session for.
     *
     * @return The access {@link Token} that the webPDF server shall provide a valid session for.
     * @throws Exception Depending on the actual implementation, the {@link TokenProvider} may throw some
     *                   {@link Exception}, that must be handled later.
     */
    @Nullable T provideToken() throws Exception;

}
