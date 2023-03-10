package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.Session;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.session.connection.SessionContextSettings;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.soap.documents.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * An instance of {@link AbstractSoapSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection
 * with a webPDF server.
 * </p>
 */
public abstract class AbstractSoapSession<T_SOAP_DOCUMENT extends SoapDocument>
        extends AbstractSession implements SoapSession<T_SOAP_DOCUMENT> {

    private final @NotNull AtomicBoolean useLocalWsdl = new AtomicBoolean(true);
    private final @NotNull AtomicReference<WSClientProxySelector> proxySelector = new AtomicReference<>();

    /**
     * <p>
     * Creates a new {@link AbstractSoapSession} instance providing connection information and authorization objects
     * for a webPDF server-client {@link SoapSession}.
     * </p>
     * <p>
     * <b>Be Aware:</b> Neither {@link SessionContext}, nor {@link AuthProvider} are required to serve multiple
     * {@link Session}s at a time. It is expected to create a new {@link SessionContext} and {@link AuthProvider}
     * per {@link Session} you create.
     * </p>
     *
     * @param serverContext The {@link SessionContext} initializing the {@link SessionContextSettings} of this
     *                      {@link SoapSession}.
     * @param authProvider  The {@link AuthProvider} for authentication/authorization of this {@link SoapSession}.
     * @throws ResultException Shall be thrown, in case establishing the {@link SoapSession} failed.
     */
    public AbstractSoapSession(@NotNull SessionContext serverContext, @NotNull AuthProvider authProvider)
            throws ResultException {
        super(WebServiceProtocol.SOAP, serverContext, authProvider);
        if (getSessionContext().getProxy() != null) {
            if (this.proxySelector.get() != null) {
                this.proxySelector.get().close();
                this.proxySelector.set(null);
            }
            this.proxySelector.set(
                    new WSClientProxySelector(new URI[]{getURI("")},
                            getSessionContext().getProxy().getHost())
            );
        }
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
     * Close the {@link SoapSession}.
     */
    @Override
    public void close() {
        if (proxySelector.get() != null) {
            proxySelector.get().close();
        }
    }

}
