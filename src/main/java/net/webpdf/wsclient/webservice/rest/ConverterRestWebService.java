package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link ConverterRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#CONVERTER}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_REST_DOCUMENT> The expected {@link RestDocument} type for the documents used by the webPDF server.
 */
public class ConverterRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<OperationConverterOperation, OperationConverter, T_REST_DOCUMENT> {

    /**
     * Creates a {@link ConverterRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link ConverterRestWebService} shall be created for.
     */
    public ConverterRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.CONVERTER);
    }

    /**
     * Returns the {@link ConverterRestWebService} specific {@link OperationConverter}, which allows setting parameters
     * for the webservice execution.
     *
     * @return The {@link OperationConverter} operation parameters.
     */
    @Override
    public @NotNull OperationConverter getOperationParameters() {
        return getOperationData().getConverter();
    }

    /**
     * Sets the {@link ConverterRestWebService} specific {@link OperationConverter} element, which allows setting
     * parameters for the webservice execution.
     *
     * @param operation Sets the {@link OperationConverter} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable OperationConverter operation) {
        if (operation != null) {
            getOperationData().setConverter(operation);
        }
    }

    /**
     * Returns the {@link OperationPdfPassword} of the current webservice.
     *
     * @return the {@link OperationPdfPassword} of the current webservice.
     */
    @Override
    public @Nullable OperationPdfPassword getPassword() {
        return getOperationData().getPassword();
    }

    /**
     * Returns the {@link OperationBilling} of the current webservice.
     *
     * @return the {@link OperationBilling} of the current webservice.
     */
    @Override
    public @Nullable OperationBilling getBilling() {
        return getOperationData().getBilling();
    }

    /**
     * Initializes and prepares the execution of this {@link ConverterRestWebService}.
     *
     * @return The prepared {@link OperationConverterOperation}.
     */
    @Override
    protected @NotNull OperationConverterOperation initOperation() {
        OperationConverterOperation operationData = new OperationConverterOperation();
        operationData.setBilling(new OperationBilling());
        operationData.setPassword(new OperationPdfPassword());
        operationData.setConverter(new OperationConverter());
        return operationData;
    }

}
