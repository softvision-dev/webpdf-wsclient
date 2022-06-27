package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.session.rest.documents.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.PdfaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link PdfaRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#PDFA}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 */
public class PdfaRestWebService<T_REST_DOCUMENT extends RestDocument> extends RestWebService<T_REST_DOCUMENT, PdfaType> {

    /**
     * Creates a {@link PdfaRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link PdfaRestWebService} shall be created for.
     */
    public PdfaRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.PDFA);
    }

    /**
     * Returns the {@link PdfaRestWebService} specific {@link PdfaType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link PdfaType} operation parameters.
     */
    @Override
    public @NotNull PdfaType getOperation() {
        return getOperationData().getPdfa();
    }

    /**
     * Sets the {@link PdfaRestWebService} specific {@link PdfaType} element, which allows setting parameters for
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
     * Initializes and prepares the execution of this {@link PdfaRestWebService} via the given {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link PdfaRestWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setPdfa(new PdfaType());
    }

}
