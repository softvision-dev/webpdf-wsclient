package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import net.webpdf.wsclient.schema.stubs.Toolbox;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.util.List;

public class ToolboxWebService extends SoapWebService<Toolbox, List<BaseToolboxType>> {

    /**
     * Creates a SOAP ToolboxWebService for the given {@link Session}
     *
     * @param session The session a SOAP ToolboxWebservice shall be created for.
     */
    ToolboxWebService(Session session) throws ResultException {
        super(session, WebServiceType.TOOLBOX);

        Service service = Service.create(getWsdlDocumentLocation(), getQName());
        this.port = service.getPort(
            new QName(WebServiceType.TOOLBOX.getSoapNamespaceURI(),
                WebServiceType.TOOLBOX.getSoapLocalPartPort()),
            Toolbox.class, this.getFeature());
    }

    /**
     * Executes webservice operation and returns {@link DataHandler} of the result document.
     *
     * @return A {@link DataHandler} of the result document.
     * @throws WebserviceException a {@link WebserviceException}
     */
    @Override
    DataHandler processService() throws WebserviceException {
        return this.port.execute(this.operation,
            this.document.getSourceDataHandler(),
            this.document.isFileSource() ? null : this.document.getSource().toString());
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
