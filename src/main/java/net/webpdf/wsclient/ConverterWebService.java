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

    ConverterWebService(Session session) throws ResultException {
        super(session, WebServiceType.CONVERTER);
        this.operation.setConverter(new ConverterType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.CONVERTER.getSoapNamespaceURI(),
                         WebServiceType.CONVERTER.getSoapLocalPartPort()),
            Converter.class, this.getFeature());
    }

    @Override
    DataHandler processService() throws WebserviceException {
        DataHandler dataHandler = this.document.getSourceDataHandler();
        return this.port.execute(this.operation, dataHandler, "");
    }

    @Override
    public ConverterType getOperation() {
        return operation.getConverter();
    }

    @Override
    public void setOperation(ConverterType operationData) throws ResultException {
        if (operationData != null) {
            operation.setConverter(operationData);
        }
    }
}
