package net.webpdf.wsclient.schema.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExceptionBean {

    private int errorCode = 0;
    private String errorMessage = "";
    private String stackTrace = "";

    public int getErrorCode() {
        return errorCode;
    }

    @XmlElement(name = "errorCode")
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @XmlElement(name = "errorMessage")
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    @XmlElement(name = "stackTrace")
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }
}
