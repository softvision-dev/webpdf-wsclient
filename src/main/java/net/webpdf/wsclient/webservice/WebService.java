package net.webpdf.wsclient.webservice;

import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BillingType;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class implementing {@link WebServiceType} wraps a wsclient connection to a specific webPDF webservice endpoint
 * ({@link WebServiceType}), using a specific {@link WebServiceProtocol} and expecting a specific {@link Document} type
 * as the result.
 *
 * @param <T_DOCUMENT>       The expected {@link Document} type for the results produced by the webPDF server.
 * @param <T_OPERATION_TYPE> The {@link WebServiceType} of the targeted webservice endpoint.
 * @param <T_RESULT>         The result type, that is expected.
 */
public interface WebService<T_DOCUMENT extends Document, T_OPERATION_TYPE, T_RESULT> {

    /**
     * Execute the webservice operation and returns the resulting {@link Document}.
     *
     * @return The resulting {@link Document}.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Nullable T_RESULT process() throws ResultException;

    /**
     * Returns the webservice specific operation element, which allows setting parameters for
     * the webservice execution.
     *
     * @return The webservice specific operation parameters.
     */
    @Nullable T_OPERATION_TYPE getOperation();

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     * @throws ResultException an {@link ResultException}
     */
    void setOperation(@Nullable T_OPERATION_TYPE operationData) throws ResultException;

    /**
     * Returns the {@link Document} which contains the source document, that shall be processed.
     *
     * @return The {@link Document} which contains the source document, that shall be processed.
     */
    @Nullable T_DOCUMENT getDocument();

    /**
     * Set the {@link Document} which contains the source document, that shall be processed.
     *
     * @param document The {@link Document} which contains the source document, that shall be processed.
     */
    void setDocument(@Nullable T_DOCUMENT document);

    /**
     * Returns the {@link PdfPasswordType} of the current webservice.
     *
     * @return the {@link PdfPasswordType} of the current webservice.
     */
    @NotNull PdfPasswordType getPassword();

    /**
     * Returns the {@link BillingType} of the current webservice.
     *
     * @return the {@link BillingType} of the current webservice.
     */
    @NotNull BillingType getBilling();

}
