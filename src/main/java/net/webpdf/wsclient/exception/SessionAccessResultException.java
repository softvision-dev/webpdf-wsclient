package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.session.access.SessionAccess;

/**
 * <p>
 * An instance of {@link SessionAccessResultException} indicates some error during the execution of a used
 * {@link SessionAccess} provider.<br>
 * The actual {@link Exception} can be requested by calling {@link #getCause()}.
 * </p>
 */
public class SessionAccessResultException extends ResultException {

    /**
     * Creates a new {@link SessionAccessResultException}, by wrapping the given {@link Exception} as it´s cause.
     *
     * @param cause The actual {@link Exception}, that caused the {@link SessionAccessResultException}.
     */
    public SessionAccessResultException(Exception cause) {
        super(cause);
    }

}
