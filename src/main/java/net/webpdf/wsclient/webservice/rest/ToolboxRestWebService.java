package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An instance of {@link ToolboxRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#TOOLBOX}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 */
public class ToolboxRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, List<BaseToolboxType>> {

    /**
     * Creates a {@link ToolboxRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link ToolboxRestWebService} shall be created for.
     */
    public ToolboxRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.TOOLBOX);
    }

    /**
     * Returns the {@link ToolboxRestWebService} specific {@link BaseToolboxType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link BaseToolboxType} operation parameters.
     */
    @Override
    public @NotNull List<BaseToolboxType> getOperation() {
        return getOperationData().getToolbox();
    }

    /**
     * Sets the {@link ToolboxRestWebService} specific {@link BaseToolboxType} element, which allows setting parameters
     * for the webservice execution.
     *
     * @param operationData Sets the {@link BaseToolboxType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable List<BaseToolboxType> operationData) {
        if (operationData != null && !operationData.isEmpty()) {
            getOperationData().getToolbox().clear();
            getOperationData().getToolbox().addAll(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link ToolboxRestWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link ToolboxRestWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        //DO NOTHING.
    }

}
