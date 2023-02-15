package net.webpdf.wsclient.exception;

import jakarta.xml.ws.WebServiceException;
import net.webpdf.wsclient.schema.stubs.FaultInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * An instance of {@link Result} will be encountered in case the wsclient failed.<br>
 * It shall describe a wsclient failure, by providing an {@link Error}, an error message and an optional exception
 * describing the issue.<br>
 * It shall describe a webPDF server failure state, by wrapping a {@link WebServiceException} - see below.
 * </p>
 * <p>
 * <b>Important:</b> The hereby contained {@link Error} codes should not be confused with the webPDF server error codes,
 * those shall be contained in a {@link FaultInfo} instance instead.
 * </p>
 * <p>
 * <b>Important:</b> Should this {@link Result} represent a webPDF server failure state, the method
 * {@link #getException()} shall return a {@link WebServiceException}, which shall contain the matching
 * {@link FaultInfo}.
 * </p>
 *
 * @see Error
 * @see FaultInfo
 */
public final class Result {

    private final @NotNull Error error;
    private final @Nullable Exception exception;
    private final @NotNull List<String> messages = new ArrayList<>();
    private final int exitCode;

    /**
     * Creates a wsclient execution {@link Result}, for further processing of failures during webservice calls.
     *
     * @param error     The {@link Error}, that has occurred.
     * @param exitCode  The exitcode of the Result.
     * @param exception The exception, that caused the failure.
     */
    private Result(@NotNull Error error, int exitCode, @Nullable Exception exception) {
        this.error = error;
        this.exitCode = exitCode;
        this.exception = exception;
    }

    /**
     * This convenience method instantiates a {@link Result} instance, matching and wrapping the given {@link Error}.
     *
     * @param error The {@link Error}, that shall be wrapped.
     * @return The resulting {@link Result} instance.
     */
    public static @NotNull Result build(@NotNull Error error) {
        return new Result(error, 0, null);
    }

    /**
     * This convenience method instantiates a {@link Result} instance, matching and wrapping the given {@link Error}.
     * <p>
     *
     * @param error    The {@link Error}, that shall be wrapped.
     * @param exitCode The numeric exitcode resulting from the wsclient operation.
     * @return The resulting {@link Result} instance.
     */
    public static @NotNull Result build(@NotNull Error error, int exitCode) {
        return new Result(error, exitCode, null);
    }

    /**
     * This convenience method instantiates a {@link Result} instance, matching and wrapping the given {@link Error}.
     *
     * @param error     The {@link Error}, that shall be wrapped.
     * @param exception The {@link Exception}, that occurred during the wsclient execution and shall be attached to the
     *                  {@link Result}.
     * @return The resulting {@link Result} instance.
     */
    public static @NotNull Result build(@NotNull Error error, @Nullable Exception exception) {
        return new Result(error, 0, exception);
    }

    /**
     * Returns {@code true}, if the {@link Result} is representing a failure state. ({@code false} otherwise.)
     *
     * @return {@code true}, if the {@link Result} is representing a failure state. ({@code false} otherwise.)
     */
    public boolean isError() {
        return !Error.NONE.equals(this.error);
    }

    /**
     * Returns {@code true}, if the {@link Result} is representing a success state. ({@code false} otherwise.)
     *
     * @return {@code true}, if the {@link Result} is representing a success state. ({@code false} otherwise.)
     */
    boolean isSuccess() {
        return Error.NONE.equals(this.error);
    }

    /**
     * Returns {@code true}, if this {@link Result} is representing the same failure state, as the given {@link Error}.
     * ({@code false} otherwise.)
     *
     * @param error The {@link Error} to compare this {@link Result with.}
     * @return {@code true}, if this {@link Result} is representing the same failure state, as the given {@link Error}.
     * ({@code false} otherwise.)
     */
    boolean equalsError(@Nullable Error error) {
        return this.error.equals(error);
    }

    /**
     * Returns the wsclient execution {@link Error} represented by this {@link Result}.
     *
     * @return The wsclient execution {@link Error} represented by this {@link Result}.
     */
    public @NotNull Error getError() {
        return this.error;
    }

    /**
     * Returns the numeric error code represented by this {@link Result}.
     *
     * @return The numeric error code represented by this {@link Result}.
     * @see Error#getCode()
     * @see #getError()
     */
    public int getCode() {
        return this.error.getCode();
    }

    /**
     * Returns a message describing the {@link Error} represented by this {@link Result}.
     *
     * @return A message describing the {@link Error} represented by this {@link Result}.
     * @see Error#getMessage()
     * @see #getError()
     */
    public @NotNull String getMessage() {

        String errorMessage = this.error.getMessage();
        String detailMessage = StringUtils.join(this.messages, "\n");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(errorMessage);
        stringBuilder.append(!detailMessage.isEmpty() && stringBuilder.length() > 0 ? "\n" : "");
        stringBuilder.append(detailMessage);
        if (this.exitCode != 0) {
            stringBuilder.append(stringBuilder.length() > 0 ? " " : "");
            stringBuilder.append("[").append(this.exitCode).append("]");
        }
        return stringBuilder.toString();
    }

    /**
     * <p>
     * Returns the (optional) {@link Exception}, that may have been attached to this {@link Result}.
     * </p>
     * <p>
     * <b>Important:</b> Should this method return a {@link WebServiceException} this {@link Result} represents a webPDF
     * server failure.
     * </p>
     *
     * @return The (optional) {@link Exception}, that may have been attached to this {@link Result}.
     */
    public @Nullable Exception getException() {
        return this.exception;
    }

    /**
     * Appends the given text to the end of the message describing this {@link Result}.
     *
     * @param message The text to append to the end of the message describing this {@link Result}.
     * @return The {@link Result} instance itself.
     */
    public @NotNull Result appendMessage(@Nullable String message) {
        if (message != null && !message.isEmpty()) {
            this.messages.add(StringUtils.capitalize(message));
        }
        return this;
    }

}
