package net.webpdf.wsclient.session;

import net.webpdf.wsclient.session.documents.Document;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.Result;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.soap.SoapWebServiceSession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * <p>
 * The {@link SessionFactory} provides the means to create a {@link Session} of a matching type establishing and
 * managing a connection with a webPDF server for the given {@link WebServiceProtocol} and context.
 * </p>
 */
public final class SessionFactory {

    /**
     * This class is not intended to be instantiated, use the static methods instead.
     */
    private SessionFactory() {
    }

    /**
     * <p>
     * Creates a HTTP {@link Session} based on the given {@link WebServiceProtocol} to the server at the given
     * {@link URL}.
     * </p>
     * <p>
     * This factory will either produce a {@link SoapWebServiceSession} or a {@link RestWebServiceSession}. It is not
     * fit to produce custom session types.
     * </p>
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     * @return The {@link Session} organizing the communication with the server at the given {@link URL}.
     * @throws ResultException Shall be thrown in case establishing the {@link Session} failed.
     */
    public static <T_DOCUMENT extends Document, T_SESSION extends Session<T_DOCUMENT>>
    @NotNull T_SESSION createInstance(@NotNull WebServiceProtocol webServiceProtocol, @NotNull URL url)
            throws ResultException {
        return createInstance(webServiceProtocol, url, null);
    }

    /**
     * <p>
     * Creates an encrypted HTTPS {@link Session} based on the given {@link WebServiceProtocol} to the server at the
     * given {@link URL}.
     * </p>
     * <p>
     * This factory will either produce a {@link SoapWebServiceSession} or a {@link RestWebServiceSession}. It is not
     * fit to produce custom session types.
     * </p>
     *
     * @param webServiceProtocol The {@link WebServiceProtocol} used to communicate with the server.
     * @param url                The {@link URL} of the server.
     * @param tlsContext         {@link TLSContext} configuring a HTTPS {@link Session}.
     * @return The {@link Session} organizing the communication with the server at the given {@link URL}.
     * @throws ResultException Shall be thrown in case establishing the {@link Session} failed.
     */
    @SuppressWarnings("unchecked")
    public static <T_DOCUMENT extends Document, T_SESSION extends Session<T_DOCUMENT>> @NotNull T_SESSION
    createInstance(@NotNull WebServiceProtocol webServiceProtocol, @NotNull URL url, @Nullable TLSContext tlsContext)
            throws ResultException {
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
