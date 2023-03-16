package net.webpdf.wsclient.schema.stubs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.ws.WebFault;

/**
 * <p>
 * An instance of {@link WebServiceException} will be encountered in case the webPDF server´s response indicates the
 * failure of a webservice call, it shall describe the failure, by providing a detailed {@link FaultInfo} describing the
 * issue.
 * </p>
 * <p>
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * </p>
 */
@WebFault(name = "WebserviceException")
public class WebServiceException extends Exception {

    /**
     * Java type that goes as SOAP envelop:Fault detail element.
     */
    private final @NotNull FaultInfo faultInfo;

    /**
     * Collects information concerning a webPDF {@link WebServiceException}, that has been encountered, while calling a
     * webPDF webservice.
     *
     * @param faultInfo The fault info containing further information concerning the error.
     * @param message   The message, describing the occurred error.
     */
    public WebServiceException(@Nullable String message, @NotNull FaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * Collects information concerning a webPDF {@link WebServiceException}, that has been encountered, while calling a
     * webPDF webservice.
     *
     * @param faultInfo The fault info containing further information concerning the error.
     * @param cause     The cause of the error.
     * @param message   The message, describing the occurred error.
     */
    public WebServiceException(@Nullable String message, @NotNull FaultInfo faultInfo, @Nullable Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * Returns the fault info object of this exception, containing more detailed information about the
     * {@link WebServiceException}, that occurred on the server side.
     *
     * @return returns fault bean: net.webpdf.wsclient.schema.stubs.toolbox.FaultInfo
     */
    public @NotNull FaultInfo getFaultInfo() {
        return faultInfo;
    }

}
