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

/**
 * An instance of {@link AbstractWebService} wraps a wsclient connection to a specific webPDF webservice endpoint
 * ({@link WebServiceType}), using a specific {@link WebServiceProtocol} and expecting a specific {@link Document} type
 * as the result.
 *
 * @param <T_DOCUMENT>       The expected {@link Document} type for the results produced by the webPDF server.
 * @param <T_SESSION>        The expected {@link Session} type for the webservice connection.
 * @param <T_OPERATION_TYPE> The {@link WebServiceType} of the targeted webservice endpoint.
 * @param <T_RESULT>         The result type, that is expected.
 */
public abstract class AbstractWebService<T_DOCUMENT extends Document, T_SESSION extends Session<T_DOCUMENT>,
        T_OPERATION_TYPE, T_RESULT> implements WebService<T_DOCUMENT, T_OPERATION_TYPE, T_RESULT> {

    private final @NotNull WebServiceType webServiceType;
    private final @NotNull Map<String, List<String>> headers = new HashMap<>();
    private final @NotNull T_SESSION session;
    private final @NotNull OperationData operationData = new OperationData();
    private @Nullable T_DOCUMENT document;

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
     * Returns the {@link WebServiceType} of the current webservice.
     *
     * @return The {@link WebServiceType} of the current webservice.
     */
    protected @NotNull WebServiceType getWebServiceType() {
        return webServiceType;
    }

    /**
     * Returns the {@link Session} of the current webservice.
     *
     * @return The {@link Session} of the current webservice.
     */
    protected @NotNull T_SESSION getSession() {
        return session;
    }

    /**
     * Returns the headers of the current webservice operation.
     *
     * @return The headers of the current webservice operation.
     */
    protected @NotNull Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Returns the {@link Document} which contains the source document, that shall be processed.
     *
     * @return The {@link Document} which contains the source document, that shall be processed.
     */
    @Override
    public @Nullable T_DOCUMENT getDocument() {
        return document;
    }

    /**
     * Set the {@link Document} which contains the source document, that shall be processed.
     *
     * @param document The {@link Document} which contains the source document, that shall be processed.
     */
    @Override
    public void setDocument(@Nullable T_DOCUMENT document) {
        this.document = document;
    }

    /**
     * Returns the {@link OperationData} of the current webservice.
     *
     * @return The {@link OperationData} of the current webservice.
     */
    public @NotNull OperationData getOperationData() {
        return operationData;
    }

    /**
     * Returns the {@link BillingType} of the current webservice.
     *
     * @return the {@link BillingType} of the current webservice.
     */
    public @NotNull BillingType getBilling() {
        return this.operationData.getBilling();
    }

    /**
     * Returns the {@link PdfPasswordType} of the current webservice.
     *
     * @return the {@link PdfPasswordType} of the current webservice.
     */
    public @NotNull PdfPasswordType getPassword() {
        return this.operationData.getPassword();
    }

    /**
     * Initializes and prepares the execution of the current webservice via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the current webservice.
     */
    protected abstract void initOperation(@NotNull OperationData operation);

}
