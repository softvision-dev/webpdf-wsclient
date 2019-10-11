package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.SignatureType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SignatureRestWebService extends RestWebservice<SignatureType> {

    /**
     * Creates a SignatureRestWebService for the given {@link Session}
     *
     * @param session The session a SignatureRestWebservice shall be created for.
     */
    SignatureRestWebService(@NotNull Session session) {
        super(session, WebServiceType.SIGNATURE);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public SignatureType getOperation() {
        return this.operation.getSignature();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable SignatureType operationData) {
        if (operationData != null) {
            operation.setSignature(operationData);
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
        this.operation.setSignature(new SignatureType());
    }

}
