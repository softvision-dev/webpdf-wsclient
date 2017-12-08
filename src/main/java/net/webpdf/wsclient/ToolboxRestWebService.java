package net.webpdf.wsclient;


import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;

import java.util.List;

public class ToolboxRestWebService extends RestWebservice<List<BaseToolboxType>> {

    public ToolboxRestWebService(Session session) {
        super(session, WebServiceType.TOOLBOX);
    }


    @Override
    public List<BaseToolboxType> getOperation() {
        return this.operation.getToolbox();
    }

    @Override
    public void setOperation(List<BaseToolboxType> operationData) throws ResultException {
        if (operationData != null && !operationData.isEmpty()) {
            operation.getToolbox().clear();
            operation.getToolbox().addAll(operationData);
        }
    }
}
