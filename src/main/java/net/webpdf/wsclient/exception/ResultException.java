package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.schema.stubs.FaultInfo;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * <p>
 * An instance of {@link ResultException} will be encountered in case a wsclient operation failed.<br>
 * It shall describe the failure, by providing a detailed {@link Result} describing the issue.<br>
 * It shall describe a webPDF server failure state, by wrapping a {@link WebServiceException} - see below.
 * </p>
 * <p>
 * The hereby contained {@link Result} and {@link Error} instances shall allow to evaluate such a wsclient failure
 * state.
 * </p>
 * <p>
 * <b>Important:</b> The hereby contained {@link Result} should not be confused with the webPDF server´s
 * {@link FaultInfo}.
 * </p>
 * <p>
 * <b>Important:</b> Should a webPDF server error ({@link WebServiceException}) occur, it shall be wrapped by a
 * {@link ResultException}, which shall return the {@link WebServiceException} via {@link #getCause()} or via the
 * contained {@link Result}.
 * </p>
 *
 * @see Result#getError()
 * @see Result#getException()
 * @see Result
 * @see Error
 */
public class ResultException extends IOException {

    private final @NotNull Result result;

    /**
     * Creates a wsclient {@link ResultException}, for further processing of failures during webservice calls.
     *
     * @param result The {@link Result} to be covered by the exception
     */
    public ResultException(@NotNull Result result) {
        super(result.getMessage(), result.getException());
        this.result = result;
    }

    /**
     * Returns the covered {@link Result}.
     *
     * @return The covered {@link Result}.
     */
    public @NotNull Result getResult() {
        return this.result;
    }

}
