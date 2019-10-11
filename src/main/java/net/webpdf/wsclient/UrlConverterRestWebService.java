package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.UrlConverterType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UrlConverterRestWebService extends RestWebservice<UrlConverterType> {

    /**
     * Creates a UrlConverterRestWebService for the given {@link Session}
     *
     * @param session The session a UrlConverterRestWebservice shall be created for.
     */
    UrlConverterRestWebService(@NotNull Session session) {
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
        return this.operation.getUrlconverter();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable UrlConverterType operationData) {
        if (operationData != null) {
            operation.setUrlconverter(operationData);
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
        this.operation.setUrlconverter(new UrlConverterType());
    }

}
