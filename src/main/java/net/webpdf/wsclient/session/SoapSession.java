package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;

import java.net.URL;

public class SoapSession extends AbstractSession {

    private boolean useLocalWsdl = true;

    /**
     * Creates new {@link SoapSession} instance
     *
     * @param url        base url for webPDF server
     * @param tlsContext Container configuring a https session.
     * @throws ResultException a {@link ResultException}
     */
    SoapSession(URL url, TLSContext tlsContext) throws ResultException {
        super(url, WebServiceProtocol.SOAP, tlsContext);
        this.dataFormat = DataFormat.XML;
    }

    /**
     * Close session on webPDF server
     */
    @Override
    public void close() {
        // nothing to do
    }

    /**
     * When returning true, a wsdl stored on the local file system shall be used instead of the WSDL published by the
     * server.
     *
     * @return returns true, if a wsdl stored on the local file system shall be used instead of the WSDL published by
     * the server.
     */
    public boolean isUseLocalWsdl() {
        return useLocalWsdl;
    }

    /**
     * When set to true, a wsdl stored on the local file system shall be used instead of the WSDL published by the
     * server.
     *
     * @param useLocalWsdl True, to use a wsdl stored on the local file system, instead of the WSDL published by the server.
     */
    public void setUseLocalWsdl(boolean useLocalWsdl) {
        this.useLocalWsdl = useLocalWsdl;
    }
}
