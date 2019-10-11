package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToolboxRestWebService extends RestWebservice<List<BaseToolboxType>> {

    /**
     * Creates a ToolboxRestWebService for the given {@link Session}
     *
     * @param session The session a ToolboxRestWebservice shall be created for.
     */
    ToolboxRestWebService(@NotNull Session session) {
        super(session, WebServiceType.TOOLBOX);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public List<BaseToolboxType> getOperation() {
        return this.operation.getToolbox();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable List<BaseToolboxType> operationData) {
        if (operationData != null && !operationData.isEmpty()) {
            operation.getToolbox().clear();
            operation.getToolbox().addAll(operationData);
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
        //DO NOTHING.
    }

}
