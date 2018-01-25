package net.webpdf.wsclient;


import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;

import java.util.List;

public class ToolboxRestWebService extends RestWebservice<List<BaseToolboxType>> {

    /**
     * Creates a ToolboxRestWebService for the given {@link Session}
     *
     * @param session The session a ToolboxRestWebservice shall be created for.
     */
    public ToolboxRestWebService(Session session) {
        super(session, WebServiceType.TOOLBOX);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    public List<BaseToolboxType> getOperation() {
        return this.operation.getToolbox();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(List<BaseToolboxType> operationData) {
        if (operationData != null && !operationData.isEmpty()) {
            operation.getToolbox().clear();
            operation.getToolbox().addAll(operationData);
        }
    }
}
