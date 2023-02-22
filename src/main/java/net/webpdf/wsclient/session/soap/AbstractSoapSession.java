package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.apache.hc.client5.http.auth.Credentials;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;

/**
 * <p>
 * An instance of {@link AbstractSoapSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection
 * with a webPDF server.
 * </p>
 */
public abstract class AbstractSoapSession
        extends AbstractSession implements SoapSession{

    private boolean useLocalWsdl = true;
    private @Nullable WSClientProxySelector proxySelector = null;

    /**
     * Creates a new {@link AbstractSoapSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link SoapSession}.
     *
     * @param url         The {@link URL} of the webPDF server
     * @param tlsContext  The {@link TLSContext} used for this https {@link SoapSession}.
     *                    ({@code null} in case an unencrypted HTTP {@link SoapSession} shall be created.)
     * @param credentials The {@link Credentials} used for authorization of this session.
     * @throws ResultException Shall be thrown, in case establishing the {@link SoapSession} failed.
     */
    public AbstractSoapSession(@NotNull URL url, @Nullable TLSContext tlsContext,
            @Nullable Credentials credentials) throws ResultException {
        super(url, WebServiceProtocol.SOAP, tlsContext, credentials);
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
     * Close the {@link SoapSession}.
     */
    @Override
    public void close() {
        if (proxySelector != null) {
            proxySelector.close();
        }
    }

}
