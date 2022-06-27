package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.session.documents.rest.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.BarcodeType;
import net.webpdf.wsclient.schema.operation.OperationData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link BarcodeRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#BARCODE}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 */
public class BarcodeRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, BarcodeType> {

    /**
     * Creates a {@link BarcodeRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link BarcodeRestWebService} shall be created for.
     */
    public BarcodeRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.BARCODE);
    }

    /**
     * Returns the {@link BarcodeRestWebService} specific {@link BarcodeType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link BarcodeType} operation parameters.
     */
    @Override
    public @NotNull BarcodeType getOperation() {
        return getOperationData().getBarcode();
    }

    /**
     * Sets the {@link BarcodeRestWebService} specific {@link BarcodeType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operationData Sets the {@link BarcodeType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable BarcodeType operationData) {
        if (operationData != null) {
            getOperationData().setBarcode(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link BarcodeRestWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link BarcodeRestWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setBarcode(new BarcodeType());
    }

}
