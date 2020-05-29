package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.UrlConverterType;
import net.webpdf.wsclient.webservice.RestWebService;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UrlConverterRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, UrlConverterType> {

    /**
     * Creates a UrlConverterRestWebService for the given {@link Session}
     *
     * @param session The session a UrlConverterRestWebservice shall be created for.
     */
    public UrlConverterRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.URLCONVERTER);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public UrlConverterType getOperation() {
        return getOperationData().getUrlconverter();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable UrlConverterType operationData) {
        if (operationData != null) {
            getOperationData().setUrlconverter(operationData);
        }
    }

    /**
     * Initialize all substructures, that must be set for this webservice to accept parameters for this
     * webservice type.
     *
     * @param operation The operationData that, shall be initialized for webservice execution.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setUrlconverter(new UrlConverterType());
    }

}
