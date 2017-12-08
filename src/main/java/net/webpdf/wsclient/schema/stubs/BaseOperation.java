package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.BillingType;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlTransient
public abstract class BaseOperation {

    BillingType billing;
    PdfPasswordType password;

    @XmlElement(name = "billing", namespace = "http://schema.webpdf.de/1.0/operation")
    public BillingType getBilling() {
        return this.billing;
    }

    public void setBilling(BillingType billing) {
        this.billing = billing;
    }

    @XmlElement(name = "password", namespace = "http://schema.webpdf.de/1.0/operation")
    public PdfPasswordType getPassword() {
        return this.password;
    }

    public void setPassword(PdfPasswordType password) {
        this.password = password;
    }
}
