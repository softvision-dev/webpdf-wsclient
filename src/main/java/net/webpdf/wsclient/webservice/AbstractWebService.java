package net.webpdf.wsclient.webservice;

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

public abstract class AbstractWebService
        <T_DOCUMENT extends Document, T_SESSION extends Session<T_DOCUMENT>, T_OPERATION_TYPE, T_RESULT>
        implements WebService<T_DOCUMENT, T_OPERATION_TYPE, T_RESULT> {

    @NotNull
    private final WebServiceType webServiceType;
    @NotNull
    private final Map<String, List<String>> headers = new HashMap<>();
    @NotNull
    private final T_SESSION session;
    @NotNull
    private final OperationData operationData = new OperationData();
    @Nullable
    private T_DOCUMENT document;

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link Session}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link Session} the webservice interface shall be created for.
     */
    public AbstractWebService(@NotNull WebServiceType webServiceType, @NotNull T_SESSION session) {
        this.session = session;
        this.webServiceType = webServiceType;
        this.operationData.setBilling(new BillingType());
        this.operationData.setPassword(new PdfPasswordType());
        initOperation(this.operationData);
    }

    /**
     * Returns the {@link Document} containing the document source.
     *
     * @return the {@link Document} containing the document source.
     */
    @Override
    @Nullable
    public T_DOCUMENT getDocument() {
        return document;
    }

    /**
     * Sets the {@link Document} containing the document source.
     *
     * @param document sets the {@link Document} containing the document source.
     */
    @Override
    public void setDocument(@Nullable T_DOCUMENT document) {
        this.document = document;
    }

    /**
     * Returns the {@link BillingType} of the current webservice.
     *
     * @return the {@link BillingType} of the current webservice.
     */
    @NotNull
    public BillingType getBilling() {
        return this.operationData.getBilling();
    }

    /**
     * Returns the {@link PdfPasswordType} of the current webservice.
     *
     * @return the {@link PdfPasswordType} of the current webservice.
     */
    @NotNull
    public PdfPasswordType getPassword() {
        return this.operationData.getPassword();
    }

    /**
     * Initialize all substructures, that must be set for this webservice to accept parameters for this
     * webservice type.
     *
     * @param operation The operationData that, shall be initialized for webservice execution.
     */
    protected abstract void initOperation(@NotNull OperationData operation);

    /**
     * Returns the {@link Session} of this webservice call.
     *
     * @return The {@link Session} of this webservice call.
     */
    @NotNull
    public T_SESSION getSession() {
        return session;
    }

    /**
     * Returns the {@link WebServiceType} of this webservice call.
     * @return The {@link WebServiceType} of this webservice call.
     */
    @NotNull
    protected WebServiceType getWebServiceType() {
        return webServiceType;
    }

    /**
     * Returns the {@link OperationData} of this webservice call.
     * @return The {@link OperationData} of this webservice call.
     */
    @NotNull
    protected OperationData getOperationData() {
        return operationData;
    }

    /**
     * Returns a map of HTTP-headers for this webservice call.
     * @return A map of HTTP-headers for this webservice call.
     */
    @NotNull
    protected Map<String, List<String>> getHeaders() {
        return headers;
    }
}
