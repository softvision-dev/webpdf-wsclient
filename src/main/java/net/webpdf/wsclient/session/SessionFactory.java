package net.webpdf.wsclient.session;

import net.webpdf.wsclient.exception.ClientResultException;
import net.webpdf.wsclient.session.auth.AnonymousAuthProvider;
import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.session.connection.SessionContext;
import net.webpdf.wsclient.exception.Error;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.rest.RestWebServiceSession;
import net.webpdf.wsclient.session.soap.SoapWebServiceSession;
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * The {@link SessionFactory} provides the means to create a {@link Session} of a matching type, establishing and
 * managing a connection with a webPDF server.<br>
 * </p>
 * <p>
 * <b>Be Aware:</b> Should you not set an {@link AuthProvider} the {@link AnonymousAuthProvider} shall be used by
 * default, and "anonymous sessions" may or may not be allowed by your webPDF server.<br>
 * <b>It is never possible to establish a session with the webPDF server without proper authorization.</b>
 * </p>
 */
@SuppressWarnings("unused")
public final class SessionFactory {

    /**
     * This class is not intended to be instantiated, use the static methods instead.
     */
    private SessionFactory() {
    }

    /**
     * <p>
     * Creates a HTTP or HTTPS {@link Session} with a webPDF server, based on the given {@link SessionContext}.
     * </p>
     * <p>
     * This factory will either produce a {@link SoapWebServiceSession} or a {@link RestWebServiceSession}. It is not
     * fit to produce custom session types.
     * </p>
     * <p>
     * <b>Be Aware:</b> This shall implicitly use the {@link AnonymousAuthProvider}, and "anonymous sessions" may or
     * may not be allowed by your webPDF server - you should check that first, before using this factory method.<br>
     * <b>It is never possible to establish a session with the webPDF server without proper authorization.</b>
     * </p>
     * <p>
     * <b>Be Aware:</b> Neither {@link SessionContext}, nor {@link AuthProvider} are required to serve multiple
     * {@link Session}s at a time. It is expected to create a new {@link SessionContext} and {@link AuthProvider}
     * per {@link Session} you create.
     * </p>
     *
     * @param serverContext The {@link SessionContext} containing advanced options for the session initialization.
     * @return The {@link Session} organizing the communication with the webPDF server.
     * @throws ResultException Shall be thrown in case establishing the {@link Session} failed.
     */
    public static <T_SESSION extends Session> @NotNull T_SESSION createInstance(
            @NotNull SessionContext serverContext) throws ResultException {
        return createInstance(serverContext, new AnonymousAuthProvider());
    }

    /**
     * <p>
     * Creates a HTTP or HTTPS {@link Session} with a webPDF server, based on the given {@link SessionContext} and
     * {@link AuthProvider}.
     * </p>
     * <p>
     * This factory will either produce a {@link SoapWebServiceSession} or a {@link RestWebServiceSession}. It is not
     * fit to produce custom session types.
     * </p>
     * <p>
     * <b>Be Aware:</b> Neither {@link SessionContext}, nor {@link AuthProvider} are required to serve multiple
     * {@link Session}s at a time. It is expected to create a new {@link SessionContext} and {@link AuthProvider}
     * per {@link Session} you create.
     * </p>
     *
     * @param serverContext The {@link SessionContext} containing advanced options for the {@link Session}.
     * @param authProvider  The {@link AuthProvider} to use for authentication/authorization of the {@link Session}.
     * @return The {@link Session} organizing the communication with the webPDF server.
     * @throws ResultException Shall be thrown in case establishing the {@link Session} failed.
     */
    @SuppressWarnings("unchecked")
    public static <T_SESSION extends Session> @NotNull T_SESSION createInstance(
            @NotNull SessionContext serverContext, @NotNull AuthProvider authProvider) throws ResultException {
        try {
            switch (serverContext.getWebServiceProtocol()) {
                case SOAP:
                    return (T_SESSION) new SoapWebServiceSession(serverContext, authProvider);
                case REST:
                    return (T_SESSION) new RestWebServiceSession(serverContext, authProvider);
                default:
                    throw new ClientResultException(Error.UNKNOWN_SESSION_TYPE);
            }
        } catch (ClassCastException ex) {
            throw new ClientResultException(Error.UNKNOWN_SESSION_TYPE, ex);
        }
    }

}