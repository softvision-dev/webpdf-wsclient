package net.webpdf.wsclient.webservice.soap;

import jakarta.activation.DataHandler;
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
import net.webpdf.wsclient.session.auth.material.AuthMaterial;
import net.webpdf.wsclient.session.soap.SoapSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.AbstractWebService;
import net.webpdf.wsclient.webservice.WebService;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.webservice.WebServiceType;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.core5.http.HttpHeaders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.net.ssl.SSLContext;
import javax.xml.namespace.QName;
import java.io.File;
import java.io.OutputStream;
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
        extends AbstractWebService<SoapSession<T_SOAP_DOCUMENT>, OperationData, T_OPERATION_PARAMETER, T_SOAP_DOCUMENT,
        BillingType, PdfPasswordType, SettingsType> {

    private static final String SSL_SOCKET_FACTORY = "com.sun.xml.ws.transport.https.client.SSLSocketFactory";
    private final @NotNull MTOMFeature feature = new MTOMFeature();
    private final @NotNull QName qname;
    private final @NotNull URI webserviceURL;
    private final @NotNull T_WEBPDF_PORT port;

    /**
     * Creates a webservice interface of the given {@link WebServiceType} for the given {@link SoapSession}.
     *
     * @param session        The {@link SoapSession} the webservice interface shall be created for.
     * @param webServiceType The {@link WebServiceType} interface, that shall be created.
     */
    public SoapWebService(@NotNull SoapSession<T_SOAP_DOCUMENT> session, @NotNull WebServiceType webServiceType)
            throws ResultException {
        super(webServiceType, session);
        this.qname = new QName(webServiceType.getSoapNamespaceURI(), webServiceType.getSoapLocalPart());
        this.webserviceURL = getSession().getURI(webServiceType.getSoapEndpoint());
        this.port = provideWSPort();
    }

    /**
     * <p>
     * Execute the webservice operation and return the resulting {@link T_SOAP_DOCUMENT}.<br>
     * You can write the result of the webservice call via: {@link SoapDocument#writeResult(File)} or
     * {@link SoapDocument#writeResult(OutputStream)}
     * </p>
     * <p>
     * <b>Be aware:</b> A {@link SoapDocument} is using {@link DataHandler} objects, which might require to be closed,
     * to avoid resource leaks. You should call {@link SoapDocument#close()} on the hereby provided result document.
     * </p>
     * <p>
     * <b>Be aware:</b> Most webservices require a source {@link T_SOAP_DOCUMENT}, with few exceptions, such as the
     * URL-converter webservice. Before using this method, make sure that this is valid for
     * the {@link WebService} call you intend to hereby execute.
     * </p>
     *
     * @return The resulting {@link T_SOAP_DOCUMENT}.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    public @NotNull T_SOAP_DOCUMENT process() throws ResultException {
        return process(getSession().createDocument());
    }

    /**
     * <p>
     * Execute the webservice operation for the given source {@link T_SOAP_DOCUMENT} and return the
     * resulting {@link T_SOAP_DOCUMENT}.<br>
     * You can write the result of the webservice call via: {@link SoapDocument#writeResult(File)} or
     * {@link SoapDocument#writeResult(OutputStream)} on either the returned or the source document.
     * </p>
     * <p>
     * <b>Be aware:</b> The hereby returned {@link SoapDocument} is identical to the given source document.<br>
     * <b>Be aware:</b> A {@link SoapDocument} is using {@link DataHandler} objects, which might require to be closed,
     * to avoid resource leaks. You should call {@link SoapDocument#close()} on the hereby provided result document.
     * </p>
     *
     * @param sourceDocument The source {@link T_SOAP_DOCUMENT}, that shall be processed.
     * @return The resulting {@link T_SOAP_DOCUMENT}.
     * @throws ResultException Shall be thrown, upon an execution failure.
     */
    @Override
    public @NotNull T_SOAP_DOCUMENT process(@NotNull T_SOAP_DOCUMENT sourceDocument) throws ResultException {
        try {
            applyOptions();
            sourceDocument.setResult(processService(sourceDocument));
            return sourceDocument;
        } catch (WebServiceException ex) {
            throw new ServerResultException(ex);
        }
        // catch unhandled exceptions.
        catch (Exception ex) {
            throw new ClientResultException(Error.SOAP_EXECUTION, ex);
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
     * @param sourceDocument The {@link T_SOAP_DOCUMENT} to process.
     * @return The {@link DataHandler} of the resulting document.
     * @throws WebServiceException Shall be thrown, upon an execution failure.
     */
    protected abstract @Nullable DataHandler processService(@NotNull T_SOAP_DOCUMENT sourceDocument)
            throws WebServiceException;

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
        AuthMaterial authMaterial = getSession().getAuthMaterial();
        String authorizationHeader = authMaterial.getRawAuthHeader();
        if (authorizationHeader != null) {
            getHeaders().put(HttpHeaders.AUTHORIZATION, Collections.singletonList(authorizationHeader));
            if (!getHeaders().isEmpty()) {
                Map<String, Object> requestContext = bindingProvider.getRequestContext();
                requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, getHeaders());
                Credentials credentials = authMaterial.getCredentials();
                if (credentials != null) {
                    requestContext.put(BindingProvider.USERNAME_PROPERTY,
                            credentials.getUserPrincipal().getName());
                    requestContext.put(BindingProvider.PASSWORD_PROPERTY,
                            new String(credentials.getPassword()));
                }
            }
        }

        // set target URL
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, this.webserviceURL.toString());
        SSLContext tlsContext = getSession().getTLSContext();
        if (tlsContext != null) {
            bindingProvider.getRequestContext().put(SSL_SOCKET_FACTORY, tlsContext.getSocketFactory());
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