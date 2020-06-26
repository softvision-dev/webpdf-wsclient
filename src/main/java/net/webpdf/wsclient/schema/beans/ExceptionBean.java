package net.webpdf.wsclient.schema.beans;

import org.jetbrains.annotations.Nullable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class ExceptionBean implements Serializable {

    private int errorCode = 0;
    @Nullable
    private String errorMessage = "no error.";
    @Nullable
    private String stackTrace = "";

    /**
     * Returns the wsclient error code, that is describing the issue.
     *
     * @return The wsclient error code, that is describing the issue.
     */
    @XmlElement(name = "errorCode")
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the wsclient error code, that is describing the issue.
     *
     * @param errorCode The wsclient error code, that is describing the issue.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns the wsclient error message, that is describing the issue.
     *
     * @return The wsclient error message, that is describing the issue.
     */
    @XmlElement(name = "errorMessage")
    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the wsclient error message, that is describing the issue.
     *
     * @param errorMessage The wsclient error message, that is describing the issue.
     */
    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the stacktrace, that lead to the creation of this exception.
     *
     * @return The stacktrace, that caused, this exception.
     */
    @XmlElement(name = "stackTrace")
    @Nullable
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Sets the stacktrace, that lead to the creation of this exception.
     *
     * @param stackTrace The stacktrace, that lead to the creation of this exception.
     */
    public void setStackTrace(@Nullable String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
