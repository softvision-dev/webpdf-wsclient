package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.stubs.Pdfa;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jakarta.activation.DataHandler;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;

/**
 * An instance of {@link PdfaWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#PDFA}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT> The expected {@link SoapDocument} type for the documents used by the webPDF server.
 */
public class PdfaWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<Pdfa, PdfaType, T_SOAP_DOCUMENT> {

    /**
     * Creates a {@link PdfaWebService} for the given {@link SoapSession}.
     *
     * @param session The {@link SoapSession} a {@link PdfaWebService} shall be created for.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    public PdfaWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.PDFA);
    }

    /**
     * Executes the {@link PdfaWebService} operation and returns the {@link DataHandler} of the result document.
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
     * Returns the {@link PdfaWebService} specific {@link PdfaType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link PdfaType} operation parameters.
     */
    @Override
    public @NotNull PdfaType getOperationParameters() {
        return getOperationData().getPdfa();
    }

    /**
     * Sets the {@link PdfaWebService} specific {@link PdfaType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operation Sets the {@link PdfaType} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable PdfaType operation) {
        if (operation != null) {
            getOperationData().setPdfa(operation);
        }
    }

    /**
     * Create a matching {@link Pdfa} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Pdfa} webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @NotNull Pdfa provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.PDFA.getSoapNamespaceURI(),
                        WebServiceType.PDFA.getSoapLocalPartPort()),
                Pdfa.class, this.getMTOMFeature());
    }

    /**
     * Initializes and prepares the execution of this {@link PdfaWebService}.
     *
     * @return The prepared {@link OperationData}.
     */
    @Override
    protected @NotNull OperationData initOperation() {
        OperationData operationData = new OperationData();
        operationData.setBilling(new BillingType());
        operationData.setPassword(new PdfPasswordType());
        operationData.setPdfa(new PdfaType());
        return operationData;
    }

}
