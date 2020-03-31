package net.webpdf.wsclient;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"unused"})
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
    @NotNull
    private final String soapNamespaceURI;
    @NotNull
    private final String soapLocalPart;
    @NotNull
    private final String soapLocalPartPort;
    @NotNull
    private final String soapEndpoint;
    @NotNull
    private final Class<? extends SoapWebService<?,?>> soapWsClass;
    @NotNull
    private final Class<? extends RestWebservice<?>> restWsClass;
    @NotNull
    private final String restEndpoint;

    /**
     * Enumerates the available webservice types and their default interface.
     *
     * @param soapWsClass       The class, that represents the SOAP webservice.
     * @param soapNamespaceURI  The namespace URI of the SOAP webservice.
     * @param soapLocalPart     The local part of the SOAP webservice.
     * @param soapLocalPartPort The port of the SOAP webservice.
     * @param soapEndpoint      The name of the webservice's SOAP endpoint.
     * @param restWsClass       The class, that represents the REST webservice.
     * @param restEndpoint      The endpoint of the REST webservice.
     */
    WebServiceType(@NotNull Class<? extends SoapWebService<?,?>> soapWsClass,
                   @NotNull String soapNamespaceURI,
                   @NotNull String soapLocalPart,
                   @NotNull String soapLocalPartPort,
                   @NotNull String soapEndpoint,
                   @NotNull Class<? extends RestWebservice<?>> restWsClass,
                   @NotNull String restEndpoint) {
        this.soapNamespaceURI = soapNamespaceURI;
        this.soapLocalPart = soapLocalPart;
        this.soapLocalPartPort = soapLocalPartPort;
        this.soapEndpoint = soapEndpoint;
        this.soapWsClass = soapWsClass;
        this.restWsClass = restWsClass;
        this.restEndpoint = restEndpoint;
    }

    /**
     * Returns the namespace URI of the SOAP webservice.
     *
     * @return the namespace URI of the SOAP webservice.
     */
    @NotNull
    public String getSoapNamespaceURI() {
        return this.soapNamespaceURI;
    }

    /**
     * Returns the local part of the SOAP webservice.
     *
     * @return the local part of the SOAP webservice.
     */
    @NotNull
    public String getSoapLocalPart() {
        return this.soapLocalPart;
    }

    /**
     * Returns the endpoint of the SOAP webservice.
     *
     * @return the endpoint of the SOAP webservice.
     */
    @NotNull
    public String getSoapEndpoint() {
        return this.soapEndpoint;
    }

    /**
     * Returns the class, that represents the SOAP webservice.
     *
     * @return the class, that represents the SOAP webservice.
     */
    @NotNull
    public Class<?> getSoapWsClass() {
        return soapWsClass;
    }

    /**
     * Returns the port of the SOAP webservice.
     *
     * @return the port of the SOAP webservice.
     */
    @NotNull
    public String getSoapLocalPartPort() {
        return soapLocalPartPort;
    }

    /**
     * Returns the class, that represents the REST webservice.
     *
     * @return the class, that represents the REST webservice.
     */
    @NotNull
    public Class<? extends RestWebservice<?>> getRestWsClass() {
        return restWsClass;
    }

    /**
     * Returns the endpoint of the REST webservice.
     *
     * @return the endpoint of the REST webservice.
     */
    @NotNull
    public String getRestEndpoint() {
        return restEndpoint;
    }
    
}
