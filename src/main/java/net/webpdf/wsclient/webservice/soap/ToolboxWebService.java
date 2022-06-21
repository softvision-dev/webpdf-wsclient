package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.stubs.Toolbox;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.activation.DataHandler;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;

import java.util.List;

/**
 * An instance of {@link ToolboxWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#TOOLBOX}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 */
public class ToolboxWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<T_SOAP_DOCUMENT, Toolbox, List<BaseToolboxType>> {

    /**
     * Creates a {@link ToolboxWebService} for the given {@link SoapSession}
     *
     * @param session The {@link SoapSession} a {@link ToolboxWebService} shall be created for.
     */
    public ToolboxWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.TOOLBOX);
    }

    /**
     * Executes the {@link ToolboxWebService} operation and returns the {@link DataHandler} of the result document.
     *
     * @return The {@link DataHandler} of the result document.
     * @throws WebServiceException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @Nullable DataHandler processService() throws WebServiceException {
        if (getDocument() == null) {
            return null;
        }
        return getPort().execute(getOperationData(),
                getDocument().getSourceDataHandler(),
                getDocument().isFileSource() || getDocument().getSource() == null ?
                        null : getDocument().getSource().toString());
    }

    /**
     * Create a matching {@link Toolbox} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Toolbox} webservice port, that shall be used for executions.
     */
    @Override
    protected @NotNull Toolbox provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.TOOLBOX.getSoapNamespaceURI(),
                        WebServiceType.TOOLBOX.getSoapLocalPartPort()),
                Toolbox.class, getMTOMFeature());
    }

    /**
     * Returns the {@link ToolboxWebService} specific {@link BaseToolboxType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link BaseToolboxType} operation parameters.
     */
    @Override
    public @NotNull List<BaseToolboxType> getOperation() {
        return getOperationData().getToolbox();
    }

    /**
     * Sets the {@link ToolboxWebService} specific {@link BaseToolboxType} element, which allows setting parameters for
     * the webservice execution.
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
     * Initializes and prepares the execution of this {@link ToolboxWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link ToolboxWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        //DO NOTHING.
    }

}
