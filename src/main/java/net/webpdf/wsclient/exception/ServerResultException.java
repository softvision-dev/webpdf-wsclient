package net.webpdf.wsclient.exception;

import net.webpdf.wsclient.openapi.WebserviceException;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link ServerResultException} indicates, that some fail state occurred webPDF server while executing
 * your request. The numerical error code provided by {@link #getErrorCode()} is identical to the
 * <a href="https://docs.webpdf.de/docs/appendix/error-codes">webPDF error codes</a>.
 * </p>
 */
public class ServerResultException extends ResultException {

    /**
     * Instantiates a new {@link ServerResultException} that wraps a webPDF server fail state.
     *
     * @param openApiException The openAPI REST {@link WebserviceException} describing the webPDF server fail state.
     */
    public ServerResultException(@Nullable WebserviceException openApiException) {
        this(
                Error.REST_EXECUTION,
                openApiException != null ? openApiException.getErrorMessage() : "",
                openApiException != null ? openApiException.getErrorCode() : 0,
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
                Error.SOAP_EXECUTION,
                soapStubException != null ? soapStubException.getFaultInfo().getErrorMessage() : "",
                soapStubException != null ? soapStubException.getFaultInfo().getErrorCode() : 0,
                soapStubException != null ? soapStubException.getFaultInfo().getStackTrace() : null
        );
    }

    /**
     * Instantiates a new {@link ServerResultException} that wraps a webPDF server fail state.
     *
     * @param errorCode    The <a href="https://docs.webpdf.de/docs/appendix/error-codes">webPDF
     *                     server error code</a>
     *                     wrapped by the {@link ServerResultException}.
     * @param errorMessage The message of the webPDF server fail state.
     * @param stackTrace   The stacktrace of the webPDF server fail state, as a {@link String}.
     */
    public ServerResultException(Error wsclientError, @Nullable String errorMessage, int errorCode,
            @Nullable String stackTrace) {
        super(wsclientError, errorMessage, errorCode, stackTrace, null);
    }

    /**
     * Returns a {@link String} representation of this {@link ServerResultException}.
     *
     * @return A {@link String} representation of this {@link ServerResultException}.
     */
    @Override
    public String toString() {
        return "Server error: " + (getMessage() != null ? getMessage() : "")
                + " (" + getErrorCode() + ")\n"
                + (getStackTraceMessage() != null && !getStackTraceMessage().isEmpty() ?
                "Server stack trace: " + getStackTraceMessage() + "\n" : "");
    }

}
