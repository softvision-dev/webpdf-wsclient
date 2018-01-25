package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.stubs.Barcode;
import net.webpdf.wsclient.schema.operation.BarcodeType;
import net.webpdf.wsclient.schema.stubs.WebserviceException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class BarcodeWebService extends SoapWebService<Barcode, BarcodeType> {

    /**
     * Creates a SOAP BarcodeWebService for the given {@link Session}
     *
     * @param session The session a SOAP BarcodeWebservice shall be created for.
     */
    BarcodeWebService(Session session) throws ResultException {
        super(session, WebServiceType.BARCODE);
        this.operation.setBarcode(new BarcodeType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.BARCODE.getSoapNamespaceURI(),
                WebServiceType.BARCODE.getSoapLocalPartPort()),
            Barcode.class, this.getFeature());
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
