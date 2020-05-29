package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.documents.soap.SoapDocument;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.proxy.ProxyConfiguration;
import net.webpdf.wsclient.session.AbstractSession;
import net.webpdf.wsclient.session.DataFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;

@SuppressWarnings("unused")
public abstract class AbstractSoapSession<T_SOAP_DOCUMENT extends SoapDocument>
        extends AbstractSession<T_SOAP_DOCUMENT> implements SoapSession<T_SOAP_DOCUMENT> {

    private boolean useLocalWsdl = true;
    private WSClientProxySelector proxySelector = null;

    /**
     * Creates new {@link AbstractSoapSession} instance
     *
     * @param url        base url for webPDF server
     * @param tlsContext Container configuring a https session.
     * @throws ResultException a {@link ResultException}
     */
    public AbstractSoapSession(@NotNull URL url, @Nullable TLSContext tlsContext) throws ResultException {
        super(url, WebServiceProtocol.SOAP, tlsContext);
        setDataFormat(DataFormat.XML);
    }

    /**
     * Set a {@link ProxyConfiguration}.
     *
     * @param proxy The {@link ProxyConfiguration}, that shall be set.
     * @throws ResultException Shall be thrown, when resolving the proxy failed.
     */
    @Override
    public void setProxy(@Nullable ProxyConfiguration proxy) throws ResultException {
        if (this.proxySelector != null) {
            this.proxySelector.close();
            this.proxySelector = null;
        }
        super.setProxy(proxy);
        if (proxy != null) {
            this.proxySelector = new WSClientProxySelector(new URI[]{getURI("")}, proxy.getHost());
        }
    }

    /**
     * Close session on webPDF server.
     */
    @Override
    public void close() {
        if (proxySelector != null) {
            proxySelector.close();
        }
    }

    /**
     * When returning true, a wsdl stored on the local file system shall be used instead of the WSDL published by the
     * server.
     *
     * @return returns true, if a wsdl stored on the local file system shall be used instead of the WSDL published by
     * the server.
     */
    @Override
    public boolean isUseLocalWsdl() {
        return useLocalWsdl;
    }

    /**
     * When set to true, a wsdl stored on the local file system shall be used instead of the WSDL published by the
     * server.
     *
     * @param useLocalWsdl True, to use a wsdl stored on the local file system, instead of the WSDL published by the server.
     */
    @SuppressWarnings({"SameParameterValue"})
    @Override
    public void setUseLocalWsdl(boolean useLocalWsdl) {
        this.useLocalWsdl = useLocalWsdl;
    }

}
