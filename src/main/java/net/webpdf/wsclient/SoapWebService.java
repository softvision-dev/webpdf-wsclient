package net.webpdf.wsclient;

import net.webpdf.wsclient.documents.SoapDocument;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.schema.stubs.WebserviceException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.SoapSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.activation.DataHandler;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.MTOMFeature;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public abstract class SoapWebService<T_WEBPDF_PORT, T_OPERATION_TYPE>
    extends AbstractWebService<SoapDocument, T_OPERATION_TYPE, SoapDocument> {

    private static final String SSL_SOCKET_FACTORY = "com.sun.xml.ws.transport.https.client.SSLSocketFactory";
    @NotNull
    private final MTOMFeature feature = new MTOMFeature();
    @NotNull
    private final QName qname;
    @NotNull
    private final URI webserviceURL;
    @NotNull
    T_WEBPDF_PORT port;
    @Nullable
    private final TLSContext tlsContext;

    /**
     * Creates a SOAP webservice interface of the given {@link WebServiceType} for the given {@link Session}.
     *
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     * @param session        The {@link Session} the webservice interface shall be created for.
     */
    SoapWebService(@NotNull Session session, @NotNull WebServiceType webServiceType) throws ResultException {
        super(webServiceType, session);
        this.qname = new QName(webServiceType.getSoapNamespaceURI(), webServiceType.getSoapLocalPart());
        this.tlsContext = this.session.getTlsContext();
        this.webserviceURL = this.session.getURI(webServiceType.getSoapEndpoint());
        this.port = provideWSPort();
    }

    /**
     * Executes webservice operation and returns {@link DataHandler} of the result document.
     *
     * @return A {@link DataHandler} of the result document.
     * @throws WebserviceException a {@link WebserviceException}
     */
    @Nullable
    abstract DataHandler processService() throws WebserviceException;

    /**
     * Executes webservice operation and returns result
     *
     * @return the result
     * @throws ResultException a {@link ResultException}
     */
    @Override
    @NotNull
    public SoapDocument process() throws ResultException {

        if (this.document == null) {
            throw new ResultException(Result.build(Error.NO_DOCUMENT));
        }

        DataHandler resultDataHandler;
        try {
            applyOptions();

            resultDataHandler = processService();

            if (resultDataHandler != null) {
                this.document.save(resultDataHandler);
            }

            return this.document;

        } catch (IOException | WebserviceException ex) {
            throw new ResultException(Result.build(Error.SOAP_EXECUTION, ex));
        }
    }

    /**
     * Returns the {@link QName} of the current SOAP webservice.
     *
     * @return the {@link QName} of the current SOAP webservice.
     */
    @NotNull
    QName getQName() {
        return this.qname;
    }

    /**
     * Returns the {@link URL} of the wsdl document.
     *
     * @return the {@link URL} of the wsdl document.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    URL getWsdlDocumentLocation() throws ResultException {

        boolean useLocalWsdl = (!(this.session instanceof SoapSession)) || ((SoapSession) session).isUseLocalWsdl();

        try {
            URL url = useLocalWsdl
                          ? ConverterWebService.class.getClassLoader().getResource(
                "wsdl/" + this.webServiceType.getSoapEndpoint() + ".wsdl")
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
    @NotNull
    MTOMFeature getFeature() {
        return feature;
    }

    /**
     * Apply the options for the Web service call
     */
    private void applyOptions() throws ResultException {

        BindingProvider bindingProvider = ((BindingProvider) port);

        // set auth information
        if (session.getCredentials() != null) {

            String authHeader = "Basic " + DatatypeConverter.printBase64Binary((
                session.getCredentials().getUserPrincipal().toString() + ":"
                    + session.getCredentials().getPassword())
                                                                                   .getBytes());

            headers.put("Authorization", Collections.singletonList(authHeader));

            if (!headers.isEmpty()) {
                Map<String, Object> requestContext = bindingProvider.getRequestContext();
                requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);
                requestContext.put(BindingProvider.USERNAME_PROPERTY, session.getCredentials().getUserPrincipal().getName());
                requestContext.put(BindingProvider.PASSWORD_PROPERTY, session.getCredentials().getPassword());
            }
        }

        // set target URL
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.webserviceURL.toString());

        if (tlsContext != null) {
            bindingProvider.getRequestContext().put(SSL_SOCKET_FACTORY, tlsContext.getSslContext().getSocketFactory());
        }
    }

    /**
     * Create a matching webservice port for future executions of this SOAP webservice.
     *
     * @return The webservice port, that shall be used for executions.
     */
    @NotNull
    protected abstract T_WEBPDF_PORT provideWSPort() throws ResultException;

}
