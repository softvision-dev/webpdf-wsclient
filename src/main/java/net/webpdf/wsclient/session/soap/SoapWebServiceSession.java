package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.ServerContext;
import net.webpdf.wsclient.session.connection.ServerContextSettings;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.session.soap.documents.factory.SoapDocumentFactory;
import net.webpdf.wsclient.session.soap.documents.SoapWebServiceDocument;
import net.webpdf.wsclient.session.soap.documents.factory.SoapWebserviceDocumentFactory;
import net.webpdf.wsclient.webservice.WebServiceFactory;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.webservice.WebServiceType;
import net.webpdf.wsclient.webservice.soap.SoapWebService;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * An instance of {@link SoapWebServiceSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection
 * with a webPDF server.
 * </p>
 */
public class SoapWebServiceSession extends AbstractSoapSession<SoapWebServiceDocument> {

    /**
     * Creates a new {@link SoapWebServiceSession} instance providing connection information, authorization objects for
     * a webPDF server-client {@link SoapSession}.
     *
     * @param serverContext The {@link ServerContext} initializing the {@link ServerContextSettings} of this
     *                      {@link SoapSession}.
     * @param authProvider  The {@link AuthProvider} for authentication/authorization of this {@link SoapSession}.
     * @throws ResultException Shall be thrown, in case establishing the session failed.
     */
    public SoapWebServiceSession(@NotNull ServerContext serverContext, @NotNull AuthProvider authProvider)
            throws ResultException {
        super(serverContext, authProvider);
    }

    /**
     * Creates a matching {@link SoapWebService} instance to execute a webPDF operation for the current session.
     *
     * @param <T_WEBSERVICE> The {@link WebServiceType} to create an interface for.
     * @param webServiceType The {@link WebServiceType} to create an interface for.
     * @return A matching {@link SoapWebService} instance.
     * @throws ResultException Shall be thrown, if the {@link SoapWebService} creation failed.
     */
    @SuppressWarnings("unchecked")
    @Override
    public @NotNull <T_WEBSERVICE extends SoapWebService<?, ?, ?>> T_WEBSERVICE createWSInstance(
            @NotNull WebServiceType webServiceType) throws ResultException {
        return (T_WEBSERVICE) WebServiceFactory.createInstance(this, webServiceType);
    }

    /**
     * Provides a {@link SoapDocumentFactory} for the creation of session appropriate {@link SoapDocument}s.
     *
     * @return A {@link SoapDocumentFactory} for the creation of session appropriate {@link SoapDocument}s.
     */
    @Override
    public @NotNull SoapDocumentFactory<SoapWebServiceDocument> createSoapDocumentFactory() {
        return new SoapWebserviceDocumentFactory();
    }

}
