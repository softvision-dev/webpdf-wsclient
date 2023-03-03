package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.connection.proxy.ProxyConfiguration;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * An instance of {@link AbstractSoapSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection
 * with a webPDF server.
 * </p>
 */
public abstract class AbstractSoapSession
        extends AbstractSession implements SoapSession {

    private final @NotNull AtomicBoolean useLocalWsdl = new AtomicBoolean(true);
    private final @NotNull AtomicReference<WSClientProxySelector> proxySelector = new AtomicReference<>();

    /**
     * Creates a new {@link AbstractSoapSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link SoapSession}.
     *
     * @param url          The {@link URL} of the webPDF server
     * @param tlsContext   The {@link TLSContext} used for this https {@link SoapSession}.
     *                     ({@code null} in case an unencrypted HTTP {@link SoapSession} shall be created.)
     * @param authProvider The {@link AuthProvider} for authentication/authorization of this {@link SoapSession}.
     * @throws ResultException Shall be thrown, in case establishing the {@link SoapSession} failed.
     */
    public AbstractSoapSession(@NotNull URL url, @Nullable TLSContext tlsContext,
            @NotNull AuthProvider authProvider) throws ResultException {
        super(url, WebServiceProtocol.SOAP, tlsContext, authProvider);
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
        return useLocalWsdl.get();
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
        this.useLocalWsdl.set(useLocalWsdl);
    }

    /**
     * Set a {@link ProxyConfiguration} for this {@link SoapSession}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set for this {@link SoapSession}.
     * @throws ResultException Shall be thrown, when resolving the {@link ProxyConfiguration} failed.
     */
    @Override
    public void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        if (this.proxySelector.get() != null) {
            this.proxySelector.get().close();
            this.proxySelector.set(null);
        }
        super.setProxy(proxy);
        if (proxy != null) {
            this.proxySelector.set(
                    new WSClientProxySelector(new URI[]{getURI("")}, proxy.getHost())
            );
        }
    }

    /**
     * Close the {@link SoapSession}.
     */
    @Override
    public void close() {
        if (proxySelector.get() != null) {
            proxySelector.get().close();
        }
    }

}
