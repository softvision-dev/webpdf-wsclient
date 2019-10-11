package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.schema.operation.BillingType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractWebService<T_DOC extends Document, T_OPERATION_TYPE, T_RESULT>
    implements WebService<T_DOC, T_OPERATION_TYPE, T_RESULT> {

    @NotNull
    final WebServiceType webServiceType;
    @NotNull
    final Map<String, List<String>> headers = new HashMap<>();
    @NotNull
    final Session session;
    @NotNull
    OperationData operation = new OperationData();
    @Nullable
    T_DOC document;

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link Session}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link Session} the webservice interface shall be created for.
     */
    AbstractWebService(@NotNull WebServiceType webServiceType, @NotNull Session session) {
        this.session = session;
        this.webServiceType = webServiceType;
        this.operation.setBilling(new BillingType());
        this.operation.setPassword(new PdfPasswordType());
        initOperation(this.operation);
    }

    /**
     * Returns the {@link Document} containing the document source.
     *
     * @return the {@link Document} containing the document source.
     */
    @Override
    @Nullable
    public T_DOC getDocument() {
        return document;
    }

    /**
     * Sets the {@link Document} containing the document source.
     *
     * @param document sets the {@link Document} containing the document source.
     */
    @Override
    public void setDocument(@Nullable T_DOC document) {
        this.document = document;
    }

    /**
     * Returns the {@link BillingType} of the current webservice.
     *
     * @return the {@link BillingType} of the current webservice.
     */
    @NotNull
    public BillingType getBilling() {
        return this.operation.getBilling();
    }

    /**
     * Returns the {@link PdfPasswordType} of the current webservice.
     *
     * @return the {@link PdfPasswordType} of the current webservice.
     */
    @NotNull
    public PdfPasswordType getPassword() {
        return this.operation.getPassword();
    }

    /**
     * Initialize all substructures, that must be set for this webservice to accept parameters for this
     * webservice type.
     *
     * @param operation The operationData that, shall be initialized for webservice execution.
     */
    protected abstract void initOperation(@NotNull OperationData operation);

}
