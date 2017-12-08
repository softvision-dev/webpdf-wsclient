package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.PdfaType;

public class PdfaRestWebService extends RestWebservice<PdfaType> {

    public PdfaRestWebService(Session sessionToken) {
        super(sessionToken, WebServiceType.PDFA);
        this.operation.setPdfa(new PdfaType());
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
