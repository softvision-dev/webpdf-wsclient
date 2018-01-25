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

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link Session}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link Session} the webservice interface shall be created for.
     */
    AbstractWebService(WebServiceType webServiceType, Session session) {
        this.session = session;
        this.webServiceType = webServiceType;
        this.operation.setBilling(new BillingType());
        this.operation.setPassword(new PdfPasswordType());
    }

    /**
     * Returns the {@link Document} containing the document source.
     *
     * @return the {@link Document} containing the document source.
     */
    @Override
    public T_DOC getDocument() {
        return document;
    }

    /**
     * Sets the {@link Document} containing the document source.
     *
     * @param document sets the {@link Document} containing the document source.
     */
    @Override
    public void setDocument(T_DOC document) {
        this.document = document;
    }

    /**
     * Returns the {@link BillingType} of the current webservice.
     *
     * @return the {@link BillingType} of the current webservice.
     */
    public BillingType getBilling() {
        return this.operation.getBilling();
    }

    /**
     * Returns the {@link PdfPasswordType} of the current webservice.
     *
     * @return the {@link PdfPasswordType} of the current webservice.
     */
    public PdfPasswordType getPassword() {
        return this.operation.getPassword();
    }
}
