package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.tools.SerializeHelper;
import net.webpdf.wsclient.webservice.rest.*;
import net.webpdf.wsclient.webservice.soap.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.transform.stream.StreamSource;

/**
 * An instance of {@link WebServiceFactory} produces {@link WebService} instances that establish connections to
 * specific webPDF webservice endpoints ({@link WebServiceType}), using a specific {@link WebServiceProtocol} and
 * expecting a specific {@link Document} type
 * as the result.
 */
public final class WebServiceFactory {

    /**
     * This class is not intended to be instantiated, use the static methods instead.
     */
    private WebServiceFactory() {
    }

    /**
     * Creates a matching {@link WebService} instance to execute a webPDF operation.
     *
     * @param <T_DOCUMENT>   The {@link Document} type, processed and created by the {@link WebService}.
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param session        The {@link Session} context for the created {@link WebService}.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @return A matching {@link WebService} instance.
     * @throws ResultException Shall be thrown, if the {@link WebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    public static <T_DOCUMENT extends Document, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, ?>>
    @NotNull T_WEBSERVICE createInstance(
            @NotNull Session<T_DOCUMENT> session, @NotNull WebServiceType webServiceType) throws ResultException {
        switch (session.getWebServiceProtocol()) {
            case SOAP:
                if (session instanceof SoapSession) {
                    return (T_WEBSERVICE) WebServiceFactory
                            .createSoapInstance((SoapSession<SoapDocument>) session, webServiceType,
                                    new OperationData());
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            case REST:
                if (session instanceof RestSession) {
                    return (T_WEBSERVICE) WebServiceFactory
                            .createRestInstance((RestSession<RestDocument>) session, webServiceType,
                                    new OperationData());
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_PROTOCOL));
        }
    }

    /**
     * <p>
     * Creates a matching {@link WebService} instance to execute a webPDF operation.
     * </p>
     * <p>
     * Detects the {@link WebServiceType} by loading the {@link OperationData} from the
     * given {@link StreamSource}.<br>
     * The {@link StreamSource} shall contain a {@link DataFormat#XML} or {@link DataFormat#JSON} data transfer object
     * defined in the given {@link Session} object translatable to the required {@link OperationData}.
     * </p>
     *
     * @param <T_DOCUMENT>   The {@link Document} type, processed and created by the {@link WebService}.
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param session        The {@link Session} context for the created {@link WebService}.
     * @param streamSource   The {@link StreamSource} to read the {@link OperationData} from and to detect the
     *                       {@link WebServiceType} by.
     * @return A matching {@link WebService} instance.
     * @throws ResultException Shall be thrown, if the {@link WebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    public static <T_DOCUMENT extends Document, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, ?>>
    @NotNull T_WEBSERVICE createInstance(
            @Nullable Session<T_DOCUMENT> session, @Nullable StreamSource streamSource) throws ResultException {
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
                if (session instanceof SoapSession) {
                    return (T_WEBSERVICE) WebServiceFactory.createSoapInstance(
                            (SoapSession<SoapDocument>) session, webServiceType, operationData
                    );
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            case REST:
                if (session instanceof RestSession) {
                    return (T_WEBSERVICE) WebServiceFactory.createRestInstance(
                            (RestSession<RestDocument>) session, webServiceType, operationData
                    );
                } else {
                    throw new ResultException(Result.build(Error.INVALID_WEBSERVICE_SESSION));
                }
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_PROTOCOL));
        }
    }

    /**
     * <p>
     * Creates a matching {@link SoapWebService} instance to execute a webPDF operation.
     * </p>
     *
     * @param <T_DOCUMENT>   The {@link Document} type, processed and created by the {@link SoapWebService}.
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param session        The {@link Session} context for the created {@link SoapWebService}.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @param operationData  The {@link OperationData} defining parameters for the {@link SoapWebService} execution.
     * @return A matching {@link SoapWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link SoapWebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    private static <T_DOCUMENT extends SoapDocument, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, ?>>
    @NotNull T_WEBSERVICE createSoapInstance(
            @NotNull SoapSession<T_DOCUMENT> session, @NotNull WebServiceType webServiceType,
            @Nullable OperationData operationData) throws ResultException {

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
     * <p>
     * Creates a matching {@link RestWebService} instance to execute a webPDF operation.
     * </p>
     *
     * @param <T_DOCUMENT>   The {@link Document} type, processed and created by the {@link RestWebService}.
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param session        The {@link Session} context for the created {@link RestWebService}.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @param operationData  The {@link OperationData} defining parameters for the {@link RestWebService} execution.
     * @return A matching {@link RestWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link RestWebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    private static <T_DOCUMENT extends RestDocument, T_WEBSERVICE extends WebService<T_DOCUMENT, ?, ?>>
    @NotNull T_WEBSERVICE createRestInstance(
            @NotNull RestSession<T_DOCUMENT> session, @NotNull WebServiceType webServiceType,
            @Nullable OperationData operationData) throws ResultException {

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
