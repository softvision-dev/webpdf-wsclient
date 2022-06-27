package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.session.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.ConverterType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.stubs.Converter;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.activation.DataHandler;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;

/**
 * An instance of {@link ConverterWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#CONVERTER}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 */
public class ConverterWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<T_SOAP_DOCUMENT, Converter, ConverterType> {

    /**
     * Creates a {@link ConverterWebService} for the given {@link SoapSession}
     *
     * @param session The {@link SoapSession} a {@link ConverterWebService} shall be created for.
     */
    public ConverterWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.CONVERTER);
    }

    /**
     * Executes the {@link ConverterWebService} operation and returns the {@link DataHandler} of the result document.
     *
     * @return The {@link DataHandler} of the result document.
     * @throws WebServiceException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @Nullable DataHandler processService() throws WebServiceException {
        if (getDocument() == null) {
            return null;
        }
        return getPort().execute(getOperationData(), getDocument().getSourceDataHandler(),
                getDocument().isFileSource() || getDocument().getSource() == null ?
                        null : getDocument().getSource().toString());
    }

    /**
     * Create a matching {@link Converter} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Converter} webservice port, that shall be used for executions.
     */
    @Override
    protected @NotNull Converter provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.CONVERTER.getSoapNamespaceURI(),
                        WebServiceType.CONVERTER.getSoapLocalPartPort()),
                Converter.class, this.getMTOMFeature());
    }

    /**
     * Returns the {@link ConverterWebService} specific {@link ConverterType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link ConverterType} operation parameters.
     */
    @Override
    public @NotNull ConverterType getOperation() {
        return getOperationData().getConverter();
    }

    /**
     * Sets the {@link ConverterWebService} specific {@link ConverterType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operationData Sets the {@link ConverterType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable ConverterType operationData) {
        if (operationData != null) {
            getOperationData().setConverter(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link ConverterWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link ConverterWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setConverter(new ConverterType());
    }

}
