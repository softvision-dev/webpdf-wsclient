package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.session.credentials.token.TokenProvider;

/**
 * <p>
 * An instance of {@link TokenProviderResultException} indicates some error during the execution of a set
 * {@link TokenProvider}.<br>
 * The actual {@link Exception} can be requested by calling {@link #getCause()}.
 * </p>
 */
public class TokenProviderResultException extends ResultException {

    /**
     * Creates a new {@link TokenProviderResultException}, by wrapping the given {@link Exception} as it´s cause.
     *
     * @param cause The actual {@link Exception}, that caused the {@link TokenProviderResultException}.
     */
    public TokenProviderResultException(Exception cause) {
        super(cause);
    }

}
