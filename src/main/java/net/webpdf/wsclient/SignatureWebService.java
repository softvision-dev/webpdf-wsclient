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

    SignatureWebService(Session session) throws ResultException {
        super(session, WebServiceType.SIGNATURE);
        this.operation.setSignature(new SignatureType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
                new QName(WebServiceType.SIGNATURE.getSoapNamespaceURI(),
                        WebServiceType.SIGNATURE.getSoapLocalPartPort()),
                Signature.class, this.getFeature());
    }

    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation,
                this.document.getSourceDataHandler(),
                this.document.isFileSource() ? null : this.document.getSource().toString());
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
