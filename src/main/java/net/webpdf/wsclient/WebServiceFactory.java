package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.DataFormat;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.tools.SerializeHelper;

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
     * @param <T>            {@link WebService} the webservice type
     * @param session        {@link Session}  context for the Web service
     * @param webServiceType the {@link WebServiceType} entry(CONVERTER, TOOLBOX, ...)
     * @return a specific {@link WebService} child instance
     * @throws ResultException a {@link ResultException}
     */
    public static <T extends WebService> T createInstance(Session session, WebServiceType webServiceType)
        throws ResultException {
        switch (session.getWebServiceProtocol()) {
            case SOAP:
                return WebServiceFactory.createSoapInstance(session, webServiceType, new OperationData());
            case REST:
                return WebServiceFactory.createRestInstance(session, webServiceType, new OperationData());
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE));
        }
    }

    /**
     * Creates a web service instance. Detects the {@link WebServiceType} by loading the {@link OperationData} from the
     * {@link StreamSource}. The {@link StreamSource} is a XML or JSON content defined by {@link DataFormat} in the
     * {@link Session} object.
     *
     * @param <T>          {@link WebService} the webservice type
     * @param session      {@link Session} context for the web service
     * @param streamSource {@link StreamSource} to create the {@link OperationData} and to detect the {@link WebServiceType}
     * @return a specific {@link WebService} child instance
     * @throws ResultException a {@link ResultException}
     */
    public static <T extends WebService> T createInstance(Session session, StreamSource streamSource)
        throws ResultException {
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
                return WebServiceFactory.createSoapInstance(session, webServiceType, operationData);
            case REST:
                return WebServiceFactory.createRestInstance(session, webServiceType, operationData);
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE));
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
    private static <T extends WebService> T createSoapInstance(Session session, WebServiceType webServiceType, OperationData operationData)
        throws ResultException {

        if (operationData == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }

        switch (webServiceType) {
            case CONVERTER:
                ConverterWebService converterWebService = new ConverterWebService(session);
                converterWebService.setOperation(operationData.getConverter());
                return (T) converterWebService;
            case SIGNATURE:
                SignatureWebService signatureWebService = new SignatureWebService(session);
                signatureWebService.setOperation(operationData.getSignature());
                return (T) signatureWebService;
            case PDFA:
                PdfaWebService pdfaWebService = new PdfaWebService(session);
                pdfaWebService.setOperation(operationData.getPdfa());
                return (T) pdfaWebService;
            case OCR:
                OcrWebService ocrWebService = new OcrWebService(session);
                ocrWebService.setOperation(operationData.getOcr());
                return (T) ocrWebService;
            case TOOLBOX:
                ToolboxWebService toolboxWebService = new ToolboxWebService(session);
                toolboxWebService.setOperation(operationData.getToolbox());
                return (T) toolboxWebService;
            case URLCONVERTER:
                UrlConverterWebService urlConverterWebService = new UrlConverterWebService(session);
                urlConverterWebService.setOperation(operationData.getUrlconverter());
                return (T) urlConverterWebService;
            case BARCODE:
                BarcodeWebService barcodeWebService = new BarcodeWebService(session);
                barcodeWebService.setOperation(operationData.getBarcode());
                return (T) barcodeWebService;
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
     * @return a specific {@link RestWebservice} child instance
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unchecked")
    private static <T extends WebService> T createRestInstance(Session session, WebServiceType webServiceType, OperationData operationData)
        throws ResultException {

        if (operationData == null) {
            throw new ResultException(Result.build(Error.INVALID_OPERATION_DATA));
        }

        switch (webServiceType) {
            case CONVERTER:
                ConverterRestWebService converterWebService = new ConverterRestWebService(session);
                converterWebService.setOperation(operationData.getConverter());
                return (T) converterWebService;
            case SIGNATURE:
                SignatureRestWebService signatureWebService = new SignatureRestWebService(session);
                signatureWebService.setOperation(operationData.getSignature());
                return (T) signatureWebService;
            case PDFA:
                PdfaRestWebService pdfaWebService = new PdfaRestWebService(session);
                pdfaWebService.setOperation(operationData.getPdfa());
                return (T) pdfaWebService;
            case OCR:
                OcrRestWebService ocrWebService = new OcrRestWebService(session);
                ocrWebService.setOperation(operationData.getOcr());
                return (T) ocrWebService;
            case TOOLBOX:
                ToolboxRestWebService toolboxWebService = new ToolboxRestWebService(session);
                toolboxWebService.setOperation(operationData.getToolbox());
                return (T) toolboxWebService;
            case URLCONVERTER:
                UrlConverterRestWebService urlConverterWebService = new UrlConverterRestWebService(session);
                urlConverterWebService.setOperation(operationData.getUrlconverter());
                return (T) urlConverterWebService;
            case BARCODE:
                BarcodeRestWebService barcodeWebService = new BarcodeRestWebService(session);
                barcodeWebService.setOperation(operationData.getBarcode());
                return (T) barcodeWebService;
            default:
                throw new ResultException(Result.build(Error.UNKNOWN_WEBSERVICE_TYPE));
        }
    }
}
