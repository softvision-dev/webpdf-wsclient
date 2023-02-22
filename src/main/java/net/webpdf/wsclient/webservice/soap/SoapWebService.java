package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.soap.MTOMFeature;
import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.exception.ServerResultException;
import net.webpdf.wsclient.schema.operation.BillingType;
import net.webpdf.wsclient.schema.operation.OperationData;
import net.webpdf.wsclient.schema.operation.PdfPasswordType;
import net.webpdf.wsclient.schema.operation.SettingsType;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.access.token.OAuthToken;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.AbstractWebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.apache.hc.core5.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

/**
 * An instance of {@link SoapWebService} wraps a wsclient connection to a specific webPDF webservice endpoint
 * ({@link WebServiceType}), using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_OPERATION_PARAMETER> The parameter type of the targeted webservice endpoint.
 * @param <T_SOAP_DOCUMENT>       The expected {@link SoapDocument} type for the documents used by the webPDF server.
 * @param <T_WEBPDF_PORT>         The interface stub type to operate the webservice endpoint with.
 */
public abstract class SoapWebService<T_WEBPDF_PORT, T_OPERATION_PARAMETER, T_SOAP_DOCUMENT extends SoapDocument>
        extends AbstractWebService<SoapSession, OperationData, T_OPERATION_PARAMETER, T_SOAP_DOCUMENT,
        BillingType, PdfPasswordType, SettingsType> {

    private static final String SSL_SOCKET_FACTORY = "com.sun.xml.ws.transport.https.client.SSLSocketFactory";
    private final @NotNull MTOMFeature feature = new MTOMFeature();
    private final @NotNull QName qname;
    private final @NotNull URI webserviceURL;
    private final @NotNull T_WEBPDF_PORT port;
    private final @Nullable TLSContext tlsContext;

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link SoapSession}.
     *
     * @param session        The {@link SoapSession} the webservice interface shall be created for.
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     */
    public SoapWebService(@NotNull SoapSession session, @NotNull WebServiceType webServiceType)
            throws ResultException {
        super(webServiceType, session);
        this.qname = new QName(webServiceType.getSoapNamespaceURI(), webServiceType.getSoapLocalPart());
        this.tlsContext = getSession().getTlsContext();
        this.webserviceURL = getSession().getURI(webServiceType.getSoapEndpoint());
        this.port = provideWSPort();
    }

    /**
     * Execute the webservice operation and returns the resulting {@link SoapDocument}.
     *
     * @return The resulting {@link SoapDocument}.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    public @NotNull T_SOAP_DOCUMENT process() throws ResultException {
        if (getSourceDocument() == null) {
            throw new ClientResultException(Error.NO_DOCUMENT);
        }

        DataHandler resultDataHandler;
        try {
            applyOptions();

            resultDataHandler = processService();

            if (resultDataHandler != null) {
                getSourceDocument().save(resultDataHandler);
            }

            return getSourceDocument();

        } catch (WebServiceException ex) {
            throw new ServerResultException(ex);
        }
    }

    /**
     * Returns the {@link PdfPasswordType} of the current webservice.
     *
     * @return the {@link PdfPasswordType} of the current webservice.
     */
    @Override
    public @NotNull PdfPasswordType getPassword() {
        return getOperationData().getPassword();
    }

    /**
     * Returns the {@link BillingType} of the current webservice.
     *
     * @return the {@link BillingType} of the current webservice.
     */
    @Override
    public @NotNull BillingType getBilling() {
        return getOperationData().getBilling();
    }

    /**
     * Returns the {@link SettingsType} of the current webservice.
     *
     * @return the {@link SettingsType} of the current webservice.
     */
    @Override
    public @Nullable SettingsType getSettings() {
        return getOperationData().getSettings();
    }

    /**
     * Execute the webservice operation and returns the {@link DataHandler} of the resulting document.
     *
     * @return The {@link DataHandler} of the resulting document.
     * @throws WebServiceException Shall be thrown, upon an execution failure.
     */
    protected abstract @Nullable DataHandler processService() throws WebServiceException;

    /**
     * Returns the {@link QName} of the current SOAP webservice.
     *
     * @return the {@link QName} of the current SOAP webservice.
     */
    protected @NotNull QName getQName() {
        return this.qname;
    }

    /**
     * Returns the {@link URL} of the wsdl document.
     *
     * @return the {@link URL} of the wsdl document.
     * @throws ResultException a {@link ResultException}
     */
    protected @NotNull URL getWsdlDocumentLocation() throws ResultException {

        boolean useLocalWsdl = getSession().isUseLocalWsdl();

        try {
            URL url = useLocalWsdl ?
                    ConverterWebService.class.getClassLoader()
                            .getResource("wsdl/" + getWebServiceType().getSoapEndpoint() + ".wsdl")
                    : this.webserviceURL.toURL();

            if (url == null) {
                throw new ClientResultException(Error.WSDL_INVALID_FILE);
            }

            return url;

        } catch (MalformedURLException ex) {
            throw new ClientResultException(Error.WSDL_INVALID_URL, ex);
        }
    }

    /**
     * Returns the configuration of the {@link MTOMFeature} for the current webservice call.
     *
     * @return The configuration of the {@link MTOMFeature} for the current webservice call.
     */
    protected @NotNull MTOMFeature getMTOMFeature() {
        return feature;
    }

    /**
     * Apply all previously set options to the Web service call
     *
     * @throws ResultException Shall be thrown, should applying the options fail.
     */
    private void applyOptions() throws ResultException {

        BindingProvider bindingProvider = ((BindingProvider) port);

        // set auth information
        if (getSession().getCredentials() != null) {

            boolean tokenCredentials = getSession().getCredentials() instanceof OAuthToken;
            if (tokenCredentials) {
                getHeaders().put(HttpHeaders.AUTHORIZATION,
                        Collections.singletonList("Bearer" +
                                getSession().getCredentials().getUserPrincipal().getName()));
            } else {
                String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(
                        (getSession().getCredentials().getUserPrincipal().getName()
                                + ":" + new String(getSession().getCredentials().getPassword()))
                                .getBytes());
                getHeaders().put(HttpHeaders.AUTHORIZATION, Collections.singletonList(basicAuth));
            }

            if (!getHeaders().isEmpty()) {
                Map<String, Object> requestContext = bindingProvider.getRequestContext();
                requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, getHeaders());

                if (!tokenCredentials) {
                    requestContext.put(BindingProvider.USERNAME_PROPERTY,
                            getSession().getCredentials().getUserPrincipal().getName());
                    requestContext.put(BindingProvider.PASSWORD_PROPERTY,
                            new String(getSession().getCredentials().getPassword()));
                }
            }
        }

        // set target URL
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.webserviceURL.toString());

        if (tlsContext != null) {
            bindingProvider.getRequestContext().put(SSL_SOCKET_FACTORY, tlsContext.getSslContext().getSocketFactory());
        }
    }

    /**
     * Create a matching webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The webservice port, that shall be used for executions.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    protected abstract @NotNull T_WEBPDF_PORT provideWSPort() throws ResultException;

    /**
     * Returns a matching webservice port for future executions of this {@link SoapWebService}.
     *
     * @return The webservice port, that shall be used for executions.
     */
    protected @NotNull T_WEBPDF_PORT getPort() {
        return port;
    }

}
