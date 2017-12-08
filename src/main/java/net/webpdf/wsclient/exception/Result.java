package net.webpdf.wsclient.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An operation result with an error code (Error), an error message and an optional exception.
 */
public final class Result {

    public static final Result SUCCESS = new Result(Error.NONE, 0, null);
    private Error error;
    private Exception exception;
    private List<String> messages = new ArrayList<>();
    private boolean joinMessages = true;
    private int exitCode;

    private Result(Error error, int exitCode, Exception exception) {
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
    public static Result build(Error error) {
        return new Result(error, 0, null);
    }

    public static Result build(Error error, int exitCode) {
        return new Result(error, exitCode, null);
    }

    /**
     * Creates a new instance of an operation result
     *
     * @param error     wrapped error code
     * @param exception wrapped exception
     * @return new result instance
     */
    public static Result build(Error error, Exception exception) {
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
    public boolean isSuccess() {
        return Error.NONE.equals(this.error);
    }

    /**
     * Compares the current error with a given error
     *
     * @param error error code to compare with
     * @return true = the error equals with the given error
     */
    public boolean equalsError(Error error) {
        return this.error != null && this.error.equals(error);
    }

    /**
     * Gets the error of the result
     *
     * @return error
     */
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
    public String getMessage() {

        String errorMessage = this.error.getMessage();
        String detailMessage = StringUtils.join(this.messages, "\n");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(detailMessage.isEmpty() || this.joinMessages ? errorMessage : "");
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
    public Exception getException() {
        return this.exception;
    }

    /**
     * Adds an additional error message text
     *
     * @param message additional message
     * @return the result object
     */
    public Result addMessage(String message) {
        if (message != null && !message.isEmpty()) {
            this.messages.add(StringUtils.capitalize(message));
        }
        return this;
    }
}
