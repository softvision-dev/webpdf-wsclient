package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;

import java.net.URL;

public class SoapSession extends AbstractSession {

    private boolean useLocalWsdl = true;

    SoapSession(URL url) throws ResultException {
        super(url, WebServiceProtocol.SOAP);
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
