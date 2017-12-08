package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.PdfaType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "pdfa"
})

@XmlRootElement(name = "operation")
public class PdfaOperation extends BaseOperation {

    private PdfaType pdfa;

    @XmlElement(name = "pdfa", namespace = "http://schema.webpdf.de/1.0/operation", required = true)
    public PdfaType getPdfa() {
        return pdfa;
    }

    public void setPdfa(PdfaType pdfa) {
        this.pdfa = pdfa;
    }
}
