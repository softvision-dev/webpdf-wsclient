package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.PdfaType;
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
 */
public class PdfaWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<T_SOAP_DOCUMENT, Pdfa, PdfaType> {

    /**
     * Creates a {@link PdfaWebService} for the given {@link SoapSession}
     *
     * @param session The {@link SoapSession} a {@link PdfaWebService} shall be created for.
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
        if (getDocument() == null) {
            return null;
        }
        return getPort().execute(getOperationData(), getDocument().getSourceDataHandler(),
                getDocument().isFileSource() || getDocument().getSource() == null ?
                        null : getDocument().getSource().toString());
    }

    /**
     * Create a matching {@link Pdfa} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Pdfa} webservice port, that shall be used for executions.
     */
    @Override
    protected @NotNull Pdfa provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.PDFA.getSoapNamespaceURI(),
                        WebServiceType.PDFA.getSoapLocalPartPort()),
                Pdfa.class, this.getMTOMFeature());
    }

    /**
     * Returns the {@link PdfaWebService} specific {@link PdfaType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link PdfaType} operation parameters.
     */
    @Override
    public @NotNull PdfaType getOperation() {
        return getOperationData().getPdfa();
    }

    /**
     * Sets the {@link PdfaWebService} specific {@link PdfaType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operationData Sets the {@link PdfaType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable PdfaType operationData) {
        if (operationData != null) {
            getOperationData().setPdfa(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link PdfaWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link PdfaWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setPdfa(new PdfaType());
    }

}
