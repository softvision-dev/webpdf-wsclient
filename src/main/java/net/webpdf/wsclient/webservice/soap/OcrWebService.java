package net.webpdf.wsclient.webservice.soap;

import net.webpdf.wsclient.session.documents.soap.SoapDocument;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OcrType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.stubs.OCR;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import jakarta.activation.DataHandler;

import javax.xml.namespace.QName;

import jakarta.xml.ws.Service;

/**
 * An instance of {@link OcrWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#OCR}, using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 */
public class OcrWebService<T_SOAP_DOCUMENT extends SoapDocument>
        extends SoapWebService<T_SOAP_DOCUMENT, OCR, OcrType> {

    /**
     * Creates a {@link OcrWebService} for the given {@link SoapSession}
     *
     * @param session The {@link SoapSession} a {@link OcrWebService} shall be created for.
     */
    public OcrWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session) throws ResultException {
        super(session, WebServiceType.OCR);
    }

    /**
     * Executes the {@link OcrWebService} operation and returns the {@link DataHandler} of the result document.
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
     * Create a matching {@link OCR} webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The {@link OCR} webservice port, that shall be used for executions.
     */
    @Override
    protected @NotNull OCR provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
                new QName(WebServiceType.OCR.getSoapNamespaceURI(),
                        WebServiceType.OCR.getSoapLocalPartPort()),
                OCR.class, this.getMTOMFeature());
    }

    /**
     * Returns the {@link OcrWebService} specific {@link OcrType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link OcrType} operation parameters.
     */
    @Override
    public @NotNull OcrType getOperation() {
        return getOperationData().getOcr();
    }

    /**
     * Sets the {@link OcrWebService} specific {@link OcrType} element, which allows setting parameters for
     * the webservice execution.
     *
     * @param operationData Sets the {@link OcrType} operation parameters.
     */
    @Override
    public void setOperation(@Nullable OcrType operationData) {
        if (operationData != null) {
            getOperationData().setOcr(operationData);
        }
    }

    /**
     * Initializes and prepares the execution of this {@link OcrWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link OcrWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setOcr(new OcrType());
    }

}
