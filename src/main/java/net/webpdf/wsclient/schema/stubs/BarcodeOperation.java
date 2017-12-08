package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.BarcodeType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "barcode"
})

@XmlRootElement(name = "operation")
public class BarcodeOperation extends BaseOperation {

    private BarcodeType barcode;

    @XmlElement(name = "barcode", namespace = "http://schema.webpdf.de/1.0/operation", required = true)
    public BarcodeType getBarcode() {
        return this.barcode;
    }

    public void setBarcode(BarcodeType barcode) {
        this.barcode = barcode;
    }
}
