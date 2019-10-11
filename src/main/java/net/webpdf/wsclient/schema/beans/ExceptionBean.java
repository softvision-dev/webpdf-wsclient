package net.webpdf.wsclient.schema.beans;

import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings({"unused", "WeakerAccess"})
@XmlRootElement
public class ExceptionBean {

    private int errorCode;
    @Nullable
    private String errorMessage;
    @Nullable
    private String stackTrace = "";

    /**
     * Creates a new exception bean, wrapping ws-client and webPDF errors.
     */
    public ExceptionBean() {
        this("no error.", 0);
    }

    /**
     * Creates a new exception bean, wrapping ws-client and webPDF errors.
     *
     * @param errorMessage The error message, describing the issue.
     * @param errorCode    The error code, describing the issue.
     */
    public ExceptionBean(@Nullable String errorMessage, int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    /**
     * Returns the wsclient error code, that is describing the issue.
     *
     * @return The wsclient error code, that is describing the issue.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the wsclient error code, that is describing the issue.
     *
     * @param errorCode The wsclient error code, that is describing the issue.
     */
    @XmlElement(name = "errorCode")
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns the wsclient error message, that is describing the issue.
     *
     * @return The wsclient error message, that is describing the issue.
     */
    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the wsclient error message, that is describing the issue.
     *
     * @param errorMessage The wsclient error message, that is describing the issue.
     */
    @XmlElement(name = "errorMessage")
    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the stacktrace, that lead to the creation of this exception.
     *
     * @return The stacktrace, that caused, this exception.
     */
    @Nullable
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the stacktrace, that lead to the creation of this exception.
     *
     * @param stackTrace The stacktrace, that lead to the creation of this exception.
     */
    @XmlElement(name = "stackTrace")
    public void setStackTrace(@Nullable String stackTrace) {
        this.stackTrace = stackTrace;
    }

}
