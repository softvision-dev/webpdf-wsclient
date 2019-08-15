
package net.webpdf.wsclient.schema.stubs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für FaultInfo complex type.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
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
@XmlType(name = "FaultInfo", propOrder = {
    "errorCode",
    "errorMessage",
    "stackTrace"
})
public class FaultInfo {

    @XmlElement(namespace = "")
    protected int errorCode;
    @XmlElement(namespace = "")
    protected String errorMessage;
    @XmlElement(namespace = "")
    protected String stackTrace;

    /**
     * Ruft den Wert der errorCode-Eigenschaft ab.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Legt den Wert der errorCode-Eigenschaft fest.
     */
    public void setErrorCode(int value) {
        this.errorCode = value;
    }

    /**
     * Ruft den Wert der errorMessage-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Legt den Wert der errorMessage-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     * Ruft den Wert der stackTrace-Eigenschaft ab.
     *
     * @return possible object is
     * {@link String }
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Legt den Wert der stackTrace-Eigenschaft fest.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setStackTrace(String value) {
        this.stackTrace = value;
    }

}
