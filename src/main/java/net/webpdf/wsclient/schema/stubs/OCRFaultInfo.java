
package net.webpdf.wsclient.schema.stubs;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link OCRFaultInfo} will be encountered in case the webPDF server´s response indicates the
 * failure of a webservice call. <br>
 * It shall describe the failure, by providing an error code, an error message and an optional exception describing the
 * issue.
 * </p>
 *
 * <p>Java-class for the FaultInfo complex type.
 *
 * <p>the following schema fragment contains the expected content, that may be contained in this class.
 *
 * <pre>
 * &lt;complexType name="FaultInfo"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="stackTrace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings({"unused", "JavadocLinkAsPlainText"})
@XmlType(name = "FaultInfo", propOrder = {
        "errorCode",
        "errorMessage",
        "stackTrace"
})
public class OCRFaultInfo implements FaultInfo {
    private int errorCode;
    private @Nullable String errorMessage;
    private @Nullable String stackTrace;

    /**
     * Return the errorCode.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Set the errorCode.
     */
    public void setErrorCode(int value) {
        this.errorCode = value;
    }

    /**
     * Return the errorMessage.
     *
     * @return possible object is
     * {@link String }
     */
    public @Nullable String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the errorMessage.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setErrorMessage(@Nullable String value) {
        this.errorMessage = value;
    }

    /**
     * Return the stacktrace.
     *
     * @return possible object is
     * {@link String }
     */
    public @Nullable String getStackTrace() {
        return stackTrace;
    }

    /**
     * Set the stacktrace.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStackTrace(@Nullable String value) {
        this.stackTrace = value;
    }

}
