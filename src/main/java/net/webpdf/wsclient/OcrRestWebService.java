package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.OcrType;

public class OcrRestWebService extends RestWebservice<OcrType> {

    /**
     * Creates a OcrRestWebService for the given {@link Session}
     *
     * @param sessionToken The session a OcrRestWebservice shall be created for.
     */
    public OcrRestWebService(Session sessionToken) {
        super(sessionToken, WebServiceType.OCR);
        this.operation.setOcr(new OcrType());
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public OcrType getOperation() {
        return this.operation.getOcr();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(OcrType operationData) {
        if (operationData != null) {
            operation.setOcr(operationData);
        }
    }
}
