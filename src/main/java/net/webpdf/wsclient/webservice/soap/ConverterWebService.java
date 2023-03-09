package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
import jakarta.xml.ws.Service;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.schema.stubs.Converter;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;

/**
 * An instance of {@link ConverterWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#CONVERTER}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT> The expected {@link SoapDocument} type for the documents used by the webPDF server.
 */
public class ConverterWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<Converter, ConverterType, T_SOAP_DOCUMENT> {

    /**
     * Creates a {@link ConverterWebService} for the given {@link SoapSession}.
     *
     * @param session The {@link SoapSession} a {@link ConverterWebService} shall be created for.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    public ConverterWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.CONVERTER);
    }

    /**
     * <p>
     * Executes the {@link ConverterWebService} operation and returns the {@link DataHandler} of the result document.
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
     * Returns the {@link ConverterWebService} specific {@link ConverterType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link ConverterType} operation parameters.
     */
    @Override
    public @NotNull ConverterType getOperationParameters() {
        return getOperationData().getConverter();
    }

    /**
     * Sets the {@link ConverterWebService} specific {@link ConverterType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operation Sets the {@link ConverterType} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable ConverterType operation) {
        if (operation != null) {
            getOperationData().setConverter(operation);
        }
    }

    /**
     * Create a matching {@link Converter} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Converter} webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @NotNull Converter provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.CONVERTER.getSoapNamespaceURI(),
                        WebServiceType.CONVERTER.getSoapLocalPartPort()),
                Converter.class, this.getMTOMFeature());
    }

    /**
     * Initializes and prepares the execution of this {@link ConverterWebService}.
     *
     * @return The prepared {@link OperationData}.
     */
    @Override
    protected @NotNull OperationData initOperation() {
        OperationData operationData = new OperationData();
        operationData.setBilling(new BillingType());
        operationData.setPassword(new PdfPasswordType());
        operationData.setSettings(new SettingsType());
        operationData.setConverter(new ConverterType());
        return operationData;
    }

}
