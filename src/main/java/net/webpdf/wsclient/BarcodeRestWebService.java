package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BarcodeType;
import net.webpdf.wsclient.session.Session;

public class BarcodeRestWebService extends RestWebservice<BarcodeType> {

    public BarcodeRestWebService(Session session) {
        super(session, WebServiceType.BARCODE);
        this.operation.setBarcode(new BarcodeType());
    }

    @Override
    public BarcodeType getOperation() {
        return this.operation.getBarcode();
    }

    @Override
    public void setOperation(BarcodeType operationData) throws ResultException {
        if (operationData != null) {
            operation.setBarcode(operationData);
        }
    }
}
