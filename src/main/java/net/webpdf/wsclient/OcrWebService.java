package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OcrType;
import net.webpdf.wsclient.schema.stubs.OCR;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class OcrWebService extends SoapWebService<OCR, OcrType> {

    OcrWebService(Session session) throws ResultException {
        super(session, WebServiceType.OCR);
        this.operation.setOcr(new OcrType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.OCR.getSoapNamespaceURI(),
                         WebServiceType.OCR.getSoapLocalPartPort()),
            OCR.class, this.getFeature());
    }

    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation,
            this.document.getSourceDataHandler(),
            this.document.isFileSource() ? null : this.document.getSource().toString());
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
