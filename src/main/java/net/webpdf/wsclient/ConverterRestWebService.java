package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.ConverterType;

public class ConverterRestWebService extends RestWebservice<ConverterType> {

    public ConverterRestWebService(Session session) {
        super(session, WebServiceType.CONVERTER);
        this.operation.setConverter(new ConverterType());
    }

    @Override
    public ConverterType getOperation() {
        return this.operation.getConverter();
    }

    @Override
    public void setOperation(ConverterType operationData) throws ResultException {
        if (operationData != null) {
            operation.setConverter(operationData);
        }
    }
}
