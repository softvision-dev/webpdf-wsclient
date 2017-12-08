package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.OcrType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "ocr"
})
@XmlRootElement(name = "operation")
public class OcrOperation extends BaseOperation {

    private OcrType ocr;

    @XmlElement(name = "ocr", namespace = "http://schema.webpdf.de/1.0/operation", required = true)
    public OcrType getOcr() {
        return this.ocr;
    }

    public void setOcr(OcrType ocr) {
        this.ocr = ocr;
    }
}
