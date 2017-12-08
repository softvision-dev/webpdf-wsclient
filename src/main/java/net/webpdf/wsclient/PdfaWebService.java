package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.PdfaType;
import net.webpdf.wsclient.schema.stubs.Pdfa;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class PdfaWebService extends SoapWebService<Pdfa, PdfaType> {

    PdfaWebService(Session session) throws ResultException {
        super(session, WebServiceType.PDFA);
        this.operation.setPdfa(new PdfaType());


        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.PDFA.getSoapNamespaceURI(),
                         WebServiceType.PDFA.getSoapLocalPartPort()),
            Pdfa.class, this.getFeature());
    }

    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation,
            this.document.getSourceDataHandler(),
            this.document.isFileSource() ? null : this.document.getSource().toString());
    }

    @Override
    public PdfaType getOperation() {
        return this.operation.getPdfa();
    }

    @Override
    public void setOperation(PdfaType operationData) throws ResultException {
        if (operationData != null) {
            operation.setPdfa(operationData);
        }
    }
}
