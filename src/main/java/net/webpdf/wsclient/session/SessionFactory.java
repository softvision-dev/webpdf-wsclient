package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;

import java.net.URL;

public final class SessionFactory {

    private SessionFactory() {
    }

    /**
     * Creates a session based on the given {@link WebServiceProtocol} to the server at the given {@link URL}.
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     * @param <T>                The type of the produced {@link Session}.
     * @return The {@link Session} organizing the communication with the server at the given {@link URL}.
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unchecked")
    public static <T extends Session> T createInstance(WebServiceProtocol webServiceProtocol, URL url)
        throws ResultException {
        return createInstance(webServiceProtocol, url, null);
    }

    /**
     * Creates a session based on the given {@link WebServiceProtocol} to the server at the given {@link URL}.
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     * @param tlsContext         Container configuring a https session.
     * @param <T>                The type of the produced {@link Session}.
     * @return The {@link Session} organizing the communication with the server at the given {@link URL}.
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unchecked")
    public static <T extends Session> T createInstance(WebServiceProtocol webServiceProtocol, URL url,
                                                       TLSContext tlsContext)
        throws ResultException {
        switch (webServiceProtocol) {
            case SOAP:
                return (T) new SoapSession(url, tlsContext);
            case REST:
                return (T) new RestSession(url, tlsContext);
            default:
                throw new ResultException(Result.build(Error.SESSION_CREATE));
        }
    }

}
