package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.schema.operation.*;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.stubs.Signature;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jakarta.activation.DataHandler;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;

/**
 * An instance of {@link SignatureWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#SIGNATURE}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT> The expected {@link SoapDocument} type for the documents used by the webPDF server.
 */
public class SignatureWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<Signature, SignatureType, T_SOAP_DOCUMENT> {

    /**
     * Creates a {@link SignatureWebService} for the given {@link SoapSession}.
     *
     * @param session The {@link SoapSession} a {@link SignatureWebService} shall be created for.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    public SignatureWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.SIGNATURE);
    }

    /**
     * Executes the {@link SignatureWebService} operation and returns the {@link DataHandler} of the result document.
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
     * Returns the {@link SignatureWebService} specific {@link SignatureType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link SignatureType} operation parameters.
     */
    @Override
    public @NotNull SignatureType getOperationParameters() {
        return getOperationData().getSignature();
    }

    /**
     * Sets the {@link SignatureWebService} specific {@link SignatureType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operationData Sets the {@link SignatureType} operation parameters.
     */
    @Override
    public void setOperationParameters(@Nullable SignatureType operationData) {
        if (operationData != null) {
            getOperationData().setSignature(operationData);
        }
    }

    /**
     * Create a matching {@link Signature} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Signature} webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    protected @NotNull Signature provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.SIGNATURE.getSoapNamespaceURI(),
                        WebServiceType.SIGNATURE.getSoapLocalPartPort()),
                Signature.class, this.getMTOMFeature());
    }

    /**
     * Initializes and prepares the execution of this {@link SignatureWebService}.
     *
     * @return The prepared {@link OperationData}.
     */
    @Override
    protected @NotNull OperationData initOperation() {
        OperationData operationData = new OperationData();
        operationData.setBilling(new BillingType());
        operationData.setPassword(new PdfPasswordType());
        operationData.setSignature(new SignatureType());
        return operationData;
    }

}
