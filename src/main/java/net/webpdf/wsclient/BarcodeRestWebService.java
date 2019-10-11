package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.BarcodeType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BarcodeRestWebService extends RestWebservice<BarcodeType> {

    /**
     * Creates a BarcodeRestWebService for the given {@link Session}
     *
     * @param session The session a BarcodeRestWebservice shall be created for.
     */
    BarcodeRestWebService(@NotNull Session session) {
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
        return this.operation.getBarcode();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable BarcodeType operationData) {
        if (operationData != null) {
            operation.setBarcode(operationData);
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
        this.operation.setBarcode(new BarcodeType());
    }

}
