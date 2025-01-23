package net.webpdf.wsclient.exception;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link ResultException} will be encountered in case a wsclient operation failed for some reason.<br>
 * A {@link ResultException} mostly serves as the common, generic, catchable base type for more specific exceptions that
 * provide more detailed information about the failure.
 * </p>
 */
public class ResultException extends Exception {

    private final @NotNull Error wsclientError;
    private final int errorCode;
    private final @Nullable String stackTraceMessage;

    /**
     * <p>
     * Constructs a new {@link ResultException} with the given information.<br>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this
     * exception's detail message.
     * </p>
     *
     * @param wsClientError     The wsclient specific {@link Error}.
     * @param errorCode         The numeric error code.
     * @param errorMessage      The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param stackTraceMessage The (optional) stacktrace message.
     * @param cause             The cause (which is saved for later retrieval by the {@link #getCause()} method).  (A {@code null}
     *                          value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    protected ResultException(@NotNull Error wsClientError, @Nullable String errorMessage, int errorCode,
            @Nullable String stackTraceMessage, @Nullable Exception cause
    ) {
        super(errorMessage, cause);
        this.wsclientError = wsClientError;
        this.errorCode = errorCode;
        this.stackTraceMessage = stackTraceMessage;
    }

    /**
     * Returns the wsclient {@link Error} of this {@link ResultException}.
     *
     * @return The wsclient {@link Error} of this {@link ResultException}.
     */
    public @NotNull Error getClientError() {
        return wsclientError;
    }

    /**
     * <p>
     * Returns the error code of this {@link ResultException}.<br>
     * For {@link ServerResultException}s this is the actual
     * <a href="https://docs.webpdf.de/docs/appendix/error-codes">webPDF server error code</a>,
     * other {@link ResultException}s shall return the numerical representation of the wsclient {@link Error}.<br>
     * </p>
     *
     * @return The error code of this {@link ResultException}.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Returns the message describing this {@link ResultException}.
     *
     * @return The message describing this {@link ResultException}.
     */
    @Override
    public @Nullable String getMessage() {
        return super.getMessage();
    }

    /**
     * <p>
     * Returns the stacktrace message of this {@link ResultException}.<br>
     * For most {@link ResultException}s this will return {@code null} - {@link ServerResultException}s shall return the
     * {@link String} representation of the stacktrace, that lead to the error on the webPDF server´s side.
     * </p>
     *
     * @return The stacktrace message of this {@link ResultException}.
     */
    public @Nullable String getStackTraceMessage() {
        return stackTraceMessage;
    }

}
