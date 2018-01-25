package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.ConverterType;
import net.webpdf.wsclient.schema.stubs.Converter;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class ConverterWebService extends SoapWebService<Converter, ConverterType> {

    /**
     * Creates a SOAP ConverterWebService for the given {@link Session}
     *
     * @param session The session a SOAP ConverterWebservice shall be created for.
     */
    ConverterWebService(Session session) throws ResultException {
        super(session, WebServiceType.CONVERTER);
        this.operation.setConverter(new ConverterType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.CONVERTER.getSoapNamespaceURI(),
                WebServiceType.CONVERTER.getSoapLocalPartPort()),
            Converter.class, this.getFeature());
    }

    /**
     * Executes webservice operation and returns {@link DataHandler} of the result document.
     *
     * @return A {@link DataHandler} of the result document.
     * @throws WebserviceException a {@link WebserviceException}
     */
    @Override
    DataHandler processService() throws WebserviceException {
        DataHandler dataHandler = this.document.getSourceDataHandler();
        return this.port.execute(this.operation, dataHandler, "");
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public ConverterType getOperation() {
        return operation.getConverter();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(ConverterType operationData) {
        if (operationData != null) {
            operation.setConverter(operationData);
        }
    }
}
