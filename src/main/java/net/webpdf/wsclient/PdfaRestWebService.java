package net.webpdf.wsclient;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.PdfaType;

public class PdfaRestWebService extends RestWebservice<PdfaType> {

    /**
     * Creates a PdfaRestWebService for the given {@link Session}
     *
     * @param sessionToken The session a PdfaRestWebservice shall be created for.
     */
    public PdfaRestWebService(Session sessionToken) {
        super(sessionToken, WebServiceType.PDFA);
        this.operation.setPdfa(new PdfaType());
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public PdfaType getOperation() {
        return this.operation.getPdfa();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(PdfaType operationData) {
        if (operationData != null) {
            operation.setPdfa(operationData);
        }
    }
}
