package net.webpdf.wsclient.webservice.rest;

import net.webpdf.wsclient.session.documents.rest.RestDocument;
import net.webpdf.wsclient.session.rest.RestSession;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.SignatureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An instance of {@link SignatureRestWebService} wraps a wsclient connection to the webPDF webservice endpoint
 * {@link WebServiceType#SIGNATURE}, using {@link WebServiceProtocol#REST} and expecting a {@link RestDocument}
 * as the result.
 */
public class SignatureRestWebService<T_REST_DOCUMENT extends RestDocument>
        extends RestWebService<T_REST_DOCUMENT, SignatureType> {

    /**
     * Creates a {@link SignatureRestWebService} for the given {@link RestSession}
     *
     * @param session The {@link RestSession} a {@link SignatureRestWebService} shall be created for.
     */
    public SignatureRestWebService(@NotNull RestSession<T_REST_DOCUMENT> session) {
        super(session, WebServiceType.SIGNATURE);
    }

    /**
     * Returns the {@link SignatureRestWebService} specific {@link SignatureType}, which allows setting parameters for
     * the webservice execution.
     *
     * @return The {@link SignatureType} operation parameters.
     */
    @Override
    public @NotNull SignatureType getOperation() {
        return getOperationData().getSignature();
    }

    /**
     * Sets the {@link SignatureRestWebService} specific {@link SignatureType} element, which allows setting parameters
     * for the webservice execution.
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
     * Initializes and prepares the execution of this {@link SignatureRestWebService} via the given
     * {@link OperationData}.
     *
     * @param operation The {@link OperationData} initializing the {@link SignatureRestWebService}.
     */
    @Override
    protected void initOperation(@NotNull OperationData operation) {
        getOperationData().setSignature(new SignatureType());
    }

}
