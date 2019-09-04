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

    public ExceptionBean() {
        this("no error.", 0);
    }

    public ExceptionBean(@Nullable String errorMessage, int errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @XmlElement(name = "errorCode")
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    @XmlElement(name = "errorMessage")
    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Nullable
    public String getStackTrace() {
        return stackTrace;
    }

    @XmlElement(name = "stackTrace")
    public void setStackTrace(@Nullable String stackTrace) {
        this.stackTrace = stackTrace;
    }

}
