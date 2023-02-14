package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link PdfaRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#PDFA}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_REST_DOCUMENT> The expected {@link RestDocument} type for the documents used by the webPDF server.
 */
public class PdfaRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<OperationPdfaOperation, OperationPdfa, T_REST_DOCUMENT> {

    /**
     * Creates a {@link PdfaRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link PdfaRestWebService} shall be created for.
     */
    public PdfaRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.PDFA);
    }

    /**
     * Returns the {@link PdfaRestWebService} specific {@link OperationPdfa}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link OperationPdfa} operation parameters.
     */
    @Override
    public @NotNull OperationPdfa getOperationParameters() {
        return getOperationData().getPdfa();
    }

    /**
     * Sets the {@link PdfaRestWebService} specific {@link OperationPdfa} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operation Sets the {@link OperationPdfa} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable OperationPdfa operation) {
        if (operation != null) {
            getOperationData().setPdfa(operation);
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
     * Initializes and prepares the execution of this {@link PdfaRestWebService}.
     *
     * @return The prepared {@link OperationPdfaOperation}.
     */
    @Override
    protected @NotNull OperationPdfaOperation initOperation() {
        OperationPdfaOperation operationData = new OperationPdfaOperation();
        operationData.setBilling(new OperationBilling());
        operationData.setPassword(new OperationPdfPassword());
        operationData.setSettings(new OperationSettings());
        operationData.setPdfa(new OperationPdfa());
        return operationData;
    }

}
