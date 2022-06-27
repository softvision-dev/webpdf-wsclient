package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.ConverterType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link ConverterRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#CONVERTER}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 */
public class ConverterRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, ConverterType> {

    /**
     * Creates a {@link ConverterRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link ConverterRestWebService} shall be created for.
     */
    public ConverterRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.CONVERTER);
    }

    /**
     * Returns the {@link ConverterRestWebService} specific {@link ConverterType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link ConverterType} operation parameters.
     */
    @Override
    public @NotNull ConverterType getOperation() {
        return getOperationData().getConverter();
    }

    /**
     * Sets the {@link ConverterRestWebService} specific {@link ConverterType} element, which allows setting parameters
     * for the webservice execution.
     *
     * @param operationData Sets the {@link ConverterType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable ConverterType operationData) {
        if (operationData != null) {
            getOperationData().setConverter(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link ConverterRestWebService} via the given
     * {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link ConverterRestWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        operation.setConverter(new ConverterType());
    }

}
