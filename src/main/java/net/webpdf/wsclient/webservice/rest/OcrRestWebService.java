package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.documents.rest.RestDocument;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.OcrType;
import net.webpdf.wsclient.webservice.RestWebService;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OcrRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, OcrType> {

    /**
     * Creates a OcrRestWebService for the given {@link Session}
     *
     * @param sessionToken The session a OcrRestWebservice shall be created for.
     */
    public OcrRestWebService(@NotNull RestSession<T_REST_DOCUMENT> sessionToken) {
        super(sessionToken, WebServiceType.OCR);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public OcrType getOperation() {
        return getOperationData().getOcr();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable OcrType operationData) {
        if (operationData != null) {
            getOperationData().setOcr(operationData);
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
        getOperationData().setOcr(new OcrType());
    }

}
