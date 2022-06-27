package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
import jakarta.xml.bind.DatatypeConverter;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.soap.MTOMFeature;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.schema.stubs.WebServiceException;
import net.webpdf.wsclient.session.credentials.TokenCredentials;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.webservice.AbstractWebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.apache.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * An instance of {@link SoapWebService} wraps a wsclient connection to a specific webPDF webservice endpoint
 * ({@link WebServiceType}), using {@link WebServiceProtocol#SOAP} and expecting a {@link SoapDocument}
 * as the result.
 *
 * @param <T_SOAP_DOCUMENT>  The expected {@link SoapDocument} type for the documents used by the webPDF server.
 * @param <T_WEBPDF_PORT>    The interface stub type to operate the webservice endpoint with.
 * @param <T_OPERATION_TYPE> The {@link WebServiceType} of the targeted webservice endpoint.
 */
public abstract class SoapWebService<T_SOAP_DOCUMENT extends SoapDocument, T_WEBPDF_PORT, T_OPERATION_TYPE>
        extends AbstractWebService<T_SOAP_DOCUMENT, SoapSession<T_SOAP_DOCUMENT>, T_OPERATION_TYPE, T_SOAP_DOCUMENT> {

    private static final String SSL_SOCKET_FACTORY = "com.sun.xml.ws.transport.https.client.SSLSocketFactory";
    private final @NotNull MTOMFeature feature = new MTOMFeature();
    private final @NotNull QName qname;
    private final @NotNull URI webserviceURL;
    private final @NotNull T_WEBPDF_PORT port;
    @Nullable
    private final TLSContext tlsContext;

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link SoapSession}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link SoapSession} the webservice interface shall be created for.
     */
    public SoapWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session, @NotNull WebServiceType webServiceType)
            throws ResultException {
        super(webServiceType, session);
        this.qname = new QName(webServiceType.getSoapNamespaceURI(), webServiceType.getSoapLocalPart());
        this.tlsContext = getSession().getTlsContext();
        this.webserviceURL = getSession().getURI(webServiceType.getSoapEndpoint());
        this.port = provideWSPort();
    }

    /**
     * Execute the webservice operation and returns the {@link DataHandler} of the resulting document.
     *
     * @return The {@link DataHandler} of the resulting document.
     * @throws WebServiceException Shall be thrown, upon an execution failure.
     */
    protected abstract @Nullable DataHandler processService() throws WebServiceException;

    /**
     * Execute the webservice operation and returns the resulting {@link SoapDocument}.
     *
     * @return The resulting {@link SoapDocument}.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    public @NotNull T_SOAP_DOCUMENT process() throws ResultException {
        if (getDocument() == null) {
            throw new ResultException(Result.build(Error.NO_DOCUMENT));
        }

        DataHandler resultDataHandler;
        try {
            applyOptions();

            resultDataHandler = processService();

            if (resultDataHandler != null) {
                getDocument().save(resultDataHandler);
            }

            return getDocument();

        } catch (IOException | WebServiceException ex) {
            throw new ResultException(Result.build(Error.SOAP_EXECUTION, ex));
        }
    }

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
                throw new ResultException(Result.build(Error.WSDL_INVALID_FILE));
            }

            return url;

        } catch (MalformedURLException ex) {
            throw new ResultException(Result.build(Error.WSDL_INVALID_URL, ex));
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

            boolean tokenCredentials = getSession().getCredentials() instanceof TokenCredentials;
            if (tokenCredentials) {
                getHeaders().put(HttpHeaders.AUTHORIZATION,
                        Collections.singletonList("Bearer" +
                                getSession().getCredentials().getUserPrincipal().getName()));
            } else {
                String basicAuth = "Basic " + DatatypeConverter.printBase64Binary(
                        (getSession().getCredentials().getUserPrincipal().getName()
                                + ":" + getSession().getCredentials().getPassword())
                                .getBytes(StandardCharsets.ISO_8859_1));
                getHeaders().put(HttpHeaders.AUTHORIZATION, Collections.singletonList(basicAuth));
            }

            if (!getHeaders().isEmpty()) {
                Map<String, Object> requestContext = bindingProvider.getRequestContext();
                requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, getHeaders());

                if (!tokenCredentials) {
                    requestContext.put(BindingProvider.USERNAME_PROPERTY,
                            getSession().getCredentials().getUserPrincipal().getName());
                    requestContext.put(BindingProvider.PASSWORD_PROPERTY,
                            getSession().getCredentials().getPassword());
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
