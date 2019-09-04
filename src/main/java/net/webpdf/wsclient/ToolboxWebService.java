package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.stubs.Toolbox;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    ToolboxWebService(@NotNull Session session) throws ResultException {
        super(session, WebServiceType.TOOLBOX);
    }

    /**
     * Executes webservice operation and returns {@link DataHandler} of the result document.
     *
     * @return A {@link DataHandler} of the result document.
     * @throws WebserviceException a {@link WebserviceException}
     */
    @Override
    @Nullable
    @SuppressWarnings("Duplicates")
    DataHandler processService() throws WebserviceException {
        if (this.document == null) {
            return null;
        }
        return this.port.execute(this.operation,
            this.document.getSourceDataHandler(),
            this.document.isFileSource() || this.document.getSource() == null ? null : this.document.getSource().toString());
    }

    /**
     * Create a matching webservice port for future executions of this SOAP webservice.
     *
     * @return The webservice port, that shall be used for executions.
     */
    @Override
    @NotNull
    protected Toolbox provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
            new QName(WebServiceType.TOOLBOX.getSoapNamespaceURI(),
                WebServiceType.TOOLBOX.getSoapLocalPartPort()),
            Toolbox.class, this.getFeature());
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
