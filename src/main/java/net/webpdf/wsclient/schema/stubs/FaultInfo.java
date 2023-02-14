
package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * An instance of {@link FaultInfo} will be encountered in case the webPDF server´s response indicates the
 * failure of a webservice call. <br>
 * It shall describe the failure, by providing an error code, an error message and an optional exception describing the
 * issue.
 * </p>
 * <p>
 * <b>Important:</b> The hereby contained error codes should not be confused with the wsclient {@link Error} codes,
 * those shall be represented by a {@link Result} instead.
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
public class FaultInfo {

    @XmlElement(namespace = "")
    private int errorCode;
    @XmlElement(namespace = "")
    private @Nullable String errorMessage;
    @XmlElement(namespace = "")
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
