package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;

import java.io.IOException;
import java.net.URL;

public class SoapSession extends AbstractSession {

    private boolean useLocalWsdl = true;

    SoapSession(URL url) throws ResultException {
        super(url, WebServiceProtocol.SOAP);
        this.dataFormat = DataFormat.XML;
    }

    @Override
    public void close() throws IOException {
        // nothing to do
    }

    public boolean isUseLocalWsdl() {
        return useLocalWsdl;
    }

    public void setUseLocalWsdl(boolean useLocalWsdl) {
        this.useLocalWsdl = useLocalWsdl;
    }
}
