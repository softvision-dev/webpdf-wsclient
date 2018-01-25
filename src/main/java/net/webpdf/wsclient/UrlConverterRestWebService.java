package net.webpdf.wsclient;


import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.UrlConverterType;

public class UrlConverterRestWebService extends RestWebservice<UrlConverterType> {

    /**
     * Creates a UrlConverterRestWebService for the given {@link Session}
     *
     * @param session The session a UrlConverterRestWebservice shall be created for.
     */
    public UrlConverterRestWebService(Session session) {
        super(session, WebServiceType.URLCONVERTER);
        this.operation.setUrlconverter(new UrlConverterType());
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
