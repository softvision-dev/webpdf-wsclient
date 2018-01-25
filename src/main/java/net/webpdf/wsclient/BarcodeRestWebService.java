package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BarcodeType;
import net.webpdf.wsclient.session.Session;

public class BarcodeRestWebService extends RestWebservice<BarcodeType> {

    /**
     * Creates a BarcodeRestWebService for the given {@link Session}
     *
     * @param session The session a BarcodeRestWebservice shall be created for.
     */
    public BarcodeRestWebService(Session session) {
        super(session, WebServiceType.BARCODE);
        this.operation.setBarcode(new BarcodeType());
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public BarcodeType getOperation() {
        return this.operation.getBarcode();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(BarcodeType operationData) {
        if (operationData != null) {
            operation.setBarcode(operationData);
        }
    }
}
