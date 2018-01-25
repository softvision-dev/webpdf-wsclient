package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.ConverterType;

public class ConverterRestWebService extends RestWebservice<ConverterType> {

    /**
     * Creates a ConverterRestWebService for the given {@link Session}
     *
     * @param session The session a ConverterRestWebservice shall be created for.
     */
    public ConverterRestWebService(Session session) {
        super(session, WebServiceType.CONVERTER);
        this.operation.setConverter(new ConverterType());
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public ConverterType getOperation() {
        return this.operation.getConverter();
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
