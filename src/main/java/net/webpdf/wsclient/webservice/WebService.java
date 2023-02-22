package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.exception.ResultException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class implementing {@link WebServiceType} wraps a wsclient connection to a specific webPDF webservice endpoint
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
public interface WebService<T_SESSION extends Session, T_OPERATION_DATA, T_OPERATION_PARAMETER,
        T_DOCUMENT extends Document, T_BILLING, T_PASSWORD, T_SETTINGS> {

    /**
     * Returns the {@link T_SESSION} of the current webservice.
     *
     * @return The {@link T_SESSION} of the current webservice.
     */
    @NotNull T_SESSION getSession();

    /**
     * Execute the webservice operation and returns the resulting {@link T_DOCUMENT}.
     *
     * @return The resulting {@link T_DOCUMENT}.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Nullable T_DOCUMENT process() throws ResultException;

    /**
     * Returns the webservice specific {@link T_OPERATION_DATA} of the current webservice.
     *
     * @return The webservice specific {@link T_OPERATION_DATA} of the current webservice.
     */
    @NotNull T_OPERATION_DATA getOperationData();

    /**
     * Returns the webservice specific {@link T_OPERATION_PARAMETER}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The webservice specific {@link T_OPERATION_PARAMETER}.
     */
    @Nullable T_OPERATION_PARAMETER getOperationParameters();

    /**
     * Sets the webservice specific {@link T_OPERATION_PARAMETER}, which allows setting parameters for
     * the webservice execution.
     *
     * @param operation The webservice specific {@link T_OPERATION_PARAMETER}.
     */
    void setOperationParameters(@Nullable T_OPERATION_PARAMETER operation);

    /**
     * Returns the {@link T_DOCUMENT} which contains the source document, that shall be processed.
     *
     * @return The {@link T_DOCUMENT} which contains the source document, that shall be processed.
     */
    @Nullable T_DOCUMENT getSourceDocument();

    /**
     * Set the {@link T_DOCUMENT} which contains the source document, that shall be processed.
     *
     * @param document The {@link T_DOCUMENT} which contains the source document, that shall be processed.
     */
    void setSourceDocument(@Nullable T_DOCUMENT document);

    /**
     * Returns the {@link T_PASSWORD} of the current webservice.
     *
     * @return the {@link T_PASSWORD} of the current webservice.
     */
    @Nullable T_PASSWORD getPassword();

    /**
     * Returns the {@link T_BILLING} of the current webservice.
     *
     * @return the {@link T_BILLING} of the current webservice.
     */
    @Nullable T_BILLING getBilling();

    /**
     * Returns the {@link T_SETTINGS} of the current webservice.
     *
     * @return the {@link T_SETTINGS} of the current webservice.
     */
    @Nullable T_SETTINGS getSettings();

}
