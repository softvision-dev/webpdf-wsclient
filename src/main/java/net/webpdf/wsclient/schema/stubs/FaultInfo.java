
package net.webpdf.wsclient.schema.stubs;

import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java-class for the FaultInfo complex type.
 *
 * <p>the following schema fragment contains the expected content, that may be contained in this class.
 *
 * <pre>
 * &lt;complexType name="FaultInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stackTrace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@SuppressWarnings({"unused"})
@XmlType(name = "FaultInfo", propOrder = {
    "errorCode",
    "errorMessage",
    "stackTrace"
})
public class FaultInfo {

    @XmlElement(namespace = "")
    private int errorCode;
    @XmlElement(namespace = "")
    @Nullable
    private String errorMessage;
    @XmlElement(namespace = "")
    @Nullable
    private String stackTrace;

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
    @Nullable
    public String getErrorMessage() {
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
    @Nullable
    public String getStackTrace() {
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
