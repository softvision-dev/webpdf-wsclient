package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.UrlConverterType;
import net.webpdf.wsclient.schema.stubs.URLConverter;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.SoapWebService;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class UrlConverterWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<T_SOAP_DOCUMENT, URLConverter, UrlConverterType> {

    /**
     * Creates a SOAP UrlConverterWebService for the given {@link Session}
     *
     * @param session The session a SOAP UrlConverterWebservice shall be created for.
     */
    public UrlConverterWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.URLCONVERTER);
    }

    /**
     * Executes webservice operation and returns {@link DataHandler} of the result document.
     *
     * @return A {@link DataHandler} of the result document.
     * @throws WebserviceException a {@link WebserviceException}
     */
    @Override
    @Nullable
    protected DataHandler processService() throws WebserviceException {
        return getPort().execute(getOperationData());
    }

    /**
     * Create a matching webservice port for future executions of this SOAP webservice.
     *
     * @return The webservice port, that shall be used for executions.
     */
    @Override
    @NotNull
    protected URLConverter provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.URLCONVERTER.getSoapNamespaceURI(),
                        WebServiceType.URLCONVERTER.getSoapLocalPartPort()),
                URLConverter.class, this.getFeature());
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public UrlConverterType getOperation() {
        return getOperationData().getUrlconverter();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable UrlConverterType operationData) {
        if (operationData != null) {
            getOperationData().setUrlconverter(operationData);
        }
    }

    /**
     * Initialize all substructures, that must be set for this webservice to accept parameters for this
     * webservice type.
     *
     * @param operation The operationData that, shall be initialized for webservice execution.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setUrlconverter(new UrlConverterType());
    }

}
