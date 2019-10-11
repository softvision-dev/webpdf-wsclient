package net.webpdf.wsclient.exception;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An operation result with an error code (Error), an error message and an optional exception.
 */
public final class Result {

    @NotNull
    private Error error;
    @Nullable
    private Exception exception;
    @NotNull
    private List<String> messages = new ArrayList<>();
    private int exitCode;

    /**
     * Creates a webservice execution result, for further processing of failures during webservice calls.
     *
     * @param error     The error, that has occurred.
     * @param exitCode  The exitcode of the Result.
     * @param exception The exception, that caused the failure.
     */
    private Result(@NotNull Error error, int exitCode, @Nullable Exception exception) {
        this.error = error;
        this.exitCode = exitCode;
        this.exception = exception;
    }

    /**
     * Builds a new instance of an operation result
     *
     * @param error wrapped error code
     * @return new result instance
     */
    public static Result build(@NotNull Error error) {
        return new Result(error, 0, null);
    }

    /**
     * Builds a new instance of an operation result
     *
     * @param error    wrapped error code
     * @param exitCode wrapped exit code
     * @return new result instance
     */
    public static Result build(@NotNull Error error, int exitCode) {
        return new Result(error, exitCode, null);
    }

    /**
     * Creates a new instance of an operation result
     *
     * @param error     wrapped error code
     * @param exception wrapped exception
     * @return new result instance
     */
    public static Result build(@NotNull Error error, @Nullable Exception exception) {
        return new Result(error, 0, exception);
    }

    /**
     * Is this result an error?
     *
     * @return true = is an error result
     */
    public boolean isError() {
        return !Error.NONE.equals(this.error);
    }

    /**
     * Is this an result for a successful operation?
     *
     * @return true = successful operation result
     */
    boolean isSuccess() {
        return Error.NONE.equals(this.error);
    }

    /**
     * Compares the current error with a given error
     *
     * @param error error code to compare with
     * @return true = the error equals with the given error
     */
    boolean equalsError(@Nullable Error error) {
        return this.error.equals(error);
    }

    /**
     * Gets the error of the result
     *
     * @return error
     */
    @NotNull
    public Error getError() {
        return this.error;
    }

    /**
     * Gets the error code of the result
     *
     * @return error code
     */
    public int getCode() {
        return this.error.getCode();
    }

    /**
     * Gets the error message of the result
     *
     * @return error message
     */
    @NotNull
    public String getMessage() {

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
     * Returns the exception behind the result
     *
     * @return exception
     */
    @Nullable
    public Exception getException() {
        return this.exception;
    }

    /**
     * Adds an additional error message text
     *
     * @param message additional message
     * @return the result object
     */
    @NotNull
    public Result addMessage(@Nullable String message) {
        if (message != null && !message.isEmpty()) {
            this.messages.add(StringUtils.capitalize(message));
        }
        return this;
    }

}
