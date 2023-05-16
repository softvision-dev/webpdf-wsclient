package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.openapi.*;
import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * An instance of {@link ToolboxRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#TOOLBOX}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 *
 * @param <T_REST_DOCUMENT> The expected {@link RestDocument} type for the documents used by the webPDF server.
 */
public class ToolboxRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<OperationToolboxOperation, List<OperationBaseToolbox>, T_REST_DOCUMENT> {

    /**
     * Creates a {@link ToolboxRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link ToolboxRestWebService} shall be created for.
     */
    public ToolboxRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.TOOLBOX);
    }

    /**
     * Returns the {@link ToolboxRestWebService} specific {@link OperationBaseToolbox}, which allows setting parameters
     * for the webservice execution.
     *
     * @return The {@link OperationBaseToolbox} operation parameters.
     */
    @Override
    public @NotNull List<OperationBaseToolbox> getOperationParameters() {
        List<OperationBaseToolbox> toolbox = getOperationData().getToolbox();
        if (toolbox == null) {
            getOperationData().setToolbox(toolbox = new ArrayList<>());
        }

        return toolbox;
    }

    /**
     * Sets the {@link ToolboxRestWebService} specific {@link OperationBaseToolbox} element, which allows setting
     * parameters for the webservice execution.
     *
     * @param operation Sets the {@link OperationBaseToolbox} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable List<OperationBaseToolbox> operation) {
        if (operation != null && !operation.isEmpty()) {
            List<OperationBaseToolbox> toolbox = getOperationData().getToolbox();
            if (toolbox == null) {
                getOperationData().setToolbox(new ArrayList<>());
            }
            getOperationData().getToolbox().clear();
            getOperationData().getToolbox().addAll(operation);
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
     * Initializes and prepares the execution of this {@link ToolboxRestWebService}.
     *
     * @return The prepared {@link OperationToolboxOperation}.
     */
    @Override
    protected @NotNull OperationToolboxOperation initOperation() {
        return new OperationToolboxOperation();
    }

}
