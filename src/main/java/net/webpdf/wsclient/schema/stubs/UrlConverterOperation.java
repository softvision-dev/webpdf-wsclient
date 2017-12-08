package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.UrlConverterType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "urlConverter"
})
@XmlRootElement(name = "operation")
public class UrlConverterOperation extends BaseOperation {

    private UrlConverterType urlconverter;

    @XmlElement(name = "urlconverter", namespace = "http://schema.webpdf.de/1.0/operation", required = true)
    public UrlConverterType getUrlConverter() {
        return this.urlconverter;
    }

    public void setUrlConverter(UrlConverterType urlconverter) {
        this.urlconverter = urlconverter;
    }
}
