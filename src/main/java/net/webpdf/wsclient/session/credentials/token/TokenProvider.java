package net.webpdf.wsclient.session.credentials.token;

import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * A class implementing {@link TokenProvider} produces access {@link Token}s, that a webPDF server shall grant a
 * {@link Session} and access for.
 */
public interface TokenProvider<T extends Token> {

    /**
     * Returns the access {@link Token} that the webPDF server shall provide a valid session for.
     *
     * @return The access {@link Token} that the webPDF server shall provide a valid session for.
     */
    @Nullable T provideToken() throws IOException;

}
