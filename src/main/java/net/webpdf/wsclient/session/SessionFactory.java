package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;

import java.net.URL;

public final class SessionFactory {

    private SessionFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Session> T createInstance(WebServiceProtocol webServiceProtocol, URL url)
            throws ResultException {

        switch (webServiceProtocol) {
            case SOAP:
                return (T) new SoapSession(url);
            case REST:
                return (T) new RestSession(url);
            default:
                throw new ResultException(Result.build(Error.SESSION_CREATE));
        }
    }

}
