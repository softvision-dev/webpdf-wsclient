package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.SignatureType;

public class SignatureRestWebService extends RestWebservice<SignatureType> {

    /**
     * Creates a SignatureRestWebService for the given {@link Session}
     *
     * @param session The session a SignatureRestWebservice shall be created for.
     */
    public SignatureRestWebService(Session session) {
        super(session, WebServiceType.SIGNATURE);
        this.operation.setSignature(new SignatureType());
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public SignatureType getOperation() {
        return this.operation.getSignature();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(SignatureType operationData) {
        if (operationData != null) {
            operation.setSignature(operationData);
        }
    }
}
