package net.webpdf.wsclient.schema.stubs;

import jakarta.xml.ws.WebFault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link ConverterWebServiceException} will be encountered in case the webPDF server´s response indicates the
 * failure of a webservice call, it shall describe the failure, by providing a detailed {@link ConverterFaultInfo} describing the
 * issue.
 * </p>
 */
@WebFault(name = "WebserviceException")
public class ConverterWebServiceException extends WebServiceException {
    /**
     * Java type that goes as SOAP envelop:Fault detail element.
     */
    private final @NotNull ConverterFaultInfo faultInfo;

    /**
     * Collects information concerning a webPDF {@link ConverterWebServiceException}, that has been encountered, while calling a
     * webPDF webservice.
     *
     * @param faultInfo The fault info containing further information concerning the error.
     * @param message   The message, describing the occurred error.
     */
    public ConverterWebServiceException(@Nullable String message, @NotNull ConverterFaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * Collects information concerning a webPDF {@link ConverterWebServiceException}, that has been encountered, while calling a
     * webPDF webservice.
     *
     * @param faultInfo The fault info containing further information concerning the error.
     * @param cause     The cause of the error.
     * @param message   The message, describing the occurred error.
     */
    public ConverterWebServiceException(@Nullable String message, @NotNull ConverterFaultInfo faultInfo, @Nullable Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * Returns the fault info object of this exception, containing more detailed information about the
     * {@link ConverterWebServiceException}, that occurred on the server side.
     *
     * @return returns fault bean: net.webpdf.wsclient.schema.stubs.ConverterFaultInfo
     */
    public @NotNull ConverterFaultInfo getFaultInfo() {
        return faultInfo;
    }

}
