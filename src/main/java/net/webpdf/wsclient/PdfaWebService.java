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

    /**
     * Creates a SOAP PdfaWebService for the given {@link Session}
     *
     * @param session The session a SOAP PdfaWebservice shall be created for.
     */
    PdfaWebService(Session session) throws ResultException {
        super(session, WebServiceType.PDFA);
        this.operation.setPdfa(new PdfaType());


        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.PDFA.getSoapNamespaceURI(),
                WebServiceType.PDFA.getSoapLocalPartPort()),
            Pdfa.class, this.getFeature());
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
    public PdfaType getOperation() {
        return this.operation.getPdfa();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(PdfaType operationData) {
        if (operationData != null) {
            operation.setPdfa(operationData);
        }
    }
}
