package net.webpdf.wsclient.exception;

/**
 * <p>
 * An instance of {@link ResultException} will be encountered in case a wsclient operation failed for some reason.<br>
 * A {@link ResultException} mostly serves as the common, generic, catchable base type for more specific exceptions that
 * provide more detailed information about the failure.
 * </p>
 */
public class ResultException extends Exception {

    /**
     * <p>
     * Constructs a new {@link ResultException} with {@code null} as its detail message.<br>
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     * </p>
     */
    protected ResultException() {
        super();
    }

    /**
     * <p>
     * Constructs a new {@link ResultException} with the specified detail message.<br>
     * The cause is not initialized, and may subsequently be initialized by a call to {@link #initCause}.
     * </p>
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()}
     *                method.
     */
    protected ResultException(String message) {
        super(message);
    }

    /**
     * <p>
     * Constructs a new {@link ResultException} with the specified detail message and cause.<br>
     * Note that the detail message associated with {@code cause} is <i>not</i> automatically incorporated in this
     * exception's detail message.
     * </p>
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).  (A {@code null}
     *                value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    protected ResultException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * <p>
     * Constructs a new {@link ResultException} with the specified cause and a detail message of
     * {@code (cause==null ? null : cause.toString())} (which typically contains the class and detail message of
     * {@code cause}).
     * </p>
     *
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null}
     *              value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    protected ResultException(Exception cause) {
        super(cause);
    }

}
