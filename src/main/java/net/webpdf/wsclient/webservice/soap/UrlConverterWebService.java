package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.session.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.UrlConverterType;
import net.webpdf.wsclient.schema.stubs.URLConverter;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.activation.DataHandler;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;

/**
 * An instance of {@link UrlConverterWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#URLCONVERTER}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 */
public class UrlConverterWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<T_SOAP_DOCUMENT, URLConverter, UrlConverterType> {

    /**
     * Creates a {@link UrlConverterWebService} for the given {@link SoapSession}
     *
     * @param session The {@link SoapSession} a {@link UrlConverterWebService} shall be created for.
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
     * Create a matching {@link URLConverter} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link URLConverter} webservice port, that shall be used for executions.
     */
    @Override
    protected @NotNull URLConverter provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.URLCONVERTER.getSoapNamespaceURI(),
                        WebServiceType.URLCONVERTER.getSoapLocalPartPort()),
                URLConverter.class, this.getMTOMFeature());
    }

    /**
     * Returns the {@link UrlConverterWebService} specific {@link UrlConverterType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link UrlConverterType} operation parameters.
     */
    @Override
    public @NotNull UrlConverterType getOperation() {
        return getOperationData().getUrlconverter();
    }

    /**
     * Sets the {@link UrlConverterWebService} specific {@link UrlConverterType} element, which allows setting
     * parameters for the webservice execution.
     *
     * @param operationData Sets the {@link UrlConverterType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable UrlConverterType operationData) {
        if (operationData != null) {
            getOperationData().setUrlconverter(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link UrlConverterWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link UrlConverterWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setUrlconverter(new UrlConverterType());
    }

}
