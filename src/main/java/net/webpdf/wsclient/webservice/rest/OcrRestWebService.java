package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.session.documents.rest.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.OcrType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link OcrRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#OCR}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 */
public class OcrRestWebService<T_REST_DOCUMENT extends RestDocument> extends RestWebService<T_REST_DOCUMENT, OcrType> {

    /**
     * Creates a {@link OcrRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link OcrRestWebService} shall be created for.
     */
    public OcrRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.OCR);
    }

    /**
     * Returns the {@link OcrRestWebService} specific {@link OcrType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link OcrType} operation parameters.
     */
    @Override
    public @NotNull OcrType getOperation() {
        return getOperationData().getOcr();
    }

    /**
     * Sets the {@link OcrRestWebService} specific {@link OcrType} element, which allows setting parameters for
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
     * Initializes and prepares the execution of this {@link OcrRestWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link OcrRestWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setOcr(new OcrType());
    }

}
