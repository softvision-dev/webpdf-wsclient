package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.ConverterType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ConverterRestWebService extends RestWebservice<ConverterType> {

    /**
     * Creates a ConverterRestWebService for the given {@link Session}
     *
     * @param session The session a ConverterRestWebservice shall be created for.
     */
    ConverterRestWebService(@NotNull Session session) {
        super(session, WebServiceType.CONVERTER);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public ConverterType getOperation() {
        return this.operation.getConverter();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable ConverterType operationData) {
        if (operationData != null) {
            operation.setConverter(operationData);
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
        operation.setConverter(new ConverterType());
    }

}
