package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.schema.operation.BillingType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.session.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractWebService<T_DOC extends Document, T_OPERATION_TYPE, T_RESULT>
    implements WebService<T_DOC, T_OPERATION_TYPE, T_RESULT> {

    final WebServiceType webServiceType;
    final Map<String, List<String>> headers = new HashMap<>();
    final Session session;
    OperationData operation = new OperationData();
    T_DOC document;

    AbstractWebService(WebServiceType webServiceType, Session session) {
        this.session = session;
        this.webServiceType = webServiceType;
        this.operation.setBilling(new BillingType());
        this.operation.setPassword(new PdfPasswordType());
    }

    @Override
    public T_DOC getDocument() {
        return document;
    }

    @Override
    public void setDocument(T_DOC document) {
        this.document = document;
    }

    public BillingType getBilling() {
        return this.operation.getBilling();
    }

    public PdfPasswordType getPassword() {
        return this.operation.getPassword();
    }
}
