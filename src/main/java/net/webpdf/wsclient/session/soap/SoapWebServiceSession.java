package net.webpdf.wsclient.session.soap;

import net.webpdf.wsclient.session.auth.AuthProvider;
import net.webpdf.wsclient.webservice.WebServiceProtocol;
import net.webpdf.wsclient.exception.ResultException;
import net.webpdf.wsclient.session.connection.https.TLSContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * <p>
 * An instance of {@link SoapWebServiceSession} establishes and manages a {@link WebServiceProtocol#SOAP} connection
 * with a webPDF server.
 * </p>
 */
public class SoapWebServiceSession extends AbstractSoapSession {

    /**
     * Creates a new {@link SoapWebServiceSession} instance providing connection information, authorization objects for
     * a webPDF server-client {@link SoapSession}.
     *
     * @param url          The {@link URL} of the webPDF server
     * @param tlsContext   The {@link TLSContext} used for this https session.
     *                     ({@code null} in case an unencrypted HTTP session shall be created.)
     * @param authProvider The {@link AuthProvider} for authentication/authorization of this {@link SoapSession}.
     * @throws ResultException Shall be thrown, in case establishing the session failed.
     */
    public SoapWebServiceSession(@NotNull URL url, @Nullable TLSContext tlsContext,
            @NotNull AuthProvider authProvider) throws ResultException {
        super(url, tlsContext, authProvider);
    }

}
