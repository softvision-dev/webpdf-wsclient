package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
import jakarta.xml.ws.Service;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.schema.stubs.OCR;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;

/**
 * An instance of {@link OcrWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#OCR}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT> The expected {@link SoapDocument} type for the documents used by the webPDF server.
 */
public class OcrWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<OCR, OcrType, T_SOAP_DOCUMENT> {

    /**
     * Creates a {@link OcrWebService} for the given {@link SoapSession}.
     *
     * @param session The {@link SoapSession} a {@link OcrWebService} shall be created for.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    public OcrWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.OCR);
    }

    /**
     * <p>
     * Executes the {@link OcrWebService} operation and returns the {@link DataHandler} of the result document.
     * </p>
     * <p>
     * <b>Be aware:</b> SOAP webservices may not be executed without a {@code sourceDocument}. It is required,
     * that you at least create a dummy document, such as: new SoapWebServiceDocument() for webservices
     * not requiring a source document. (Such as the URL-converter.)
     * </p>
     *
     * @param sourceDocument The source {@link T_SOAP_DOCUMENT}, that shall be processed.
     * @return The {@link DataHandler} of the result document.
     * @throws WebServiceException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @Nullable DataHandler processService(@NotNull T_SOAP_DOCUMENT sourceDocument) throws WebServiceException {
        return getPort().execute(getOperationData(), sourceDocument.getSourceDataHandler(),
                sourceDocument.isFileSource() || sourceDocument.getSource() == null ?
                        null : sourceDocument.getSource().toString());
    }

    /**
     * Returns the {@link OcrWebService} specific {@link OcrType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link OcrType} operation parameters.
     */
    @Override
    public @NotNull OcrType getOperationParameters() {
        return getOperationData().getOcr();
    }

    /**
     * Sets the {@link OcrWebService} specific {@link OcrType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operation Sets the {@link OcrType} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable OcrType operation) {
        if (operation != null) {
            getOperationData().setOcr(operation);
        }
    }

    /**
     * Create a matching {@link OCR} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link OCR} webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @NotNull OCR provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.OCR.getSoapNamespaceURI(),
                        WebServiceType.OCR.getSoapLocalPartPort()),
                OCR.class, this.getMTOMFeature());
    }

    /**
     * Initializes and prepares the execution of this {@link OcrWebService}.
     *
     * @return The prepared {@link OperationData}.
     */
    @Override
    protected @NotNull OperationData initOperation() {
        OperationData operationData = new OperationData();
        operationData.setBilling(new BillingType());
        operationData.setPassword(new PdfPasswordType());
        operationData.setSettings(new SettingsType());
        operationData.setOcr(new OcrType());
        return operationData;
    }

}
