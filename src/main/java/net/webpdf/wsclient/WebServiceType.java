package net.webpdf.wsclient;

public enum WebServiceType {

    CONVERTER(
            ConverterWebService.class,
            "http://schema.webpdf.de/1.0/soap/converter",
            "ConverterService",
            "ConverterPort",
            "converter",
            ConverterRestWebService.class,
            "converter/{documentId}"),
    TOOLBOX(
            ToolboxWebService.class,
            "http://schema.webpdf.de/1.0/soap/toolbox",
            "ToolboxService",
            "ToolboxPort",
            "toolbox",
            ToolboxRestWebService.class,
            "toolbox/{documentId}"),
    PDFA(
            PdfaWebService.class,
            "http://schema.webpdf.de/1.0/soap/pdfa",
            "PdfaService",
            "PdfaPort",
            "pdfa",
            PdfaRestWebService.class,
            "pdfa/{documentId}"),
    OCR(
            OcrWebService.class,
            "http://schema.webpdf.de/1.0/soap/ocr",
            "OCRService",
            "OCRPort",
            "ocr",
            OcrRestWebService.class,
            "ocr/{documentId}"),
    SIGNATURE(
            SignatureWebService.class,
            "http://schema.webpdf.de/1.0/soap/signature",
            "SignatureService",
            "SignaturePort",
            "signature",
            SignatureRestWebService.class,
            "signature/{documentId}"),
    URLCONVERTER(
            UrlConverterWebService.class,
            "http://schema.webpdf.de/1.0/soap/urlconverter",
            "URLConverterService",
            "URLConverterPort",
            "urlconverter",
            UrlConverterRestWebService.class,
            "urlconverter"),
    BARCODE(
            BarcodeWebService.class,
            "http://schema.webpdf.de/1.0/soap/barcode",
            "BarcodeService",
            "BarcodePort",
            "barcode",
            BarcodeRestWebService.class,
            "barcode/{documentId}");

    public final static String ID_PLACEHOLDER = "{documentId}";
    private final String soapNamespaceURI;
    private final String soapLocalPart;
    private final String soapLocalPartPort;
    private final String soapEndpoint;
    private final Class<? extends SoapWebService> soapWsClass;
    private final Class<? extends RestWebservice> restWsClass;
    private final String restEndpoint;

    WebServiceType(Class<? extends SoapWebService> soapWsClass,
                   String soapNamespaceURI,
                   String soapLocalPart,
                   String soapLocalPartPort,
                   String soapEndpoint,
                   Class<? extends RestWebservice> restWsClass,
                   String restEndpoint) {
        this.soapNamespaceURI = soapNamespaceURI;
        this.soapLocalPart = soapLocalPart;
        this.soapLocalPartPort = soapLocalPartPort;
        this.soapEndpoint = soapEndpoint;
        this.soapWsClass = soapWsClass;
        this.restWsClass = restWsClass;
        this.restEndpoint = restEndpoint;
    }

    public String getSoapNamespaceURI() {
        return this.soapNamespaceURI;
    }

    public String getSoapLocalPart() {
        return this.soapLocalPart;
    }

    public String getSoapEndpoint() {
        return this.soapEndpoint;
    }

    public Class<?> getSoapWsClass() {
        return soapWsClass;
    }

    public String getSoapLocalPartPort() {
        return soapLocalPartPort;
    }

    public Class<? extends RestWebservice> getRestWsClass() {
        return restWsClass;
    }

    public String getRestEndpoint() {
        return restEndpoint;
    }

}
