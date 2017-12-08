package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.OcrType;

public class OcrRestWebService extends RestWebservice<OcrType> {

    public OcrRestWebService(Session sessionToken) {
        super(sessionToken, WebServiceType.OCR);
        this.operation.setOcr(new OcrType());
    }

    @Override
    public OcrType getOperation() {
        return this.operation.getOcr();
    }

    @Override
    public void setOperation(OcrType operationData) throws ResultException {
        if (operationData != null) {
            operation.setOcr(operationData);
        }
    }
}
