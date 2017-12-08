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

    BarcodeWebService(Session session) throws ResultException {
        super(session, WebServiceType.BARCODE);
        this.operation.setBarcode(new BarcodeType());

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
                new QName(WebServiceType.BARCODE.getSoapNamespaceURI(),
                        WebServiceType.BARCODE.getSoapLocalPartPort()),
                Barcode.class, this.getFeature());
    }

    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation,
                this.document.getSourceDataHandler(),
                this.document.isFileSource() ? null : this.document.getSource().toString());
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
