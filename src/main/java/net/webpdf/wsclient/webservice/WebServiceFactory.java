package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
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
    public static <T_DOCUMENT extends Document, T_WEBSERVICE extends WebService<?, ?, ?, T_DOCUMENT, ?, ?, ?>>
    @NotNull T_WEBSERVICE createInstance(
            @NotNull Session session, @NotNull WebServiceType webServiceType) throws ResultException {
        switch (session.getWebServiceProtocol()) {
            case SOAP:
                if (session instanceof SoapSession) {
                    return (T_WEBSERVICE) WebServiceFactory
                            .createSoapInstance((SoapSession) session, webServiceType,
                                    createSoapParameters(webServiceType));
                } else {
                    throw new ClientResultException(Error.INVALID_WEBSERVICE_SESSION);
                }
            case REST:
                if (session instanceof RestSession) {
                    return (T_WEBSERVICE) WebServiceFactory
                            .createRestInstance((RestSession<RestDocument>) session, webServiceType,
                                    createRestParameters(webServiceType));
                } else {
                    throw new ClientResultException(Error.INVALID_WEBSERVICE_SESSION);
                }
            default:
                throw new ClientResultException(Error.UNKNOWN_WEBSERVICE_PROTOCOL);
        }
    }

    /**
     * Creates the operation data container for a {@link SoapWebService} of the given {@link WebServiceType}.
     *
     * @param webServiceType The {@link WebServiceType} a operation data container shall be created for.
     * @return The resulting {@link OperationData}.
     * @throws ResultException Shall be thrown, should the {@link WebServiceType} be unknown.
     */
    private static @NotNull OperationData createSoapParameters(@NotNull WebServiceType webServiceType)
            throws ResultException {
        OperationData operationData = new OperationData();
        switch (webServiceType) {
            case CONVERTER:
                operationData.setConverter(new ConverterType());
                break;
            case URLCONVERTER:
                operationData.setUrlconverter(new UrlConverterType());
                break;
            case PDFA:
                operationData.setPdfa(new PdfaType());
                break;
            case TOOLBOX:
                break;
            case OCR:
                operationData.setOcr(new OcrType());
                break;
            case SIGNATURE:
                operationData.setSignature(new SignatureType());
                break;
            case BARCODE:
                operationData.setBarcode(new BarcodeType());
                break;
            default:
                throw new ClientResultException(Error.UNKNOWN_WEBSERVICE_TYPE);
        }
        return operationData;
    }

    /**
     * Creates the {@link RestOperationData} container for a {@link RestWebService} of the given {@link WebServiceType}.
     *
     * @param webServiceType The {@link WebServiceType} a operation data container shall be created for.
     * @return The resulting {@link RestOperationData} container.
     * @throws ResultException Shall be thrown, should the {@link WebServiceType} be unknown.
     */
    private static @NotNull RestOperationData createRestParameters(@NotNull WebServiceType webServiceType)
            throws ResultException {
        RestOperationData operation;
        switch (webServiceType) {
            case CONVERTER:
                OperationConverterOperation converterOperation = new OperationConverterOperation();
                converterOperation.setConverter(new OperationConverter());
                operation = new RestOperationData(converterOperation);
                break;
            case URLCONVERTER:
                OperationUrlConverterOperation urlConverterOperation = new OperationUrlConverterOperation();
                urlConverterOperation.setUrlconverter(new OperationUrlConverter());
                operation = new RestOperationData(urlConverterOperation);
                break;
            case PDFA:
                OperationPdfaOperation pdfaOperation = new OperationPdfaOperation();
                pdfaOperation.setPdfa(new OperationPdfa());
                operation = new RestOperationData(pdfaOperation);
                break;
            case TOOLBOX:
                operation = new RestOperationData(new OperationToolboxOperation());
                break;
            case OCR:
                OperationOcrOperation ocrOperation = new OperationOcrOperation();
                ocrOperation.setOcr(new OperationOcr());
                operation = new RestOperationData(ocrOperation);
                break;
            case SIGNATURE:
                OperationSignatureOperation signatureOperation = new OperationSignatureOperation();
                signatureOperation.setSignature(new OperationSignature());
                operation = new RestOperationData(signatureOperation);
                break;
            case BARCODE:
                OperationBarcodeOperation barcodeOperation = new OperationBarcodeOperation();
                barcodeOperation.setBarcode(new OperationBarcode());
                operation = new RestOperationData(barcodeOperation);
                break;
            default:
                throw new ClientResultException(Error.UNKNOWN_WEBSERVICE_TYPE);
        }
        return operation;
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
    public static <T_DOCUMENT extends Document, T_WEBSERVICE extends WebService<?, ?, ?, T_DOCUMENT, ?, ?, ?>>
    @NotNull T_WEBSERVICE createInstance(
            @Nullable Session session, @Nullable StreamSource streamSource) throws ResultException {
        if (session == null) {
            throw new ClientResultException(Error.SESSION_CREATE);
        }

        // get the data format
        DataFormat dataFormat = session.getDataFormat();
        if (dataFormat == null) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA);
        }

        // create the web service instance
        switch (session.getWebServiceProtocol()) {
            case SOAP:
                // convert the data into a operation object
                OperationData soapOperationData = dataFormat.equals(DataFormat.XML)
                        ? SerializeHelper.fromXML(streamSource, OperationData.class)
                        : SerializeHelper.fromJSON(streamSource, OperationData.class);

                // detect the web service with the operation data
                if (session instanceof SoapSession) {
                    return (T_WEBSERVICE) WebServiceFactory.createSoapInstance(
                            (SoapSession) session, determineWebServiceType(soapOperationData),
                            soapOperationData
                    );
                } else {
                    throw new ClientResultException(Error.INVALID_WEBSERVICE_SESSION);
                }
            case REST:
                // convert the data into a operation object
                RestOperationData restOperationData = dataFormat.equals(DataFormat.XML)
                        ? SerializeHelper.fromXML(streamSource, RestOperationData.class)
                        : SerializeHelper.fromJSON(streamSource, RestOperationData.class);

                // detect the web service with the operation data
                if (session instanceof RestSession) {
                    return (T_WEBSERVICE) WebServiceFactory.createRestInstance(
                            (RestSession<RestDocument>) session, determineWebServiceType(restOperationData),
                            restOperationData
                    );
                } else {
                    throw new ClientResultException(Error.INVALID_WEBSERVICE_SESSION);
                }
            default:
                throw new ClientResultException(Error.UNKNOWN_WEBSERVICE_PROTOCOL);
        }
    }

    /**
     * Generalizes the Translation of an operation data container to a {@link WebServiceType} selection for the REST
     * and SOAP operation data containers: {@link OperationData} and {@link RestOperationData}.
     *
     * @param operationData The operation data object to determine the {@link WebServiceType} for. (it should be either
     *                      a {@link OperationData} or a {@link RestOperationData} container.)
     * @return The resulting {@link WebServiceType}.
     * @throws ResultException Shall be thrown, should the determination of a matching {@link WebServiceType} fail.
     */
    @NotNull
    private static WebServiceType determineWebServiceType(@NotNull Object operationData) throws ResultException {
        if (
                (operationData instanceof OperationData && ((OperationData) operationData).isSetConverter()) ||
                        (operationData instanceof RestOperationData && ((RestOperationData) operationData).isSetConverter())
        ) {
            return WebServiceType.CONVERTER;
        } else if (
                (operationData instanceof OperationData && ((OperationData) operationData).isSetBarcode()) ||
                        (operationData instanceof RestOperationData && ((RestOperationData) operationData).isSetBarcode())
        ) {
            return WebServiceType.BARCODE;
        } else if (
                (operationData instanceof OperationData && ((OperationData) operationData).isSetOcr()) ||
                        (operationData instanceof RestOperationData && ((RestOperationData) operationData).isSetOcr())
        ) {
            return WebServiceType.OCR;
        } else if (
                (operationData instanceof OperationData && ((OperationData) operationData).isSetPdfa()) ||
                        (operationData instanceof RestOperationData && ((RestOperationData) operationData).isSetPdfa())
        ) {
            return WebServiceType.PDFA;
        } else if (
                (operationData instanceof OperationData && ((OperationData) operationData).isSetSignature()) ||
                        (operationData instanceof RestOperationData && ((RestOperationData) operationData).isSetSignature())
        ) {
            return WebServiceType.SIGNATURE;
        } else if (
                (operationData instanceof OperationData && ((OperationData) operationData).isSetToolbox()) ||
                        (operationData instanceof RestOperationData && ((RestOperationData) operationData).isSetToolbox())
        ) {
            return WebServiceType.TOOLBOX;
        } else if (
                (operationData instanceof OperationData && ((OperationData) operationData).isSetUrlconverter()) ||
                        (operationData instanceof RestOperationData && ((RestOperationData) operationData).isSetUrlconverter())
        ) {
            return WebServiceType.URLCONVERTER;
        } else {
            throw new ClientResultException(Error.UNKNOWN_WEBSERVICE_TYPE);
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
    private static <T_DOCUMENT extends SoapDocument, T_WEBSERVICE extends WebService<?, ?, ?, T_DOCUMENT, ?, ?, ?>>
    @NotNull T_WEBSERVICE createSoapInstance(
            @NotNull SoapSession session, @NotNull WebServiceType webServiceType,
            @Nullable OperationData operationData) throws ResultException {

        if (operationData == null) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA);
        }

        switch (webServiceType) {
            case CONVERTER:
                ConverterWebService<T_DOCUMENT> converterWebService = new ConverterWebService<>(session);
                converterWebService.setOperationParameters(operationData.getConverter());
                return (T_WEBSERVICE) converterWebService;
            case SIGNATURE:
                SignatureWebService<T_DOCUMENT> signatureWebService = new SignatureWebService<>(session);
                signatureWebService.setOperationParameters(operationData.getSignature());
                return (T_WEBSERVICE) signatureWebService;
            case PDFA:
                PdfaWebService<T_DOCUMENT> pdfaWebService = new PdfaWebService<>(session);
                pdfaWebService.setOperationParameters(operationData.getPdfa());
                return (T_WEBSERVICE) pdfaWebService;
            case OCR:
                OcrWebService<T_DOCUMENT> ocrWebService = new OcrWebService<>(session);
                ocrWebService.setOperationParameters(operationData.getOcr());
                return (T_WEBSERVICE) ocrWebService;
            case TOOLBOX:
                ToolboxWebService<T_DOCUMENT> toolboxWebService = new ToolboxWebService<>(session);
                toolboxWebService.setOperationParameters(operationData.getToolbox());
                return (T_WEBSERVICE) toolboxWebService;
            case URLCONVERTER:
                UrlConverterWebService<T_DOCUMENT> urlConverterWebService = new UrlConverterWebService<>(session);
                urlConverterWebService.setOperationParameters(operationData.getUrlconverter());
                return (T_WEBSERVICE) urlConverterWebService;
            case BARCODE:
                BarcodeWebService<T_DOCUMENT> barcodeWebService = new BarcodeWebService<>(session);
                barcodeWebService.setOperationParameters(operationData.getBarcode());
                return (T_WEBSERVICE) barcodeWebService;
            default:
                throw new ClientResultException(Error.UNKNOWN_WEBSERVICE_TYPE);
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
     * @param operationData  The operation data defining parameters for the {@link RestWebService} execution.
     * @return A matching {@link RestWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link RestWebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    private static <T_DOCUMENT extends RestDocument, T_WEBSERVICE extends RestWebService<?, ?, T_DOCUMENT>>
    @NotNull T_WEBSERVICE createRestInstance(
            @NotNull RestSession<T_DOCUMENT> session, @NotNull WebServiceType webServiceType,
            @Nullable RestOperationData operationData) throws ResultException {

        if (operationData == null) {
            throw new ClientResultException(Error.INVALID_OPERATION_DATA);
        }

        switch (webServiceType) {
            case CONVERTER:
                ConverterRestWebService<T_DOCUMENT> converterWebService = new ConverterRestWebService<>(session);
                converterWebService.setOperationParameters(operationData.getConverter());
                return (T_WEBSERVICE) converterWebService;
            case SIGNATURE:
                SignatureRestWebService<T_DOCUMENT> signatureWebService = new SignatureRestWebService<>(session);
                signatureWebService.setOperationParameters(operationData.getSignature());
                return (T_WEBSERVICE) signatureWebService;
            case PDFA:
                PdfaRestWebService<T_DOCUMENT> pdfaWebService = new PdfaRestWebService<>(session);
                pdfaWebService.setOperationParameters(operationData.getPdfa());
                return (T_WEBSERVICE) pdfaWebService;
            case OCR:
                OcrRestWebService<T_DOCUMENT> ocrWebService = new OcrRestWebService<>(session);
                ocrWebService.setOperationParameters(operationData.getOcr());
                return (T_WEBSERVICE) ocrWebService;
            case TOOLBOX:
                ToolboxRestWebService<T_DOCUMENT> toolboxWebService = new ToolboxRestWebService<>(session);
                toolboxWebService.setOperationParameters(operationData.getToolbox());
                return (T_WEBSERVICE) toolboxWebService;
            case URLCONVERTER:
                UrlConverterRestWebService<T_DOCUMENT> urlConverterWebService = new UrlConverterRestWebService<>(session);
                urlConverterWebService.setOperationParameters(operationData.getUrlconverter());
                return (T_WEBSERVICE) urlConverterWebService;
            case BARCODE:
                BarcodeRestWebService<T_DOCUMENT> barcodeWebService = new BarcodeRestWebService<>(session);
                barcodeWebService.setOperationParameters(operationData.getBarcode());
                return (T_WEBSERVICE) barcodeWebService;
            default:
                throw new ClientResultException(Error.UNKNOWN_WEBSERVICE_TYPE);
        }
    }

}
