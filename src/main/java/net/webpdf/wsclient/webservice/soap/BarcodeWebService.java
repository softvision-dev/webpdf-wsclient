package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
import jakarta.xml.ws.Service;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.schema.stubs.Barcode;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;

/**
 * An instance of {@link BarcodeWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#BARCODE}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT> The expected {@link SoapDocument} type for the documents used by the webPDF server.
 */
public class BarcodeWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<Barcode, BarcodeType, T_SOAP_DOCUMENT> {

    /**
     * Creates a {@link BarcodeWebService} for the given {@link SoapSession}.
     *
     * @param session The {@link SoapSession} a {@link BarcodeWebService} shall be created for.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    public BarcodeWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.BARCODE);
    }

    /**
     * <p>
     * Executes the {@link BarcodeWebService} operation and returns the {@link DataHandler} of the result document.
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
     * Returns the {@link BarcodeWebService} specific {@link BarcodeType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link BarcodeType} operation parameters.
     */
    @Override
    public @NotNull BarcodeType getOperationParameters() {
        return getOperationData().getBarcode();
    }

    /**
     * Sets the {@link BarcodeWebService} specific {@link BarcodeType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operation Sets the {@link BarcodeType} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable BarcodeType operation) {
        if (operation != null) {
            getOperationData().setBarcode(operation);
        }
    }

    /**
     * Create a matching {@link Barcode} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Barcode} webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @NotNull Barcode provideWebServicePort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.BARCODE.getSoapNamespaceURI(),
                        WebServiceType.BARCODE.getSoapLocalPartPort()),
                Barcode.class, this.getMTOMFeature());
    }

    /**
     * Initializes and prepares the execution of this {@link BarcodeWebService}.
     *
     * @return The prepared {@link OperationData}.
     */
    @Override
    protected @NotNull OperationData initOperation() {
        OperationData operationData = new OperationData();
        operationData.setBilling(new BillingType());
        operationData.setPassword(new PdfPasswordType());
        operationData.setSettings(new SettingsType());
        operationData.setBarcode(new BarcodeType());
        return operationData;
    }

}
