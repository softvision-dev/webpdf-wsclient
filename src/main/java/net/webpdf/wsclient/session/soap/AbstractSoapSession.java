package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.credentials.TokenCredentials;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import net.webpdf.wsclient.session.credentials.token.Token;
import net.webpdf.wsclient.session.credentials.token.TokenProvider;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.hc.client5.http.auth.Credentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * <p>
 * An instance of {@link AbstractSoapSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection
 * with a webPDF server.
 * </p>
 *
 * @param <T_SOAP_DOCUMENT>> The {@link SoapDocument} type used by this {@link SoapSession}.
 */
public abstract class AbstractSoapSession<T_SOAP_DOCUMENT extends SoapDocument>
        extends AbstractSession<T_SOAP_DOCUMENT> implements SoapSession<T_SOAP_DOCUMENT> {

    private boolean useLocalWsdl = true;
    private @Nullable WSClientProxySelector proxySelector = null;

    /**
     * Creates a new {@link AbstractSoapSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link SoapSession}.
     *
     * @param url        The {@link URL} of the webPDF server
     * @param tlsContext The {@link TLSContext} used for this https {@link SoapSession}.
     *                   ({@code null} in case an unencrypted HTTP {@link SoapSession} shall be created.)
     * @throws ResultException Shall be thrown, in case establishing the {@link SoapSession} failed.
     */
    public AbstractSoapSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, WebServiceProtocol.SOAP, tlsContext);
    }

    /**
     * When returning {@code true}, a wsdl stored on the local file system shall be used instead of the WSDL published
     * by the webPDF server.
     *
     * @return {@code true}, if a wsdl stored on the local file system shall be used instead of the WSDL published by
     * the webPDF server.
     */
    @Override
    public boolean isUseLocalWsdl() {
        return useLocalWsdl;
    }

    /**
     * When set to {@code true}, a wsdl stored on the local file system shall be used instead of the WSDL published by
     * the webPDF server.
     *
     * @param useLocalWsdl {@code true}, to use a wsdl stored on the local file system, instead of the WSDL published
     *                     by the webPDF server.
     */
    @SuppressWarnings({"SameParameterValue"})
    @Override
    public void setUseLocalWsdl(boolean useLocalWsdl) {
        this.useLocalWsdl = useLocalWsdl;
    }

    /**
     * Set a {@link ProxyConfiguration} for this {@link SoapSession}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set for this {@link SoapSession}.
     * @throws ResultException Shall be thrown, when resolving the {@link ProxyConfiguration} failed.
     */
    @Override
    public void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        if (this.proxySelector != null) {
            this.proxySelector.close();
            this.proxySelector = null;
        }
        super.setProxy(proxy);
        if (proxy != null) {
            this.proxySelector = new WSClientProxySelector(
                    new URI[]{getURI("")}, proxy.getHost());
        }
    }

    /**
     * Uses the given {@link Token} as the {@link Credentials} authorizing this session.
     *
     * @param token The {@link Token} authorizing this session.
     */
    @Override
    public void setCredentials(@Nullable Token token) {
        setCredentials(token != null ? new TokenCredentials<>(token) : null);
    }

    /**
     * Uses the given {@link TokenProvider}, to produce a {@link Token}, that shall be used as the {@link Credentials}
     * authorizing this session.
     *
     * @param tokenProvider The {@link TokenProvider} creating the {@link Token} authorizing this session.
     */
    @Override
    public void setCredentials(@Nullable TokenProvider<?> tokenProvider) throws IOException {
        setCredentials(tokenProvider != null ? tokenProvider.provideToken() : null);
    }

    /**
     * Close the {@link SoapSession}.
     */
    @Override
    public void close() {
        if (proxySelector != null) {
            proxySelector.close();
        }
    }

}
