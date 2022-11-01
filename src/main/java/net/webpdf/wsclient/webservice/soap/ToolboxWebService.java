package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
import jakarta.xml.ws.Service;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.BaseToolboxType;
import net.webpdf.wsclient.schema.operation.BillingType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.schema.stubs.Toolbox;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * An instance of {@link ToolboxWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#TOOLBOX}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT> The expected {@link SoapDocument} type for the documents used by the webPDF server.
 */
public class ToolboxWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<Toolbox, List<BaseToolboxType>, T_SOAP_DOCUMENT> {

    /**
     * Creates a {@link ToolboxWebService} for the given {@link SoapSession}.
     *
     * @param session The {@link SoapSession} a {@link ToolboxWebService} shall be created for.
     * @throws ResultException Shall be thrown, upon an execution failure.
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
        if (getSourceDocument() == null) {
            return null;
        }
        return getPort().execute(getOperationData(), getSourceDocument().getSourceDataHandler(),
                getSourceDocument().isFileSource() || getSourceDocument().getSource() == null ?
                        null : getSourceDocument().getSource().toString());
    }

    /**
     * Returns the {@link ToolboxWebService} specific {@link BaseToolboxType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link BaseToolboxType} operation parameters.
     */
    @Override
    public @NotNull List<BaseToolboxType> getOperationParameters() {
        return getOperationData().getToolbox();
    }

    /**
     * Sets the {@link ToolboxWebService} specific {@link BaseToolboxType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operationData Sets the {@link BaseToolboxType} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable List<BaseToolboxType> operationData) {
        if (operationData != null && !operationData.isEmpty()) {
            getOperationData().getToolbox().clear();
            getOperationData().getToolbox().addAll(operationData);
        }
    }

    /**
     * Create a matching {@link Toolbox} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Toolbox} webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @NotNull Toolbox provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.TOOLBOX.getSoapNamespaceURI(),
                        WebServiceType.TOOLBOX.getSoapLocalPartPort()),
                Toolbox.class, getMTOMFeature());
    }

    /**
     * Initializes and prepares the execution of this {@link ToolboxWebService}.
     *
     * @return The prepared {@link OperationData}.
     */
    @Override
    protected @NotNull OperationData initOperation() {
        OperationData operationData = new OperationData();
        operationData.setBilling(new BillingType());
        operationData.setPassword(new PdfPasswordType());
        return operationData;
    }

}
