package net.webpdf.wsclient.session.access.token;

import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;

/**
 * An instance of {@link Token} wraps an access token that can be used to authorize a webPDF server {@link Session}.
 */
public interface Token {

    /**
     * Returns the access token {@code String} value.
     *
     * @return The access token {@code String} value.
     */
    @NotNull String getToken();

}
