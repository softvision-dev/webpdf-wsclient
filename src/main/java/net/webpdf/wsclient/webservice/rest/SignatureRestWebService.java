package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link SignatureRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#SIGNATURE}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_REST_DOCUMENT> The expected {@link RestDocument} type for the documents used by the webPDF server.
 */
public class SignatureRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<OperationSignatureOperation, OperationSignature, T_REST_DOCUMENT> {

    /**
     * Creates a {@link SignatureRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link SignatureRestWebService} shall be created for.
     */
    public SignatureRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.SIGNATURE);
    }

    /**
     * Returns the {@link SignatureRestWebService} specific {@link OperationSignature}, which allows setting parameters
     * for the webservice execution.
     *
     * @return The {@link OperationSignature} operation parameters.
     */
    @Override
    public @NotNull OperationSignature getOperationParameters() {
        return getOperationData().getSignature();
    }

    /**
     * Sets the {@link SignatureRestWebService} specific {@link OperationSignature} element, which allows setting
     * parameters for the webservice execution.
     *
     * @param operation Sets the {@link OperationSignature} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable OperationSignature operation) {
        if (operation != null) {
            getOperationData().setSignature(operation);
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
     * Sets the {@link OperationPdfPassword} for the current webservice.
     *
     * @param password The {@link OperationPdfPassword} for the current webservice.
     */
    @Override
    public void setPassword(@Nullable OperationPdfPassword password) {
        this.getOperationData().setPassword(password);
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
     * Sets the {@link OperationBilling} for the current webservice.
     *
     * @param billing The {@link OperationBilling} for the current webservice.
     */
    @Override
    public void setBilling(@Nullable OperationBilling billing) {
        this.getOperationData().setBilling(billing);
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
     * Sets the {@link OperationSettings} for the current webservice.
     *
     * @param settings The {@link OperationSettings} for the current webservice.
     */
    @Override
    public void setSettings(@Nullable OperationSettings settings) {
        this.getOperationData().setSettings(settings);
    }

    /**
     * Initializes and prepares the execution of this {@link SignatureRestWebService}.
     *
     * @return The prepared {@link OperationSignatureOperation}.
     */
    @Override
    protected @NotNull OperationSignatureOperation initOperation() {
        OperationSignatureOperation operationData = new OperationSignatureOperation();
        operationData.setSignature(new OperationSignature());
        return operationData;
    }

}
