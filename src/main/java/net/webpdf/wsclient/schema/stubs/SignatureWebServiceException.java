package net.webpdf.wsclient.schema.stubs;

import jakarta.xml.ws.WebFault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link SignatureWebServiceException} will be encountered in case the webPDF server´s response indicates the
 * failure of a webservice call, it shall describe the failure, by providing a detailed {@link FaultInfo} describing the
 * issue.
 * </p>
 */
@WebFault(name = "WebserviceException")
public class SignatureWebServiceException extends WebServiceException {
    /**
     * Java type that goes as SOAP envelop:Fault detail element.
     */
    private final @NotNull SignatureFaultInfo faultInfo;

    /**
     * Collects information concerning a webPDF {@link SignatureWebServiceException}, that has been encountered, while calling a
     * webPDF webservice.
     *
     * @param faultInfo The fault info containing further information concerning the error.
     * @param message   The message, describing the occurred error.
     */
    public SignatureWebServiceException(@Nullable String message, @NotNull SignatureFaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * Collects information concerning a webPDF {@link SignatureWebServiceException}, that has been encountered, while calling a
     * webPDF webservice.
     *
     * @param faultInfo The fault info containing further information concerning the error.
     * @param cause     The cause of the error.
     * @param message   The message, describing the occurred error.
     */
    public SignatureWebServiceException(@Nullable String message, @NotNull SignatureFaultInfo faultInfo, @Nullable Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * Returns the fault info object of this exception, containing more detailed information about the
     * {@link SignatureWebServiceException}, that occurred on the server side.
     *
     * @return returns fault bean: net.webpdf.wsclient.schema.stubs.SignatureFaultInfo
     */
    public @NotNull SignatureFaultInfo getFaultInfo() {
        return faultInfo;
    }

}
