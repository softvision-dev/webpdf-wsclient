package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.SignatureType;
import net.webpdf.wsclient.schema.stubs.Signature;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class SignatureWebService extends SoapWebService<Signature, SignatureType> {

    /**
     * Creates a SOAP SignatureWebService for the given {@link Session}
     *
     * @param session The session a SOAP SignatureWebservice shall be created for.
     */
    SignatureWebService(Session session) throws ResultException {
        super(session, WebServiceType.SIGNATURE);
        this.operation.setSignature(new SignatureType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.SIGNATURE.getSoapNamespaceURI(),
                WebServiceType.SIGNATURE.getSoapLocalPartPort()),
            Signature.class, this.getFeature());
    }

    /**
     * Executes webservice operation and returns {@link DataHandler} of the result document.
     *
     * @return A {@link DataHandler} of the result document.
     * @throws WebserviceException a {@link WebserviceException}
     */
    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation,
            this.document.getSourceDataHandler(),
            this.document.isFileSource() ? null : this.document.getSource().toString());
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
