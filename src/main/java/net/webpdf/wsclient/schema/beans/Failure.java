package net.webpdf.wsclient.schema.beans;

import net.webpdf.wsclient.schema.stubs.FaultInfo;
import org.jetbrains.annotations.Nullable;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

/**
 * An instance of {@link Failure} is intended to wrap and simplify the webPDF server´s {@link FaultInfo} responses
 * and shall provide the error code, message and stacktrace of a failure encountered by the webPDF server.
 */
@SuppressWarnings("unused")
@XmlRootElement
public class Failure implements Serializable {

    private int errorCode = 0;
    private @Nullable String errorMessage = "no error.";
    private @Nullable String stackTrace = "";

    /**
     * Returns numeric webPDF server error code, that is describing the issue.
     *
     * @return The numeric webPDF server error code, that is describing the issue.
     */
    @XmlElement(name = "errorCode")
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the numeric webPDF server error code, that is describing the issue.
     *
     * @param errorCode The numeric webPDF server error code, that is describing the issue.
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Returns the webPDF server error message, that is describing the issue.
     *
     * @return The webPDF server error message, that is describing the issue.
     */
    @XmlElement(name = "errorMessage")
    public @Nullable String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the webPDF server error message, that is describing the issue.
     *
     * @param errorMessage The webPDF server error message, that is describing the issue.
     */
    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the stacktrace, that lead to the creation of this exception.
     *
     * @return The stacktrace, that lead to the creation of this exception.
     */
    @XmlElement(name = "stackTrace")
    public @Nullable String getStackTrace() {
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
