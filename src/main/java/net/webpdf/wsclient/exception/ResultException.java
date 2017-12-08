package net.webpdf.wsclient.exception;

import java.io.IOException;

/**
 * Covers an operation result into a exception
 */
public class ResultException extends IOException {

    private Result result = Result.build(Error.UNKNOWN_EXCEPTION);

    /**
     * Creates a new exception
     *
     * @param result result to be covered by the exception
     */
    public ResultException(Result result) {
        super(result != null ? result.getMessage() : "",
                result != null ? result.getException() : null);

        if (result != null) {
            this.result = result;
        }
    }

    /**
     * Returns the covered result
     *
     * @return result object
     */
    public Result getResult() {
        return this.result;
    }

}
