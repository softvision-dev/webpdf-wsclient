
package net.webpdf.wsclient.schema.stubs;

import org.jetbrains.annotations.Nullable;

/**
 * <p>
 * An instance of {@link FaultInfo} will be encountered in case the webPDF server´s response indicates the
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
public interface FaultInfo {
    /**
     * Return the errorCode.
     */
    int getErrorCode();

    /**
     * Set the errorCode.
     */
    void setErrorCode(int value);

    /**
     * Return the errorMessage.
     *
     * @return possible object is
     * {@link String }
     */
    @Nullable String getErrorMessage();

    /**
     * Set the errorMessage.
     *
     * @param value allowed object is
     *              {@link String }
     */
    void setErrorMessage(@Nullable String value);

    /**
     * Return the stacktrace.
     *
     * @return possible object is
     * {@link String }
     */
    @Nullable String getStackTrace();

    /**
     * Set the stacktrace.
     *
     * @param value allowed object is
     *              {@link String }
     */
    void setStackTrace(@Nullable String value);
}
