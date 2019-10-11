package net.webpdf.wsclient.exception;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Covers an operation result into a exception
 */
public class ResultException extends IOException {

    @NotNull
    private Result result;

    /**
     * Creates a new exception
     *
     * @param result result to be covered by the exception
     */
    public ResultException(@NotNull Result result) {
        super(result.getMessage(), result.getException());
        this.result = result;
    }

    /**
     * Returns the covered result
     *
     * @return result object
     */
    @NotNull
    public Result getResult() {
        return this.result;
    }

}
