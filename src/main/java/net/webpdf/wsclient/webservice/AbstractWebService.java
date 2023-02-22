package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.session.documents.Document;
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
 * @param <T_SESSION>             The expected {@link Session} type for the webservice connection.
 * @param <T_OPERATION_DATA>      The operation type of the targeted webservice endpoint.
 * @param <T_OPERATION_PARAMETER> The parameter type of the targeted webservice endpoint.
 * @param <T_DOCUMENT>            The expected {@link Document} type for the results produced by the webPDF server.
 * @param <T_BILLING>             The operation´s billing type configuring the server´s billing log entries.
 * @param <T_PASSWORD>            The operation´s password type, used to configure material for password-protected
 *                                documents.
 * @param <T_SETTINGS>            The operation´s additional settings type, used to configure webservice independent
 *                                options and parameters.
 */
public abstract class AbstractWebService<T_SESSION extends Session, T_OPERATION_DATA, T_OPERATION_PARAMETER,
        T_DOCUMENT extends Document, T_BILLING, T_PASSWORD, T_SETTINGS>
        implements WebService<T_SESSION, T_OPERATION_DATA, T_OPERATION_PARAMETER, T_DOCUMENT, T_BILLING, T_PASSWORD,
        T_SETTINGS> {

    private final @NotNull WebServiceType webServiceType;
    private final @NotNull Map<String, List<String>> headers = new HashMap<>();
    private final @NotNull T_SESSION session;
    private @Nullable T_DOCUMENT document;
    private final @NotNull T_OPERATION_DATA operationData;

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link T_SESSION}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link T_SESSION} the webservice interface shall be created for.
     */
    public AbstractWebService(@NotNull WebServiceType webServiceType, @NotNull T_SESSION session) {
        this.session = session;
        this.webServiceType = webServiceType;
        this.operationData = initOperation();
    }

    /**
     * Returns the {@link T_SESSION} of the current webservice.
     *
     * @return The {@link T_SESSION} of the current webservice.
     */
    @Override
    public @NotNull T_SESSION getSession() {
        return session;
    }

    /**
     * Returns the {@link T_OPERATION_DATA} of the current webservice.
     *
     * @return The {@link T_OPERATION_DATA} of the current webservice.
     */
    @Override
    public @NotNull T_OPERATION_DATA getOperationData() {
        return operationData;
    }

    /**
     * Returns the {@link T_DOCUMENT} which contains the source document, that shall be processed.
     *
     * @return The {@link T_DOCUMENT} which contains the source document, that shall be processed.
     */
    @Override
    public @Nullable T_DOCUMENT getSourceDocument() {
        return document;
    }

    /**
     * Set the {@link T_DOCUMENT} which contains the source document, that shall be processed.
     *
     * @param document The {@link T_DOCUMENT} which contains the source document, that shall be processed.
     */
    @Override
    public void setSourceDocument(@Nullable T_DOCUMENT document) {
        this.document = document;
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
     * Returns the headers of the current webservice operation.
     *
     * @return The headers of the current webservice operation.
     */
    protected @NotNull Map<String, List<String>> getHeaders() {
        return headers;
    }

    /**
     * Initializes and prepares the {@link T_OPERATION_DATA} of the current webservice.
     *
     * @return The initialized {@link T_OPERATION_DATA} of the current webservice.
     */
    protected abstract @NotNull T_OPERATION_DATA initOperation();

}
