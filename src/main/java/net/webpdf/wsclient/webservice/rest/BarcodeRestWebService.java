package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link BarcodeRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#BARCODE}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_REST_DOCUMENT> The expected {@link RestDocument} type for the documents used by the webPDF server.
 */
public class BarcodeRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<OperationBarcodeOperation, OperationBarcode, T_REST_DOCUMENT> {

    /**
     * Creates a {@link BarcodeRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link BarcodeRestWebService} shall be created for.
     */
    public BarcodeRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.BARCODE);
    }

    /**
     * Returns the {@link BarcodeRestWebService} specific {@link OperationBarcode}, which allows setting parameters
     * for the webservice execution.
     *
     * @return The {@link OperationBarcode} operation parameters.
     */
    @Override
    public @NotNull OperationBarcode getOperationParameters() {
        return getOperationData().getBarcode();
    }

    /**
     * Sets the {@link BarcodeRestWebService} specific {@link OperationBarcode} element, which allows setting
     * parameters for the webservice execution.
     *
     * @param operation Sets the {@link OperationBarcode} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable OperationBarcode operation) {
        if (operation != null) {
            getOperationData().setBarcode(operation);
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
     * Returns the {@link OperationSettings} of the current webservice.
     *
     * @return the {@link OperationSettings} of the current webservice.
     */
    @Override
    public @Nullable OperationSettings getSettings() {
        return getOperationData().getSettings();
    }

    /**
     * Initializes and prepares the execution of this {@link BarcodeRestWebService}.
     *
     * @return The prepared {@link OperationBarcodeOperation}.
     */
    @Override
    protected @NotNull OperationBarcodeOperation initOperation() {
        OperationBarcodeOperation operationData = new OperationBarcodeOperation();
        operationData.setBilling(new OperationBilling());
        operationData.setPassword(new OperationPdfPassword());
        operationData.setSettings(new OperationSettings());
        operationData.setBarcode(new OperationBarcode());
        return operationData;
    }

}
