package net.webpdf.wsclient.exception;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * An instance of {@link ClientResultException} will be encountered in case a wsclient operation failed on the client´s
 * side. It shall describe the failure, by providing a {@link Error} and a message describing the issue.
 * </p>
 * <p>
 * <b>Important:</b> A {@link ClientResultException} is describing failures in an application implementing a wsclient
 * and should never be used to indicate fail states on the side of the webPDF server.<br>
 * {@link ServerResultException} shall be reserved for that purpose.
 * </p>
 *
 * @see Error
 */
public class ClientResultException extends ResultException {

    private final @NotNull List<String> messages = new ArrayList<>();

    /**
     * Creates a new {@link ResultException} wrapping the given wsclient {@link Error} fail state.
     *
     * @param error The wsclient {@link Error} fail state to wrap.
     */
    public ClientResultException(@NotNull Error error) {
        this(error, null);
    }

    /**
     * <p>
     * Creates a new {@link ResultException} wrapping the given wsclient {@link Error} fail state, exit code and
     * {@link Exception} cause.
     * </p>
     * <p>
     * <b>Important:</b> A {@link ClientResultException} is describing failures in an application implementing a
     * wsclient and should never be used to indicate fail states on the side of the webPDF server.<br>
     * {@link ServerResultException} shall be reserved for that purpose.
     * </p>
     *
     * @param error The wsclient {@link Error} fail state to wrap.
     * @param cause The {@link Exception} that caused this failure.
     */
    public ClientResultException(@NotNull Error error, @Nullable Exception cause) {
        super(error, error.getMessage(), error.getCode(), null, cause);
    }

    /**
     * Returns the detail message string of this throwable.
     *
     * @return the detail message string of this {@code Throwable} instance
     * (which may be {@code null}).
     */
    @Override
    public @Nullable String getMessage() {
        String errorMessage = getWsclientError().getMessage();
        String detailMessage = StringUtils.join(this.messages, "\n");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(errorMessage);
        stringBuilder.append(!detailMessage.isEmpty() && stringBuilder.length() > 0 ? "\n" : "");
        stringBuilder.append(detailMessage);
        return stringBuilder.toString();
    }

    /**
     * Appends the given text to the end of the message describing this {@link ResultException}.
     *
     * @param message The text to append to the end of the message describing this {@link ResultException}.
     * @return The {@link ResultException} instance itself.
     */
    public @NotNull ResultException appendMessage(@Nullable String message) {
        if (message != null && !message.isEmpty()) {
            this.messages.add(StringUtils.capitalize(message));
        }
        return this;
    }

    /**
     * Returns {@code true}, if this {@link ResultException} is representing the same failure state, as the given
     * {@link Error}. ({@code false} otherwise.)
     *
     * @param error The {@link Error} to compare this {@link ResultException with.}
     * @return {@code true}, if this {@link ResultException} is representing the same failure state, as the given
     * {@link Error}. ({@code false} otherwise.)
     */
    @SuppressWarnings("unused")
    boolean equalsError(@Nullable Error error) {
        return getWsclientError().equals(error);
    }

    /**
     * Returns a {@link String} representation of this {@link ClientResultException}.
     *
     * @return A {@link String} representation of this {@link ClientResultException}.
     */
    @Override
    public String toString() {
        return getMessage();
    }

}
