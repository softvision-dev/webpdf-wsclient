package net.webpdf.wsclient.schema.stubs;

import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * An instance of {@link WebServiceException} will be encountered in case the webPDF server´s response indicates the
 * failure of a webservice call, it shall describe the failure, by providing a detailed {@link FaultInfo} describing the
 * issue.
 * </p>
 */
public abstract class WebServiceException extends Exception {
    public WebServiceException(String message) {
        super(message);
    }

    public WebServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Returns the fault info object of this exception, containing more detailed information about the
     * {@link WebServiceException}, that occurred on the server side.
     *
     * @return returns fault bean: net.webpdf.wsclient.schema.stubs.toolbox.FaultInfo
     */
    public abstract @NotNull FaultInfo getFaultInfo();
}
