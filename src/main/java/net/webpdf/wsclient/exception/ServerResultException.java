package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.openapi.WebserviceException;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link ServerResultException} indicates, that some fail state occurred webPDF server while executing
 * your request. The numerical error code provided by {@link #getErrorCode()} is identical to the
 * <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF error codes</a>.
 * </p>
 */
public class ServerResultException extends ResultException {

    private final int errorCode;
    private final String errorMessage;
    private final String stackTraceMessage;

    /**
     * Instantiates a new {@link ServerResultException} that wraps a webPDF server fail state.
     *
     * @param openApiException The openAPI REST {@link WebserviceException} describing the webPDF server fail state.
     */
    public ServerResultException(@Nullable WebserviceException openApiException) {
        this(
                openApiException != null ? openApiException.getErrorCode() : 0,
                openApiException != null ? openApiException.getErrorMessage() : "",
                openApiException != null ? openApiException.getStackTrace() : null
        );
    }

    /**
     * Instantiates a new {@link ServerResultException} that wraps a webPDF server fail state.
     *
     * @param soapStubException The SOAP {@link WebserviceException} describing the webPDF server fail state.
     */
    public ServerResultException(@Nullable WebServiceException soapStubException) {
        this(
                soapStubException != null ? soapStubException.getFaultInfo().getErrorCode() : 0,
                soapStubException != null ? soapStubException.getFaultInfo().getErrorMessage() : "",
                soapStubException != null ? soapStubException.getFaultInfo().getStackTrace() : null
        );
    }

    /**
     * Instantiates a new {@link ServerResultException} that wraps a webPDF server fail state.
     *
     * @param errorCode    The <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF
     *                     server error code</a>
     *                     wrapped by the {@link ServerResultException}.
     * @param errorMessage The message of the webPDF server fail state.
     * @param stackTrace   The stacktrace of the webPDF server fail state, as a {@link String}.
     */
    public ServerResultException(int errorCode, @Nullable String errorMessage, @Nullable String stackTrace) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.stackTraceMessage = stackTrace;
    }

    /**
     * Returns the <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF server error code</a>
     * wrapped by this {@link ServerResultException}.
     *
     * @return The <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF server error code</a>
     * wrapped by this {@link ServerResultException}.
     */
    public int getErrorCode() {
        return this.errorCode;
    }

    /**
     * Returns the message belonging to the wrapped
     * <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF server error code</a>.
     *
     * @return The message belonging to the wrapped
     * <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF server error code</a>.
     */
    public @Nullable String getErrorMessage() {
        return this.errorMessage;
    }

    /**
     * Returns the stacktrace that lead to the wrapped
     * <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF server error code</a>.
     * as a {@link String}.
     *
     * @return The stacktrace that lead to the wrapped
     * <a href="https://portal.webpdf.de/webPDF/help/doc/en/appendix/error_codes.html">webPDF server error code</a>.
     * as a {@link String}.
     */
    public @Nullable String getStackTraceMessage() {
        return this.stackTraceMessage;
    }

    /**
     * Returns a {@link String} representation of this {@link ServerResultException}.
     *
     * @return A {@link String} representation of this {@link ServerResultException}.
     */
    @Override
    public String toString() {
        return "Server error: " + (getErrorMessage() != null ? getErrorMessage() : "")
                + " (" + getErrorCode() + ")\n"
                + (getStackTraceMessage() != null && !getStackTraceMessage().isEmpty() ?
                "Server stack trace: " + getStackTraceMessage() + "\n" : "");
    }

}
