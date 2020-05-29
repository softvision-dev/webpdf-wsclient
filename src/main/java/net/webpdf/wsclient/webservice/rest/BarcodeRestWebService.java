package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.schema.operation.BarcodeType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.webservice.RestWebService;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarcodeRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, BarcodeType> {

    /**
     * Creates a BarcodeRestWebService for the given {@link Session}
     *
     * @param session The session a BarcodeRestWebservice shall be created for.
     */
    public BarcodeRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.BARCODE);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public BarcodeType getOperation() {
        return getOperationData().getBarcode();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable BarcodeType operationData) {
        if (operationData != null) {
            getOperationData().setBarcode(operationData);
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
        getOperationData().setBarcode(new BarcodeType());
    }

}
