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

    /**
     * Creates a SOAP OcrWebService for the given {@link Session}
     *
     * @param session The session a SOAP OcrWebservice shall be created for.
     */
    OcrWebService(Session session) throws ResultException {
        super(session, WebServiceType.OCR);
        this.operation.setOcr(new OcrType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.OCR.getSoapNamespaceURI(),
                         WebServiceType.OCR.getSoapLocalPartPort()),
            OCR.class, this.getFeature());
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
