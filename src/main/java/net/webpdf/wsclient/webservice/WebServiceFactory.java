package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.*;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.soap.AbstractSoapSession;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import net.webpdf.wsclient.webservice.rest.*;
import net.webpdf.wsclient.webservice.soap.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.transform.stream.StreamSource;

/**
 * Factory for webPDF Web services
 */
public final class WebServiceFactory {

    private WebServiceFactory() {
    }

    /**
     * Create a web service instance
     *
     * @param <T_DOCUMENT>   {@link Document} the document type
     * @param <T_WEBSERVICE> {@link WebService} the webservice type
     * @param session        {@link Session}  context for the Web service
     * @param webServiceType the {@link WebServiceType} entry(CONVERTER, TOOLBOX, ...)
     * @return a specific {@link WebService} child instance
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @NotNull
    public static <T_DOCUMENT extends Document, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, T_DOCUMENT>>
    T_WEBSERVICE createInstance(@NotNull Session<T_DOCUMENT> session, @NotNull WebServiceType webServiceType)
            throws ResultException {
        switch (session.getWebServiceProtocol()) {
            case SOAP:
                if (session instanceof SoapSession) {
                    return (T_WEBSERVICE) WebServiceFactory
                            .createSoapInstance((SoapSession) session, webServiceType, new OperationData());
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            case REST:
                if (session instanceof RestSession) {
                    return (T_WEBSERVICE) WebServiceFactory
                            .createRestInstance((RestSession) session, webServiceType, new OperationData());
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_PROTOCOL));
        }
    }

    /**
     * Creates a web service instance. Detects the {@link WebServiceType} by loading the {@link OperationData} from the
     * {@link StreamSource}. The {@link StreamSource} is a XML or JSON content defined by {@link DataFormat} in the
     * {@link Session} object.
     *
     * @param <T_DOCUMENT>   {@link Document} the document type
     * @param <T_WEBSERVICE> {@link WebService} the webservice type
     * @param session        {@link Session} context for the web service
     * @param streamSource   {@link StreamSource} to create the {@link OperationData} and to detect the {@link WebServiceType}
     * @return a specific {@link WebService} child instance
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @NotNull
    public static <T_DOCUMENT extends Document, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, ?>> T_WEBSERVICE createInstance(
            @Nullable Session<T_DOCUMENT> session, @Nullable StreamSource streamSource
    ) throws ResultException {
        if (session == null) {
            throw new ResultException(Result.build(Error.SESSION_CREATE));
        }

        // get the data format
        DataFormat dataFormat = session.getDataFormat();
        if (dataFormat == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }

        // convert the data into a operation object
        OperationData operationData = dataFormat.equals(DataFormat.XML)
                ? SerializeHelper.fromXML(streamSource, OperationData.class)
                : SerializeHelper.fromJSON(streamSource, OperationData.class);

        // detect the web service with the operation data
        WebServiceType webServiceType;
        if (operationData.isSetConverter()) {
            webServiceType = WebServiceType.CONVERTER;
        } else if (operationData.isSetBarcode()) {
            webServiceType = WebServiceType.BARCODE;
        } else if (operationData.isSetOcr()) {
            webServiceType = WebServiceType.OCR;
        } else if (operationData.isSetPdfa()) {
            webServiceType = WebServiceType.PDFA;
        } else if (operationData.isSetSignature()) {
            webServiceType = WebServiceType.SIGNATURE;
        } else if (operationData.isSetToolbox()) {
            webServiceType = WebServiceType.TOOLBOX;
        } else if (operationData.isSetUrlconverter()) {
            webServiceType = WebServiceType.URLCONVERTER;
        } else {
            throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_TYPE));
        }

        // create the web service instance
        switch (session.getWebServiceProtocol()) {
            case SOAP:
                if (session instanceof AbstractSoapSession) {
                    return (T_WEBSERVICE) WebServiceFactory.createSoapInstance(
                            (SoapSession) session, webServiceType, operationData
                    );
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            case REST:
                if (session instanceof RestWebServiceSession) {
                    return (T_WEBSERVICE) WebServiceFactory.createRestInstance(
                            (RestSession) session, webServiceType, operationData
                    );
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_PROTOCOL));
        }
    }

    /**
     * Create a SOAP web service instance
     *
     * @param session        the {@link Session} instance
     * @param webServiceType the {@link WebServiceType} entry(CONVERTER, TOOLBOX, ...)
     * @param operationData  the webservice calls operation data
     * @return a specific {@link SoapWebService} child instance
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unchecked")
    private static <T_DOCUMENT extends SoapDocument, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, ?>>
    T_WEBSERVICE createSoapInstance(
            @NotNull SoapSession<T_DOCUMENT> session, @NotNull WebServiceType webServiceType,
            @Nullable OperationData operationData
    ) throws ResultException {

        if (operationData == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }

        switch (webServiceType) {
            case CONVERTER:
                ConverterWebService<T_DOCUMENT> converterWebService = new ConverterWebService<>(session);
                converterWebService.setOperation(operationData.getConverter());
                return (T_WEBSERVICE) converterWebService;
            case SIGNATURE:
                SignatureWebService<T_DOCUMENT> signatureWebService = new SignatureWebService<>(session);
                signatureWebService.setOperation(operationData.getSignature());
                return (T_WEBSERVICE) signatureWebService;
            case PDFA:
                PdfaWebService<T_DOCUMENT> pdfaWebService = new PdfaWebService<>(session);
                pdfaWebService.setOperation(operationData.getPdfa());
                return (T_WEBSERVICE) pdfaWebService;
            case OCR:
                OcrWebService<T_DOCUMENT> ocrWebService = new OcrWebService<>(session);
                ocrWebService.setOperation(operationData.getOcr());
                return (T_WEBSERVICE) ocrWebService;
            case TOOLBOX:
                ToolboxWebService<T_DOCUMENT> toolboxWebService = new ToolboxWebService<>(session);
                toolboxWebService.setOperation(operationData.getToolbox());
                return (T_WEBSERVICE) toolboxWebService;
            case URLCONVERTER:
                UrlConverterWebService<T_DOCUMENT> urlConverterWebService = new UrlConverterWebService<>(session);
                urlConverterWebService.setOperation(operationData.getUrlconverter());
                return (T_WEBSERVICE) urlConverterWebService;
            case BARCODE:
                BarcodeWebService<T_DOCUMENT> barcodeWebService = new BarcodeWebService<>(session);
                barcodeWebService.setOperation(operationData.getBarcode());
                return (T_WEBSERVICE) barcodeWebService;
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_TYPE));
        }
    }

    /**
     * Create a REST web service instance
     *
     * @param session        the {@link Session} instance
     * @param webServiceType the {@link WebServiceType} entry(CONVERTER, TOOLBOX, ...)
     * @param operationData  the webservice calls operation data
     * @return a specific {@link RestWebService} child instance
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unchecked")
    private static <T_DOCUMENT extends RestDocument, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, ?>>
    T_WEBSERVICE createRestInstance(
            @NotNull RestSession<T_DOCUMENT> session, @NotNull WebServiceType webServiceType,
            @Nullable OperationData operationData
    ) throws ResultException {

        if (operationData == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }

        switch (webServiceType) {
            case CONVERTER:
                ConverterRestWebService<T_DOCUMENT> converterWebService = new ConverterRestWebService<>(session);
                converterWebService.setOperation(operationData.getConverter());
                return (T_WEBSERVICE) converterWebService;
            case SIGNATURE:
                SignatureRestWebService<T_DOCUMENT> signatureWebService = new SignatureRestWebService<>(session);
                signatureWebService.setOperation(operationData.getSignature());
                return (T_WEBSERVICE) signatureWebService;
            case PDFA:
                PdfaRestWebService<T_DOCUMENT> pdfaWebService = new PdfaRestWebService<>(session);
                pdfaWebService.setOperation(operationData.getPdfa());
                return (T_WEBSERVICE) pdfaWebService;
            case OCR:
                OcrRestWebService<T_DOCUMENT> ocrWebService = new OcrRestWebService<>(session);
                ocrWebService.setOperation(operationData.getOcr());
                return (T_WEBSERVICE) ocrWebService;
            case TOOLBOX:
                ToolboxRestWebService<T_DOCUMENT> toolboxWebService = new ToolboxRestWebService<>(session);
                toolboxWebService.setOperation(operationData.getToolbox());
                return (T_WEBSERVICE) toolboxWebService;
            case URLCONVERTER:
                UrlConverterRestWebService<T_DOCUMENT> urlConverterWebService = new UrlConverterRestWebService<>(session);
                urlConverterWebService.setOperation(operationData.getUrlconverter());
                return (T_WEBSERVICE) urlConverterWebService;
            case BARCODE:
                BarcodeRestWebService<T_DOCUMENT> barcodeWebService = new BarcodeRestWebService<>(session);
                barcodeWebService.setOperation(operationData.getBarcode());
                return (T_WEBSERVICE) barcodeWebService;
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_TYPE));
        }
    }

}
