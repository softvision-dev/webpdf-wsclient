package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.SignatureType;

public class SignatureRestWebService extends RestWebservice<SignatureType> {

    public SignatureRestWebService(Session session) {
        super(session, WebServiceType.SIGNATURE);
        this.operation.setSignature(new SignatureType());
    }

    @Override
    public SignatureType getOperation() {
        return this.operation.getSignature();
    }

    @Override
    public void setOperation(SignatureType operationData) throws ResultException {
        if (operationData != null) {
            operation.setSignature(operationData);
        }
    }
}
