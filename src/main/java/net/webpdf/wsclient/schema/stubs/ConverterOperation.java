package net.webpdf.wsclient.schema.stubs;

import net.webpdf.wsclient.schema.operation.ConverterType;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "", propOrder = {
        "billing",
        "password",
        "converter"
})

@XmlRootElement(name = "operation")
public class ConverterOperation extends BaseOperation {

    private ConverterType converter;

    @XmlElement(name = "converter", namespace = "http://schema.webpdf.de/1.0/operation", required = true)
    public ConverterType getConverter() {
        return this.converter;
    }

    public void setConverter(ConverterType converter) {
        this.converter = converter;
    }
}
