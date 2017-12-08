package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.SignatureType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "signature"
})
@XmlRootElement(name = "operation")
public class SignatureOperation extends BaseOperation {

    private SignatureType signature;

    @XmlElement(name = "signature", namespace = "http://schema.webpdf.de/1.0/operation", required = true)
    public SignatureType getSignature() {
        return this.signature;
    }

    public void setSignature(SignatureType signature) {
        this.signature = signature;
    }
}
