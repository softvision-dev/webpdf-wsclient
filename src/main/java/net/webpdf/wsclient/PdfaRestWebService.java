package net.webpdf.wsclient;

import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.schema.operation.PdfaType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PdfaRestWebService extends RestWebservice<PdfaType> {

    /**
     * Creates a PdfaRestWebService for the given {@link Session}
     *
     * @param sessionToken The session a PdfaRestWebservice shall be created for.
     */
    PdfaRestWebService(@NotNull Session sessionToken) {
        super(sessionToken, WebServiceType.PDFA);
    }

    /**
     * Get webservice specific operation element to set webservice specific parameters
     *
     * @return operation type element
     */
    @Override
    @NotNull
    public PdfaType getOperation() {
        return this.operation.getPdfa();
    }

    /**
     * Sets the web service operation data
     *
     * @param operationData the web service operation data
     */
    @Override
    public void setOperation(@Nullable PdfaType operationData) {
        if (operationData != null) {
            operation.setPdfa(operationData);
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
        this.operation.setPdfa(new PdfaType());
    }

}
