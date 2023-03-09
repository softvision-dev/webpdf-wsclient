package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.session.auth.SessionAuthProvider;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link AuthResultException} indicates some error during the execution of a used
 * {@link SessionAuthProvider}.<br>
 * The actual {@link Exception} can be requested by calling {@link #getCause()}.
 * </p>
 */
public class AuthResultException extends ResultException {

    /**
     * Creates a new {@link AuthResultException}, by wrapping the given {@link Exception} as it´s cause.
     * <p>
     *
     * @param cause The actual {@link Exception}, that caused the {@link AuthResultException}.
     */
    public AuthResultException(@Nullable Exception cause) {
        super(Error.AUTH_ERROR, cause != null ? cause.getMessage() : null, Error.AUTH_ERROR.getCode(), null, cause);
    }

}