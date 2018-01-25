package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.UrlConverterType;
import net.webpdf.wsclient.schema.stubs.URLConverter;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class UrlConverterWebService extends SoapWebService<URLConverter, UrlConverterType> {

    /**
     * Creates a SOAP UrlConverterWebService for the given {@link Session}
     *
     * @param session The session a SOAP UrlConverterWebservice shall be created for.
     */
    UrlConverterWebService(Session session) throws ResultException {
        super(session, WebServiceType.URLCONVERTER);
        this.operation.setUrlconverter(new UrlConverterType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.URLCONVERTER.getSoapNamespaceURI(),
                WebServiceType.URLCONVERTER.getSoapLocalPartPort()),
            URLConverter.class, this.getFeature());
    }

    /**
     * Executes webservice operation and returns {@link DataHandler} of the result document.
     *
     * @return A {@link DataHandler} of the result document.
     * @throws WebserviceException a {@link WebserviceException}
     */
    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public UrlConverterType getOperation() {
        return this.operation.getUrlconverter();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(UrlConverterType operationData) {
        if (operationData != null) {
            operation.setUrlconverter(operationData);
        }
    }
}
