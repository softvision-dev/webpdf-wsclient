package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.SignatureType;
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
 */
public class SignatureWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<T_SOAP_DOCUMENT, Signature, SignatureType> {

    /**
     * Creates a {@link SignatureWebService} for the given {@link SoapSession}
     *
     * @param session The {@link SoapSession} a {@link SignatureWebService} shall be created for.
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
        if (getDocument() == null) {
            return null;
        }
        return getPort().execute(getOperationData(),
                getDocument().getSourceDataHandler(),
                getDocument().isFileSource() || getDocument().getSource() == null ?
                        null : getDocument().getSource().toString());
    }

    /**
     * Create a matching {@link Signature} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link Signature} webservice port, that shall be used for executions.
     */
    @Override
    protected @NotNull Signature provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.SIGNATURE.getSoapNamespaceURI(),
                        WebServiceType.SIGNATURE.getSoapLocalPartPort()),
                Signature.class, this.getMTOMFeature());
    }

    /**
     * Returns the {@link SignatureWebService} specific {@link SignatureType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link SignatureType} operation parameters.
     */
    @Override
    public @NotNull SignatureType getOperation() {
        return getOperationData().getSignature();
    }

    /**
     * Sets the {@link SignatureWebService} specific {@link SignatureType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operationData Sets the {@link SignatureType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable SignatureType operationData) {
        if (operationData != null) {
            getOperationData().setSignature(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link SignatureWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link SignatureWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setSignature(new SignatureType());
    }

}
