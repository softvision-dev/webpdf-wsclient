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

    UrlConverterWebService(Session session) throws ResultException {
        super(session, WebServiceType.URLCONVERTER);
        this.operation.setUrlconverter(new UrlConverterType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.URLCONVERTER.getSoapNamespaceURI(),
                         WebServiceType.URLCONVERTER.getSoapLocalPartPort()),
            URLConverter.class, this.getFeature());
    }

    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation);
    }

    @Override
    public UrlConverterType getOperation() {
        return this.operation.getUrlconverter();
    }

    @Override
    public void setOperation(UrlConverterType operationData) throws ResultException {
        if (operationData != null) {
            operation.setUrlconverter(operationData);
        }
    }
}
