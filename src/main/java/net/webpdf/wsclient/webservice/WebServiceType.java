package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.schema.stubs.*;
import net.webpdf.wsclient.webservice.rest.*;
import net.webpdf.wsclient.webservice.soap.*;
import org.jetbrains.annotations.NotNull;

/**
 * Enumerates the available {@link WebService} endpoints of the webPDF server, that are known to this version of the
 * wsclient.
 *
 * @see #CONVERTER
 * @see #TOOLBOX
 * @see #PDFA
 * @see #OCR
 * @see #SIGNATURE
 * @see #URLCONVERTER
 * @see #BARCODE
 */
@SuppressWarnings({"unused"})
public enum WebServiceType {

    /**
     * The {@link Converter} webservice provides the means to convert different file formats to PDF.
     */
    CONVERTER(
            ConverterWebService.class,
            "http://schema.webpdf.de/1.0/soap/converter",
            "ConverterService",
            "ConverterPort",
            "converter",
            ConverterRestWebService.class,
            "converter/{documentId}"),
    /**
     * The {@link Toolbox} webservice provides the means to manipulate and analyze documents.
     */
    TOOLBOX(
            ToolboxWebService.class,
            "http://schema.webpdf.de/1.0/soap/toolbox",
            "ToolboxService",
            "ToolboxPort",
            "toolbox",
            ToolboxRestWebService.class,
            "toolbox/{documentId}"),
    /**
     * The {@link Pdfa} webservice provides the means to convert a PDF document to PDF/A.
     */
    PDFA(
            PdfaWebService.class,
            "http://schema.webpdf.de/1.0/soap/pdfa",
            "PdfaService",
            "PdfaPort",
            "pdfa",
            PdfaRestWebService.class,
            "pdfa/{documentId}"),
    /**
     * The {@link OCR} webservice allows to recognize text and add a text layer to documents.
     */
    OCR(
            OcrWebService.class,
            "http://schema.webpdf.de/1.0/soap/ocr",
            "OCRService",
            "OCRPort",
            "ocr",
            OcrRestWebService.class,
            "ocr/{documentId}"),
    /**
     * The {@link Signature} webservice allows adding digital signatures to documents.
     */
    SIGNATURE(
            SignatureWebService.class,
            "http://schema.webpdf.de/1.0/soap/signature",
            "SignatureService",
            "SignaturePort",
            "signature",
            SignatureRestWebService.class,
            "signature/{documentId}"),
    /**
     * The {@link URLConverter} webservice allows converting a URL resource to PDF.
     */
    URLCONVERTER(
            UrlConverterWebService.class,
            "http://schema.webpdf.de/1.0/soap/urlconverter",
            "URLConverterService",
            "URLConverterPort",
            "urlconverter",
            UrlConverterRestWebService.class,
            "urlconverter"),
    /**
     * The {@link Barcode} webservice allows creating barcodes for and reading barcodes from documents.
     */
    BARCODE(
            BarcodeWebService.class,
            "http://schema.webpdf.de/1.0/soap/barcode",
            "BarcodeService",
            "BarcodePort",
            "barcode",
            BarcodeRestWebService.class,
            "barcode/{documentId}");

    public final static @NotNull String ID_PLACEHOLDER = "{documentId}";
    private final @NotNull String soapNamespaceURI;
    private final @NotNull String soapLocalPart;
    private final @NotNull String soapLocalPartPort;
    private final @NotNull String soapEndpoint;
    private final @NotNull Class<? extends SoapWebService<?, ?, ?>> soapWsClass;
    private final @NotNull Class<? extends RestWebService<?, ?>> restWsClass;
    private final @NotNull String restEndpoint;

    /**
     * Enumerates the available webservice types and their default interface.
     *
     * @param soapWsClass       The class, that represents the {@link WebServiceProtocol#SOAP} webservice.
     * @param soapNamespaceURI  The namespace URI of the {@link WebServiceProtocol#SOAP} webservice.
     * @param soapLocalPart     The local part of the {@link WebServiceProtocol#SOAP} webservice.
     * @param soapLocalPartPort The port of the {@link WebServiceProtocol#SOAP} webservice.
     * @param soapEndpoint      The name of the webservice's {@link WebServiceProtocol#SOAP} endpoint.
     * @param restWsClass       The class, that represents the {@link WebServiceProtocol#REST} webservice.
     * @param restEndpoint      The endpoint of the {@link WebServiceProtocol#REST} webservice.
     */
    <T_SOAP_WS extends SoapWebService<?, ?, ?>, T_REST_WS extends RestWebService<?, ?>>
    WebServiceType(@NotNull Class<T_SOAP_WS> soapWsClass,
            @NotNull String soapNamespaceURI,
            @NotNull String soapLocalPart,
            @NotNull String soapLocalPartPort,
            @NotNull String soapEndpoint,
            @NotNull Class<T_REST_WS> restWsClass,
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
     * Returns the namespace URI of the {@link WebServiceProtocol#SOAP} webservice.
     *
     * @return the namespace URI of the {@link WebServiceProtocol#SOAP} webservice.
     */
    public @NotNull String getSoapNamespaceURI() {
        return this.soapNamespaceURI;
    }

    /**
     * Returns the local part of the {@link WebServiceProtocol#SOAP} webservice.
     *
     * @return the local part of the {@link WebServiceProtocol#SOAP} webservice.
     */
    public @NotNull String getSoapLocalPart() {
        return this.soapLocalPart;
    }

    /**
     * Returns the endpoint of the {@link WebServiceProtocol#SOAP} webservice.
     *
     * @return the endpoint of the {@link WebServiceProtocol#SOAP} webservice.
     */
    public @NotNull String getSoapEndpoint() {
        return this.soapEndpoint;
    }

    /**
     * Returns the class, that represents the {@link WebServiceProtocol#SOAP} webservice.
     *
     * @return the class, that represents the {@link WebServiceProtocol#SOAP} webservice.
     */
    public @NotNull Class<?> getSoapWsClass() {
        return soapWsClass;
    }

    /**
     * Returns the port of the {@link WebServiceProtocol#SOAP} webservice.
     *
     * @return the port of the {@link WebServiceProtocol#SOAP} webservice.
     */
    public @NotNull String getSoapLocalPartPort() {
        return soapLocalPartPort;
    }

    /**
     * Returns the class, that represents the {@link WebServiceProtocol#REST} webservice.
     *
     * @return the class, that represents the {@link WebServiceProtocol#REST} webservice.
     */
    public @NotNull Class<? extends RestWebService<?, ?>> getRestWsClass() {
        return restWsClass;
    }

    /**
     * Returns the endpoint of the {@link WebServiceProtocol#REST} webservice.
     *
     * @return the endpoint of the {@link WebServiceProtocol#REST} webservice.
     */
    public @NotNull String getRestEndpoint() {
        return restEndpoint;
    }

}
