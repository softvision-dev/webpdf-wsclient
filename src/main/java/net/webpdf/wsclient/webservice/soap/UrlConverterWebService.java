package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
import jakarta.xml.ws.Service;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.schema.stubs.URLConverter;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;

/**
 * An instance of {@link UrlConverterWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#URLCONVERTER}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT> The expected {@link SoapDocument} type for the documents used by the webPDF server.
 */
public class UrlConverterWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<URLConverter, UrlConverterType, T_SOAP_DOCUMENT> {

    /**
     * Creates a {@link UrlConverterWebService} for the given {@link SoapSession}
     *
     * @param session The {@link SoapSession} a {@link UrlConverterWebService} shall be created for.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    public UrlConverterWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.URLCONVERTER);
    }

    /**
     * Executes the {@link UrlConverterWebService} operation and returns the {@link DataHandler} of the result document.
     *
     * @return The {@link DataHandler} of the result document.
     * @throws WebServiceException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @Nullable DataHandler processService() throws WebServiceException {
        return getPort().execute(getOperationData());
    }

    /**
     * Returns the {@link UrlConverterWebService} specific {@link UrlConverterType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link UrlConverterType} operation parameters.
     */
    @Override
    public @NotNull UrlConverterType getOperationParameters() {
        return getOperationData().getUrlconverter();
    }

    /**
     * Sets the {@link UrlConverterWebService} specific {@link UrlConverterType} element, which allows setting
     * parameters for the webservice execution.
     *
     * @param operation Sets the {@link UrlConverterType} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable UrlConverterType operation) {
        if (operation != null) {
            getOperationData().setUrlconverter(operation);
        }
    }

    /**
     * Create a matching {@link URLConverter} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link URLConverter} webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @NotNull URLConverter provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.URLCONVERTER.getSoapNamespaceURI(),
                        WebServiceType.URLCONVERTER.getSoapLocalPartPort()),
                URLConverter.class, this.getMTOMFeature());
    }

    /**
     * Initializes and prepares the execution of this {@link UrlConverterWebService}.
     *
     * @return The prepared {@link OperationData}.
     */
    @Override
    protected @NotNull OperationData initOperation() {
        OperationData operationData = new OperationData();
        operationData.setBilling(new BillingType());
        operationData.setPassword(new PdfPasswordType());
        operationData.setSettings(new SettingsType());
        operationData.setUrlconverter(new UrlConverterType());
        return operationData;
    }

}
