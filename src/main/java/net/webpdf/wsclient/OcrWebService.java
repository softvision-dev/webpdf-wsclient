package net.webpdf.wsclient;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.schema.operation.OcrType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.stubs.OCR;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class OcrWebService extends SoapWebService<OCR, OcrType> {

    /**
     * Creates a SOAP OcrWebService for the given {@link Session}
     *
     * @param session The session a SOAP OcrWebservice shall be created for.
     */
    OcrWebService(@NotNull Session session) throws ResultException {
        super(session, WebServiceType.OCR);
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
    protected OCR provideWSPort() throws ResultException {
        return Service.create(getWsdlDocumentLocation(), getQName()).getPort(
            new QName(WebServiceType.OCR.getSoapNamespaceURI(),
                WebServiceType.OCR.getSoapLocalPartPort()),
            OCR.class, this.getFeature());
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public OcrType getOperation() {
        return this.operation.getOcr();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable OcrType operationData) {
        if (operationData != null) {
            operation.setOcr(operationData);
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
        this.operation.setOcr(new OcrType());
    }

}
