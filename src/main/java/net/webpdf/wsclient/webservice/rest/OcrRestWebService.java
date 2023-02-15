package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link OcrRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#OCR}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_REST_DOCUMENT> The expected {@link RestDocument} type for the documents used by the webPDF server.
 */
public class OcrRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<OperationOcrOperation, OperationOcr, T_REST_DOCUMENT> {

    /**
     * Creates a {@link OcrRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link OcrRestWebService} shall be created for.
     */
    public OcrRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.OCR);
    }

    /**
     * Returns the {@link OcrRestWebService} specific {@link OperationOcr}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link OperationOcr} operation parameters.
     */
    @Override
    public @NotNull OperationOcr getOperationParameters() {
        return getOperationData().getOcr();
    }

    /**
     * Sets the {@link OcrRestWebService} specific {@link OperationOcr} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operation Sets the {@link OperationOcr} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable OperationOcr operation) {
        if (operation != null) {
            getOperationData().setOcr(operation);
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
     * Initializes and prepares the execution of this {@link OcrRestWebService}.
     *
     * @return The prepared {@link OperationOcrOperation}.
     */
    @Override
    protected @NotNull OperationOcrOperation initOperation() {
        OperationOcrOperation operationData = new OperationOcrOperation();
        operationData.setBilling(new OperationBilling());
        operationData.setPassword(new OperationPdfPassword());
        operationData.setSettings(new OperationSettings());
        operationData.setOcr(new OperationOcr());
        return operationData;
    }

}
