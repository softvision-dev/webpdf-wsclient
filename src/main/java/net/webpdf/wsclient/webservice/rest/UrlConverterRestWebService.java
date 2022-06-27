package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.UrlConverterType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link UrlConverterRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#URLCONVERTER}, using {@link WebServiceProtocol#REST} and expecting a
 * {@link RestDocument} as the result.
 */
public class UrlConverterRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, UrlConverterType> {

    /**
     * Creates a {@link UrlConverterRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link UrlConverterRestWebService} shall be created for.
     */
    public UrlConverterRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.URLCONVERTER);
    }

    /**
     * Returns the {@link UrlConverterRestWebService} specific {@link UrlConverterType}, which allows setting parameters
     * for the webservice execution.
     *
     * @return The {@link UrlConverterType} operation parameters.
     */
    @Override
    public @NotNull UrlConverterType getOperation() {
        return getOperationData().getUrlconverter();
    }

    /**
     * Sets the {@link UrlConverterRestWebService} specific {@link UrlConverterType} element, which allows setting
     * parameters for the webservice execution.
     *
     * @param operationData Sets the {@link UrlConverterType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable UrlConverterType operationData) {
        if (operationData != null) {
            getOperationData().setUrlconverter(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link UrlConverterRestWebService} via the given
     * {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link UrlConverterRestWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setUrlconverter(new UrlConverterType());
    }

}
