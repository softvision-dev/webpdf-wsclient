package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BillingType;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @param <T_DOC>            Document which contains source and target resource
 * @param <T_OPERATION_TYPE> Webservice specific operation type
 * @param <T_RESULT>         The result type
 */
public interface WebService<T_DOC extends Document, T_OPERATION_TYPE, T_RESULT> {

    /**
     * Execute webservice operation and returns result
     *
     * @return the result
     * @throws ResultException an {@link ResultException}
     */
    @Nullable
    T_RESULT process() throws ResultException;

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Nullable
    T_OPERATION_TYPE getOperation();

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     * @throws ResultException an {@link ResultException}
     */
    void setOperation(@Nullable T_OPERATION_TYPE operationData) throws ResultException;

    /**
     * Get the document which contains the source
     *
     * @return document which contains the source
     */
    @Nullable
    T_DOC getDocument();

    /**
     * Set the document which contains the source
     *
     * @param document document which contains the source
     */
    void setDocument(@Nullable T_DOC document);

    /**
     * Returns the {@link PdfPasswordType} from the webservice specific operation
     *
     * @return the {@link PdfPasswordType} from the webservice specific operation
     */
    @NotNull
    PdfPasswordType getPassword();

    /**
     * Returns the {@link BillingType} from the webservice specific operation
     * This is the functional part and webservice specific. For the common operation parts billing
     * and password call getBilling and getPassword
     *
     * @return the {@link BillingType} from the webservice specific operation
     */
    @NotNull
    BillingType getBilling();

}
