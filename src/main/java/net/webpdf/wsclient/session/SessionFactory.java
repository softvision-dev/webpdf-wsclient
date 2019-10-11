package net.webpdf.wsclient.session;

import net.webpdf.wsclient.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.https.TLSContext;
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
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     * @param <T>                The type of the produced {@link Session}.
     * @return The {@link Session} organizing the communication with the server at the given {@link URL}.
     * @throws ResultException a {@link ResultException}
     */
    @NotNull
    public static <T extends Session> T createInstance(@NotNull WebServiceProtocol webServiceProtocol, @NotNull URL url)
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
    @NotNull
    public static <T extends Session> T createInstance(@NotNull WebServiceProtocol webServiceProtocol, @NotNull URL url,
                                                       @Nullable TLSContext tlsContext)
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
