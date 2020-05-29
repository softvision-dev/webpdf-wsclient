package net.webpdf.wsclient.session;

import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.documents.Document;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.soap.SoapWebServiceSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public final class SessionFactory {

    /**
     * Prepares a session skeleton not yet containing a WebService Protocol or an Server URL.
     **/
    private SessionFactory() {
    }

    /**
     * Creates a session based on the given {@link WebServiceProtocol} to the server at the given {@link URL}.
     * This factory will either produce a {@link SoapWebServiceSession} or a {@link RestWebServiceSession}. It is not
     * fit to produce custom session types.
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     * @param <T_DOCUMENT>>      The type of {@link Document} handled by the {@link Session}.
     * @param <T_SESSION>        The type of the produced {@link Session}.
     * @return The {@link Session} organizing the communication with the server at the given {@link URL}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public static <T_DOCUMENT extends Document, T_SESSION extends Session<T_DOCUMENT>>
    T_SESSION createInstance(
            @NotNull WebServiceProtocol webServiceProtocol, @NotNull URL url
    ) throws ResultException {
        return createInstance(webServiceProtocol, url, null);
    }

    /**
     * Creates a session based on the given {@link WebServiceProtocol} to the server at the given {@link URL}.
     * This factory will either produce a {@link SoapWebServiceSession} or a {@link RestWebServiceSession}. It is not
     * fit to produce custom session types.
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     * @param tlsContext         Container configuring a https session.
     * @param <T_DOCUMENT>>      The type of {@link Document} handled by the {@link Session}.
     * @param <T_SESSION>        The type of the produced {@link Session}.
     * @return The {@link Session} organizing the communication with the server at the given {@link URL}.
     * @throws ResultException a {@link ResultException}
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public static <T_DOCUMENT extends Document, T_SESSION extends Session<T_DOCUMENT>>
    T_SESSION createInstance(
            @NotNull WebServiceProtocol webServiceProtocol, @NotNull URL url, @Nullable TLSContext tlsContext
    ) throws ResultException {
        try {
            switch (webServiceProtocol) {
                case SOAP:
                    return (T_SESSION) new SoapWebServiceSession(url, tlsContext);
                case REST:
                    return (T_SESSION) new RestWebServiceSession(url, tlsContext);
                default:
                    throw new ResultException(Result.build(Error.SESSION_CREATE));
            }
        } catch (ClassCastException ex) {
            throw new ResultException(Result.build(Error.SESSION_CREATE));
        }
    }

}
