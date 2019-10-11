package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.OcrType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OcrRestWebService extends RestWebservice<OcrType> {

    /**
     * Creates a OcrRestWebService for the given {@link Session}
     *
     * @param sessionToken The session a OcrRestWebservice shall be created for.
     */
    OcrRestWebService(@NotNull Session sessionToken) {
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
