package net.webpdf.wsclient;


import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.UrlConverterType;

public class UrlConverterRestWebService extends RestWebservice<UrlConverterType> {

    public UrlConverterRestWebService(Session session) {
        super(session, WebServiceType.URLCONVERTER);
        this.operation.setUrlconverter(new UrlConverterType());
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
